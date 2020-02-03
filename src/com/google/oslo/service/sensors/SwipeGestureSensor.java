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
import com.google.oslo.service.serviceinterface.output.OsloSwipeOutput;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import java.io.IOException;

public final class SwipeGestureSensor extends CHRESensor {
    private static final String TAG = "Oslo/SwipeGestureSensor";
    private final StatusSensor mStatusSensor;

    public SwipeGestureSensor(Context context, StatusSensor statusSensor) {
        super(context);
        this.mStatusSensor = statusSensor;
        updateOsloAvailability();
    }

    /* access modifiers changed from: protected */
    public String getTag() {
        return TAG;
    }

    /* access modifiers changed from: protected */
    public byte[] buildGestureDisable() {
        return new byte[0];
    }

    /* access modifiers changed from: protected */
    public byte[] buildGestureEnable(OsloService.OsloGestureClient client) throws IOException {
        OsloGestureConfig osloGestureConfig = new OsloGestureConfig(client.getGestureConfig());
        OsloMessages.SwipeEnable swipeEnable = new OsloMessages.SwipeEnable();
        swipeEnable.radius = osloGestureConfig.getRadius();
        swipeEnable.sensitivity = osloGestureConfig.getSensitivity();
        swipeEnable.granularity = osloGestureConfig.getGranularity();
        return serializeProtobuf(swipeEnable);
    }

    /* access modifiers changed from: protected */
    public void handleContextHubMessageReceipt(NanoAppMessage nanoAppMessage) throws InvalidProtocolBufferNanoException {
        if (nanoAppMessage.getMessageType() == 15) {
            OsloMessages.SwipeOutput message = OsloMessages.SwipeOutput.parseFrom(nanoAppMessage.getMessageBody());
            if (!this.mListenerStack.empty()) {
                IBinder listener = ((OsloService.OsloGestureClient) this.mListenerStack.peek()).getListener();
                IOsloServiceGestureListener swipeListener = IOsloServiceGestureListener.Stub.asInterface(listener);
                OsloSwipeOutput swipeOutput = new OsloSwipeOutput(message.detected, message.likelihood, message.distance, message.axialVelocity, message.direction);
                Bundle swipeOutputBundle = swipeOutput.toBundle();
                String tag = getTag();
                Log.d(tag, "Swipe received: detected=" + message.detected + ", likelihood=" + message.likelihood + ", distance=" + message.distance + ", axialVelocity=" + message.axialVelocity + ", direction=" + message.direction);
                try {
                    swipeListener.onGestureDetected(swipeOutputBundle);
                    this.mStatusSensor.reportGestureDetectedEvent(swipeOutput);
                } catch (DeadObjectException e) {
                    Log.e(TAG, "Listener crashed or closed without unregistering", e);
                    unregisterListener(listener);
                } catch (RemoteException e2) {
                    Log.e(TAG, "Unable to send onGestureDetected; removing listener", e2);
                    unregisterListener(listener);
                }
                sendEchoEvents(swipeOutputBundle);
                return;
            }
            return;
        }
        Log.e(TAG, "Unknown message type: " + nanoAppMessage.getMessageType());
    }

    /* access modifiers changed from: protected */
    public int getDisableMessageType() {
        return 14;
    }

    /* access modifiers changed from: protected */
    public int getEnableMessageType() {
        return 13;
    }

    public void unregisterListener(IBinder listener) {
        super.unregisterListener(listener);
        super.unregisterEchoListener(listener);
    }

    /* access modifiers changed from: protected */
    public StatusSensor getStatusSensor() {
        return this.mStatusSensor;
    }

    /* access modifiers changed from: protected */
    public int getGesture() {
        return 7;
    }

    /* access modifiers changed from: protected */
    public int getEchoGesture() {
        return 8;
    }
}
