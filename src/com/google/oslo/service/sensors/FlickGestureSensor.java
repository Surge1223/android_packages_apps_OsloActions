package com.google.oslo.service.sensors;

import android.content.Context;
import android.hardware.location.NanoAppMessage;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import com.google.oslo.service.OsloService;
import com.google.oslo.service.UIRotationMonitor;
import com.google.oslo.service.proto.nano.OsloMessages;
import com.google.oslo.service.serviceinterface.aidl.IOsloServiceGestureListener;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;
import com.google.oslo.service.serviceinterface.output.OsloFlickOutput;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

public final class FlickGestureSensor extends CHRESensor {
    private static final long GATE_DURATION_IN_MS = 480;
    private static final String TAG = "Oslo/FlickGestureSensor";
    private long mLastGateTimestamp = SystemClock.elapsedRealtime();
    private final UIRotationMonitor mRotationMonitor;
    private final StatusSensor mStatusSensor;

    public FlickGestureSensor(Context context, StatusSensor statusSensor) {
        super(context);
        this.mStatusSensor = statusSensor;
        this.mRotationMonitor = new UIRotationMonitor(context);
        updateOsloAvailability();
    }

    
    public String getTag() {
        return TAG;
    }

    
    public byte[] buildGestureDisable() {
        return new byte[0];
    }

    
    public byte[] buildGestureEnable(OsloService.OsloGestureClient client) throws IOException {
        OsloGestureConfig osloGestureConfig = new OsloGestureConfig(client.getGestureConfig());
        OsloMessages.FlickEnable flickEnable = new OsloMessages.FlickEnable();
        flickEnable.radius = osloGestureConfig.getRadius();
        flickEnable.sensitivity = osloGestureConfig.getSensitivity();
        flickEnable.granularity = osloGestureConfig.getGranularity();
        return serializeProtobuf(flickEnable);
    }

    
    public void handleContextHubMessageReceipt(NanoAppMessage nanoAppMessage) throws InvalidProtocolBufferNanoException {
        if (nanoAppMessage.getMessageType() == 3) {
            OsloMessages.FlickOutput message = OsloMessages.FlickOutput.parseFrom(nanoAppMessage.getMessageBody());
            if (!this.mListenerStack.empty()) {
                OsloService.OsloGestureClient client = getNextSwipeListener();
                IBinder listener = null;
                if (client != null) {
                    listener = client.getListener();
                }
                if (listener != null) {
                    if (message.detected) {
                        if (!shouldGateCurrentGesture(message.direction)) {
                            setGateStartTimestamp(SystemClock.elapsedRealtime());
                            message.direction = 0;
                        } else {
                            return;
                        }
                    }
                } else if (!shouldGateCurrentGesture(message.direction) && !isSwipeGesture(message.direction)) {
                    listener = ((OsloService.OsloGestureClient) this.mListenerStack.peek()).getListener();
                } else {
                    return;
                }
                IOsloServiceGestureListener flickListener = IOsloServiceGestureListener.Stub.asInterface(listener);
                int gestureDirection = message.direction;
                if (!isSwipeGesture(message.direction)) {
                    gestureDirection = this.mRotationMonitor.adjustDirection(message.direction);
                    Log.d(TAG, "Direction adjusted from " + getDirectionString(message.direction) + " to " + getDirectionString(gestureDirection));
                }
                OsloFlickOutput flickOutput = new OsloFlickOutput(message.detected, message.likelihood, message.distance, gestureDirection);
                Bundle flickOutputBundle = flickOutput.toBundle();
                String tag = getTag();
                Log.d(tag, "Flick received: detected=" + message.detected + ", likelihood=" + message.likelihood + ", distance=" + message.distance + ", direction=" + getDirectionString(gestureDirection));
                try {
                    flickListener.onGestureDetected(flickOutputBundle);
                    this.mStatusSensor.reportGestureDetectedEvent(flickOutput);
                } catch (DeadObjectException e) {
                    Log.e(TAG, "Listener crashed or closed without unregistering", e);
                    unregisterListener(listener);
                } catch (RemoteException e2) {
                    Log.e(TAG, "Unable to send onGestureDetected; removing listener", e2);
                    unregisterListener(listener);
                }
                sendEchoEvents(flickOutputBundle);
                return;
            }
            return;
        }
        Log.e(TAG, "Unknown message type: " + nanoAppMessage.getMessageType());
    }

    private OsloService.OsloGestureClient getNextSwipeListener() {
        ArrayList<OsloService.OsloGestureClient> list = new ArrayList<>(this.mListenerStack);
        ListIterator<OsloService.OsloGestureClient> itr = list.listIterator(list.size());
        while (itr.hasPrevious()) {
            OsloService.OsloGestureClient client = itr.previous();
            if (client.getType() == 7) {
                return client;
            }
        }
        return null;
    }

    private boolean shouldGateCurrentGesture(int direction) {
        if (SystemClock.elapsedRealtime() - this.mLastGateTimestamp > GATE_DURATION_IN_MS) {
            return false;
        }
        String tag = getTag();
        Log.d(tag, "Gate flick gesture with direction: " + direction);
        return true;
    }

    private boolean isSwipeGesture(int direction) {
        return direction == 0;
    }

    private void setGateStartTimestamp(long timestamp) {
        this.mLastGateTimestamp = timestamp;
    }

    private String getDirectionString(int direction) {
        if (direction == 1) {
            return "EAST";
        }
        if (direction == 2) {
            return "NORTH_EAST";
        }
        if (direction == 3) {
            return "NORTH";
        }
        if (direction == 4) {
            return "NORTH_WEST";
        }
        if (direction == 5) {
            return "WEST";
        }
        if (direction == 6) {
            return "SOUTH_WEST";
        }
        if (direction == 7) {
            return "SOUTH";
        }
        return direction == 8 ? "SOUTH_EAST" : "UNKNOWN";
    }

    
    public int getEnableMessageType() {
        return 1;
    }

    
    public int getDisableMessageType() {
        return 2;
    }

    public void registerListener(OsloService.OsloGestureClient client) {
        super.registerListener(client);
        OsloService.OsloGestureClient swipeClient = getNextSwipeListener();
        if (swipeClient != null) {
            updateStatusReport(getGesture(), getSubscriberCount(), swipeClient);
        }
    }

    public void unregisterListener(IBinder listener) {
        super.unregisterListener(listener);
        super.unregisterEchoListener(listener);
    }

    
    public StatusSensor getStatusSensor() {
        return this.mStatusSensor;
    }

    
    public int getGesture() {
        return 1;
    }

    
    public int getEchoGesture() {
        return 2;
    }
}
