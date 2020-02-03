package com.google.oslo.service;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import java.util.function.Consumer;

public class UserContentObserver extends ContentObserver {
    private static final String TAG = "Oslo/UserContentObserver";
    private final Consumer<Uri> mCallback;
    private final Context mContext;
    private final Uri mSettingsUri;

    public UserContentObserver(Context context, Uri uri, Consumer<Uri> callback) {
        this(context, uri, callback, true);
    }

    public UserContentObserver(Context context, Uri uri, Consumer<Uri> callback, boolean activate) {
        super(new Handler(context.getMainLooper()));
        this.mContext = context;
        this.mSettingsUri = uri;
        this.mCallback = callback;
        if (activate) {
            activate();
        }
    }

    public void activate() {
        updateContentObserver();
    }

    public void deactivate() {
        this.mContext.getContentResolver().unregisterContentObserver(this);
    }

    private void updateContentObserver() {
        this.mContext.getContentResolver().unregisterContentObserver(this);
        this.mContext.getContentResolver().registerContentObserver(this.mSettingsUri, false, this);
    }

    public void onChange(boolean selfChange, Uri uri) {
        this.mCallback.accept(uri);
    }
}
