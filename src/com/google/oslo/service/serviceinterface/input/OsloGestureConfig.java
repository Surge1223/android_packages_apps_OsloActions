package com.google.oslo.service.serviceinterface.input;

import android.frameworks.stats.V1_0.VendorAtom;
import android.frameworks.stats.V1_0.VendorAtom.Value;
import android.os.Bundle;
import com.google.oslo.service.serviceinterface.OsloAtoms;
import java.util.ArrayList;

import static com.google.oslo.service.serviceinterface.OsloStrings.OSLO_GESTURE_CLIENT_REGISTER_VALUE;
import static com.google.oslo.service.serviceinterface.OsloStrings.OSLO_GESTURE_CLIENT_UNREGISTER_VALUE;

public class OsloGestureConfig {
    private static final String KEY_GRANULARITY = "granularity";
    private static final String KEY_ID = "id";
    private static final String KEY_PRIORITY = "priority";
    private static final String KEY_RADIUS = "radius";
    private static final String KEY_SENSITIVITY = "sensitivity";
    protected static final String UNKNOWN_ID = "unknown";
    private final int mGranularity;
    private final String mId;
    private final int mPriority;
    private final float mRadius;
    private final int mSensitivity;

    public OsloGestureConfig(String id, float radius, int sensitivity, int granularity, int priority) {
        this.mId = id;
        this.mRadius = radius;
        this.mSensitivity = sensitivity;
        this.mGranularity = granularity;
        this.mPriority = priority;
    }

    public OsloGestureConfig(String id, float radius, int sensitivity, int granularity) {
        this(id, radius, sensitivity, granularity, 2);
    }

    public OsloGestureConfig(float radius, int sensitivity, int granularity) {
        this(UNKNOWN_ID, radius, sensitivity, granularity, 2);
    }

    public OsloGestureConfig(Bundle config) {
        if (config != null) {
            this.mId = config.getString(KEY_ID);
            this.mRadius = config.getFloat(KEY_RADIUS);
            this.mSensitivity = config.getInt(KEY_SENSITIVITY);
            this.mGranularity = config.getInt(KEY_GRANULARITY);
            this.mPriority = config.getInt(KEY_PRIORITY);
            return;
        }
        this.mId = UNKNOWN_ID;
        this.mRadius = 0.0f;
        this.mSensitivity = 0;
        this.mGranularity = 0;
        this.mPriority = 0;
    }

    public VendorAtom toVendorAtom(int type, int maxCapacity) {
        VendorAtom vendorAtom = new VendorAtom();
        vendorAtom.atomId = OSLO_GESTURE_CLIENT_REGISTER_VALUE;
        ArrayList<Value> list = OsloAtoms.constructArrayList(maxCapacity);
        ((Value) list.get(0)).intValue(type);
        ((Value) list.get(1)).stringValue(this.mId);
        ((Value) list.get(2)).floatValue(this.mRadius);
        ((Value) list.get(3)).intValue(this.mSensitivity);
        ((Value) list.get(4)).intValue(this.mGranularity);
        vendorAtom.values = list;
        return vendorAtom;
    }

    public VendorAtom toVendorAtom(int type) {
        return toVendorAtom(type, 5);
    }

    public VendorAtom unregisterToVendorAtom(int type, long registrationDuration) {
        VendorAtom vendorAtom = new VendorAtom();
        vendorAtom.atomId = OSLO_GESTURE_CLIENT_UNREGISTER_VALUE;
        ArrayList<Value> list = OsloAtoms.constructArrayList(3);
        ((Value) list.get(0)).intValue(type);
        ((Value) list.get(1)).stringValue(this.mId);
        ((Value) list.get(2)).longValue(registrationDuration);
        vendorAtom.values = list;
        return vendorAtom;
    }

    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString(KEY_ID, this.mId);
        b.putFloat(KEY_RADIUS, this.mRadius);
        b.putInt(KEY_SENSITIVITY, this.mSensitivity);
        b.putInt(KEY_GRANULARITY, this.mGranularity);
        b.putInt(KEY_PRIORITY, this.mPriority);
        return b;
    }

    public String getId() {
        return this.mId;
    }

    public float getRadius() {
        return this.mRadius;
    }

    public int getSensitivity() {
        return this.mSensitivity;
    }

    public int getGranularity() {
        return this.mGranularity;
    }

    public int getPriority() {
        return this.mPriority;
    }
}
