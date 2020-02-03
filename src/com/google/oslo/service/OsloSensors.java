package com.google.oslo.service;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import com.google.oslo.service.OsloService;
import com.google.oslo.service.sensors.FlickGestureSensor;
import com.google.oslo.service.sensors.PresenceGestureSensor;
import com.google.oslo.service.sensors.ReachGestureSensor;
import com.google.oslo.service.sensors.StatusSensor;
import com.google.oslo.service.sensors.SwipeGestureSensor;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class OsloSensors {
    private static final String TAG = "Oslo/OsloSensors";
    private final Context mContext;
    private final FlickGestureSensor mFlickGestureSensor;
    private final PresenceGestureSensor mPresenceGestureSensor;
    private final ReachGestureSensor mReachGestureSensor;
    private final StatusSensor mStatusSensor;
    private final SwipeGestureSensor mSwipeGestureSensor;

    public OsloSensors(Context context) {
        this.mContext = context;
        this.mStatusSensor = new StatusSensor(context);
        this.mFlickGestureSensor = new FlickGestureSensor(context, this.mStatusSensor);
        this.mPresenceGestureSensor = new PresenceGestureSensor(context, this.mStatusSensor);
        this.mReachGestureSensor = new ReachGestureSensor(context, this.mStatusSensor);
        this.mSwipeGestureSensor = new SwipeGestureSensor(context, this.mStatusSensor);
    }

    public void registerListener(OsloService.OsloGestureClient client) {
        switch (client.getType()) {
            case 1:
                this.mFlickGestureSensor.registerListener(client);
                return;
            case 2:
                this.mFlickGestureSensor.registerEchoListener(client);
                return;
            case 3:
                this.mPresenceGestureSensor.registerListener(client);
                return;
            case 4:
                this.mReachGestureSensor.registerListener(client);
                return;
            case 5:
                this.mReachGestureSensor.registerEchoListener(client);
                return;
            case 6:
                this.mStatusSensor.registerListener(client);
                return;
            case 7:
                this.mFlickGestureSensor.registerListener(client);
                return;
            case 8:
                this.mFlickGestureSensor.registerEchoListener(client);
                return;
            default:
                Log.e(TAG, "Invalid gesture type");
                return;
        }
    }

    public void unregisterListener(IBinder listener) {
        this.mFlickGestureSensor.unregisterListener(listener);
        this.mPresenceGestureSensor.unregisterListener(listener);
        this.mReachGestureSensor.unregisterListener(listener);
        this.mStatusSensor.unregisterListener(listener);
        this.mSwipeGestureSensor.unregisterListener(listener);
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mFlickGestureSensor.dump(fd, pw, args);
        this.mPresenceGestureSensor.dump(fd, pw, args);
        this.mReachGestureSensor.dump(fd, pw, args);
        this.mStatusSensor.dump(fd, pw, args);
        this.mSwipeGestureSensor.dump(fd, pw, args);
    }
}
