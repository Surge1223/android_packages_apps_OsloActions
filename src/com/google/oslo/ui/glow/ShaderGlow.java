package com.google.oslo.ui.glow;

import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.content.res.Resources;
import java.nio.Buffer;
import android.opengl.GLES20;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import android.os.SystemClock;
import android.os.Looper;
import java.nio.FloatBuffer;
import android.graphics.Point;
import android.content.Context;
import java.nio.ShortBuffer;
import com.google.oslo.actions.R;
import com.google.oslo.ui.glow.attributes.LineAttributes;
import com.google.oslo.ui.glow.attributes.GlowAttributes;
import com.google.oslo.ui.glow.attributes.DeviceAttributes;
import com.google.oslo.ui.glow.animations.AnimationController;

public class ShaderGlow
{
    private static final String ATTRIBUTE_POSITION = "aPosition";
    private static final float MILLIS_TO_SECS = 0.001f;
    private static final float[] QUAD_VERTICES;
    private static final int VERTEX_BYTE_SIZE = 8;
    private static final int VERTEX_DIM = 2;
    private static final short[] VERTEX_ORDER;
    private boolean mActive;
    private AnimationController mAnimator;
    private boolean mAsleep;
    private boolean mDarkMode;
    private DeviceAttributes mDeviceAttributes;
    private GlowAttributes mGlowAttributes;
    private float mGlowPulsateDefaultAmp;
    private final GlowHandler mHandler;
    private LineAttributes mLineAttributes;
    private final ShortBuffer mOrderBuffer;
    private final Context mPluginContext;
    private final ShaderProgram mProgram;
    private Point mScreenSize;
    private long mStartTime;
    private State mState;
    private final Context mSysuiContext;
    private final FloatBuffer mVertexBuffer;

    static {
        QUAD_VERTICES = new float[] { -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f };
        VERTEX_ORDER = new short[] { 0, 1, 2, 0, 3, 1 };
    }

    public ShaderGlow(Context pluginContext, Context sysuiContext) {
        this.mPluginContext = pluginContext;
        this.mSysuiContext = sysuiContext;
        this.mHandler = new GlowHandler(Looper.getMainLooper());
        this.mStartTime = SystemClock.elapsedRealtime();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.mGlowPulsateDefaultAmp = pluginContext.getResources().getFloat( R.dimen.glow_asleep_pulsate_amp);
        }
        ByteBuffer verBB = ByteBuffer.allocateDirect(QUAD_VERTICES.length * 4);
        verBB.order(ByteOrder.nativeOrder());
        ByteBuffer orderBB = ByteBuffer.allocateDirect(VERTEX_ORDER.length * 2);
        orderBB.order(ByteOrder.nativeOrder());
        this.mVertexBuffer = verBB.asFloatBuffer();
        this.mVertexBuffer.put(QUAD_VERTICES);
        this.mVertexBuffer.position(0);
        this.mOrderBuffer = orderBB.asShortBuffer();
        this.mOrderBuffer.put(VERTEX_ORDER);
        this.mOrderBuffer.position(0);
        this.mProgram = new ShaderProgram(this.mPluginContext);
        this.mDeviceAttributes = new DeviceAttributes();
        this.mGlowAttributes = new GlowAttributes();
        this.mLineAttributes = new LineAttributes();
        this.mAnimator = new AnimationController(3, pluginContext.getResources());
    }

    public void init() {
        updateDefaultValues();
        setAnimationValues();
        mProgram.useGLProgram(R.raw.glow_vert, R.raw.glow_frag);
        enablePosition(this.mProgram.getAttributeHandle(ATTRIBUTE_POSITION));
        setUniforms();
    }

    
    public void draw() {
        long time = this.mAsleep ? SystemClock.elapsedRealtime() - this.mStartTime : 0;
        this.mDeviceAttributes.updateUniforms(this.mProgram);
        PointF glowPos = this.mAnimator.getGlowPosition();
        this.mGlowAttributes.setPosition(glowPos.x, glowPos.y);
        this.mGlowAttributes.setColors(this.mAnimator.getGlowColors());
        this.mGlowAttributes.setScale(this.mAnimator.getGlowScale());
        this.mGlowAttributes.setOpacity(this.mAnimator.getOpacity());
        this.mGlowAttributes.setPulsateAmp(this.mAsleep ? this.mGlowPulsateDefaultAmp : 0.0f);
        this.mGlowAttributes.setTime(((float) time) * MILLIS_TO_SECS);
        this.mGlowAttributes.updateUniforms(this.mProgram);
        this.mLineAttributes.setWidth(this.mAnimator.getLineWidth());
        this.mLineAttributes.setAlpha(this.mAnimator.getLineAlpha());
        this.mLineAttributes.setColor(this.mAnimator.getLineColor());
        this.mLineAttributes.setPositionX(this.mAnimator.getLinePositionX());
        this.mLineAttributes.updateUniforms(this.mProgram);
        GLES20.glDrawElements(4, VERTEX_ORDER.length, 5123, this.mOrderBuffer);
    }

    

    private void enablePosition(final int n) {
        GLES20.glEnableVertexAttribArray(n);
        GLES20.glVertexAttribPointer(n, 2, 5126, false, 8, (Buffer)this.mVertexBuffer);
    }

    private float getRoundedCornerRadius(Resources res) {
        int topResId = res.getIdentifier("rounded_corner_radius_top", "dimen", "android");
        int topAdjustmentResId = res.getIdentifier("rounded_corner_radius_top_adjustment", "dimen", "android");
        int roundedCornerResId = res.getIdentifier("rounded_corner_radius", "dimen", "android");
        int adjustmentResId = res.getIdentifier("rounded_corner_radius_adjustment", "dimen", "android");
        int cornerRadius = res.getDimensionPixelSize(topResId) - res.getDimensionPixelSize(topAdjustmentResId);
        if (cornerRadius == 0) {
            cornerRadius = res.getDimensionPixelSize(roundedCornerResId) - res.getDimensionPixelSize(adjustmentResId);
        }
        return (float) cornerRadius;
    }
    
    private void setAnimationValues() {
        this.mHandler.removeMessages(2);
        this.mHandler.sendMessage(Message.obtain((Handler)this.mHandler, 2, (Object)this));
    }

    private void setDefaultDeviceAttributes(final Resources resources, final Point point) {
        this.mDeviceAttributes.setCornerRadius(this.getRoundedCornerRadius(resources));
    }

    private void setDefaultGlowAttributes(Resources resources, Point screenSize) {
        PointF glowRadius = new PointF(((float) resources.getDimensionPixelSize( R.dimen.glow_width)) / ((float) (screenSize.x * 2)), ((float) resources.getDimensionPixelSize( R.dimen.glow_height)) / ((float) (screenSize.y * 2)));
        glowRadius.y *= ((float) screenSize.y) / ((float) screenSize.x);
        this.mGlowAttributes.setGlowRadius(glowRadius.x, glowRadius.y);
        this.mGlowAttributes.setBlurIntensity(((float) resources.getDimensionPixelSize( R.dimen.glow_blur_intensity)) / ((float) screenSize.x));
        TypedValue stop1 = new TypedValue();
        TypedValue stop2 = new TypedValue();
        TypedValue stop3 = new TypedValue();
        resources.getValue( R.dimen.glow_gradient_stop_1, stop1, false);
        resources.getValue( R.dimen.glow_gradient_stop_2, stop2, false);
        resources.getValue( R.dimen.glow_gradient_stop_3, stop3, false);
        this.mGlowAttributes.setStops(stop1.getFloat(), stop2.getFloat(), stop3.getFloat());
    }

    private void setDefaultLineAttributes(Resources resources, Point screenSize) {
        this.mLineAttributes.setThickness(((float) resources.getDimensionPixelSize( R.dimen.line_thickness)) / ((float) screenSize.x));
        this.mLineAttributes.setFadeStops(((float) resources.getDimensionPixelSize( R.dimen.line_mask_fade_start)) / ((float) screenSize.x), ((float) resources.getDimensionPixelSize( R.dimen.line_mask_fade_end)) / ((float) screenSize.x));
    }

    private void setUniforms() {
        this.mDeviceAttributes.setUniforms(this.mProgram);
        this.mGlowAttributes.setUniforms(this.mProgram);
        this.mLineAttributes.setUniforms(this.mProgram);
    }

    private boolean updateActive(final boolean mActive) {
        this.setAsleep(false);
        if (this.mActive == mActive) {
            return false;
        }
        this.mActive = mActive;
        return true;
    }

    private void updateDefaultValues() {
        final Resources resources = this.mPluginContext.getResources();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        this.mScreenSize = new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
        this.setDefaultDeviceAttributes(this.mSysuiContext.getResources(), this.mScreenSize);
        this.setDefaultGlowAttributes(resources, this.mScreenSize);
        this.setDefaultLineAttributes(resources, this.mScreenSize);
        this.mAnimator.setDefaultValues(resources, this.mScreenSize, this.mGlowAttributes);
    }

    public void animateFlickReengage() {
        this.mAnimator.animateFlickReengage(this.mState);
    }

    public void animateOmniswipeReengage() {
        this.mAnimator.animateOmniswipeReengage(this.mState);
    }

    public void cancelAllAnimations() {
        this.mAnimator.cancelAll();
    }


    public void forceInactive(final boolean b) {
        this.mActive = false;
        this.mAnimator.setOpacity(0.0f, b);
    }

    public State getState() {
        return this.mState;
    }

    public boolean isActive() {
        return this.mActive;
    }

    public boolean isAnimating() {
        return this.mAsleep || this.mAnimator.isAnimating();
    }

    public boolean isAnimatingFlick() {
        return this.mAnimator.isAnimatingFlick();
    }

    public boolean isAnimatingOmniswipe() {
        return this.mAnimator.isAnimatingOmniswipe();
    }

    public void onSizeChanged(final int n, final int n2) {
        this.mDeviceAttributes.setViewSize(n, n2);
        this.updateDefaultValues();
        this.setAnimationValues();
        this.setUniforms();
    }

    public void setActive(final boolean b) {
        this.setActive(b, true);
    }

    public void setActive(final boolean b, final boolean b2) {
        if (!this.updateActive(b)) {
            return;
        }
        final AnimationController mAnimator = this.mAnimator;
        float n;
        if (this.mActive) {
            n = 1.0f;
        }
        else {
            n = 0.0f;
        }
        mAnimator.setOpacity(n, b2);
        this.mAnimator.setScale(this.mActive, b2);
    }

    public void setAsleep(final boolean mAsleep) {
        if (this.mAsleep == mAsleep) {
            return;
        }
        this.mAsleep = mAsleep;
        if (this.mActive) {
            this.mAnimator.setAsleepMode(mAsleep, this.mDarkMode, this.mState);
        }
        if (this.mAsleep) {
            this.mActive = true;
            this.mStartTime = SystemClock.elapsedRealtime();
        }
    }

    public void setDarkMode(final boolean b) {
        this.mDarkMode = b;
        final boolean mAsleep = this.mAsleep;
        if (!mAsleep) {
            this.mAnimator.setDarkColorMode(b);
        }
        else {
            this.mAnimator.setAsleepMode(mAsleep, b, this.mState);
        }
    }

    public void setState(final State state) {
        this.setState(state, true);
    }

    public void setState(final State mState, final boolean b) {
        if (!b && mState == this.mState) {
            return;
        }
        if (b) {
            final AnimationController mAnimator = this.mAnimator;
            final State mState2 = this.mState;
            final boolean mAsleep = this.mAsleep;
            mAnimator.setState(mState, mState2, mAsleep, mAsleep ^ true);
        }
        this.mState = mState;
        this.updateActive(this.mState != State.AWAY);
    }

    public void switchActive() {
        this.setActive( !this.mActive );
    }

    public void triggerFlick(final FlickDirection flickDirection) {
        if (!this.mAsleep) {
            this.mActive = true;
            this.mAnimator.animateFlick(flickDirection, this.mState);
        }
    }

    public void triggerOmniswipe() {
        if (!this.mAsleep) {
            this.mActive = true;
            this.mAnimator.animateOmniswipe();
        }
    }

    public void triggerPartialFlick(final FlickDirection flickDirection) {
        if (!this.mAsleep) {
            this.mActive = true;
            this.mAnimator.animatePartialFlick(flickDirection);
        }
    }

    public void triggerPartialOmniswipe() {
        if (!this.mAsleep) {
            this.mActive = true;
            this.mAnimator.animatePartialOmniswipe();
        }
    }

    public enum FlickDirection
    {
        LEFT,
        RIGHT;
    }

    private class GlowHandler extends Handler
    {
        static final int SET_ANIMATION = 2;

        public GlowHandler(final Looper looper) {
            super(looper);
        }

        public void handleMessage(final Message message) {
            if (message != null && message.what == 2) {
                final ShaderGlow shaderGlow = (ShaderGlow)message.obj;
                ShaderGlow.this.mAnimator.setState(shaderGlow.mState, shaderGlow.mState, ShaderGlow.this.mAsleep, false);
                ShaderGlow.this.mAnimator.setDarkColorMode(shaderGlow.mDarkMode, false);
            }
        }
    }

    public enum State
    {
        ACTIVE,
        AWARE,
        AWAY,
        ENGAGED;
    }
}

