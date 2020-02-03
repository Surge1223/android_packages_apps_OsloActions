package com.google.oslo.service.actions;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import com.google.oslo.service.UserContentObserver;
import com.google.oslo.service.serviceinterface.OsloServiceManager;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;

public abstract class Action {
    private static final int DEFAULT_SETTINGS_ENABLED_STATE = 0;
    private boolean mActionDetectorRegistered = false;
    private final Context mContext;
    protected final OsloServiceManager.GestureListener mGestureListener = new OsloServiceManager.GestureListener() {
        public void onGestureDetected(Bundle gestureOutput) {
            Action.this.onTrigger(gestureOutput);
        }
    };
    private boolean mListenerRegistered = false;
    private final OsloServiceManager mOsloServiceManager;
    protected UserContentObserver mSettingsObserver;
    
    public abstract OsloGestureConfig getGestureConfig();
    public abstract int getListenerType();
    public abstract String getSettingName();
    public abstract String getTag();
    public abstract void onTrigger(Bundle bundle);
    public abstract void registerActionDetector();
    public abstract void unregisterActionDetector();

    public Action(Context context, OsloServiceManager osloServiceManager) {
        this.mContext = context;
        this.mOsloServiceManager = osloServiceManager;
    }

    
    public void registerListener() {
        if (!this.mListenerRegistered) {
            this.mOsloServiceManager.registerListener(this.mGestureListener, getListenerType(), getGestureConfig());
            this.mListenerRegistered = true;
        }
    }

    
    public void unregisterListener() {
        if (this.mListenerRegistered) {
            this.mOsloServiceManager.unregisterListener(this.mGestureListener);
            this.mListenerRegistered = false;
        }
    }

    
    public void updateActionDetectorRegistration() {
        if (isEnabledInSettings() && !this.mActionDetectorRegistered) {
            registerActionDetector();
            this.mActionDetectorRegistered = true;
        } else if (!isEnabledInSettings() && this.mActionDetectorRegistered) {
            unregisterActionDetector();
            unregisterListener();
            this.mActionDetectorRegistered = false;
        }
    }

    private boolean isEnabledInSettings() {
        return Settings.Secure.getIntForUser(getContext().getContentResolver(), getSettingName(), 0, -2) != 0;
    }

    public Context getContext() {
        return this.mContext;
    }
}
