package com.google.oslo.service.serviceinterface.output;

import android.frameworks.stats.V1_0.VendorAtom;
import android.os.Bundle;
import android.os.Parcelable;
import com.google.oslo.service.serviceinterface.OsloAtoms;
import java.util.ArrayList;

import static com.google.oslo.service.serviceinterface.OsloStrings.OSLO_STATUS_OUTPUT_VALUE;

public class OsloStatusOutput {
    public static final int ENABLED_BY_AIRPLANE_MODE_INDEX = 1;
    public static final int ENABLED_BY_BATTERY_SAVER_INDEX = 2;
    public static final int ENABLED_BY_COUNTRY_INDEX = 3;
    public static final int ENABLED_BY_SETTINGS_INDEX = 0;
    public static final int ENABLED_REASON_MAX = 4;
    private static final String KEY_ENABLED_REASONS = "enabledReasons";
    private static final String KEY_GATING_REASON = "gatingReason";
    private static final String KEY_IS_ENABLED = "isEnabled";
    private static final String KEY_STATUS_REPORT_DATA = "statusReportData";
    private boolean[] mEnabledReasons;
    private int mGatingReason;
    private boolean mIsEnabled;
    private StatusReportData[] mStatusReportData;

    public OsloStatusOutput() {
        this.mStatusReportData = new StatusReportData[9];
        this.mGatingReason = 0;
        for (int i = 0; i < 9; i++) {
            this.mStatusReportData[i] = new StatusReportData();
        }
        this.mEnabledReasons = new boolean[4];
    }

    public OsloStatusOutput(Bundle output) {
        this.mStatusReportData = new StatusReportData[9];
        output.setClassLoader(StatusReportData.class.getClassLoader());
        this.mGatingReason = output.getInt(KEY_GATING_REASON);
        this.mIsEnabled = output.getBoolean(KEY_IS_ENABLED);
        this.mEnabledReasons = output.getBooleanArray(KEY_ENABLED_REASONS);
        Parcelable[] parcelArray = output.getParcelableArray(KEY_STATUS_REPORT_DATA);
        System.arraycopy(parcelArray, 0, this.mStatusReportData, 0, parcelArray.length);
    }

    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putInt(KEY_GATING_REASON, this.mGatingReason);
        b.putBoolean(KEY_IS_ENABLED, this.mIsEnabled);
        b.putBooleanArray(KEY_ENABLED_REASONS, this.mEnabledReasons);
        b.putParcelableArray(KEY_STATUS_REPORT_DATA, this.mStatusReportData);
        return b;
    }

    public void setGatingReason(int gatingReason) {
        this.mGatingReason = gatingReason;
    }

    public void setEnabledReasons(boolean[] enabledReasons) {
        this.mEnabledReasons = enabledReasons;
    }

    public int getGatingReason() {
        return this.mGatingReason;
    }

    public boolean[] getEnabledReasons() {
        return this.mEnabledReasons;
    }

    public void setEnabled(boolean enabled) {
        this.mIsEnabled = enabled;
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public StatusReportData getStatusReportDataElement(int i) {
        return this.mStatusReportData[i];
    }

    public void setStatusReportDataElement(int i, StatusReportData data) {
        this.mStatusReportData[i] = data;
    }

    private String getGestureTypeString(int gestureType) {
        switch (gestureType) {
            case 1:
                return "flick";
            case 2:
                return "flick echo";
            case 3:
                return "presence";
            case 4:
                return "reach";
            case 5:
                return "reach echo";
            case 6:
                return "status";
            case 7:
                return "swipe";
            case 8:
                return "swipe echo";
            default:
                return "unknown";
        }
    }

    public VendorAtom toVendorAtom() {
        VendorAtom vendorAtom = new VendorAtom();
        vendorAtom.atomId = OSLO_STATUS_OUTPUT_VALUE;
        ArrayList<VendorAtom.Value> list = OsloAtoms.constructArrayList(1);
        list.get(0).intValue(this.mGatingReason);
        vendorAtom.values = list;
        return vendorAtom;
    }

    public String toString() {
        new String();
        String logString =  "Oslo status output: mGatingReason = " + this.mGatingReason + ", mIsEnabled = " + this.mIsEnabled + "\n";
        String logString2 = logString + "Oslo enabled by settings: " + this.mEnabledReasons[0] + ", by airplane mode: " + this.mEnabledReasons[1] + ", by battery saver: " + this.mEnabledReasons[2] + ", by country: " + this.mEnabledReasons[3] + "\n";
        for (int i = 0; i < 9; i++) {
            logString2 = logString2 + getGestureTypeString(i) + " subscribers: " + this.mStatusReportData[i].getSubscriberCount() + ", active subscriber: " + this.mStatusReportData[i].getActiveSubscriberId() + "\n";
        }
        return logString2;
    }
}
