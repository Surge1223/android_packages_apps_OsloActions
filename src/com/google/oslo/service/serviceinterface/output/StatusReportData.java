package com.google.oslo.service.serviceinterface.output;

import android.os.Parcel;
import android.os.Parcelable;

public class StatusReportData implements Parcelable {
    public static final Parcelable.Creator<StatusReportData> CREATOR = new Parcelable.Creator<StatusReportData>() {
        public StatusReportData createFromParcel(Parcel in) {
            return new StatusReportData(in);
        }

        public StatusReportData[] newArray(int size) {
            return new StatusReportData[size];
        }
    };
    private String mActiveSubscriberId;
    private int mSubscriberCount;

    public StatusReportData() {
        this.mSubscriberCount = 0;
        this.mActiveSubscriberId = null;
    }

    public StatusReportData(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.mSubscriberCount = in.readInt();
        this.mActiveSubscriberId = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSubscriberCount);
        dest.writeString(this.mActiveSubscriberId);
    }

    public void setSubscriberCount(int count) {
        this.mSubscriberCount = count;
    }

    public int getSubscriberCount() {
        return this.mSubscriberCount;
    }

    public void setActiveSubscriberId(String id) {
        this.mActiveSubscriberId = id;
    }

    public String getActiveSubscriberId() {
        return this.mActiveSubscriberId;
    }
}
