package com.google.oslo.service.actions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.oslo.service.UserContentObserver;
import com.google.oslo.service.serviceinterface.OsloServiceManager;
import com.google.oslo.service.serviceinterface.OsloStrings;
import java.util.function.Consumer;

abstract class PhoneCallAction extends Action {
    private static final boolean DEBUG = true;
    private static final String DIALER_BROADCAST_PERMISSION = "com.google.android.dialer.permission.RECEIVE_RING_STATE";
    private static final String DIALER_RING_STATE_CHANGED = "com.google.android.dialer.intent.action.RING_STATE_CHANGED";
    private final AudioManager mAudioManager = ((AudioManager) getContext().getSystemService(AudioManager.class));
    private final BroadcastReceiver mCallScreenRingStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean ringStarted = intent.getBooleanExtra("ringStarted", false);
            String tag = PhoneCallAction.this.getTag();
            Log.d(tag, "Ring started: " + ringStarted);
            if (ringStarted) {
                PhoneCallAction.this.registerListener();
            } else {
                PhoneCallAction.this.unregisterListener();
            }
        }
    };
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int phoneState, String incomingNumber) {
            if (PhoneCallAction.this.isPhoneRinging(phoneState)) {
                PhoneCallAction.this.registerListener();
            } else {
                PhoneCallAction.this.unregisterListener();
            }
        }
    };
    private boolean mSilenceSettingEnabled = false;
    private final TelecomManager mTelecomManager = ((TelecomManager) getContext().getSystemService(TelecomManager.class));
    private final TelephonyManager mTelephonyManager = ((TelephonyManager) getContext().getSystemService(TelephonyManager.class));

    PhoneCallAction(Context context, OsloServiceManager osloServiceManager) {
        super(context, osloServiceManager);
        updateActionDetectorRegistration();
        this.mSettingsObserver = new UserContentObserver(getContext(), Settings.Secure.getUriFor(getSettingName()), new Consumer() {
            public final void accept(Object obj) {
                PhoneCallAction.this.lambda$new$0$PhoneCallAction((Uri) obj);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$PhoneCallAction(Uri u) {
        updateActionDetectorRegistration();
    }

    /* access modifiers changed from: protected */
    public String getSettingName() {
        return OsloStrings.OsloSettings.OSLO_SILENCE_INTERRUPTIONS_ENABLED;
    }

    private static String getDialerBroadcastPermission() {
        return DIALER_BROADCAST_PERMISSION;
    }

    private static String getDialerRingStateChanged() {
        return DIALER_RING_STATE_CHANGED;
    }

    /* access modifiers changed from: protected */
    public void unregisterActionDetector() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        getContext().unregisterReceiver(this.mCallScreenRingStateReceiver);
    }

    /* access modifiers changed from: protected */
    public void registerActionDetector() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
        getContext().registerReceiver(this.mCallScreenRingStateReceiver, new IntentFilter(getDialerRingStateChanged()), getDialerBroadcastPermission(), (Handler) null);
    }

    /* access modifiers changed from: private */
    public boolean isPhoneRinging(int phoneState) {
        if (phoneState == 1) {
            return DEBUG;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void silenceRinger() {
        this.mTelecomManager.silenceRinger();
        AudioUtils.attenuateVolume(this.mAudioManager, 0.0f);
    }
}
