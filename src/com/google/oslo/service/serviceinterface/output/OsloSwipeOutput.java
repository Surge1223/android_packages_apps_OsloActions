package com.google.oslo.service.serviceinterface.output;

import android.frameworks.stats.V1_0.VendorAtom;
import android.os.Bundle;

import static com.google.oslo.service.serviceinterface.OsloStrings.OSLO_SWIPE_OUTPUT_VALUE;

public class OsloSwipeOutput extends OsloGestureOutput {
    private static final String KEY_AXIAL_VELOCITY = "axialVelocity";
    private static final String KEY_DIRECTION = "direction";
    private final float mAxialVelocity;
    private final int mDirection;

    public OsloSwipeOutput(boolean detected, float likelihood, float distance, float axialVelocity, int direction) {
        super(detected, likelihood, distance);
        this.mAxialVelocity = axialVelocity;
        this.mDirection = direction;
    }

    public OsloSwipeOutput(Bundle output) {
        super(output);
        this.mAxialVelocity = output.getFloat(KEY_AXIAL_VELOCITY);
        this.mDirection = output.getInt(KEY_DIRECTION);
    }

    public Bundle toBundle() {
        Bundle b = super.toBundle();
        b.putFloat(KEY_AXIAL_VELOCITY, this.mAxialVelocity);
        b.putInt(KEY_DIRECTION, this.mDirection);
        return b;
    }

    public VendorAtom toVendorAtom() {
        return toVendorAtom(5);
    }

    public VendorAtom toVendorAtom(int maxCapacity) {
        VendorAtom vendorAtom = super.toVendorAtom(maxCapacity);
        vendorAtom.atomId = OSLO_SWIPE_OUTPUT_VALUE;
        vendorAtom.values.get(3).floatValue(this.mAxialVelocity);
        vendorAtom.values.get(4).intValue(this.mDirection);
        return vendorAtom;
    }

    public String toString() {
        return "Oslo swipe output: " + super.toString() + ", mAxialVelocity = " + this.mAxialVelocity + ", mDirection = " + this.mDirection;
    }
}
