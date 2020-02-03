package com.google.oslo.service.actions;

import android.content.Context;
import android.os.Bundle;
import com.google.oslo.service.serviceinterface.OsloServiceManager;
import com.google.oslo.service.serviceinterface.OsloStrings;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;
import com.google.oslo.service.serviceinterface.output.OsloSwipeOutput;

public class SilenceRinger extends PhoneCallAction {
    private static final float CONFIG_DETECTION_RADIUS = 1.0f;
    private static final int CONFIG_GRANULARITY = 3;
    private static final int CONFIG_SENSITIVITY = 1;
    private static final String TAG = "Oslo/SilenceRinger";

    public SilenceRinger(Context context, OsloServiceManager osloServiceManager) {
        super(context, osloServiceManager);
    }

    /* access modifiers changed from: protected */
    public void onTrigger(Bundle gestureOutput) {
        if (new OsloSwipeOutput(gestureOutput).getDetected()) {
            silenceRinger();
            unregisterListener();
        }
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
    public OsloGestureConfig getGestureConfig() {
        return new OsloGestureConfig(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SILENCE_RINGER, CONFIG_DETECTION_RADIUS, 1, 3);
    }
}
