package com.google.oslo.service.serviceinterface.output;

import android.frameworks.stats.V1_0.VendorAtom;
import android.os.Bundle;

import static com.google.oslo.service.serviceinterface.OsloStrings.OSLO_FLICK_OUTPUT_VALUE;

public class OsloFlickOutput extends OsloGestureOutput {
    private static final String KEY_DIRECTION = "direction";
    private final int mDirection;

    public OsloFlickOutput(boolean detected, float likelihood, float distance, int direction) {
        super(detected, likelihood, distance);
        this.mDirection = direction;
    }

    public OsloFlickOutput(Bundle output) {
        super(output);
        this.mDirection = output.getInt(KEY_DIRECTION);
    }

    public Bundle toBundle() {
        Bundle b = super.toBundle();
        b.putInt(KEY_DIRECTION, this.mDirection);
        return b;
    }

    public VendorAtom toVendorAtom() {
        return toVendorAtom(4);
    }

    public VendorAtom toVendorAtom(int maxCapacity) {
        VendorAtom vendorAtom = super.toVendorAtom(maxCapacity);
        vendorAtom.atomId = OSLO_FLICK_OUTPUT_VALUE;
        vendorAtom.values.get(3).intValue(this.mDirection);
        return vendorAtom;
    }

    public String toString() {
        return "Oslo flick output: " + super.toString() + ", mDirection = " + this.mDirection;
    }

    public int getDirection() {
        return this.mDirection;
    }
}
