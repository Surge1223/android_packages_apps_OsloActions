package com.google.oslo.service;

import android.content.Context;
import android.frameworks.stats.V1_0.VendorAtom;
import android.net.Uri;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import com.google.oslo.service.OsloService;
import com.google.oslo.service.sensors.CHRESensor;
import com.google.oslo.service.serviceinterface.OsloAtoms;
import com.google.oslo.service.serviceinterface.OsloStrings;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;
import com.google.oslo.service.serviceinterface.output.OsloFlickOutput;
import com.google.oslo.service.serviceinterface.output.OsloPresenceOutput;
import com.google.oslo.service.serviceinterface.output.OsloReachOutput;
import com.google.oslo.service.serviceinterface.output.OsloStatusOutput;
import com.google.oslo.service.serviceinterface.output.OsloSwipeOutput;
import java.util.HashMap;
import java.util.function.Consumer;

public class OsloMetrics {
    protected static final boolean DEBUG = true;
    private static final int DEFAULT_SETTINGS_ENABLED_STATE = 0;
    private static final String TAG = "Oslo/OsloMetrics";
    private final Context mContext;
    private final HashMap<String, ClientStats> mFlickClients = new HashMap<>();
    private final HashMap<String, ClientStats> mFlickEchoClients = new HashMap<>();
    private boolean mOsloEnabledInSettings;
    private final HashMap<String, ClientStats> mPresenceClients = new HashMap<>();
    private final HashMap<String, ClientStats> mReachClients = new HashMap<>();
    private final HashMap<String, ClientStats> mReachEchoClients = new HashMap<>();
    private final UserContentObserver mSettingsObserver;
    private final HashMap<String, ClientStats> mStatusClients = new HashMap<>();
    private final HashMap<String, ClientStats> mSwipeClients = new HashMap<>();
    private final HashMap<String, ClientStats> mSwipeEchoClients = new HashMap<>();

    private class ClientStats {

        public long mLastElapsedTime;

        public long mLastRegisteredTime;

        public long mLastUnregisteredTime;

        public int mRegisteredCount;

        public int mUnregisteredCount;

        private ClientStats() {
            mRegisteredCount = 0;
            mUnregisteredCount = 0;
            mLastElapsedTime = 0;
            mLastRegisteredTime = 0;
            mLastUnregisteredTime = 0;
        }
    }

    public OsloMetrics(Context context) {
        mContext = context;
        mSettingsObserver = new UserContentObserver(mContext, Settings.Secure.getUriFor(getSettingName()), (Consumer) obj -> OsloMetrics.this.newOsloMetrics((Uri) obj) );

    }

    public void newOsloMetrics(Uri uri) {
        logOsloSetting();
    }

    public void logEvent(OsloPresenceOutput presence) {
        Log.d(TAG, "logEvent: " + presence.toString());
        reportVendorAtom(presence.toVendorAtom());
    }

    public void logEvent(OsloReachOutput reach) {
        Log.d(TAG, "logEvent: " + reach.toString());
        reportVendorAtom(reach.toVendorAtom(5));
    }

    public void logEvent(OsloSwipeOutput swipe) {
        Log.d(TAG, "logEvent: " + swipe.toString());
        reportVendorAtom(swipe.toVendorAtom());
    }

    public void logEvent(OsloFlickOutput flick) {
        Log.d(TAG, "logEvent: " + flick.toString());
        reportVendorAtom(flick.toVendorAtom());
    }

    public void logEvent(OsloStatusOutput status) {
        Log.d(TAG, "logEvent: " + status.toString());
        reportVendorAtom(status.toVendorAtom());
    }

    private void updateClientRequestStats(HashMap<String, ClientStats> gestureMap, OsloService.OsloGestureClient client, boolean register) {
        String clientName = new OsloGestureConfig(client.getGestureConfig()).getId();
        if (gestureMap.containsKey(clientName)) {
            ClientStats stats = gestureMap.get(clientName);
            if (register) {
                int unused = stats.mRegisteredCount = stats.mRegisteredCount + 1;
                long unused2 = stats.mLastRegisteredTime = SystemClock.elapsedRealtime();
                Log.d(TAG, "Register " + stats.mRegisteredCount + " timestamp " + stats.mLastRegisteredTime);
                reportVendorAtom(client.toVendorAtom());
                return;
            }
            int unused3 = stats.mUnregisteredCount = stats.mUnregisteredCount + 1;
            long unused4 = stats.mLastUnregisteredTime = SystemClock.elapsedRealtime();
            long unused5 = stats.mLastElapsedTime = stats.mLastUnregisteredTime - stats.mLastRegisteredTime;
            Log.d(TAG, "Unregister, registered count " + stats.mRegisteredCount + " unregistered count " + stats.mUnregisteredCount + " timestamp " + stats.mLastUnregisteredTime + " elapsed duration " + stats.mLastElapsedTime);
            reportVendorAtom(client.unregisterToVendorAtom(stats.mLastElapsedTime));
            return;
        }
        ClientStats stats2 = new ClientStats();
        if (register) {
            int unused6 = stats2.mRegisteredCount = stats2.mRegisteredCount + 1;
            long unused7 = stats2.mLastRegisteredTime = SystemClock.elapsedRealtime();
            Log.d(TAG, "First time register " + stats2.mRegisteredCount + " timestamp " + stats2.mLastRegisteredTime);
            reportVendorAtom(client.toVendorAtom());
        } else {
            int unused8 = stats2.mUnregisteredCount = stats2.mUnregisteredCount + 1;
            Log.e(TAG, "Received a client's request to unregister without any previoustrack record of client");
        }
        gestureMap.put(clientName, stats2);
    }

    public void logClientRequest(OsloService.OsloGestureClient client, boolean register) {
        int gesture = client.getType();
        String clientName = CHRESensor.getGestureConfigId(client);
        switch (gesture) {
            case 1:
                Log.d(TAG, "log flick client " + clientName + " gesture " + gesture + " register " + register);
                updateClientRequestStats(mFlickClients, client, register);
                return;
            case 2:
                Log.d(TAG, "log flick echo client " + clientName + " gesture " + gesture + " register " + register);
                updateClientRequestStats(mFlickEchoClients, client, register);
                return;
            case 3:
                Log.d(TAG, "log presence client " + clientName + " gesture " + gesture + " register " + register);
                updateClientRequestStats(mPresenceClients, client, register);
                return;
            case 4:
                Log.d(TAG, "log reach client " + clientName + " gesture " + gesture + " register " + register);
                updateClientRequestStats(mReachClients, client, register);
                return;
            case 5:
                Log.d(TAG, "log reach echo client " + clientName + " gesture " + gesture + " register " + register);
                updateClientRequestStats(mReachEchoClients, client, register);
                return;
            case 6:
                Log.d(TAG, "log status client " + clientName + " gesture " + gesture + " register " + register);
                updateClientRequestStats(mStatusClients, client, register);
                return;
            case 7:
                Log.d(TAG, "log swipe client " + clientName + " gesture " + gesture + " register " + register);
                updateClientRequestStats(mSwipeClients, client, register);
                return;
            case 8:
                Log.d(TAG, "log swipe echo client " + clientName + " gesture " + gesture + " register " + register);
                updateClientRequestStats(mSwipeEchoClients, client, register);
                return;
            default:
                Log.d(TAG, "Invalid gesture type " + gesture);
                return;
        }
    }

    private String getSettingName() {
        return OsloStrings.OsloSettings.OSLO_ENABLED;
    }

    private void reportVendorAtom(VendorAtom atom) {
    }

    private void logOsloSetting() {
        boolean enableOsloSettings = false;
        if (Settings.Secure.getIntForUser(mContext.getContentResolver(), getSettingName(), 0, -2) != 0) {
            enableOsloSettings = DEBUG;
        }
        mOsloEnabledInSettings = enableOsloSettings;
        Log.d(TAG, "Oslo enabled: " + mOsloEnabledInSettings);
        reportVendorAtom(OsloAtoms.packIntoVendorAtom(mOsloEnabledInSettings));
    }
}
