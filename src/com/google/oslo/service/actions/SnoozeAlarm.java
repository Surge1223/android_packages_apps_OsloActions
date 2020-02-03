package com.google.oslo.service.actions;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import com.google.oslo.service.serviceinterface.OsloServiceManager;
import com.google.oslo.service.serviceinterface.OsloStrings;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;
import com.google.oslo.service.serviceinterface.output.OsloSwipeOutput;

public class SnoozeAlarm extends DeskClockAction {
    private static final float CONFIG_DETECTION_RADIUS = 1.0f;
    private static final int CONFIG_GRANULARITY = 3;
    private static final int CONFIG_SENSITIVITY = 1;
    private static final String TAG = "Oslo/SnoozeAlarm";

    public SnoozeAlarm(Context context, OsloServiceManager osloServiceManager) {
        super(context, osloServiceManager);
    }

    /* access modifiers changed from: protected */
    public String getAlertAction() {
        return "com.google.android.deskclock.action.ALARM_ALERT";
    }

    /* access modifiers changed from: protected */
    public String getDoneAction() {
        return "com.google.android.deskclock.action.ALARM_DONE";
    }

    private Intent createActionIntent() {
        return new Intent("android.intent.action.SNOOZE_ALARM");
    }

    /* access modifiers changed from: protected */
    public String getTag() {
        return TAG;
    }

    /* access modifiers changed from: protected */
    public int getListenerType() {
        return 7;
    }

    /* access modifiers changed from: protected */
    public void onTrigger(Bundle gestureOutput) {
        if (new OsloSwipeOutput(gestureOutput).getDetected()) {
            try {
                Intent intent = createActionIntent();
                ActivityOptions options = ActivityOptions.makeBasic();
                options.setDisallowEnterPictureInPictureWhileLaunching(true);
                intent.setFlags(268435456);
                intent.putExtra("android.intent.extra.REFERRER", Uri.parse("android-app://" + getContext().getPackageName()));
                getContext().startActivityAsUser(intent, options.toBundle(), UserHandle.CURRENT);
            } catch (ActivityNotFoundException e) {
                Log.e(getTag(), "Failed to dismiss alert", e);
            }
            setAlertFiring(false);
            unregisterListener();
        }
    }

    /* access modifiers changed from: protected */
    public OsloGestureConfig getGestureConfig() {
        return new OsloGestureConfig(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SNOOZE_ALARM, CONFIG_DETECTION_RADIUS, 1, 3);
    }
}
