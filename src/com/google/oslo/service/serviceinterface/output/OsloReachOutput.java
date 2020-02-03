package com.google.oslo.service.serviceinterface.output;

import android.frameworks.stats.V1_0.VendorAtom;
import android.os.Bundle;

import static com.google.oslo.service.serviceinterface.OsloStrings.OSLO_REACH_OUTPUT_VALUE;

public class OsloReachOutput extends OsloGestureOutput {
    private static final String KEY_ANGLE = "angle";
    private static final String KEY_AXIAL_VELOCITY = "axialVelocity";
    private final float[] mAngle;
    private final float mAxialVelocity;

    public OsloReachOutput(boolean detected, float likelihood, float distance, float axialVelocity, float[] angle) {
        super(detected, likelihood, distance);
        this.mAxialVelocity = axialVelocity;
        this.mAngle = (float[]) angle.clone();
    }

    public OsloReachOutput(Bundle output) {
        super(output);
        this.mAxialVelocity = output.getFloat(KEY_AXIAL_VELOCITY);
        this.mAngle = output.getFloatArray(KEY_ANGLE);
    }

    public Bundle toBundle() {
        Bundle b = super.toBundle();
        b.putFloat(KEY_AXIAL_VELOCITY, this.mAxialVelocity);
        b.putFloatArray(KEY_ANGLE, this.mAngle);
        return b;
    }

    public VendorAtom toVendorAtom() {
        return toVendorAtom(6);
    }

    public VendorAtom toVendorAtom(int maxCapacity) {
        VendorAtom vendorAtom = super.toVendorAtom(maxCapacity);
        vendorAtom.atomId = OSLO_REACH_OUTPUT_VALUE;
        vendorAtom.values.get(3).floatValue(this.mAxialVelocity);
        vendorAtom.values.get(4).floatValue(this.mAngle[0]);
        vendorAtom.values.get(5).floatValue(this.mAngle[1]);
        return vendorAtom;
    }

    public String toString() {
        return "Oslo reach output: " + super.toString() + ", mAxialVelocity = " + this.mAxialVelocity + ", mAngle[0] = " + this.mAngle[0] + ", mAngle[1] = " + this.mAngle[1];
    }
}
