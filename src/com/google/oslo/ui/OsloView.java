package com.google.oslo.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.google.oslo.OsloOverlay;
import com.google.oslo.actions.R;

abstract class OsloView extends View implements DarkIconDispatcher.DarkReceiver, StatusBarStateController.StateListener {
    public static final int FULLSCREEN_USER_SWITCHER = 3;
    public static final int KEYGUARD = 1;
    public static final int SHADE = 0;
    public static final int SHADE_LOCKED = 2;
    private static final String TAG = "OsloView";
    public boolean mAttachedToWindow;
    private int mBurnInOffset;
    private boolean mCollapsed;
    public Context mContext;
    private DarkIconDispatcher mDarkIconDispatcher;
    public int mDarkIconDispatcherColor = -1;
    public float mDarkIconDispatcherIntensity = -1.0f;
    public int mDeviceHeight;
    public int mDeviceWidth;
    protected boolean mDozing;
    private ViewTreeObserver.OnComputeInternalInsetsListener mOnComputeInternalInsetsListener;
    protected boolean mPulsing;
    public int mStatusBarHeight;
    private StatusBarStateController mStatusBarStateController;
    private int mSystemUiVisibility = 0;
    public WindowManager mWindowManager;

    public abstract void cancelAllAnimations();

    public abstract void dozeTimeTick();

    public abstract void updateColor(int i, float f, String str);

    @VisibleForTesting
    public OsloView(Context context) {
        super(context);
    }

    public OsloView(Context sysuiContext, Context pluginContext, DarkIconDispatcher darkIconDispatcher, StatusBarStateController statusBarStateController, boolean collapsed) {
        super(sysuiContext);
        init(sysuiContext, pluginContext, darkIconDispatcher, statusBarStateController, collapsed);
    }

    public OsloView(Context sysuiContext, DarkIconDispatcher darkIconDispatcher, Context pluginContext, boolean collapsed) {
        super(sysuiContext);
        init(sysuiContext, pluginContext, darkIconDispatcher, null, collapsed);
    }

    private void init(Context sysuiContext, Context pluginContext, DarkIconDispatcher darkIconDispatcher, StatusBarStateController statusBarStateController, boolean collapsed) {
        mContext = sysuiContext;
        mDarkIconDispatcher = darkIconDispatcher;
        mStatusBarStateController = statusBarStateController;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mBurnInOffset = pluginContext.getResources().getDimensionPixelSize(R.dimen.default_feedback_burn_in_prevention_offset);
        mCollapsed = collapsed;
        updateScreenDimensions();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        registerDarkIconDispatcher();
        StatusBarStateController statusBarStateController = mStatusBarStateController;
        if (statusBarStateController != null) {
            statusBarStateController.addCallback(this);
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedToWindow = false;
        unregisterDarkIconDispatcher();
        StatusBarStateController statusBarStateController = mStatusBarStateController;
        if (statusBarStateController != null) {
            statusBarStateController.removeCallback(this);
        }
    }

    public void registerDarkIconDispatcher() {
        DarkIconDispatcher darkIconDispatcher = mDarkIconDispatcher;
        if (darkIconDispatcher != null) {
            darkIconDispatcher.addDarkReceiver( this );
        }
    }

    public void unregisterDarkIconDispatcher() {
        DarkIconDispatcher darkIconDispatcher = mDarkIconDispatcher;
        if (darkIconDispatcher != null) {
            darkIconDispatcher.removeDarkReceiver( this );
        }
    }

    public void onDarkChanged(Rect area, float darkIntensity, int tint) {
        mDarkIconDispatcherColor = DarkIconDispatcher.getTint(area, this, tint);
        mDarkIconDispatcherIntensity = darkIntensity;
        updateColor(mDarkIconDispatcherColor, mDarkIconDispatcherIntensity, "onDarkChanged");
    }

    public void onSystemUiVisibilityChanged(int visibility) {
        mSystemUiVisibility = visibility;
    }

    public boolean inFullScreen() {
        return OsloOverlay.isFullScreen(mSystemUiVisibility);
    }

    public void onDozingChanged(boolean isDozing) {
        mDozing = isDozing;
        dozeTimeTick();
        if (mDozing && !OsloOverlay.shouldControlScreenOff() && !mPulsing) {
            cancelAllAnimations();
        }
    }

    public void onPulsingChanged(boolean isPulsing) {
        mPulsing = isPulsing;
    }

    public void updateConfiguration() {
        updateScreenDimensions();
        invalidate();
    }

    private void updateScreenDimensions() {
        Display display = mWindowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getRealMetrics(dm);
        mDeviceWidth = dm.widthPixels;
        mDeviceHeight = dm.heightPixels;
        mStatusBarHeight = mContext.getResources().getDimensionPixelSize(mContext.getResources().getIdentifier("status_bar_height", "dimen", "android"));
    }

    public void onConfigurationChanged(Configuration newConfig) {
        updateConfiguration();
    }

    public void setCollapseDesired(boolean collapsedDesired) {
        mCollapsed = collapsedDesired;
        String reason = "{mCollapse=" + mCollapsed + "}";
        if (!mCollapsed) {
            updateColor(mDarkIconDispatcherColor, mDarkIconDispatcherIntensity, reason);
        } else {
            updateColor(-1, -1.0f, reason);
        }
    }

    public int getBurnInOffset() {
        if (!mDozing) {
            return 0;
        }
        return (int) ((System.currentTimeMillis() / 60000) % ((long) mBurnInOffset));
    }

    public boolean inAOD1() {
        return mDozing && !mPulsing;
    }
}

