package com.google.oslo.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.google.oslo.OsloOverlay;

public abstract class OsloFeedbackView extends OsloView implements OsloOverlay.StatusListener {
    public final int ASLEEP = 10;
    final boolean DEBUG = OsloOverlay.DEBUG;
    public final int DISENGAGED = 7;
    public final int ENGAGED = 2;
    public final int ENGAGED_LEFT = 8;
    public final int ENGAGED_RIGHT = 9;
    public final int FLICKL = 3;
    public final int FLICKL_WEAK = 11;
    public final int FLICKR = 4;
    public final int FLICKR_WEAK = 12;
    private final long FULLSCREEN_FLICK_DELAY = 200;
    public final int MINIMIZE = 14;
    public final int NONE = 0;
    public final int OMNISWIPE = 6;
    public final int OMNISWIPE_WEAK = 13;
    public final int REACH = 5;
    public final int RESTING_ENGAGED = 1;
    final String TAG = "OsloViewHandler";
    public final int UNMINIMIZE = 15;
    private String mActiveFlickSubscriber = null;
    public Callback mDisengagedCallback;
    final H mHandler = new H();
    protected boolean mIsEnabled;
    /* access modifiers changed from: private */
    public boolean mIsMinimizing;
    protected final OsloOverlay.Minimizer mMinimizer;

    public interface Callback {
        void onAnimationEnd();
    }

    private final class H extends Handler {
        private static final int ASLEEP = 7;
        private static final int DISENGAGED = 6;
        private static final int ENGAGED = 1;
        private static final int FLICKL = 2;
        private static final int FLICKR = 3;
        private static final int MINIMIZE = 8;
        private static final int OMNISWIPE = 5;
        private static final int REACH = 4;
        private static final int UNMINIMIZE = 9;

        public H() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message msg) {
            boolean exitAsleepEarly = false;
            if (OsloFeedbackView.this.getVisibility() != View.VISIBLE ) {
                if (OsloFeedbackView.this.DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Don't animate.  viewVisible=");
                    if (OsloFeedbackView.this.getVisibility() == View.VISIBLE) {
                        exitAsleepEarly = true;
                    }
                    sb.append(exitAsleepEarly);
                    sb.append(" animation=");
                    sb.append(gestureToString(msg.what));
                    Log.d("OsloViewHandler", sb.toString());
                }
                OsloFeedbackView.this.mDisengagedCallback.onAnimationEnd();
                OsloFeedbackView.this.cancelAllAnimations();
                return;
            }
            boolean hasActiveSubscriber = OsloFeedbackView.this.hasActiveSubscriber();
            boolean isAnimatingGesture = OsloFeedbackView.this.isAnimatingAirGesture();
            boolean inAOD1 = OsloFeedbackView.this.inAOD1();
            switch (msg.what) {
                case 1:
                    boolean exitEngagedEarly = !hasActiveSubscriber || isAnimatingGesture || inAOD1;
                    if (OsloFeedbackView.this.DEBUG) {
                        Log.d("OsloViewHandler", "onEngagedH exitEngagedEarly=" + exitEngagedEarly + " hasActiveSubscriber=" + hasActiveSubscriber + " isAnimatingGesture=" + isAnimatingGesture + " inAOD1=" + inAOD1);
                    }
                    OsloFeedbackView.this.mHandler.removeMessages(1);
                    OsloFeedbackView.this.removeMinimizeMessages();
                    if (!exitEngagedEarly) {
                        boolean unused = OsloFeedbackView.this.mIsMinimizing = false;
                        OsloFeedbackView.this.mMinimizer.addInteractionListeners();
                        OsloFeedbackView.this.onEngagedH();
                        return;
                    }
                    return;
                case 2:
                    if (OsloFeedbackView.this.DEBUG) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("onFlickLeftH weak=");
                        sb2.append(msg.arg1 != 1);
                        Log.d("OsloViewHandler", sb2.toString());
                    }
                    OsloFeedbackView.this.removeMessagesWithLowerPrecedence();
                    OsloFeedbackView.this.mHandler.removeMessages(2);
                    boolean unused2 = OsloFeedbackView.this.mIsMinimizing = false;
                    OsloFeedbackView.this.mMinimizer.addInteractionListeners();
                    OsloFeedbackView osloFeedbackView = OsloFeedbackView.this;
                    if (msg.arg1 == 1) {
                        exitAsleepEarly = true;
                    }
                    osloFeedbackView.onFlickLeftH(exitAsleepEarly);
                    return;
                case 3:
                    if (OsloFeedbackView.this.DEBUG) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("onFlickRightH weak=");
                        sb3.append(msg.arg1 != 1);
                        Log.d("OsloViewHandler", sb3.toString());
                    }
                    OsloFeedbackView.this.removeMessagesWithLowerPrecedence();
                    OsloFeedbackView.this.mHandler.removeMessages(3);
                    boolean unused3 = OsloFeedbackView.this.mIsMinimizing = false;
                    OsloFeedbackView.this.mMinimizer.addInteractionListeners();
                    OsloFeedbackView osloFeedbackView2 = OsloFeedbackView.this;
                    if (msg.arg1 == 1) {
                        exitAsleepEarly = true;
                    }
                    osloFeedbackView2.onFlickRightH(exitAsleepEarly);
                    return;
                case 4:
                    boolean reachIn = msg.arg1 == 1;
                    if (OsloFeedbackView.this.mIsMinimizing || !hasActiveSubscriber || isAnimatingGesture || inAOD1) {
                        exitAsleepEarly = true;
                    }
                    if (OsloFeedbackView.this.DEBUG) {
                        Log.d("OsloViewHandler", "onReachH reachIn=" + reachIn + " exitReachEarly=" + exitAsleepEarly + " mIsMinimizing=" + OsloFeedbackView.this.mIsMinimizing + " hasActiveSubscriber=" + hasActiveSubscriber + " isAnimatingGesture=" + isAnimatingGesture + " inAOD1=" + inAOD1);
                    }
                    OsloFeedbackView.this.mHandler.removeMessages(4);
                    if (!exitAsleepEarly) {
                        OsloFeedbackView.this.mMinimizer.addInteractionListeners();
                        OsloFeedbackView.this.onReachH(reachIn);
                        return;
                    }
                    return;
                case 5:
                    if (OsloFeedbackView.this.DEBUG) {
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("onOmniswipeH weak=");
                        sb4.append(msg.arg1 != 1);
                        Log.d("OsloViewHandler", sb4.toString());
                    }
                    OsloFeedbackView.this.removeMessagesWithLowerPrecedence();
                    OsloFeedbackView.this.mHandler.removeMessages(5);
                    boolean unused4 = OsloFeedbackView.this.mIsMinimizing = false;
                    OsloFeedbackView.this.mMinimizer.addInteractionListeners();
                    OsloFeedbackView osloFeedbackView3 = OsloFeedbackView.this;
                    if (msg.arg1 == 1) {
                        exitAsleepEarly = true;
                    }
                    osloFeedbackView3.onOmniswipeH(exitAsleepEarly);
                    return;
                case 6:
                    boolean exitDisengagedEarly = isAnimatingGesture;
                    if (OsloFeedbackView.this.DEBUG) {
                        Log.d("OsloViewHandler", "onDisengagedH exitDisengagedEarly=" + exitDisengagedEarly);
                    }
                    OsloFeedbackView.this.mHandler.removeMessages(6);
                    if (!exitDisengagedEarly) {
                        boolean unused5 = OsloFeedbackView.this.mIsMinimizing = false;
                        OsloFeedbackView.this.mMinimizer.removeInteractionListeners();
                        OsloFeedbackView.this.onDisengagedH();
                        return;
                    }
                    return;
                case 7:
                    if (OsloFeedbackView.this.mIsMinimizing || !hasActiveSubscriber) {
                        exitAsleepEarly = true;
                    }
                    if (OsloFeedbackView.this.DEBUG) {
                        Log.d("OsloViewHandler", "onAsleepH exitAsleepEarly=" + exitAsleepEarly + " isMinimizing=" + OsloFeedbackView.this.mIsMinimizing + " hasActiveSubscriber=" + hasActiveSubscriber);
                    }
                    OsloFeedbackView.this.mHandler.removeMessages(7);
                    if (!exitAsleepEarly) {
                        OsloFeedbackView.this.mMinimizer.addInteractionListeners();
                        OsloFeedbackView.this.onAsleepH();
                        return;
                    }
                    return;
                case 8:
                    if (OsloFeedbackView.this.DEBUG) {
                        Log.d("OsloViewHandler", "Minimize");
                    }
                    boolean unused6 = OsloFeedbackView.this.mIsMinimizing = true;
                    OsloFeedbackView.this.mMinimizer.removeInteractionListeners();
                    OsloFeedbackView.this.onMinimizeH(true);
                    return;
                case 9:
                    if (OsloFeedbackView.this.DEBUG) {
                        Log.d("OsloViewHandler", "Unminimize");
                    }
                    boolean unused7 = OsloFeedbackView.this.mIsMinimizing = false;
                    OsloFeedbackView.this.mHandler.removeMessages(9);
                    OsloFeedbackView.this.mMinimizer.addInteractionListeners();
                    if (OsloFeedbackView.this.hasActiveSubscriber()) {
                        OsloFeedbackView.this.onMinimizeH(false);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        private String gestureToString(int gesture) {
            switch (gesture) {
                case 1:
                    return "ENGAGED";
                case 2:
                    return "FLICK_LEFT";
                case 3:
                    return "FLICK_RIGHT";
                case 4:
                    return "REACH";
                case 5:
                    return "OMNISWIPE";
                case 6:
                    return "DISENGAGED";
                case 7:
                    return "ASLEEP";
                case 8:
                    return "UNMINIMIZE";
                case 9:
                    return "UNMINIMIZE";
                default:
                    return "UNKNOWN";
            }
        }
    }

    /* access modifiers changed from: protected */
    public abstract boolean isAnimatingAirGesture();

    /* access modifiers changed from: protected */
    public abstract void onAsleepH();

    /* access modifiers changed from: protected */
    public abstract void onDisengagedH();

    /* access modifiers changed from: protected */
    public abstract void onEngagedH();

    /* access modifiers changed from: protected */
    public abstract void onFlickLeftH(boolean z);

    /* access modifiers changed from: protected */
    public abstract void onFlickRightH(boolean z);

    /* access modifiers changed from: protected */
    public abstract void onMinimizeH(boolean z);

    /* access modifiers changed from: protected */
    public abstract void onOmniswipeH(boolean z);

    /* access modifiers changed from: protected */
    public abstract void onReachH(boolean z);

    public abstract void reset();

    public abstract void setStrokeWidth(float f);

    public int getBurnInOffset() {
        return super.getBurnInOffset();
    }

    public boolean inFullScreen() {
        return super.inFullScreen();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        super.onDarkChanged(rect, f, i);
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onDozingChanged(boolean z) {
        super.onDozingChanged(z);
    }

    public void onPulsingChanged(boolean z) {
        super.onPulsingChanged(z);
    }

    public void onSystemUiVisibilityChanged(int i) {
        super.onSystemUiVisibilityChanged(i);
    }

    public void registerDarkIconDispatcher() {
        super.registerDarkIconDispatcher();
    }

    public void setCollapseDesired(boolean z) {
        super.setCollapseDesired(z);
    }

    public void unregisterDarkIconDispatcher() {
        super.unregisterDarkIconDispatcher();
    }

    public void updateConfiguration() {
        super.updateConfiguration();
    }

    public OsloFeedbackView(Context sysuiContext, Context pluginContext, DarkIconDispatcher darkIconDispatcher, StatusBarStateController statusBarStateController, boolean collapsed, OsloOverlay.Minimizer minimizer) {
        super(sysuiContext, pluginContext, darkIconDispatcher, statusBarStateController, collapsed);
        this.mMinimizer = minimizer;
    }

    public boolean isMinimized() {
        return this.mIsMinimizing;
    }

    public void setDisengagedCallback(Callback callback) {
        this.mDisengagedCallback = callback;
    }

    /* access modifiers changed from: protected */
    public boolean isAirGesture(int gesture) {
        return gesture == 4 || gesture == 3 || gesture == 6 || isWeakGesture(gesture);
    }

    /* access modifiers changed from: protected */
    public boolean isWeakGesture(int gesture) {
        return gesture == 12 || gesture == 11 || gesture == 13;
    }

    /* access modifiers changed from: private */
    public void removeMessagesWithLowerPrecedence() {
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(4);
        removeMinimizeMessages();
    }

    public void onEngaged() {
        H h = this.mHandler;
        h.sendMessageDelayed(h.obtainMessage(1), 50);
    }

    public void onDisengaged() {
        this.mHandler.obtainMessage(6).sendToTarget();
    }

    public void onFlickLeft(boolean detected) {
        Message msg = this.mHandler.obtainMessage(2);
//        msg.arg1 = detected;
        this.mHandler.removeMessages(2);
        this.mHandler.sendMessageDelayed(msg, inFullScreen() ? 200 : 0);
    }

    public void onFlickRight(boolean detected) {
        Message msg = this.mHandler.obtainMessage(3);
//        msg.arg1 = detected;
        this.mHandler.removeMessages(3);
        this.mHandler.sendMessageDelayed(msg, inFullScreen() ? 200 : 0);
    }

    public void onReach(boolean reachIn) {
        Message msg = this.mHandler.obtainMessage(4);
//        msg.arg1 = reachIn;
        this.mHandler.sendMessage(msg);
    }

    public void onOmniswipe(boolean detected) {
        Message msg = this.mHandler.obtainMessage(5);
//        msg.arg1 = detected;
        this.mHandler.sendMessage(msg);
    }

    public void onMinimize(boolean minimize, long delay) {
        if (minimize) {
            this.mHandler.removeMessages(8);
            H h = this.mHandler;
            h.sendMessageDelayed(h.obtainMessage(8), delay);
            return;
        }
        this.mHandler.removeMessages(9);
        H h2 = this.mHandler;
        h2.sendMessageDelayed(h2.obtainMessage(9), delay);
    }

    public void onMinimize(boolean minimize) {
        onMinimize(minimize, 0);
    }

    public void removeMinimizeMessages() {
        this.mHandler.removeMessages(8);
        this.mHandler.removeMessages(9);
    }

    public void onAsleep() {
        this.mHandler.obtainMessage(7).sendToTarget();
    }

    public void onActiveFlickSubscriberChanged(String subscriber) {
        this.mActiveFlickSubscriber = subscriber;
    }

    public void cancelAllAnimations() {
        this.mHandler.removeCallbacksAndMessages((Object) null);
    }

    public void setEnabled(boolean enabled) {
        this.mIsEnabled = enabled;
    }

    /* access modifiers changed from: protected */
    public boolean hasActiveSubscriber() {
        return this.mActiveFlickSubscriber != null;
    }
}

