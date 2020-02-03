package com.google.oslo.service.serviceinterface.output;

import android.frameworks.stats.V1_0.VendorAtom;
import android.os.Bundle;

import static com.google.oslo.service.serviceinterface.OsloStrings.OSLO_PRESENCE_OUTPUT_VALUE;

public class OsloPresenceOutput extends OsloGestureOutput {
    private static final String KEY_ANGLE = "angle";
    private static final String KEY_AXIAL_VELOCITY = "axialVelocity";
    private final float mAngle;
    private final float mAxialVelocity;

    public OsloPresenceOutput(boolean detected, float likelihood, float distance, float axialVelocity, float angle) {
        super(detected, likelihood, distance);
        this.mAxialVelocity = axialVelocity;
        this.mAngle = angle;
    }

    public OsloPresenceOutput(Bundle output) {
        super(output);
        this.mAxialVelocity = output.getFloat(KEY_AXIAL_VELOCITY);
        this.mAngle = output.getFloat(KEY_ANGLE);
    }

    public Bundle toBundle() {
        Bundle b = super.toBundle();
        b.putFloat(KEY_AXIAL_VELOCITY, this.mAxialVelocity);
        b.putFloat(KEY_ANGLE, this.mAngle);
        return b;
    }

    public VendorAtom toVendorAtom() {
        return toVendorAtom(5);
    }

    public VendorAtom toVendorAtom(int maxCapacity) {
        VendorAtom vendorAtom = super.toVendorAtom(maxCapacity);
        vendorAtom.atomId = OSLO_PRESENCE_OUTPUT_VALUE;
        vendorAtom.values.get(3).floatValue(this.mAxialVelocity);
        vendorAtom.values.get(4).floatValue(this.mAngle);
        return vendorAtom;
    }

    public String toString() {
        return "Oslo presence output: " + super.toString() + ", mAxialVelocity = " + this.mAxialVelocity + ", mAngle = " + this.mAngle;
    }
}
