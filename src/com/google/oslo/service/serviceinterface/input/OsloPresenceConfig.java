package com.google.oslo.service.serviceinterface.input;

import android.frameworks.stats.V1_0.VendorAtom;
import android.os.Bundle;

import static com.google.oslo.service.serviceinterface.OsloStrings.OSLO_PRESENCE_CLIENT_REGISTER_VALUE;

public class OsloPresenceConfig extends OsloGestureConfig {
    private static final String KEY_DEBOUNCE = "debounce";
    private final float mDebounce;
    public static final int OSLO_PRESENCE_CLIENT_REGISTER = 100007;


    public OsloPresenceConfig(String id, float radius, int sensitivity, int granularity, float debounce) {
        super(id, radius, sensitivity, granularity);
        this.mDebounce = debounce;
    }

    public OsloPresenceConfig(float radius, int sensitivity, int granularity, float debounce) {
        this("unknown", radius, sensitivity, granularity, debounce);
    }

    public OsloPresenceConfig(Bundle config) {
        super(config);
        this.mDebounce = config.getFloat(KEY_DEBOUNCE);
    }

    public VendorAtom toVendorAtom(int type, int maxCapacity) {
        VendorAtom vendorAtom = super.toVendorAtom(type, maxCapacity);
        vendorAtom.atomId = OSLO_PRESENCE_CLIENT_REGISTER_VALUE;
        vendorAtom.values.get(5).floatValue(this.mDebounce);
        return vendorAtom;
    }

    public VendorAtom toVendorAtom(int type) {
        return toVendorAtom(type, 6);
    }

    public Bundle toBundle() {
        Bundle b = super.toBundle();
        b.putFloat(KEY_DEBOUNCE, this.mDebounce);
        return b;
    }

    public float getDebounce() {
        return this.mDebounce;
    }
}
