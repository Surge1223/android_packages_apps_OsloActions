package com.google.oslo.service.sensors;

import android.content.Context;
import android.hardware.location.NanoAppMessage;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.oslo.service.OsloService;
import com.google.oslo.service.proto.nano.OsloMessages;
import com.google.oslo.service.serviceinterface.aidl.IOsloServiceGestureListener;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;
import com.google.oslo.service.serviceinterface.output.OsloReachOutput;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import java.io.IOException;

public final class ReachGestureSensor extends CHRESensor {
    private static final String TAG = "Oslo/ReachGestureSensor";
    private boolean mPrevDetectedState;
    private final StatusSensor mStatusSensor;

    public ReachGestureSensor(Context context, StatusSensor statusSensor) {
        super(context);
        this.mStatusSensor = statusSensor;
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
        OsloMessages.ReachEnable reachEnable = new OsloMessages.ReachEnable();
        reachEnable.radius = osloGestureConfig.getRadius();
        reachEnable.sensitivity = osloGestureConfig.getSensitivity();
        reachEnable.granularity = osloGestureConfig.getGranularity();
        return serializeProtobuf(reachEnable);
    }

    
    public void handleContextHubMessageReceipt(NanoAppMessage nanoAppMessage) throws InvalidProtocolBufferNanoException {
        if (nanoAppMessage.getMessageType() == 9) {
            OsloMessages.ReachOutput message = OsloMessages.ReachOutput.parseFrom(nanoAppMessage.getMessageBody());
            if (!this.mListenerStack.empty()) {
                IBinder listener = ((OsloService.OsloGestureClient) this.mListenerStack.peek()).getListener();
                IOsloServiceGestureListener reachListener = IOsloServiceGestureListener.Stub.asInterface(listener);
                OsloReachOutput reachOutput = new OsloReachOutput(message.detected, message.likelihood, message.distance, message.axialVelocity, message.angle);
                Bundle reachOutputBundle = reachOutput.toBundle();
                boolean detectedStateChanged = message.detected ^ this.mPrevDetectedState;
                if (detectedStateChanged) {
                    String tag = getTag();
                    Log.d(tag, "Reach received: detected=" + message.detected + ", likelihood=" + message.likelihood + ", distance=" + message.distance + ", axialVelocity=" + message.axialVelocity + ", angle[0]=" + message.angle[0] + ", angle[1]=" + message.angle[1]);
                }
                try {
                    reachListener.onGestureDetected(reachOutputBundle);
                    if (detectedStateChanged) {
                        this.mStatusSensor.reportGestureDetectedEvent(reachOutput);
                    }
                } catch (DeadObjectException e) {
                    Log.e(TAG, "Listener crashed or closed without unregistering", e);
                    unregisterListener(listener);
                } catch (RemoteException e2) {
                    Log.e(TAG, "Unable to send onGestureDetected; removing listener", e2);
                    unregisterListener(listener);
                }
                this.mPrevDetectedState = message.detected;
                sendEchoEvents(reachOutputBundle);
                return;
            }
            return;
        }
        Log.e(TAG, "Unknown message type: " + nanoAppMessage.getMessageType());
    }

    
    public int getDisableMessageType() {
        return 8;
    }

    
    public int getEnableMessageType() {
        return 7;
    }

    public void unregisterListener(IBinder listener) {
        super.unregisterListener(listener);
        super.unregisterEchoListener(listener);
    }

    
    public StatusSensor getStatusSensor() {
        return this.mStatusSensor;
    }

    
    public int getGesture() {
        return 4;
    }

    
    public int getEchoGesture() {
        return 5;
    }
}
