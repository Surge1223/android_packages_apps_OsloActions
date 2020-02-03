package com.google.oslo.ui.glow;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;

import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.google.oslo.OsloOverlay;
import com.google.oslo.OsloSounds;
import com.google.oslo.ui.OsloFeedbackView;

public class GlowFeedbackView extends OsloFeedbackView
{
    private static final float DARK_INTENSITY_THRESHOLD = 0.5f;
    private static final boolean DEBUG;
    private static final boolean DEBUG_EVENT;
    private static final long FULLSCREEN_AUTOMINIMIZE_DELAY = 5000L;
    private static final String TAG = "GlowFeedbackView";
    private int mCurrAnim;
    private boolean mFullScreen;
    private final ShaderGlow mGlow;
    private boolean mPreviouslyReachIn;
    private final RenderHandler mRenderHandler;
    private final ShaderView mShaderView;
    private OsloSounds mSounds;
    private final View mStatusBar;

    static {
        DEBUG = OsloOverlay.DEBUG;
        DEBUG_EVENT = GlowFeedbackView.DEBUG;
    }

    public GlowFeedbackView(final Context context, final Context context2, final DarkIconDispatcher darkIconDispatcher, final StatusBarStateController statusBarStateController, final boolean b, final OsloOverlay.Minimizer minimizer, final OsloSounds mSounds, final View mStatusBar) {
        super(context, context2, darkIconDispatcher, statusBarStateController, b, minimizer);
        this.mCurrAnim = 0;
        this.mShaderView = new ShaderView(context2, context);
        this.mGlow = this.mShaderView.getGlow();
        this.mSounds = mSounds;
        this.mStatusBar = mStatusBar;
        this.mRenderHandler = new RenderHandler();
    }

    private void animateAfterAirGesture(final int n) {
        this.mRenderHandler.render();
        if (this.hasActiveSubscriber()) {
            if (!this.inAOD1() && (this.isWeakGesture(n) || !this.mFullScreen)) {
                this.mCurrAnim = 2;
                if (n != 3 && n != 4) {
                    if (n == 6) {
                        this.mMinimizer.addInteractionListeners();
                        this.mGlow.animateOmniswipeReengage();
                    }
                }
                else {
                    this.mMinimizer.addInteractionListeners();
                    this.mGlow.animateFlickReengage();
                }
                this.onMinimize(true, 5000L);
            }
            else {
                this.mCurrAnim = 14;
                this.mGlow.forceInactive(true);
                this.mMinimizer.removeInteractionListeners();
            }
        }
        else {
            this.mCurrAnim = 0;
            this.mGlow.setState(ShaderGlow.State.AWAY, false);
            this.mMinimizer.removeInteractionListeners();
            this.mDisengagedCallback.onAnimationEnd();
        }
    }

    private void animateDozingChange(final boolean b) {
        if (this.inAOD1()) {
            this.mCurrAnim = 14;
            this.mGlow.setActive(false, b || OsloOverlay.shouldControlScreenOff());
            this.mRenderHandler.render();
        }
        else if (this.hasActiveSubscriber()) {
            this.onEngaged();
            this.mRenderHandler.render();
        }
    }

    @Override
    public void cancelAllAnimations() {
        super.cancelAllAnimations();
        this.mGlow.cancelAllAnimations();
    }

    @Override
    public void dozeTimeTick() {
    }

    public ShaderView getView() {
        return this.mShaderView;
    }

    public boolean isAnimatingAirGesture() {
        return this.mGlow.isAnimatingOmniswipe() || this.mGlow.isAnimatingFlick();
    }

    public void onAsleepH() {
        this.mCurrAnim = 10;
        if (this.inAOD1()) {
            return;
        }
        this.mRenderHandler.render();
        this.mGlow.setAsleep(true);
    }

    public void onDisengagedH() {
        final int mCurrAnim = this.mCurrAnim;
        if (mCurrAnim != 7 && mCurrAnim != 14) {
            this.mCurrAnim = 7;
            this.mPreviouslyReachIn = false;
            this.mRenderHandler.render();
            this.mGlow.setState(ShaderGlow.State.AWAY);
            return;
        }
        this.mCurrAnim = 7;
        if (GlowFeedbackView.DEBUG_EVENT) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Not performing disengaged animation. mCurrAnim=");
            sb.append(this.mCurrAnim);
            Log.d("GlowFeedbackView", sb.toString());
        }
        this.mGlow.setState(ShaderGlow.State.AWAY, false);
    }

    @Override
    public void onStatePreChange(int oldState, int newState) {

    }

    @Override
    public void onStatePostChange() {

    }

    @Override
    public void onStateChanged(int newState) {

    }

    @Override
    public void onDozingChanged(final boolean b) {
        if (GlowFeedbackView.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("onDozingChanged isDozing=");
            sb.append(b);
            sb.append(" wasDozing=");
            sb.append(this.mDozing);
            sb.append(" mPulsing=");
            sb.append(this.mPulsing);
            sb.append(" hasActiveSubscriber=");
            sb.append(this.hasActiveSubscriber());
            Log.d("GlowFeedbackView", sb.toString());
        }
        super.onDozingChanged(b);
        this.animateDozingChange(false);
    }

    @Override
    public void onDozeAmountChanged(float linear, float eased) {

    }

    public void onEngagedH() {
        this.onMinimize(true, 5000L);
        if (this.mCurrAnim == 2) {
            if (GlowFeedbackView.DEBUG_EVENT) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Not performing engaged animation. mCurrAnim=");
                sb.append(this.mCurrAnim);
                Log.d("GlowFeedbackView", sb.toString());
            }
            return;
        }
        this.mCurrAnim = 2;
        this.setVisibility(View.VISIBLE);
        this.mPreviouslyReachIn = false;
        this.mRenderHandler.render();
        this.mGlow.setState(ShaderGlow.State.ENGAGED);
    }

    public void onFlickLeftH(final boolean b) {
        this.mRenderHandler.render();
        this.mPreviouslyReachIn = false;
        if (!b) {
            if (this.mCurrAnim == 11) {
                return;
            }
            this.mCurrAnim = 11;
            this.mGlow.triggerPartialFlick(ShaderGlow.FlickDirection.LEFT);
        }
        else {
            if (this.mCurrAnim == 3) {
                return;
            }
            this.mCurrAnim = 3;
            this.mSounds.playFlickLeftSound();
            this.mGlow.triggerFlick(ShaderGlow.FlickDirection.LEFT);
        }
    }

    public void onFlickRightH(final boolean b) {
        this.mRenderHandler.render();
        this.mPreviouslyReachIn = false;
        if (!b) {
            if (this.mCurrAnim == 12) {
                return;
            }
            this.mCurrAnim = 12;
            this.mGlow.triggerPartialFlick(ShaderGlow.FlickDirection.RIGHT);
        }
        else {
            if (this.mCurrAnim == 4) {
                return;
            }
            this.mCurrAnim = 4;
            this.mSounds.playFlickRightSound();
            this.mGlow.triggerFlick(ShaderGlow.FlickDirection.RIGHT);
        }
    }

    public void onMinimizeH(final boolean b) {
        int mCurrAnim;
        if (b) {
            mCurrAnim = 14;
        }
        else {
            mCurrAnim = 15;
        }
        this.mCurrAnim = mCurrAnim;
        this.mRenderHandler.render();
        if (this.hasActiveSubscriber() && !b && this.mIsEnabled && !this.inAOD1()) {
            this.setVisibility(View.VISIBLE);
            this.mGlow.setActive(true);
        }
        else {
            this.mGlow.setActive(false);
        }
    }

    public void onOmniswipeH(final boolean b) {
        this.mRenderHandler.render();
        this.mPreviouslyReachIn = false;
        if (!b) {
            this.mCurrAnim = 13;
            this.mGlow.triggerPartialOmniswipe();
        }
        else {
            this.mCurrAnim = 6;
            this.mSounds.playOmniswipeSound();
            this.mGlow.triggerOmniswipe();
        }
    }

    @Override
    public void onPulsingChanged(final boolean b) {
        if (GlowFeedbackView.DEBUG) {
            final StringBuilder sb = new StringBuilder();
            sb.append("onPulsingChanged  isPulsing=");
            sb.append(b);
            sb.append(" wasPulsing=");
            sb.append(this.mPulsing);
            sb.append(" mDozing=");
            sb.append(this.mDozing);
            sb.append(" hasActiveSubscriber=");
            sb.append(this.hasActiveSubscriber());
            Log.d("GlowFeedbackView", sb.toString());
        }
        super.onPulsingChanged(b);
        this.animateDozingChange(true);
    }

    public void onReachH(final boolean b) {
        this.onMinimize(true, 5000L);
        this.mCurrAnim = 5;
        this.mRenderHandler.render();
        if (b) {
            this.mGlow.setState(ShaderGlow.State.ACTIVE);
            this.mPreviouslyReachIn = b;
        }
        else if (!b && this.hasActiveSubscriber() && this.mPreviouslyReachIn) {
            this.mGlow.setState(ShaderGlow.State.ENGAGED);
            this.mPreviouslyReachIn = b;
        }
        else if (!this.hasActiveSubscriber()) {
            this.mGlow.setState(ShaderGlow.State.AWAY);
            this.mPreviouslyReachIn = false;
        }
    }

    @Override
    public void onSystemUiVisibilityChanged(final int n) {
        super.onSystemUiVisibilityChanged(n);
        this.mFullScreen = this.inFullScreen();
    }

    @Override
    public void reset() {
        this.mRenderHandler.render();
        this.mGlow.setState(ShaderGlow.State.AWAY);
    }

    @Override
    public void setStrokeWidth(final float n) {
    }

    public void setVisibility(final int n) {
        super.setVisibility(n);
        this.mShaderView.setVisibility(n);
    }

    @Override
    public void updateColor(final int n, final float n2, final String s) {
        this.mRenderHandler.render();
        this.mGlow.setDarkMode(n2 > 0.5f);
    }

    @Override
    public void updateConfiguration() {
        this.mRenderHandler.render();
        this.invalidate();
    }

    private final class RenderHandler extends Handler implements Choreographer.FrameCallback
    {
        private static final int RENDER = 1;
        private static final int RENDER_FINAL_FRAME = 2;

        public RenderHandler() {
            super(Looper.getMainLooper());
        }

        public void doFrame(final long n) {
            GlowFeedbackView.this.mShaderView.requestRender();
            if (GlowFeedbackView.this.mGlow.isAnimating()) {
                Choreographer.getInstance().postFrameCallback((Choreographer.FrameCallback)this);
            }
            else {
                GlowFeedbackView.this.mRenderHandler.obtainMessage(2).sendToTarget();
                final int access$300 = GlowFeedbackView.this.mCurrAnim;
                GlowFeedbackView.this.mCurrAnim = 0;
                if (isAirGesture(access$300))
                    GlowFeedbackView.this.animateAfterAirGesture( access$300 );
                else if ((access$300 == 7 || access$300 == 14) && GlowFeedbackView.this.mDisengagedCallback != null) {
                    if (access$300 == 7) {
                        GlowFeedbackView.this.mGlow.setState(ShaderGlow.State.AWAY, false);
                    }
                    GlowFeedbackView.this.mDisengagedCallback.onAnimationEnd();
                }
            }
        }

        public void handleMessage(final Message message) {
            if (GlowFeedbackView.this.getVisibility() != View.VISIBLE) {
                GlowFeedbackView.this.mDisengagedCallback.onAnimationEnd();
                return;
            }
            final int what = message.what;
            if (what != 1) {
                if (what == 2) {
                    GlowFeedbackView.this.mShaderView.requestRender();
                }
            }
            else {
                Choreographer.getInstance().postFrameCallback((Choreographer.FrameCallback)this);
            }
        }

        public void render() {
            GlowFeedbackView.this.mRenderHandler.obtainMessage(1).sendToTarget();
        }
    }
}

