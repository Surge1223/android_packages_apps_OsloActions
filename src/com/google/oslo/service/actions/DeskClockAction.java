package com.google.oslo.service.actions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import com.google.oslo.service.UserContentObserver;
import com.google.oslo.service.serviceinterface.OsloServiceManager;
import com.google.oslo.service.serviceinterface.OsloStrings;
import java.util.function.Consumer;

abstract class DeskClockAction extends Action {
    private static final String ALERT_BROADCASTS_PERMISSION = "com.android.systemui.permission.SEND_ALERT_BROADCASTS";
    private boolean mAlertFiring = false;
    private final BroadcastReceiver mAlertReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DeskClockAction.this.getAlertAction())) {
                DeskClockAction.this.setAlertFiring(true);
            } else if (intent.getAction().equals(DeskClockAction.this.getDoneAction())) {
                DeskClockAction.this.setAlertFiring(false);
            }
            if (DeskClockAction.this.getAlertFiring()) {
                DeskClockAction.this.registerListener();
            } else {
                DeskClockAction.this.unregisterListener();
            }
        }
    };
    private boolean mReceiverRegistered = false;

    
    public abstract String getAlertAction();

    
    public abstract String getDoneAction();

    DeskClockAction(Context context, OsloServiceManager osloServiceManager) {
        super(context, osloServiceManager);
        updateActionDetectorRegistration();
        this.mSettingsObserver = new UserContentObserver(getContext(), Settings.Secure.getUriFor(getSettingName()), new Consumer() {
            public final void accept(Object obj) {
                DeskClockAction.this.lambda$new$0$DeskClockAction((Uri) obj);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$DeskClockAction(Uri u) {
        updateActionDetectorRegistration();
    }

    
    public String getSettingName() {
        return OsloStrings.OsloSettings.OSLO_SILENCE_INTERRUPTIONS_ENABLED;
    }

    
    public void unregisterActionDetector() {
        getContext().unregisterReceiver(this.mAlertReceiver);
    }

    
    public void registerActionDetector() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(getAlertAction());
        filter.addAction(getDoneAction());
        getContext().registerReceiverAsUser(this.mAlertReceiver, UserHandle.CURRENT, filter, ALERT_BROADCASTS_PERMISSION, (Handler) null);
    }

    
    public void setAlertFiring(boolean alertFiring) {
        this.mAlertFiring = alertFiring;
    }

    
    public boolean getAlertFiring() {
        return this.mAlertFiring;
    }
}
