package com.google.oslo.service.actions;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import com.google.oslo.service.serviceinterface.OsloServiceManager;
import com.google.oslo.service.serviceinterface.OsloStrings;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;
import com.google.oslo.service.serviceinterface.output.OsloReachOutput;

public class AttenuateAlarmVolume extends DeskClockAction {
    private static final int CONFIG_GRANULARITY = 1;
    private static final int CONFIG_SENSITIVITY = 3;
    private static final String TAG = "Oslo/AttenuateAlarmVolume";
    private final AudioManager mAudioManager = ((AudioManager) getContext().getSystemService(AudioManager.class));

    public AttenuateAlarmVolume(Context context, OsloServiceManager osloServiceManager) {
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

    /* access modifiers changed from: protected */
    public String getTag() {
        return TAG;
    }

    /* access modifiers changed from: protected */
    public int getListenerType() {
        return 4;
    }

    /* access modifiers changed from: protected */
    public void onTrigger(Bundle gestureOutput) {
        if (new OsloReachOutput(gestureOutput).getDetected()) {
            AudioUtils.attenuateVolume(this.mAudioManager);
            setAlertFiring(false);
            unregisterListener();
        }
    }

    /* access modifiers changed from: protected */
    public OsloGestureConfig getGestureConfig() {
        return new OsloGestureConfig(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_ATTENUATE_ALARM, 0.2f, 3, 1);
    }
}
