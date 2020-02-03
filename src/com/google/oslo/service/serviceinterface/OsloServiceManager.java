package com.google.oslo.service.serviceinterface;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.oslo.service.serviceinterface.aidl.IOsloService;
import com.google.oslo.service.serviceinterface.aidl.IOsloServiceGestureListener;
import com.google.oslo.service.serviceinterface.aidl.IOsloServiceStatusListener;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;
import java.util.ArrayList;
import java.util.List;

public class OsloServiceManager {
    public static final int FLICK = 1;
    public static final int FLICK_ECHO = 2;
    public static final int MAX_SIZE = 9;
    public static final int PRESENCE = 3;
    public static final int REACH = 4;
    public static final int REACH_ECHO = 5;
    public static final int STATUS = 6;
    public static final int SWIPE = 7;
    public static final int SWIPE_ECHO = 8;
    private static final String SYSUI_CLASS = "com.google.oslo.service.OsloService";
    private static final String SYSUI_PACKAGE = "com.google.oslo";
    private static final String TAG = "Oslo/OsloServiceManager";
    public static final int UNKNOWN = 0;
    private boolean mBoundToService;
    public Runnable mCallback;
    private final Context mContext;
    
    public final List<ListenerRegistrationData> mListenerRegistrationData = new ArrayList();
    
    public IOsloService mOsloService;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            IOsloService unused = OsloServiceManager.this.mOsloService = IOsloService.Stub.asInterface(service);
            for (int i = 0; i < OsloServiceManager.this.mListenerRegistrationData.size(); i++) {
                OsloServiceManager osloServiceManager = OsloServiceManager.this;
                osloServiceManager.registerListener( osloServiceManager.mListenerRegistrationData.get(i).mListener, OsloServiceManager.this.mListenerRegistrationData.get(i).mType, OsloServiceManager.this.mListenerRegistrationData.get(i).mGestureConfig);
            }
            OsloServiceManager.this.mListenerRegistrationData.clear();
        }

        public void onServiceDisconnected(ComponentName className) {
            IOsloService unused = OsloServiceManager.this.mOsloService = null;
            OsloServiceManager.this.bindToService();
            if (OsloServiceManager.this.mCallback != null) {
                OsloServiceManager.this.mCallback.run();
            }
        }
    };
    private final IBinder mToken = new Binder();

    public static abstract class GestureListener extends IOsloServiceGestureListener.Stub {
    }

    private static final class ListenerRegistrationData {
        public OsloGestureConfig mGestureConfig;
        public IBinder mListener;
        public int mType;

        ListenerRegistrationData(IBinder listener, int type, OsloGestureConfig config) {
            this.mListener = listener;
            this.mType = type;
            this.mGestureConfig = config;
        }
    }

    public static abstract class StatusListener extends IOsloServiceStatusListener.Stub {
    }

    public OsloServiceManager(Context context) {
        this.mContext = context;
        this.mCallback = null;
        bindToService();
    }

    public OsloServiceManager(Context context, Runnable callback) {
        this.mContext = context;
        this.mCallback = callback;
        bindToService();
    }

    
    public void bindToService() {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(SYSUI_PACKAGE, SYSUI_CLASS));
            this.mContext.bindService(intent, this.mServiceConnection, Context.BIND_AUTO_CREATE);
            this.mBoundToService = true;
        } catch (SecurityException e) {
            Log.e(TAG, "Unable to bind to OsloService", e);
        }
    }

    public synchronized void registerListener(IBinder listener, int type, OsloGestureConfig config) {
        Bundle configBundle;
        if (this.mOsloService != null) {
            if (config == null) {
                configBundle = null;
            } else {
                configBundle = config.toBundle();
            }
            try {
                this.mOsloService.registerListener(this.mToken, listener, type, configBundle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            this.mListenerRegistrationData.add(new ListenerRegistrationData(listener, type, config));
        }
    }

    public void unregisterListener(IBinder listener) {
        IOsloService iOsloService = this.mOsloService;
        if (iOsloService != null) {
            try {
                iOsloService.unregisterListener(this.mToken, listener);
            } catch (RemoteException e) {
                Log.e(TAG, "unregisterListener() failed", e);
            }
        }
        this.mListenerRegistrationData.clear();
    }

    public void unbindFromService() {
        if (this.mBoundToService) {
            this.mContext.unbindService(this.mServiceConnection);
            this.mBoundToService = false;
        }
    }
}
