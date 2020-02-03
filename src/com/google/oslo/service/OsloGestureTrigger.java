package com.google.oslo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.soundtrigger.SoundTriggerDetector;
import android.media.soundtrigger.SoundTriggerManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import com.google.oslo.service.serviceinterface.OsloStrings;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

public class OsloGestureTrigger {
    private static final boolean DEBUG = true;
    private static final int DEFAULT_SETTINGS_ENABLED_STATE = 0;
    private static final int MODEL_DATA_BUFFER_SIZE = 1024;
    private static final UUID MODEL_UUID = UUID.fromString("5c0c296d-204c-4c2b-9f85-e50746caf914");
    private static final int MSG_RELOAD_PLUGIN = 0;
    private static final int RELOAD_PLUGIN_DELAY_MS = 5000;
    private static final String TAG = "Oslo/OsloGestureTrigger";
    private static final UUID VENDOR_UUID = UUID.fromString("5c0c296d-204c-4c2b-9f85-e50746caf914");
    private boolean mBatterySaverEnabled = false;
    private boolean mBootCompleted = false;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.LOCKED_BOOT_COMPLETED".equals(intent.getAction()) || "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                OsloGestureTrigger.this.updateBootCompleted();
            } else if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
                OsloGestureTrigger.this.updateBatterySaverState(context);
            } else if ("android.intent.action.USER_BACKGROUND".equals(intent.getAction())) {
                Log.v(OsloGestureTrigger.TAG, "Received user background");
                boolean unused = OsloGestureTrigger.this.mUserForeground = false;
                OsloGestureTrigger.this.updateOsloEnabled();
            } else if ("android.intent.action.USER_FOREGROUND".equals(intent.getAction())) {
                Log.v(OsloGestureTrigger.TAG, "Received user foreground");
                boolean unused2 = OsloGestureTrigger.this.mUserForeground = OsloGestureTrigger.DEBUG;
                OsloGestureTrigger.this.updateOsloEnabled();
            }
        }
    };
    private final Context mContext;
    private final SoundTriggerDetector mGestureTriggerDetector;
    private final SoundTriggerManager mGestureTriggerManager;

    public final MyHandler mHandler;
    private final SoundTriggerManager.Model mModel;
    private boolean mOsloSettingEnabled = false;
    private boolean mPluginLoaded = false;
    private final Random mRandom;
    private final UserContentObserver mSettingsObserver;

    public boolean mUserForeground = DEBUG;

    private class DetectorCallback extends SoundTriggerDetector.Callback {
        private DetectorCallback() {
        }

        public void onAvailabilityChanged(int status) {
            Log.d(OsloGestureTrigger.TAG, String.format("Availability status changed to %d", new Object[]{Integer.valueOf(status)}));
        }

        public void onDetected(SoundTriggerDetector.EventPayload event) {
            Log.d(OsloGestureTrigger.TAG, "onDetected()");
        }

        public void onError() {
            Log.d(OsloGestureTrigger.TAG, "onError");
            OsloGestureTrigger.this.mHandler.sendEmptyMessageDelayed(0, 5000);
        }

        public void onRecognitionPaused() {
            Log.d(OsloGestureTrigger.TAG, "onRecognitionPaused()");
        }

        public void onRecognitionResumed() {
            Log.d(OsloGestureTrigger.TAG, "onRecognitionResumed()");
        }
    }

    private final class MyHandler extends Handler {
        MyHandler() {
        }

        MyHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what != 0) {
                super.handleMessage(msg);
                return;
            }
            Log.d(OsloGestureTrigger.TAG, "Reloading plugin");
            unloadGesturePlugin();
            OsloGestureTrigger.this.loadGesturePlugin();
        }
    }

    public OsloGestureTrigger(Context context) {
        this.mContext = context;
        this.mHandler = new MyHandler();
        this.mRandom = new Random();
        this.mModel = SoundTriggerManager.Model.create(MODEL_UUID, VENDOR_UUID, getModelData());
        this.mGestureTriggerManager = (SoundTriggerManager) this.mContext.getSystemService(SoundTriggerManager.class);
        this.mGestureTriggerDetector = createSoundTriggerDetector(new DetectorCallback());
        this.mSettingsObserver = new UserContentObserver(this.mContext, Settings.Secure.getUriFor(getSettingName()), new Consumer() {
            public final void accept(Object obj) {
                OsloGestureTrigger.this.lambda$new$0$OsloGestureTrigger((Uri) obj);
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.LOCKED_BOOT_COMPLETED");
        filter.addAction("android.intent.action.BOOT_COMPLETED");
        filter.addAction("android.intent.action.USER_BACKGROUND");
        filter.addAction("android.intent.action.USER_FOREGROUND");
        filter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        this.mContext.registerReceiver(this.mBroadcastReceiver, filter);
        updateOsloAvailability();
    }

    public /* synthetic */ void lambda$new$0$OsloGestureTrigger(Uri uri) {
        updateOsloSetting();
    }


    public void loadGesturePlugin() {
        if (!this.mPluginLoaded) {
            Log.d(TAG, "Updating Sound Model");
            this.mGestureTriggerManager.updateModel(this.mModel);
            Log.d(TAG, "Starting recognition");
            if (this.mGestureTriggerDetector.startRecognition(2)) {
                this.mPluginLoaded = DEBUG;
                return;
            }
            Log.e(TAG, "startRecognition failed");
            this.mGestureTriggerManager.deleteModel(MODEL_UUID);
            this.mHandler.sendEmptyMessageDelayed(0, 5000);
        }
    }


    public void unloadGesturePlugin() {
        if (this.mPluginLoaded) {
            this.mGestureTriggerDetector.stopRecognition();
            this.mGestureTriggerManager.deleteModel(MODEL_UUID);
            this.mPluginLoaded = false;
        }
    }

    private SoundTriggerDetector createSoundTriggerDetector(SoundTriggerDetector.Callback callback) {
        return this.mGestureTriggerManager.createSoundTriggerDetector(MODEL_UUID, callback, (Handler) null);
    }

    private byte[] getModelData() {
        byte[] modelData = new byte[MODEL_DATA_BUFFER_SIZE];
        this.mRandom.nextBytes(modelData);
        return modelData;
    }

    private boolean osloEnabled() {
        if (!this.mBootCompleted || !this.mOsloSettingEnabled || this.mBatterySaverEnabled || !this.mUserForeground) {
            return false;
        }
        return DEBUG;
    }


    public void updateOsloEnabled() {
        if (osloEnabled()) {
            loadGesturePlugin();
        } else {
            unloadGesturePlugin();
        }
    }


    public void updateBootCompleted() {
        this.mBootCompleted = "1".equals(SystemProperties.get("sys.boot_completed"));
        Log.v(TAG, "Received BootCompleted change: " + this.mBootCompleted);
        updateOsloEnabled();
    }


    public void updateBatterySaverState(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
        if (powerManager != null) {
            boolean isEnabled = powerManager.getPowerSaveState(13).batterySaverEnabled;
            this.mBatterySaverEnabled = isEnabled;
            Log.v(TAG, "Received BatterySaver state change: " + isEnabled);
            updateOsloEnabled();
            return;
        }
        Log.e(TAG, "Failed to get PowerManager service");
    }

    private String getSettingName() {
        return OsloStrings.OsloSettings.OSLO_ENABLED;
    }

    private void updateOsloSetting() {
        boolean z = false;
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), getSettingName(), 0, -2) != 0) {
            z = DEBUG;
        }
        boolean isEnabled = z;
        this.mOsloSettingEnabled = isEnabled;
        Log.v(TAG, "Received an Oslo settings change: " + isEnabled);
        updateOsloEnabled();
    }

    private void updateOsloAvailability() {
        updateBootCompleted();
        updateBatterySaverState(this.mContext);
        updateOsloSetting();
    }
}
