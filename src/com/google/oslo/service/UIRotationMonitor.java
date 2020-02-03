package com.google.oslo.service;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.IRotationWatcher;
import android.view.IWindowManager;

public class UIRotationMonitor {
    private static final String TAG = "Oslo/UIRotationMonitor";
    private final IRotationWatcher.Stub mRotationWatcher = new IRotationWatcher.Stub() {
        public void onRotationChanged(int rotation) {
            int unused = UIRotationMonitor.this.mScreenRotation = rotation;
        }
    };
    /* access modifiers changed from: private */
    public int mScreenRotation;
    private final IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));

    public UIRotationMonitor(Context context) {
        try {
            this.mScreenRotation = this.mWindowManager.watchRotation(this.mRotationWatcher, context.getDisplay().getDisplayId());
        } catch (RemoteException e) {
            Log.e(TAG, "Couldn't get screen rotation or set watcher", e);
            this.mScreenRotation = 0;
        }
    }

    public int adjustDirection(int originalDirection) {
        int originalTheta;
        int deltaTheta;
        int i = originalDirection;
        if (i == 2) {
            originalTheta = 45;
        } else if (i == 3) {
            originalTheta = 90;
        } else if (i == 4) {
            originalTheta = 135;
        } else if (i == 5) {
            originalTheta = 180;
        } else if (i == 6) {
            originalTheta = 225;
        } else if (i == 7) {
            originalTheta = 270;
        } else {
            originalTheta = i == 8 ? 315 : 0;
        }
        int i2 = this.mScreenRotation;
        if (i2 == 1) {
            deltaTheta = 90;
        } else if (i2 == 2) {
            deltaTheta = 180;
        } else {
            deltaTheta = i2 == 3 ? 270 : 0;
        }
        int adjustedTheta = (originalTheta + deltaTheta) % 360;
        if (adjustedTheta == 0) {
            return 1;
        }
        if (adjustedTheta == 45) {
            return 2;
        }
        if (adjustedTheta == 90) {
            return 3;
        }
        if (adjustedTheta == 135) {
            return 4;
        }
        if (adjustedTheta == 180) {
            return 5;
        }
        if (adjustedTheta == 225) {
            return 6;
        }
        if (adjustedTheta == 270) {
            return 7;
        }
        return adjustedTheta == 315 ? 8 : 0;
    }
}
