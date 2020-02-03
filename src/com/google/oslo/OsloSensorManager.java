package com.google.oslo;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.SensorManagerPlugin;
import com.android.systemui.plugins.annotations.Requires;
import com.google.oslo.service.serviceinterface.OsloServiceManager;
import com.google.oslo.service.serviceinterface.OsloStrings;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;
import com.google.oslo.service.serviceinterface.input.OsloPresenceConfig;
import com.google.oslo.service.serviceinterface.output.OsloPresenceOutput;
import com.google.oslo.service.serviceinterface.output.OsloReachOutput;
import com.google.oslo.service.serviceinterface.output.OsloStatusOutput;
import com.google.oslo.service.serviceinterface.output.OsloSwipeOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

@Requires(target = SensorManagerPlugin.class, version = 1)
public class OsloSensorManager implements SensorManagerPlugin {
    private static final String TAG = "OsloSensorManager";
    public static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);
    private static boolean IS_DEBUGGABLE = true;
    public static final boolean DEBUG_EVENTS = (DEBUG || IS_DEBUGGABLE);
    private static final String OSLO_CONFIG_ID = "SystemUI";
    @VisibleForTesting
    protected final OsloServiceManager.GestureListener mGestureListenerPresence;
    @VisibleForTesting
    protected final OsloServiceManager.GestureListener mGestureListenerReach;
    @VisibleForTesting
    protected final SkipStatusListener mGestureListenerStatus;
    @VisibleForTesting
    protected final OsloServiceManager.GestureListener mGestureListenerSwipe;
    private final OsloPresenceConfig mOsloPresenceConfig;
    private final OsloGestureConfig mOsloReachConfig = new OsloGestureConfig("SystemUI", 0.2f, 1, 1);
    @VisibleForTesting
    protected OsloServiceManager mOsloServiceManager;
    private final OsloGestureConfig mOsloSkipStatusConfig;
    private final OsloGestureConfig mOsloSwipeConfig;

    public final ArrayList<Pair<SensorManagerPlugin.Sensor, SensorManagerPlugin.SensorEventListener>> mPresenceListeners;
    public final ArrayList<Pair<SensorManagerPlugin.Sensor, SensorManagerPlugin.SensorEventListener>> mReachListeners;
    public final ArrayList<Pair<SensorManagerPlugin.Sensor, SensorManagerPlugin.SensorEventListener>> mSkipStatusListeners;
    public final ArrayList<Pair<SensorManagerPlugin.Sensor, SensorManagerPlugin.SensorEventListener>> mSwipeListeners;

    private class SkipStatusListener extends OsloServiceManager.StatusListener {
        private boolean mSkipSongActive;

        private SkipStatusListener() {
        }

        public void onStatusChanged(Bundle statusOutput) {
            OsloStatusOutput osloStatusOutput = new OsloStatusOutput(statusOutput);
            if (OsloSensorManager.DEBUG) {
                Log.d(OsloSensorManager.TAG, "Send status to " + OsloSensorManager.this.mSkipStatusListeners.size() + " listeners - " + osloStatusOutput);
            }
            boolean skipSongActive = true;
            if (!osloStatusOutput.isEnabled() || !Objects.equals(osloStatusOutput.getStatusReportDataElement(1).getActiveSubscriberId(), OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SKIP_MEDIA)) {
                skipSongActive = false;
            }
            if (this.mSkipSongActive != skipSongActive) {
                this.mSkipSongActive = skipSongActive;
                Iterator it = new ArrayList(OsloSensorManager.this.mSkipStatusListeners).iterator();
                while (it.hasNext()) {
                    Pair<SensorManagerPlugin.Sensor, SensorManagerPlugin.SensorEventListener> pair = (Pair) it.next();
                    sendUpdate( pair.first, pair.second );
                }
            }
        }

        public void sendUpdate(SensorManagerPlugin.Sensor sensor, SensorManagerPlugin.SensorEventListener client) {
            float[] fArr = new float[1];
            fArr[0] = this.mSkipSongActive ? 1.0f : 0.0f;
            client.onSensorChanged(new SensorManagerPlugin.SensorEvent(sensor, 4, fArr));
        }
    }

    public OsloSensorManager() {
        OsloPresenceConfig osloPresenceConfig = new OsloPresenceConfig("SystemUI", 1.3f, 2, 1, 0.1f);
        this.mOsloPresenceConfig = osloPresenceConfig;
        this.mOsloSwipeConfig = new OsloGestureConfig("SystemUI", 0.25f, 1, 3);
        this.mOsloSkipStatusConfig = new OsloGestureConfig("SystemUI", 0.0f, 0, 0);
        this.mReachListeners = new ArrayList<>();
        this.mPresenceListeners = new ArrayList<>();
        this.mSwipeListeners = new ArrayList<>();
        this.mSkipStatusListeners = new ArrayList<>();
        this.mGestureListenerPresence = new OsloServiceManager.GestureListener() {
            public void onGestureDetected(Bundle gestureOutput) {
                OsloPresenceOutput presenceOutput = new OsloPresenceOutput(gestureOutput);
                if (OsloSensorManager.DEBUG_EVENTS) {
                    Log.d(OsloSensorManager.TAG, "Trigger presence to " + OsloSensorManager.this.mPresenceListeners.size() + " listeners - " + presenceOutput);
                }
                float[] values = new float[1];
                values[0] = presenceOutput.getDetected() ? 1.0f : 0.0f;
                Iterator it = new ArrayList(OsloSensorManager.this.mPresenceListeners).iterator();
                while (it.hasNext()) {
                    Pair<SensorManagerPlugin.Sensor, SensorManagerPlugin.SensorEventListener> pair = (Pair) it.next();
                    pair.second.onSensorChanged(new SensorManagerPlugin.SensorEvent( pair.first, 2, values));
                }
            }
        };
        this.mGestureListenerReach = new OsloServiceManager.GestureListener() {
            public void onGestureDetected(Bundle gestureOutput) {
                OsloReachOutput reachOutput = new OsloReachOutput(gestureOutput);
                if (OsloSensorManager.DEBUG_EVENTS) {
                    Log.d(OsloSensorManager.TAG, "Trigger reach to " + OsloSensorManager.this.mReachListeners.size() + " listeners - " + reachOutput);
                }
                float[] values = new float[1];
                values[0] = reachOutput.getDetected() ? 1.0f : 0.0f;
                Iterator it = new ArrayList(OsloSensorManager.this.mReachListeners).iterator();
                while (it.hasNext()) {
                    Pair<SensorManagerPlugin.Sensor, SensorManagerPlugin.SensorEventListener> pair = (Pair) it.next();
                    pair.second.onSensorChanged(new SensorManagerPlugin.SensorEvent( pair.first, 1, values));
                }
            }
        };
        this.mGestureListenerSwipe = new OsloServiceManager.GestureListener() {
            public void onGestureDetected(Bundle gestureOutput) {
                OsloSwipeOutput swipeOutput = new OsloSwipeOutput(gestureOutput);
                if (OsloSensorManager.DEBUG) {
                    Log.d(OsloSensorManager.TAG, "Trigger swipe to " + OsloSensorManager.this.mSwipeListeners.size() + " listeners - " + swipeOutput);
                }
                float[] values = new float[1];
                values[0] = swipeOutput.getDetected() ? 1.0f : 0.0f;
                Iterator it = new ArrayList(OsloSensorManager.this.mSwipeListeners).iterator();
                while (it.hasNext()) {
                    Pair<SensorManagerPlugin.Sensor, SensorManagerPlugin.SensorEventListener> pair = (Pair) it.next();
                    pair.second.onSensorChanged(new SensorManagerPlugin.SensorEvent( pair.first, 3, values));
                }
            }
        };
        this.mGestureListenerStatus = new SkipStatusListener();
    }

    public void registerListener(SensorManagerPlugin.Sensor sensor, SensorManagerPlugin.SensorEventListener listener) {
        if (DEBUG) {
            Log.d(TAG, "Registered listener: " + sensor.getType() + " -> " + listener);
        }
        if (sensor.getType() == 1) {
            this.mReachListeners.add(new Pair(sensor, listener));
            if (this.mReachListeners.size() == 1) {
                this.mOsloServiceManager.registerListener(this.mGestureListenerReach, 4, this.mOsloReachConfig);
            }
        } else if (sensor.getType() == 2) {
            this.mPresenceListeners.add(new Pair(sensor, listener));
            if (this.mPresenceListeners.size() == 1) {
                this.mOsloServiceManager.registerListener(this.mGestureListenerPresence, 3, this.mOsloPresenceConfig);
            }
        } else if (sensor.getType() == 3) {
            this.mSwipeListeners.add(new Pair(sensor, listener));
            if (this.mSwipeListeners.size() == 1) {
                this.mOsloServiceManager.registerListener(this.mGestureListenerSwipe, 7, this.mOsloSwipeConfig);
            }
        } else if (sensor.getType() == 4) {
            this.mSkipStatusListeners.add(new Pair(sensor, listener));
            if (this.mSkipStatusListeners.size() == 1) {
                this.mOsloServiceManager.registerListener(this.mGestureListenerStatus, 6, this.mOsloSkipStatusConfig);
            }
            this.mGestureListenerStatus.sendUpdate(sensor, listener);
        }
    }

    public void unregisterListener(SensorManagerPlugin.Sensor sensor, SensorManagerPlugin.SensorEventListener listener) {
        if (DEBUG) {
            Log.d(TAG, "unregistered listener: " + sensor.getType() + " -> " + listener);
        }
        if (sensor.getType() == 1) {
            this.mReachListeners.remove(new Pair(sensor, listener));
            if (this.mReachListeners.isEmpty()) {
                this.mOsloServiceManager.unregisterListener(this.mGestureListenerReach);
            }
        } else if (sensor.getType() == 2) {
            this.mPresenceListeners.remove(new Pair(sensor, listener));
            if (this.mPresenceListeners.isEmpty()) {
                this.mOsloServiceManager.unregisterListener(this.mGestureListenerPresence);
            }
        } else if (sensor.getType() == 3) {
            this.mSwipeListeners.remove(new Pair(sensor, listener));
            if (this.mSwipeListeners.isEmpty()) {
                this.mOsloServiceManager.unregisterListener(this.mGestureListenerSwipe);
            }
        } else if (sensor.getType() == 4) {
            this.mSkipStatusListeners.remove(new Pair(sensor, listener));
            if (this.mSkipStatusListeners.isEmpty()) {
                this.mOsloServiceManager.unregisterListener(this.mGestureListenerStatus);
            }
        }
    }

    @Override
    public int getVersion() {
        return 1;
    }

    public void onCreate(Context sysuiContext, Context pluginContext) {
        if (DEBUG) {
            Log.d(TAG, "Oslo sensor manager created");
        }
        this.mOsloServiceManager = new OsloServiceManager(pluginContext, new Runnable() {
            public final void run() {
                OsloSensorManager.this.lambda$onCreate$0$OsloSensorManager();
            }
        });
    }

    @VisibleForTesting
    public void lambda$onCreate$0$OsloSensorManager() {
        if (DEBUG) {
            Log.d(TAG, "Oslo sensor manager onServiceDisconnected");
        }
        registerAllListeners();
    }

    private void registerAllListeners() {
        if (DEBUG) {
            Log.d(TAG, "re-register all listeners");
        }
        if (!this.mReachListeners.isEmpty()) {
            this.mOsloServiceManager.registerListener(this.mGestureListenerReach, 4, this.mOsloReachConfig);
        }
        if (!this.mPresenceListeners.isEmpty()) {
            this.mOsloServiceManager.registerListener(this.mGestureListenerPresence, 3, this.mOsloPresenceConfig);
        }
        if (!this.mSwipeListeners.isEmpty()) {
            this.mOsloServiceManager.registerListener(this.mGestureListenerSwipe, 7, this.mOsloSwipeConfig);
        }
        if (!this.mSkipStatusListeners.isEmpty()) {
            this.mOsloServiceManager.registerListener(this.mGestureListenerStatus, 6, this.mOsloSkipStatusConfig);
            Iterator<Pair<SensorManagerPlugin.Sensor, SensorManagerPlugin.SensorEventListener>> it = this.mSkipStatusListeners.iterator();
            while (it.hasNext()) {
                Pair<SensorManagerPlugin.Sensor, SensorManagerPlugin.SensorEventListener> pair = it.next();
                this.mGestureListenerStatus.sendUpdate( pair.first, pair.second );
            }
        }
    }

    public void onDestroy() {
        if (!this.mPresenceListeners.isEmpty()) {
            this.mOsloServiceManager.unregisterListener(this.mGestureListenerPresence);
        }
        if (!this.mReachListeners.isEmpty()) {
            this.mOsloServiceManager.unregisterListener(this.mGestureListenerReach);
        }
        if (!this.mSwipeListeners.isEmpty()) {
            this.mOsloServiceManager.unregisterListener(this.mGestureListenerSwipe);
        }
        if (!this.mSkipStatusListeners.isEmpty()) {
            this.mOsloServiceManager.unregisterListener(this.mGestureListenerStatus);
        }
    }
}
