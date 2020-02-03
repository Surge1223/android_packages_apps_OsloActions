package com.google.oslo.service.serviceinterface.output;

import android.frameworks.stats.V1_0.VendorAtom;
import android.os.Bundle;
import com.google.oslo.service.serviceinterface.OsloAtoms;
import java.util.ArrayList;

public class OsloGestureOutput {
    private static final String KEY_DETECTED = "detected";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_LIKELIHOOD = "likelihood";
    private final boolean mDetected;
    private final float mDistance;
    private final float mLikelihood;

    public OsloGestureOutput(boolean detected, float likelihood, float distance) {
        this.mDetected = detected;
        this.mLikelihood = likelihood;
        this.mDistance = distance;
    }

    protected OsloGestureOutput(Bundle output) {
        this.mDetected = output.getBoolean(KEY_DETECTED);
        this.mLikelihood = output.getFloat(KEY_LIKELIHOOD);
        this.mDistance = output.getFloat(KEY_DISTANCE);
    }

    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putBoolean(KEY_DETECTED, this.mDetected);
        b.putFloat(KEY_LIKELIHOOD, this.mLikelihood);
        b.putFloat(KEY_DISTANCE, this.mDistance);
        return b;
    }

    public VendorAtom toVendorAtom(int maxCapacity) {
        VendorAtom vendorAtom = new VendorAtom();
        ArrayList<VendorAtom.Value> list = OsloAtoms.constructArrayList(maxCapacity);
        list.get(0).intValue((this.mDetected ? 1 : 0));
        list.get(1).floatValue(this.mLikelihood);
        list.get(2).floatValue(this.mDistance);
        vendorAtom.values = list;
        return vendorAtom;
    }

    public String toString() {
        return "mDetected = " + this.mDetected + ", mLikelihood = " + this.mLikelihood + ", mDistance = " + this.mDistance;
    }

    public boolean getDetected() {
        return this.mDetected;
    }

    public float getLikelihood() {
        return this.mLikelihood;
    }

    public float getDistance() {
        return this.mDistance;
    }

    private static ArrayList<VendorAtom.Value> constructArrayList(int maxCapacity) {
        ArrayList<VendorAtom.Value> list = new ArrayList<>(maxCapacity);
        for (int i = 0; i < maxCapacity; i++) {
            list.add(new VendorAtom.Value());
        }
        return list;
    }
}
