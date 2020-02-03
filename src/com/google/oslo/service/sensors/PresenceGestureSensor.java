package com.google.oslo.service.sensors;

import android.content.Context;
import android.hardware.location.NanoAppMessage;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.oslo.service.OsloService;
import com.google.oslo.service.proto.nano.OsloMessages;
import com.google.oslo.service.serviceinterface.aidl.IOsloServiceGestureListener;
import com.google.oslo.service.serviceinterface.input.OsloPresenceConfig;
import com.google.oslo.service.serviceinterface.output.OsloPresenceOutput;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import java.io.IOException;

public final class PresenceGestureSensor extends CHRESensor {
    private static final String TAG = "Oslo/PresenceGestureSensor";
    private final StatusSensor mStatusSensor;

    public PresenceGestureSensor(Context context, StatusSensor statusSensor) {
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
        OsloPresenceConfig osloPresenceConfig = new OsloPresenceConfig(client.getGestureConfig());
        OsloMessages.PresenceEnable presenceEnable = new OsloMessages.PresenceEnable();
        presenceEnable.radius = osloPresenceConfig.getRadius();
        presenceEnable.sensitivity = osloPresenceConfig.getSensitivity();
        presenceEnable.granularity = osloPresenceConfig.getGranularity();
        presenceEnable.debounce = osloPresenceConfig.getDebounce();
        return serializeProtobuf(presenceEnable);
    }

    /* access modifiers changed from: protected */
    public void handleContextHubMessageReceipt(NanoAppMessage nanoAppMessage) throws InvalidProtocolBufferNanoException {
        if (nanoAppMessage.getMessageType() == 6) {
            OsloMessages.PresenceOutput message = OsloMessages.PresenceOutput.parseFrom(nanoAppMessage.getMessageBody());
            OsloPresenceOutput presenceOutput = new OsloPresenceOutput(message.detected, message.likelihood, message.distance, message.axialVelocity, message.angle);
            String tag = getTag();
            Log.d(tag, "Presence received: detected=" + message.detected + ", likelihood=" + message.likelihood + ", distance=" + message.distance + ", axialVelocity=" + message.axialVelocity + ", angle=" + message.angle);
            for (int i = 0; i < this.mListenerStack.size(); i++) {
                IBinder listener = ((OsloService.OsloGestureClient) this.mListenerStack.get(i)).getListener();
                try {
                    IOsloServiceGestureListener.Stub.asInterface(listener).onGestureDetected(presenceOutput.toBundle());
                    this.mStatusSensor.reportGestureDetectedEvent(presenceOutput);
                } catch (DeadObjectException e) {
                    Log.e(TAG, "Listener crashed or closed without unregistering", e);
                    unregisterListener(listener);
                } catch (RemoteException e2) {
                    Log.e(TAG, "Unable to send onGestureDetected; removing listener", e2);
                    unregisterListener(listener);
                }
            }
            return;
        }
        Log.e(TAG, "Unknown message type: " + nanoAppMessage.getMessageType());
    }

    /* access modifiers changed from: protected */
    public int getDisableMessageType() {
        return 5;
    }

    /* access modifiers changed from: protected */
    public int getEnableMessageType() {
        return 4;
    }

    /* access modifiers changed from: protected */
    public StatusSensor getStatusSensor() {
        return this.mStatusSensor;
    }

    /* access modifiers changed from: protected */
    public int getGesture() {
        return 3;
    }
}
