package com.google.oslo.service.sensors;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.location.ContextHubClient;
import android.hardware.location.NanoAppMessage;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import com.google.oslo.actions.R;
import com.google.oslo.service.OsloMetrics;
import com.google.oslo.service.OsloService;
import com.google.oslo.service.actions.AudioUtils;
import com.google.oslo.service.proto.nano.OsloMessages;
import com.google.oslo.service.serviceinterface.aidl.IOsloServiceStatusListener;
import com.google.oslo.service.serviceinterface.output.OsloFlickOutput;
import com.google.oslo.service.serviceinterface.output.OsloPresenceOutput;
import com.google.oslo.service.serviceinterface.output.OsloReachOutput;
import com.google.oslo.service.serviceinterface.output.OsloStatusOutput;
import com.google.oslo.service.serviceinterface.output.OsloSwipeOutput;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class StatusSensor extends CHRESensor {
    private static final String AWARE_SETTING_ACTION = "com.android.settings.action.AWARE_SETTING";
    private static final String CHANNEL_ID = "oslo_notification";
    private static final int DEFAULT_MAX_TX_POWER = 255;
    private static final String GATING_PROPERTY = "pixel.oslo.gating";
    private static final int SOLI_SDK_MAX_TX_POWER = 255;
    private static final int SOLI_SDK_MIN_TX_POWER = 0;
    private static final String TAG = "Oslo/StatusSensor";
    private static final String TX_POWER_CALIBRATION_FILE = "/mnt/vendor/persist/oslo/tx_power.cal";
    private final AudioDeviceCallback mAudioDeviceCallback = new AudioDeviceCallback() {
        public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
            super.onAudioDevicesAdded(addedDevices);
            StatusSensor statusSensor = StatusSensor.this;
            statusSensor.updateLocalAudioPlaying(statusSensor.mAudioManager.getActivePlaybackConfigurations());
        }

        public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
            super.onAudioDevicesAdded(removedDevices);
            StatusSensor statusSensor = StatusSensor.this;
            statusSensor.updateLocalAudioPlaying(statusSensor.mAudioManager.getActivePlaybackConfigurations());
        }
    };
    
    public final AudioManager mAudioManager;
    private final AudioManager.AudioPlaybackCallback mAudioPlaybackCallback = new AudioManager.AudioPlaybackCallback() {
        public void onPlaybackConfigChanged(List<AudioPlaybackConfiguration> configs) {
            super.onPlaybackConfigChanged(configs);
            StatusSensor.this.updateLocalAudioPlaying(configs);
        }
    };
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(StatusSensor.this.getTag(), "Received intent " + intent.getAction());
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                int i = 0;
                boolean onWireless = (intent.getIntExtra("plugged", 0) & 4) == 4;
                if (StatusSensor.this.mChargingWirelessly != onWireless) {
                    boolean unused = StatusSensor.this.mChargingWirelessly = onWireless;
                    Log.d(StatusSensor.this.getTag(), "setting wireless charging to: " + StatusSensor.this.mChargingWirelessly);
                    try {
                        StatusSensor statusSensor = StatusSensor.this;
                        int access$300 = StatusSensor.this.getSetParamMessageType();
                        StatusSensor statusSensor2 = StatusSensor.this;
                        if (StatusSensor.this.mChargingWirelessly) {
                            i = 1;
                        }
                        statusSensor.sendMessageToNanoApp(access$300, statusSensor2.buildSetParam(2, i));
                    } catch (IOException e) {
                        Log.e(StatusSensor.this.getTag(), "Unable to serialize setParam proto", e);
                    }
                }
            } else if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                StatusSensor statusSensor3 = StatusSensor.this;
                statusSensor3.updateGatingMode(statusSensor3.mDefaultGatingMode);
            } else if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                StatusSensor.this.updateGatingMode(2);
            }
        }
    };

    public boolean mChargingWirelessly = false;
    private int mCurrentGatingMode;
    public final int mDefaultGatingMode;
    private Intent mIntent;
    private boolean mLocalAudioPlaying = false;
    private int mMaxTxPower = 255;
    private NotificationManager mNotificationManager;
    private final OsloMetrics mOsloMetrics;
    private PendingIntent mPendingIntent;
    private boolean[] mPrevEnabledReasons;
    private final OsloStatusOutput mStatusOutput;


    public void updateLocalAudioPlaying(List<AudioPlaybackConfiguration> configs) {
        boolean localAudioPlaying = AudioUtils.isLocalSpeakerActive(this.mAudioManager, configs);
        if (localAudioPlaying != this.mLocalAudioPlaying) {
            this.mLocalAudioPlaying = localAudioPlaying;
            String tag = getTag();
            Log.d(tag, "setting local audio playback to: " + this.mLocalAudioPlaying);
            try {
                sendMessageToNanoApp(getSetParamMessageType(), buildSetParam(1, this.mLocalAudioPlaying ? 1 : 0));
            } catch (IOException e) {
                Log.e(getTag(), "Unable to serialize setParam proto", e);
            }
        }
    }

    public StatusSensor(Context context) {
        super(context);
        this.mAudioManager = (AudioManager) context.getSystemService(AudioManager.class);
        this.mAudioManager.registerAudioPlaybackCallback(this.mAudioPlaybackCallback, (Handler) null);
        this.mAudioManager.registerAudioDeviceCallback(this.mAudioDeviceCallback, (Handler) null);
        updateLocalAudioPlaying(this.mAudioManager.getActivePlaybackConfigurations());
        this.mStatusOutput = new OsloStatusOutput();
        this.mOsloMetrics = new OsloMetrics(context);
        this.mDefaultGatingMode = getGatingProperty();
        updateGatingMode(this.mDefaultGatingMode);
        this.mPrevEnabledReasons = new boolean[4];
        updateOsloAvailability();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        this.mContext.registerReceiver(this.mBroadcastReceiver, filter);
        readTxPowerCalibrationFile();
        sendMaxTxPower();
    }

    public void reportGestureDetectedEvent(OsloPresenceOutput presence) {
        this.mOsloMetrics.logEvent(presence);
    }

    public void reportGestureDetectedEvent(OsloReachOutput reach) {
        this.mOsloMetrics.logEvent(reach);
    }

    public void reportGestureDetectedEvent(OsloSwipeOutput swipe) {
        this.mOsloMetrics.logEvent(swipe);
    }

    public void reportGestureDetectedEvent(OsloFlickOutput flick) {
        this.mOsloMetrics.logEvent(flick);
    }

    public void reportListenerEvent(OsloService.OsloGestureClient client, boolean register) {
        this.mOsloMetrics.logClientRequest(client, register);
    }

    private void sendStatusEvents() {
        for (int i = 0; i < this.mListenerStack.size(); i++) {
            IBinder listener = ((OsloService.OsloGestureClient) this.mListenerStack.get(i)).getListener();
            if (!listener.pingBinder()) {
                String tag = getTag();
                Log.i(tag, "Status listener " + listener + " is dead");
            } else {
                try {
                    IOsloServiceStatusListener.Stub.asInterface(listener).onStatusChanged(this.mStatusOutput.toBundle());
                } catch (DeadObjectException e) {
                    Log.e(TAG, "Listener crashed or closed without unregistering", e);
                    unregisterListener(listener);
                } catch (RemoteException e2) {
                    Log.e(TAG, "Unable to send onStatusChanged; removing listener", e2);
                    unregisterListener(listener);
                }
            }
        }
    }


    public void updateOsloEnabled() {
        if (!Arrays.equals(this.mPrevEnabledReasons, this.mEnabledReasons)) {
            boolean isEnabled = osloEnabled();
            String tag = getTag();
            Log.d(tag, "OsloEnabled changed: enabled " + isEnabled + ", in settings " + this.mEnabledReasons[0] + ", by battery saver " + this.mEnabledReasons[2] + ", by airplane mode " + this.mEnabledReasons[1] + ", in country " + this.mEnabledReasons[3]);
            this.mStatusOutput.setEnabled(isEnabled);
            this.mStatusOutput.setEnabledReasons(this.mEnabledReasons);
            sendStatusEvents();
        }
        super.updateOsloEnabled();
    }

    private String getSettingsStringResource(String identifier) {
        if (TextUtils.isEmpty(identifier)) {
            return null;
        }
        try {
            Resources resources = this.mContext.getPackageManager().getResourcesForApplication("com.android.settings");
            int id = resources.getIdentifier(identifier, "string", "com.android.settings");
            if (id != 0) {
                return resources.getString(id);
            }
            Log.d(getTag(), "String resource not found");
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            String tag = getTag();
            Log.e(tag, "Caught exception: " + e);
            return null;
        }
    }

    private void sendNotification(boolean osloEnabled) {
        String contentText;
        String contentTitle;
        Intent intent = getNotificationIntent();
        if (osloEnabled) {
            contentTitle = getSettingsStringResource("aware_settings_enabled_info_dialog_title");
            contentText = this.mContext.getString(R.string.aware_settings_enabled_info_dialog_description);
            intent.putExtra("show_aware_dialog", 1);
        } else {
            contentTitle = getSettingsStringResource("aware_settings_disabled_info_dialog_title");
            contentText = this.mContext.getString(R.string.aware_settings_disabled_info_dialog_description);
            intent.putExtra("show_aware_dialog", 0);
        }
        this.mPendingIntent = PendingIntent.getActivity(this.mContext, 0, intent, 134217728);
        getNotificationChannel().notify(1, new Notification.Builder(this.mContext, CHANNEL_ID).setSmallIcon(R.drawable.motion_sense).setContentTitle(contentTitle).setContentText(contentText).setStyle(new Notification.BigTextStyle().bigText(contentText)).setPriority(2).setContentIntent(this.mPendingIntent).setAutoCancel(true).build());
        Log.d(getTag(), "Sent Oslo enabled notification to user");
    }

    private NotificationManager getNotificationChannel() {
        if (this.mNotificationManager == null) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, this.mContext.getResources().getString(R.string.app_name), 3);
            this.mNotificationManager = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
            this.mNotificationManager.createNotificationChannel(channel);
        }
        return this.mNotificationManager;
    }

    private Intent getNotificationIntent() {
        if (this.mIntent == null) {
            this.mIntent = new Intent();
            this.mIntent.setAction(AWARE_SETTING_ACTION);
        }
        return this.mIntent;
    }


    public void updateGestureSubscriberData(int gestureType, int subscriberCount, String id) {
        this.mStatusOutput.getStatusReportDataElement(gestureType).setSubscriberCount(subscriberCount);
        this.mStatusOutput.getStatusReportDataElement(gestureType).setActiveSubscriberId(id);
        if (osloEnabled() || gestureType == 6) {
            sendStatusEvents();
        }
    }

    
    public boolean updateOsloCountry(String countryCode) {
        boolean availabilityChanged = super.updateOsloCountry(countryCode);
        if (availabilityChanged && this.mEnabledReasons[0]) {
            if (this.mEnabledReasons[3]) {
                sendNotification(true);
            } else {
                sendNotification(false);
            }
        }
        return availabilityChanged;
    }

    
    public String getTag() {
        return TAG;
    }

    
    public byte[] buildGestureDisable() {
        return new byte[0];
    }

    
    public byte[] buildGestureEnable(OsloService.OsloGestureClient client) throws IOException {
        return new byte[0];
    }

    
    public void handleContextHubMessageReceipt(NanoAppMessage nanoAppMessage) throws InvalidProtocolBufferNanoException {
        if (nanoAppMessage.getMessageType() == 12) {
            this.mStatusOutput.setGatingReason(OsloMessages.StatusOutput.parseFrom(nanoAppMessage.getMessageBody()).gatingReason);
            this.mOsloMetrics.logEvent(this.mStatusOutput);
            if (osloEnabled()) {
                sendStatusEvents();
                return;
            }
            return;
        }
        Log.e(TAG, "Unknown message type: " + nanoAppMessage.getMessageType());
    }

    
    public void sendCurrentConfigsToNanoapp(ContextHubClient client) {
        super.sendCurrentConfigsToNanoapp(client);
        sendMaxTxPower();
        sendGatingConfig(this.mCurrentGatingMode);
    }

    
    public int getDisableMessageType() {
        return 11;
    }

    
    public int getEnableMessageType() {
        return 10;
    }

    
    public StatusSensor getStatusSensor() {
        return this;
    }

    
    public int getGesture() {
        return 6;
    }

    private int getGatingProperty() {
        String propVal = SystemProperties.get(GATING_PROPERTY);
        if (!propVal.isEmpty()) {
            return Integer.parseInt(propVal);
        }
        Log.d(TAG, "Gating property not set, default to DEFAULT");
        return 4;
    }

    private int getGatingMessageType() {
        return 16;
    }

    
    public int getSetParamMessageType() {
        return 17;
    }

    private byte[] buildGatingConfig(int gatingConfig) throws IOException {
        OsloMessages.GatingConfig mGatingConfig = new OsloMessages.GatingConfig();
        mGatingConfig.mode = gatingConfig;
        return serializeProtobuf(mGatingConfig);
    }

    
    public void updateGatingMode(int gatingMode) {
        this.mCurrentGatingMode = gatingMode;
        sendGatingConfig(this.mCurrentGatingMode);
    }

    private void sendGatingConfig(int gatingMode) {
        Log.d(TAG, "gatingMode: " + gatingMode);
        try {
            sendMessageToNanoApp(getGatingMessageType(), buildGatingConfig(gatingMode));
        } catch (IOException e) {
            Log.e(getTag(), "Unable to serialize start proto", e);
        }
    }

    
    public byte[] buildSetParam(int param, int value) throws IOException {
        OsloMessages.SetParam setParam = new OsloMessages.SetParam();
        setParam.param = param;
        setParam.value = value;
        return serializeProtobuf(setParam);
    }

    private void handleTxPower(String paramVal) {
        try {
            int newMaxTxPower = Integer.parseInt(paramVal);
            if (newMaxTxPower < 0) {
                String tag = getTag();
                Log.w(tag, "provided max TX power of " + this.mMaxTxPower + " below minimum allowed");
            } else if (newMaxTxPower > 255) {
                String tag2 = getTag();
                Log.w(tag2, "provided max TX power of " + this.mMaxTxPower + " above maximum allowed");
            } else {
                this.mMaxTxPower = newMaxTxPower;
                String tag3 = getTag();
                Log.d(tag3, "Max TX power is now: " + this.mMaxTxPower);
            }
        } catch (NumberFormatException e) {
            Log.w(getTag(), "TxPower was not numeric", e);
        }
    }

    private void readTxPowerCalibrationFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(TX_POWER_CALIBRATION_FILE)));
            while (true) {
                String readLine = br.readLine();
                String st = readLine;
                if (readLine != null) {
                    String[] parsed = st.split(":\\s+", 2);
                    if (2 == parsed.length && parsed[0].equals("tx_power")) {
                        handleTxPower(parsed[1]);
                    }
                } else {
                    return;
                }
            }
        } catch (IOException e) {
            Log.w(getTag(), "Unable to read oslo calibration data", e);
        }
    }

    private void sendMaxTxPower() {
        try {
            sendMessageToNanoApp(getSetParamMessageType(), buildSetParam(3, this.mMaxTxPower));
        } catch (IOException e) {
            Log.e(getTag(), "Unable to serialize setParam proto", e);
        }
    }
}
