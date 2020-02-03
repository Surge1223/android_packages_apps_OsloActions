package com.google.oslo.service.sensors;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.location.ContextHubClient;
import android.hardware.location.ContextHubClientCallback;
import android.hardware.location.ContextHubInfo;
import android.hardware.location.ContextHubManager;
import android.hardware.location.ContextHubTransaction;
import android.hardware.location.NanoAppMessage;
import android.hardware.location.NanoAppState;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.oslo.service.OsloDeviceConfig;
import com.google.oslo.service.OsloService;
import com.google.oslo.service.UserContentObserver;
import com.google.oslo.service.serviceinterface.OsloStrings;
import com.google.oslo.service.serviceinterface.aidl.IOsloServiceGestureListener;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public abstract class CHRESensor {
    private static final String AWARE_ALLOWED_OVERRIDE_PROPERTY = "pixel.oslo.allowed_override";
    private static final long CHRE_OSLO_NANOAPP_ID = 5147455389092024340L;
    protected static final boolean DEBUG = true;
    private static final int DEFAULT_SETTINGS_ENABLED_STATE = 3;
    private static final String PREFERENCES_STORAGE_NAME = "pixel.oslo.preferences";
    private static final String PREFERENCE_COUNTRY_CODE = "pixel.oslo.country_code";
    private static final long QUERY_TIMEOUT_SECONDS = 5;
    private static final String TAG = "Oslo/CHRESensor";
    
    public static Set<String> sMccWhitelist = OsloDeviceConfig.createSetFromString(OsloDeviceConfig.getMccList());
    private boolean mAvailabilityKnown;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.telephony.action.NETWORK_COUNTRY_CHANGED".equals(intent.getAction())) {
                String countryCode = intent.getStringExtra("android.telephony.extra.NETWORK_COUNTRY");
                int phoneId = intent.getIntExtra("phone", -1);
                if (SubscriptionManager.isValidPhoneId(phoneId)) {
                    CHRESensor.this.mCountryCodeFromTelephony[phoneId] = countryCode;
                    for (String networkCountry : CHRESensor.this.mCountryCodeFromTelephony) {
                        if (!TextUtils.isEmpty(networkCountry)) {
                            countryCode = networkCountry;
                        }
                    }
                }
                CHRESensor.this.updateOsloCountry(countryCode);
            } else if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
                CHRESensor.this.updateBatterySaverState(context);
            } else if ("android.intent.action.AIRPLANE_MODE".equals(intent.getAction())) {
                CHRESensor.this.updateAirplaneModeState(context);
            }
        }
    };
    protected final Context mContext;
    public ContextHubClient mContextHubClient;
    private final ContextHubClientCallback mContextHubClientCallback = new ContextHubClientCallback() {
        public void onMessageFromNanoApp(ContextHubClient client, NanoAppMessage message) {
            if (message.getNanoAppId() == CHRESensor.CHRE_OSLO_NANOAPP_ID) {
                if (!CHRESensor.this.mNanoAppFound) {
                    Log.wtf(CHRESensor.this.getTag(), "onMessageReceipt(): nanoapp not found");
                    return;
                }
                try {
                    if (message.getMessageType() == 18) {
                        CHRESensor.this.sendCurrentConfigsToNanoapp(client);
                    } else {
                        CHRESensor.this.handleContextHubMessageReceipt(message);
                    }
                } catch (InvalidProtocolBufferNanoException e) {
                    Log.e(CHRESensor.this.getTag(), "Invalid protocol buffer", e);
                }
            }
        }

        public void onHubReset(ContextHubClient client) {
            Log.d(CHRESensor.this.getTag(), "Context hub was reset ");
            ContextHubClient unused = CHRESensor.this.mContextHubClient = client;
        }
    };
    private ContextHubInfo mContextHubInfo;
    private final ContextHubManager mContextHubManager;
    
    public String[] mCountryCodeFromTelephony;
    private String mCurrentCountryCode;
    private final DeviceConfig.OnPropertiesChangedListener mDeviceConfigListener = properties -> {
        if (properties.getKeyset().contains(OsloDeviceConfig.FLAG_MCC_LIST)) {
            Set unused = CHRESensor.sMccWhitelist = OsloDeviceConfig.createSetFromString(properties.getString(OsloDeviceConfig.FLAG_MCC_LIST, OsloDeviceConfig.DEFAULT_MCC_WHITELIST));
            CHRESensor cHRESensor = CHRESensor.this;
            cHRESensor.updateOsloCountry(cHRESensor.getNetworkCountry());
        }
    };
    private Stack<OsloService.OsloGestureClient> mEchoStack;
    protected boolean[] mEnabledReasons;
    private int mFindNanoAppRetries;
    private boolean mGestureComputationStarted;
    protected Stack<OsloService.OsloGestureClient> mListenerStack;
    
    public boolean mNanoAppFound;
    private final boolean mNanoAppFoundOnBoot;
    private boolean mOsloAvailableInCountryOverride;
    private UserContentObserver mSettingsObserver;

   
    public abstract byte[] buildGestureDisable();

   
    public abstract byte[] buildGestureEnable(OsloService.OsloGestureClient osloGestureClient) throws IOException;

   
    public abstract int getDisableMessageType();

   
    public abstract int getEnableMessageType();

   
    public abstract int getGesture();

   
    public abstract StatusSensor getStatusSensor();

   
    public abstract String getTag();

   
    public abstract void handleContextHubMessageReceipt(NanoAppMessage nanoAppMessage) throws InvalidProtocolBufferNanoException;

    public CHRESensor(Context context) {
        mContext = context;
        mContextHubManager = mContext.getSystemService(ContextHubManager.class);
        mNanoAppFoundOnBoot = findNanoApp();
        mListenerStack = new Stack<>();
        mEchoStack = new Stack<>();
        mEnabledReasons = new boolean[4];
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.telephony.action.NETWORK_COUNTRY_CHANGED");
        filter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        filter.addAction("android.intent.action.AIRPLANE_MODE");
        mContext.registerReceiver(mBroadcastReceiver, filter);
        mCountryCodeFromTelephony = new String[mContext.getSystemService(TelephonyManager.class).getPhoneCount()];
        mSettingsObserver = new UserContentObserver(mContext, Settings.Secure.getUriFor(getSettingName()), new Consumer() {
            public final void accept(Object obj) {
                CHRESensor.this.updateOsloSettings((Uri) obj);
            }
        });
        startListening();
        mOsloAvailableInCountryOverride = getAwareAllowedOverrideProperty();
        Log.d(TAG, "Aware allowed override property: " + mOsloAvailableInCountryOverride);
        DeviceConfig.addOnPropertiesChangedListener(OsloDeviceConfig.NAMESPACE, mContext.getMainExecutor(), mDeviceConfigListener);
    }

    public  void updateOsloSettings(Uri uri) {
        updateOsloSetting();
    }

   
    public void sendCurrentConfigsToNanoapp(ContextHubClient client) {
        Log.d(getTag(), "sending current configs to nanoapp");
        mGestureComputationStarted = false;
        if (getSubscriberCount() > 0) {
            startGestureDetection(mListenerStack.peek());
        }
    }

   
    public void updateOsloAvailability() {
        updateOsloSetting();
        updateBatterySaverState(mContext);
        updateAirplaneModeState(mContext);
        updateOsloCountry(getNetworkCountry());
    }

    
    public String getNetworkCountry() {
        TelephonyManager tm = mContext.getSystemService(TelephonyManager.class);
        String networkCountryIso = "";
        for (int i = 0; i < tm.getPhoneCount(); i++) {
            networkCountryIso = tm.getNetworkCountryIsoForPhone(i).toLowerCase();
            if (!TextUtils.isEmpty(networkCountryIso)) {
                return networkCountryIso;
            }
        }
        return networkCountryIso;
    }

    private boolean findNanoApp() {
        if (mNanoAppFound) {
            return DEBUG;
        }
        mFindNanoAppRetries++;
        List<ContextHubInfo> contextHubInfos = mContextHubManager.getContextHubs();
        if (contextHubInfos.size() == 0) {
            Log.e(TAG, "No context hubs found");
            return false;
        }
        mContextHubInfo = contextHubInfos.get(0);
        try {
            ContextHubTransaction.Response<List<NanoAppState>> response = mContextHubManager.queryNanoApps(contextHubInfos.get(0)).waitForResponse(QUERY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            int i = 0;
            while (true) {
                if (i >= response.getContents().size()) {
                    break;
                } else if (((NanoAppState) ((List) response.getContents()).get(i)).getNanoAppId() == CHRE_OSLO_NANOAPP_ID) {
                    mNanoAppFound = DEBUG;
                    break;
                } else {
                    i++;
                }
            }
            return DEBUG;
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted while looking for nanoapp");
            return false;
        } catch (TimeoutException e2) {
            Log.e(TAG, "Timed out looking for nanoapp");
            return false;
        }
    }

    private void startListening() {
        if (!mNanoAppFound) {
            boolean found = findNanoApp();
            Log.e(TAG, "startListening(): nanoapp not found, refind = " + found);
            if (!found) {
                return;
            }
        }
        mContextHubClient = mContextHubManager.createClient(mContextHubInfo, mContextHubClientCallback);
    }

    private void startGestureDetection(OsloService.OsloGestureClient client) {
        if (!mGestureComputationStarted && osloEnabled()) {
            try {
                sendMessageToNanoApp(getEnableMessageType(), buildGestureEnable(client));
            } catch (IOException e) {
                Log.e(getTag(), "Unable to serialize start proto", e);
            }
            mGestureComputationStarted = DEBUG;
        }
    }

    private void stopGestureDetection() {
        if (mGestureComputationStarted) {
            sendMessageToNanoApp(getDisableMessageType(), buildGestureDisable());
            mGestureComputationStarted = false;
        }
    }

   
    public void sendMessageToNanoApp(int messageType, byte[] bytes) {
        if (mContextHubClient == null) {
            Log.e(getTag(), "mContextHubClient is not initialized.");
            return;
        }
        if (mContextHubClient.sendMessageToNanoApp(NanoAppMessage.createMessageToNanoApp(CHRE_OSLO_NANOAPP_ID, messageType, bytes)) != 0) {
            Log.e(getTag(), "Unable to send message to nanoapp");
        }
    }

   
    public byte[] serializeProtobuf(MessageNano message) throws IOException {
        byte[] bytes = new byte[message.getSerializedSize()];
        message.writeTo(CodedOutputByteBufferNano.newInstance(bytes));
        return bytes;
    }

    private void updateOsloSetting() {
        boolean isEnabled = Settings.Secure.getIntForUser( mContext.getContentResolver(), getSettingName(), 0, -2 ) != 0 && DEBUG;
        mEnabledReasons[0] = isEnabled;
        String tag = getTag();
        Log.v(tag, "Received an Oslo settings change: " + isEnabled);
        Log.v(tag, "attempting to hack past plugin: " + isEnabled);
        String countryCode = "us";
        mEnabledReasons[3] = true;
        mAvailabilityKnown = DEBUG;
        setStoredCountryCode(countryCode);
        CHRESensor cHRESensor = CHRESensor.this;
        cHRESensor.updateOsloCountry(cHRESensor.getNetworkCountry());
        updateOsloEnabled();
    }

   
    public boolean updateOsloCountry(String countryCode) {
        boolean availabilityStateChanged = false;
        if (!mOsloAvailableInCountryOverride && TextUtils.isEmpty(countryCode)) {
            String storedCountryCode = getStoredCountryCode();
            if (TextUtils.isEmpty(storedCountryCode)) {
                return false;
            }
            Log.v(getTag(), "Use last known country code [" + storedCountryCode + "] instead of received [" + countryCode + "]");
            countryCode = storedCountryCode;
        }
        boolean prevAvailability = mEnabledReasons[3];
        int i = 0;
        boolean isAvailable = isAvailableInCountry(countryCode) || mOsloAvailableInCountryOverride;
        if (mAvailabilityKnown) {
            availabilityStateChanged = prevAvailability ^ isAvailable;
        }
        Log.v(getTag(), "Received country change: \"" + countryCode + "\", availability: " + isAvailable);
        ContentResolver contentResolver = mContext.getContentResolver();
        if (isAvailable) {
            i = 1;
        }
        Settings.Global.putInt(contentResolver, "aware_allowed", i);
        mEnabledReasons[3] = isAvailable;
        updateOsloEnabled();
        mAvailabilityKnown = DEBUG;
        setStoredCountryCode(countryCode);
        return availabilityStateChanged;
    }

    private String getStoredCountryCode() {
        return mContext.createDeviceProtectedStorageContext().getSharedPreferences(PREFERENCES_STORAGE_NAME, 0).getString(PREFERENCE_COUNTRY_CODE, "");
    }

    private void setStoredCountryCode(String countryCode) {
        SharedPreferences.Editor editor = mContext.createDeviceProtectedStorageContext().getSharedPreferences(PREFERENCES_STORAGE_NAME, 0).edit();
        editor.putString(PREFERENCE_COUNTRY_CODE, countryCode);
        editor.commit();
    }

    private boolean isAvailableInCountry(String countryCode) {
        return sMccWhitelist.contains(countryCode);
    }

   
    public void updateBatterySaverState(Context context) {
        PowerManager powerManager = context.getSystemService(PowerManager.class);
        if (powerManager != null) {
            boolean isEnabled = powerManager.getPowerSaveState(13).batterySaverEnabled;
            mEnabledReasons[2] = isEnabled ^ DEBUG;
            String tag = getTag();
            Log.v(tag, "Received BatterySaver state change: " + isEnabled);
            updateOsloEnabled();
            return;
        }
        Log.e(getTag(), "Failed to get PowerManager service");
    }

   
    public void updateAirplaneModeState(Context context) {
        boolean z = false;
        boolean isEnabled = Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) == 1;
        boolean[] zArr = mEnabledReasons;
        if (!isEnabled) {
            z = true;
        }
        zArr[1] = z;
        Log.v(getTag(), "Received an Airplane mode state chage: " + isEnabled);
        updateOsloEnabled();
    }

   
    public void updateOsloEnabled() {
        if (!osloEnabled()) {
            stopGestureDetection();
        } else if (getSubscriberCount() > 0) {
            startGestureDetection(mListenerStack.peek());
        }
    }

    private boolean getAwareAllowedOverrideProperty() {
        return SystemProperties.get(AWARE_ALLOWED_OVERRIDE_PROPERTY).isEmpty() ^ DEBUG;
    }

   
    public boolean osloEnabled() {
        boolean[] zArr = mEnabledReasons;
        if (!zArr[3] || !zArr[0] || !zArr[2] || !zArr[1]) {
            return false;
        }
        return DEBUG;
    }

    private int getEchoSubscriberCount() {
        return mEchoStack.size();
    }

    public static String getGestureConfigId(OsloService.OsloGestureClient client) {
        return new OsloGestureConfig(client.getGestureConfig()).getId();
    }

   
    public void updateStatusReport(int gestureType, int subscriberCount, OsloService.OsloGestureClient client) {
        String id = null;
        if (client != null) {
            id = getGestureConfigId(client);
        }
        getStatusSensor().updateGestureSubscriberData(gestureType, subscriberCount, id);
    }

    private void reportListenerEvent(OsloService.OsloGestureClient client, boolean register) {
        getStatusSensor().reportListenerEvent(client, register);
    }

    public void registerEchoListener(OsloService.OsloGestureClient client) {
        mEchoStack.push(client);
        updateStatusReport(getEchoGesture(), getEchoSubscriberCount(), null );
        reportListenerEvent(client, DEBUG);
    }

   
    public void unregisterEchoListener(IBinder listener) {
        OsloService.OsloGestureClient unsubscribingGestureClient = removeListenerFromStack(mEchoStack, listener);
        if (unsubscribingGestureClient != null) {
            updateStatusReport(getEchoGesture(), getEchoSubscriberCount(), null );
            reportListenerEvent(unsubscribingGestureClient, false);
        }
    }

    private OsloService.OsloGestureClient removeListenerFromStack(Stack<OsloService.OsloGestureClient> stack, IBinder listener) {
        OsloService.OsloGestureClient removedClient = null;
        Iterator<OsloService.OsloGestureClient> iterator = stack.iterator();
        while (iterator.hasNext()) {
            OsloService.OsloGestureClient client = iterator.next();
            if (client.getListener() == listener) {
                removedClient = client;
                client.unlinkToDeath();
                iterator.remove();
            }
        }
        return removedClient;
    }

   
    public int getSubscriberCount() {
        return mListenerStack.size();
    }

    public void registerListener(OsloService.OsloGestureClient client) {
        if (new OsloGestureConfig(client.getGestureConfig()).getPriority() == 1) {
            mListenerStack.add(0, client);
            startGestureDetection(client);
        } else {
            stopGestureDetection();
            startGestureDetection(client);
            mListenerStack.push(client);
        }
        updateStatusReport(getGesture(), getSubscriberCount(), mListenerStack.peek());
        reportListenerEvent(client, DEBUG);
    }

    public void unregisterListener(IBinder listener) {
        if (getSubscriberCount() > 0) {
            OsloService.OsloGestureClient oldActiveClient = mListenerStack.peek();
            OsloService.OsloGestureClient unsubscribingGestureClient = removeListenerFromStack(mListenerStack, listener);
            if (unsubscribingGestureClient != null) {
                reportListenerEvent(unsubscribingGestureClient, false);
                if (getSubscriberCount() == 0) {
                    stopGestureDetection();
                    updateStatusReport(getGesture(), getSubscriberCount(), null );
                } else if (oldActiveClient != mListenerStack.peek()) {
                    stopGestureDetection();
                    startGestureDetection(mListenerStack.peek());
                    updateStatusReport(getGesture(), getSubscriberCount(), mListenerStack.peek());
                } else {
                    updateStatusReport(getGesture(), getSubscriberCount(), mListenerStack.peek());
                }
            }
        }
    }

   
    public void sendEchoEvents(Bundle data) {
        for (int i = 0; i < mEchoStack.size(); i++) {
            IBinder listener = mEchoStack.get(i).getListener();
            try {
                IOsloServiceGestureListener.Stub.asInterface(listener).onGestureDetected(data);
            } catch (DeadObjectException e) {
                Log.e(getTag(), "Listener crashed or closed without unregistering", e);
                unregisterEchoListener(listener);
            } catch (RemoteException e2) {
                Log.e(getTag(), "Unable to send onGestureDetected; removing listener", e2);
                unregisterEchoListener(listener);
            }
        }
    }

    private String getSettingName() {
        return OsloStrings.OsloSettings.OSLO_ENABLED;
    }

   
    public int getEchoGesture() {
        return 0;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println(CHRESensor.class.getSimpleName() + " state:");
        pw.println("  mGestureComputationStarted: " + mGestureComputationStarted);
        pw.println("  enabled in settings: " + mEnabledReasons[0]);
        pw.println("  enabled by airplane mode: " + mEnabledReasons[1]);
        pw.println("  enabled by batterysaver mode: " + mEnabledReasons[2]);
        pw.println("  enabled in country: " + mEnabledReasons[3]);
        pw.println("  mNanoAppFound: " + mNanoAppFound);
        pw.println("  mNanoAppFoundOnBoot: " + mNanoAppFoundOnBoot);
        pw.println("  mFindNanoAppRetries: " + mFindNanoAppRetries);
    }
}

