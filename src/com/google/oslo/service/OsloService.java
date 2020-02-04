package com.google.oslo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.frameworks.stats.V1_0.VendorAtom;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.google.oslo.service.serviceinterface.aidl.IOsloService.Stub;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;
import com.google.oslo.service.serviceinterface.input.OsloPresenceConfig;

public class OsloService extends Service {
    private static int REGISTER_LISTENER = 0;
    private static String RESTRICTED_ASSIST_GESTURE_PROVIDER = "com.google.restricted_assist_gesture.permission.RESTRICTED_ASSIST_GESTURE_PROVIDER";
    private static String TAG = "OsloActions/OsloService";
    private static int UNREGISTER_LISTENER = 1;
    private Stub mBinder = new Stub() {
        public void registerListener(IBinder token, IBinder listener, int type, Bundle config) {
            OsloService.this.checkPermission();
            OsloGestureClient osloGestureClient = new OsloGestureClient(token, listener, type, config);
            Message.obtain(OsloService.this.mHandler, 0, osloGestureClient).sendToTarget();
        }

        public void unregisterListener(IBinder token, IBinder listener) {
            OsloService.this.checkPermission();
            Message.obtain(OsloService.this.mHandler, 1, listener).sendToTarget();
        }
    };
    private Context mContext;
   
    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                OsloService.this.mOsloSensors.registerListener((OsloGestureClient) msg.obj);
            } else if (msg.what == 1) {
                OsloService.this.mOsloSensors.unregisterListener((IBinder) msg.obj);
            }
        }
    };
    private OsloBuiltInActions mOsloBuiltInActions;
    private OsloGestureTrigger mOsloGestureTrigger;
   
    public OsloSensors mOsloSensors;

    public class OsloGestureClient implements DeathRecipient {
        private Bundle mConfig;
        private IBinder mListener;
        private IBinder mToken;
        private int mType;

        public OsloGestureClient(IBinder token, IBinder listener, int type, Bundle config) {
            mToken = token;
            mListener = listener;
            mType = type;
            mConfig = config;
            linkToDeath();
        }

        public IBinder getListener() {
            return mListener;
        }

        public int getType() {
            return mType;
        }

        public Bundle getGestureConfig() {
            return mConfig;
        }

        public VendorAtom toVendorAtom() {
            if (mType == 3) {
                return new OsloPresenceConfig(mConfig).toVendorAtom(mType);
            }
            return new OsloGestureConfig(mConfig).toVendorAtom(mType);
        }

        public VendorAtom unregisterToVendorAtom(long registrationDuration) {
            return new OsloGestureConfig(mConfig).unregisterToVendorAtom(mType, registrationDuration);
        }

        private void linkToDeath() {
            IBinder iBinder = mToken;
            if (iBinder != null) {
                try {
                    iBinder.linkToDeath(this, 0);
                } catch (RemoteException e) {
                    Log.e(OsloService.TAG, "Unable to linkToDeath", e);
                }
            }
        }

        public void unlinkToDeath() {
            IBinder iBinder = mToken;
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
            }
        }

        public void binderDied() {
            Log.w(OsloService.TAG, "OsloService client binder died");
            Message.obtain(OsloService.this.mHandler, 1, mListener).sendToTarget();
        }
    }

    public void onCreate() {
        mContext = getBaseContext();
        mOsloSensors = new OsloSensors(this);
        mOsloBuiltInActions = new OsloBuiltInActions(this);
        mOsloGestureTrigger = new OsloGestureTrigger(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

   
    public void checkPermission() {
        enforceCallingOrSelfPermission("com.google.restricted_assist_gesture.permission.RESTRICTED_ASSIST_GESTURE_PROVIDER", "Must have com.google.restricted_assist_gesture.permission.RESTRICTED_ASSIST_GESTURE_PROVIDER permission");
    }
}
