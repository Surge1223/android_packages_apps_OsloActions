package com.google.oslo.ui.glow.animations;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import com.google.oslo.R;
import com.google.oslo.ui.glow.ShaderGlow;
import com.google.oslo.ui.glow.animations.AnimatedAttributes;
import com.google.oslo.ui.glow.animations.AnimationInterpolators;
import com.google.oslo.ui.glow.animations.AnimationTimes;
import com.google.oslo.ui.glow.animations.AnimatorHelper;
import com.google.oslo.ui.glow.attributes.GlowAttributes;

public class AnimationController extends AnimatedAttributes {
    private Animator mAnimColor;
    private AnimatorSet mAnimFlick;
    private AnimatorSet mAnimOmniswipe;
    private Animator mAnimOpacity;
    private AnimatorSet mAnimReengage;
    private Animator mAnimScale;
    private AnimatorSet mAnimState;
    private final Color[] mDefaultGlowColorsAsleepDark;
    private final Color[] mDefaultGlowColorsAsleepLight;
    private final Color[] mDefaultGlowColorsDark;
    private final Color[] mDefaultGlowColorsLight;
    private final AnimatedAttributes.Position mDefaultGlowEndFlick = new AnimatedAttributes.Position();
    private final AnimatedAttributes.Position mDefaultGlowFlick = new AnimatedAttributes.Position();
    private final AnimatedAttributes.Position mDefaultGlowPartialFlick = new AnimatedAttributes.Position();
    private final AnimatedAttributes.Position mDefaultGlowPosActive = new AnimatedAttributes.Position();
    /* access modifiers changed from: private */
    public final AnimatedAttributes.Position mDefaultGlowPosAway = new AnimatedAttributes.Position();
    private final AnimatedAttributes.Position mDefaultGlowPosEngaged = new AnimatedAttributes.Position();
    private final Color mDefaultLineColorDark;
    private final Color mDefaultLineColorLight;
    private float mDefaultLineWidth;
    private float mDefaultLineWidthExpanded;
    private final float mScaleInactive;

    /* renamed from: com.google.oslo.ui.glow.animations.AnimationController$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$oslo$ui$glow$ShaderGlow$State = new int[ShaderGlow.State.values().length];

        static {
            try {
                $SwitchMap$com$google$oslo$ui$glow$ShaderGlow$State[ShaderGlow.State.ENGAGED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$oslo$ui$glow$ShaderGlow$State[ShaderGlow.State.AWARE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$oslo$ui$glow$ShaderGlow$State[ShaderGlow.State.ACTIVE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private class AnimatorListenerReset implements Animator.AnimatorListener {
        private AnimatorListenerReset() {
        }

        /* synthetic */ AnimatorListenerReset(AnimationController x0, AnonymousClass1 x1) {
            this();
        }

        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            AnimationController.this.setGlowScale(1.0f);
            AnimationController animationController = AnimationController.this;
            animationController.setGlowPosition(new AnimatedAttributes.Position(0.0f, animationController.mDefaultGlowPosAway.getY()));
        }

        public void onAnimationCancel(Animator animation) {
        }

        public void onAnimationRepeat(Animator animation) {
        }
    }

    public /* bridge */ /* synthetic */ Color[] getGlowColors() {
        return super.getGlowColors();
    }

    public /* bridge */ /* synthetic */ PointF getGlowPosition() {
        return super.getGlowPosition();
    }

    public /* bridge */ /* synthetic */ float getGlowScale() {
        return super.getGlowScale();
    }

    public /* bridge */ /* synthetic */ float getLineAlpha() {
        return super.getLineAlpha();
    }

    public /* bridge */ /* synthetic */ Color getLineColor() {
        return super.getLineColor();
    }

    public /* bridge */ /* synthetic */ float getLinePositionX() {
        return super.getLinePositionX();
    }

    public /* bridge */ /* synthetic */ float getLineWidth() {
        return super.getLineWidth();
    }

    public /* bridge */ /* synthetic */ float getOpacity() {
        return super.getOpacity();
    }

    public /* bridge */ /* synthetic */ AnimatedAttributes.Position getPosition() {
        return super.getPosition();
    }

    public /* bridge */ /* synthetic */ void setGlowScale(float f) {
        super.setGlowScale(f);
    }

    public /* bridge */ /* synthetic */ void setLineAlpha(float f) {
        super.setLineAlpha(f);
    }

    public /* bridge */ /* synthetic */ void setLinePositionX(float f) {
        super.setLinePositionX(f);
    }

    public /* bridge */ /* synthetic */ void setLineWidth(float f) {
        super.setLineWidth(f);
    }

    public /* bridge */ /* synthetic */ void setOpacity(float f) {
        super.setOpacity(f);
    }

    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    public AnimationController(int numStopsGlow, Resources pluginResources) {
        super(numStopsGlow);
        this.mDefaultGlowColorsLight = new Color[]{Color.valueOf(pluginResources.getColor(R.color.glow_light_color_1, (Resources.Theme) null)), Color.valueOf(pluginResources.getColor(R.color.glow_light_color_2, (Resources.Theme) null)), Color.valueOf(pluginResources.getColor(R.color.glow_light_color_3, (Resources.Theme) null))};
        this.mDefaultGlowColorsDark = new Color[]{Color.valueOf(pluginResources.getColor(R.color.glow_dark_color_1, (Resources.Theme) null)), Color.valueOf(pluginResources.getColor(R.color.glow_dark_color_2, (Resources.Theme) null)), Color.valueOf(pluginResources.getColor(R.color.glow_dark_color_3, (Resources.Theme) null))};
        this.mDefaultGlowColorsAsleepLight = new Color[]{Color.valueOf(pluginResources.getColor(R.color.glow_light_asleep_color_1, (Resources.Theme) null)), Color.valueOf(pluginResources.getColor(R.color.glow_light_asleep_color_2, (Resources.Theme) null)), Color.valueOf(pluginResources.getColor(R.color.glow_light_asleep_color_3, (Resources.Theme) null))};
        this.mDefaultGlowColorsAsleepDark = new Color[]{Color.valueOf(pluginResources.getColor(R.color.glow_dark_asleep_color_1, (Resources.Theme) null)), Color.valueOf(pluginResources.getColor(R.color.glow_dark_asleep_color_2, (Resources.Theme) null)), Color.valueOf(pluginResources.getColor(R.color.glow_dark_asleep_color_3, (Resources.Theme) null))};
        this.mDefaultLineColorLight = Color.valueOf(pluginResources.getColor(R.color.glow_light_line_color, (Resources.Theme) null));
        this.mDefaultLineColorDark = Color.valueOf(pluginResources.getColor(R.color.glow_dark_line_color, (Resources.Theme) null));
        this.mScaleInactive = pluginResources.getFloat(R.dimen.glow_scale_inactive);
    }

    public void cancelAll() {
        cancel(this.mAnimState);
        cancel(this.mAnimOmniswipe);
        cancel(this.mAnimFlick);
        cancel(this.mAnimOpacity);
        cancel(this.mAnimColor);
        cancel(this.mAnimReengage);
        cancel(this.mAnimScale);
    }

    private void cancelStateAnimations() {
        cancel(this.mAnimState);
        cancel(this.mAnimOpacity);
        cancel(this.mAnimScale);
    }

    private void cancel(Animator animator) {
        if (animator != null) {
            animator.cancel();
        }
    }

    public void setDefaultValues(Resources resources, Point screenSize, GlowAttributes glowAttributes) {
        this.mDefaultGlowPosAway.setY(((float) (-resources.getDimensionPixelSize(R.dimen.glow_position_y_away))) / ((float) screenSize.x));
        this.mDefaultGlowPosEngaged.setY(((float) (-resources.getDimensionPixelSize(R.dimen.glow_position_y_engaged))) / ((float) screenSize.x));
        this.mDefaultGlowPosActive.setY(((float) (-resources.getDimensionPixelSize(R.dimen.glow_position_y_active))) / ((float) screenSize.x));
        this.mDefaultGlowPartialFlick.setX(((float) resources.getDimensionPixelSize(R.dimen.glow_position_x_partial_flick)) / ((float) screenSize.x));
        this.mDefaultGlowFlick.setX(0.55f);
        this.mDefaultGlowEndFlick.setY(((float) (-resources.getDimensionPixelSize(R.dimen.glow_position_y_flick))) / ((float) screenSize.x));
        this.mDefaultLineWidth = glowAttributes.getRadius().x * 2.38f;
        this.mDefaultLineWidthExpanded = glowAttributes.getRadius().x * 2.8f;
    }

    public void setOpacity(float opacity, boolean animate) {
        cancelStateAnimations();
        if (animate) {
            this.mAnimOpacity = animateOpacity(opacity, opacity == 0.0f);
            if (opacity == 0.0f) {
                this.mAnimOpacity.addListener(new AnimatorListenerReset(this, (AnonymousClass1) null));
            }
            this.mAnimOpacity.start();
            return;
        }
        setOpacity(opacity);
    }

    public void setScale(boolean active, boolean animate) {
        float targetScale = active ? 1.0f : this.mScaleInactive;
        cancel(this.mAnimScale);
        if (animate) {
            this.mAnimScale = animateScale(1.0f, targetScale, active);
            this.mAnimScale.start();
            return;
        }
        setGlowScale(targetScale);
    }

    public void setState(ShaderGlow.State state, ShaderGlow.State prevState, boolean asleep) {
        setState(state, prevState, asleep, true);
    }

    public void setState(ShaderGlow.State state, ShaderGlow.State prevState, boolean asleep, boolean animate) {
        float lineWidth;
        float lineAlpha;
        Interpolator interpolator;
        AnimatedAttributes.Position position;
        ShaderGlow.State state2 = state;
        ShaderGlow.State state3 = prevState;
        float lineWidth2 = this.mDefaultLineWidth;
        Interpolator interpolator2 = AnimationInterpolators.Generic.FAST_OUT_SLOW_IN;
        int i = AnonymousClass1.$SwitchMap$com$google$oslo$ui$glow$ShaderGlow$State[state.ordinal()];
        if (i != 1) {
            if (i == 2) {
                AnimatedAttributes.Position position2 = this.mDefaultGlowPosAway;
                lineAlpha = 0.0f;
                lineWidth = lineWidth2;
                interpolator = AnimationInterpolators.Quart.EASE_IN;
                position = position2;
            } else if (i != 3) {
                AnimatedAttributes.Position position3 = this.mDefaultGlowPosAway;
                lineAlpha = 0.0f;
                lineWidth = lineWidth2;
                interpolator = AnimationInterpolators.Quart.EASE_IN;
                position = position3;
            } else {
                AnimatedAttributes.Position position4 = asleep ? this.mDefaultGlowPosEngaged : this.mDefaultGlowPosActive;
                lineAlpha = 0.0f;
                lineWidth = lineWidth2;
                interpolator = AnimationInterpolators.Generic.LINEAR_OUT_SLOW_IN;
                position = position4;
            }
        } else if (state3 == ShaderGlow.State.ACTIVE || !animate) {
            lineAlpha = 0.0f;
            lineWidth = lineWidth2;
            interpolator = interpolator2;
            position = this.mDefaultGlowPosEngaged;
        } else {
            AnimatedAttributes.Position position5 = this.mDefaultGlowPosActive;
            lineAlpha = asleep ? 0.0f : 1.0f;
            lineWidth = this.mDefaultLineWidthExpanded;
            interpolator = interpolator2;
            position = position5;
        }
        cancelStateAnimations();
        if (!animate) {
            setGlowPosition(position);
            setLineAlpha(0.0f);
            setLineWidth(this.mDefaultLineWidth);
            return;
        }
        if (state2 == ShaderGlow.State.ENGAGED && state3 == ShaderGlow.State.ENGAGED) {
            this.mAnimState = softEngagedAnimation(ShaderGlow.State.ENGAGED, 0);
        } else if (state2 != ShaderGlow.State.ENGAGED || state3 == ShaderGlow.State.ACTIVE) {
            this.mAnimState = defaultStateAnimation(position.getY(), lineAlpha, lineWidth, interpolator);
        } else {
            this.mAnimState = engagedStateAnimation(position.getY(), lineAlpha, lineWidth, this.mDefaultGlowPosEngaged.getY(), 0, this.mDefaultLineWidth);
        }
        this.mAnimState.start();
    }

    public boolean isAnimating() {
        return isAnimating(this.mAnimState) || isAnimating(this.mAnimOmniswipe) || isAnimating(this.mAnimFlick) || isAnimating(this.mAnimOpacity) || isAnimating(this.mAnimColor) || isAnimating(this.mAnimReengage) || isAnimating(this.mAnimScale);
    }

    public boolean isAnimatingOmniswipe() {
        return isAnimating(this.mAnimOmniswipe);
    }

    public boolean isAnimatingFlick() {
        return isAnimating(this.mAnimFlick);
    }

    private boolean isAnimating(Animator animator) {
        return animator != null && animator.isStarted();
    }

    public void animateOmniswipeReengage(ShaderGlow.State state) {
        cancel(this.mAnimState);
        this.mAnimState = softEngagedAnimation(state, AnimationTimes.Omniswipe.GLOW_IN_START_DELAY);
        this.mAnimState.start();
    }

    public void animateOmniswipe() {
        cancel(this.mAnimOmniswipe);
        this.mAnimOmniswipe = omniswipeAnimation();
        this.mAnimOmniswipe.start();
    }

    public void animateFlickReengage(ShaderGlow.State state) {
        cancel(this.mAnimState);
        this.mAnimState = softEngagedAnimation(state, 0);
        this.mAnimState.start();
    }

    private AnimatorSet softEngagedAnimation(ShaderGlow.State state, long delay) {
        cancel(this.mAnimOpacity);
        cancel(this.mAnimScale);
        new AnimatorSet();
        ObjectAnimator glowResetXPos = AnimatorHelper.glowPositionXAnimation(this, 0.0f, 0, (Interpolator) null);
        ObjectAnimator glowResetYPos = AnimatorHelper.glowPositionYAnimation(this, this.mDefaultGlowPosEngaged.getY(), 0, (Interpolator) null);
        this.mAnimScale = AnimatorHelper.glowScaleAnimation(this, 0.0f, 1.0f, AnimationTimes.StateEngaged.SOFT_GLOW_IN_TIME, AnimationInterpolators.Quart.EASE_OUT);
        this.mAnimOpacity = AnimatorHelper.opacityAnimation(this, 1.0f, AnimationTimes.StateEngaged.SOFT_GLOW_IN_TIME, AnimationInterpolators.Quart.EASE_OUT);
        AnimatorSet reset = new AnimatorSet();
        reset.playTogether(new Animator[]{glowResetXPos, glowResetYPos});
        AnimatorSet animateIn = new AnimatorSet();
        animateIn.playTogether(new Animator[]{this.mAnimOpacity, this.mAnimScale});
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(new Animator[]{reset, animateIn});
        animatorSet.setStartDelay(delay);
        return animatorSet;
    }

    public void animateFlick(ShaderGlow.FlickDirection direction, ShaderGlow.State state) {
        cancel(this.mAnimFlick);
        this.mAnimFlick = flickAnimation(direction, state);
        this.mAnimFlick.start();
    }

    public void animatePartialOmniswipe() {
        cancel(this.mAnimOmniswipe);
        this.mAnimOmniswipe = partialOmniswipeAnimation();
        this.mAnimOmniswipe.start();
    }

    public void animatePartialFlick(ShaderGlow.FlickDirection direction) {
        cancel(this.mAnimFlick);
        this.mAnimFlick = partialFlickAnimation(direction);
        this.mAnimFlick.start();
    }

    public void setDarkColorMode(boolean darkMode) {
        setDarkColorMode(darkMode, true);
    }

    public void setDarkColorMode(boolean darkMode, boolean animate) {
        setDarkColorMode(darkMode, animate ? AnimationTimes.COLOR_DARK_TIME : 0);
    }

    /* access modifiers changed from: package-private */
    public void setDarkColorMode(boolean darkMode, long time) {
        this.mAnimColor = AnimatorHelper.animateColors(this, darkMode ? this.mDefaultGlowColorsDark : this.mDefaultGlowColorsLight, darkMode ? this.mDefaultLineColorDark : this.mDefaultLineColorLight, time);
    }

    public void setAsleepMode(boolean asleep, boolean darkMode, ShaderGlow.State state) {
        Color[] colorArr;
        if (asleep) {
            colorArr = darkMode ? this.mDefaultGlowColorsAsleepDark : this.mDefaultGlowColorsAsleepLight;
        } else {
            colorArr = darkMode ? this.mDefaultGlowColorsDark : this.mDefaultGlowColorsLight;
        }
        this.mAnimColor = AnimatorHelper.animateColors(this, colorArr, darkMode ? this.mDefaultLineColorDark : this.mDefaultLineColorLight, AnimationTimes.COLOR_ASLEEP_TIME);
        if (state == ShaderGlow.State.ACTIVE || state == ShaderGlow.State.ENGAGED) {
            cancel(this.mAnimState);
            this.mAnimState = defaultStateAnimation(this.mDefaultGlowPosEngaged.getY(), 0.0f, this.mDefaultLineWidth, AnimationInterpolators.Generic.LINEAR_OUT_SLOW_IN);
            this.mAnimState.start();
        }
    }

    private AnimatorSet engagedStateAnimation(float y, float lineAlpha, float lineWidth, float y1, int lineAlpha1, float lineWidth1) {
        AnimatorSet animatorSet = new AnimatorSet();
        setOpacity(1.0f, false);
        setScale(true, false);
        ObjectAnimator glowResetXPos = AnimatorHelper.glowPositionXAnimation(this, 0.0f, 0, (Interpolator) null);
        AnimatorSet glowAnimator = new AnimatorSet();
        ObjectAnimator glowIn = AnimatorHelper.glowPositionYAnimation(this, y, AnimationTimes.StateEngaged.GLOW_IN_TIME, AnimationInterpolators.Quart.EASE_OUT);
        ObjectAnimator glowOut = AnimatorHelper.glowPositionYAnimation(this, y1, AnimationTimes.StateEngaged.GLOW_OUT_TIME, AnimationInterpolators.Generic.FAST_OUT_SLOW_IN);
        glowOut.setStartDelay(AnimationTimes.StateEngaged.GLOW_OUT_START_DELAY);
        glowAnimator.play(glowResetXPos).with(glowIn).before(glowOut);
        AnimatorSet lineAnimator = new AnimatorSet();
        ObjectAnimator lineResetXPos = AnimatorHelper.linePositionXAnimation(this, 0.0f, 0, (Interpolator) null);
        AnimatorSet lineIn = AnimatorHelper.lineAnimation(this, lineAlpha, lineWidth, AnimationTimes.StateEngaged.LINE_IN_TIME, AnimationInterpolators.Generic.LINEAR_OUT_SLOW_IN);
        lineIn.setStartDelay(AnimationTimes.StateEngaged.LINE_IN_START_DELAY);
        AnimatorSet lineOut = AnimatorHelper.lineAnimation(this, (float) lineAlpha1, lineWidth1, AnimationTimes.StateEngaged.LINE_OUT_TIME, AnimationInterpolators.Generic.FAST_OUT_SLOW_IN);
        lineOut.setStartDelay(AnimationTimes.StateEngaged.LINE_OUT_START_DELAY);
        lineAnimator.play(lineIn).before(lineOut);
        animatorSet.play(lineResetXPos).with(glowAnimator).with(lineAnimator);
        return animatorSet;
    }

    private AnimatorSet defaultStateAnimation(float glowPosY, float lineAlpha, float lineWidth, Interpolator interpolator) {
        setOpacity(1.0f, false);
        setScale(true, false);
        ObjectAnimator glowAnimator = AnimatorHelper.glowPositionYAnimation(this, glowPosY, AnimationTimes.StateDefault.TIME, interpolator);
        AnimatorSet lineAnimator = AnimatorHelper.lineAnimation(this, lineAlpha, lineWidth, AnimationTimes.StateDefault.TIME, interpolator);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(glowAnimator).with(lineAnimator);
        return animatorSet;
    }

    private AnimatorSet partialOmniswipeAnimation() {
        cancel(this.mAnimState);
        cancel(this.mAnimScale);
        AnimatorSet glowOut = AnimatorHelper.glowScaleAnimation(this, 0.75f, AnimationTimes.PartialOmniswipe.GLOW_OUT_TIME, AnimationInterpolators.Generic.LINEAR_OUT_SLOW_IN);
        AnimatorSet glowIn = AnimatorHelper.glowScaleAnimation(this, 1.0f, AnimationTimes.PartialOmniswipe.GLOW_IN_TIME, new AccelerateDecelerateInterpolator());
        AnimatorSet mAnimOmniswipe2 = new AnimatorSet();
        mAnimOmniswipe2.playSequentially(new Animator[]{resetToEngagedState(), glowOut, glowIn});
        return mAnimOmniswipe2;
    }

    private AnimatorSet partialFlickAnimation(ShaderGlow.FlickDirection direction) {
        ObjectAnimator glowOut = AnimatorHelper.glowPositionXAnimation(this, this.mDefaultGlowPartialFlick.getX() * ((float) (direction == ShaderGlow.FlickDirection.LEFT ? -1 : 1)), AnimationTimes.PartialFlick.GLOW_OUT_TIME, AnimationInterpolators.Quart.EASE_OUT);
        ObjectAnimator glowIn = AnimatorHelper.glowPositionXAnimation(this, 0.0f, AnimationTimes.PartialFlick.GLOW_IN_TIME, AnimationInterpolators.Generic.FAST_OUT_SLOW_IN);
        AnimatorSet mAnimFlick2 = new AnimatorSet();
        mAnimFlick2.playSequentially(new Animator[]{resetToEngagedState(), glowOut, glowIn});
        return mAnimFlick2;
    }

    private Animator resetToEngagedState() {
        setOpacity(1.0f, false);
        setScale(true, false);
        ObjectAnimator linePosReset = AnimatorHelper.linePositionXAnimation(this, 0.0f, 10, (Interpolator) null);
        ObjectAnimator glowYReset = AnimatorHelper.glowPositionYAnimation(this, this.mDefaultGlowPosEngaged.getY(), 10, (Interpolator) null);
        ObjectAnimator glowXReset = AnimatorHelper.glowPositionXAnimation(this, 0.0f, 10, (Interpolator) null);
        AnimatorSet resetValues = new AnimatorSet();
        resetValues.playTogether(new Animator[]{linePosReset, glowXReset, glowYReset});
        return resetValues;
    }

    private AnimatorSet flickAnimation(ShaderGlow.FlickDirection direction, ShaderGlow.State state) {
        float f;
        cancelStateAnimations();
        float x = this.mDefaultGlowFlick.getX() * ((float) (direction == ShaderGlow.FlickDirection.LEFT ? -1 : 1));
        if (state == ShaderGlow.State.ACTIVE) {
            f = this.mDefaultGlowPosActive.getY();
        } else {
            f = this.mDefaultGlowPosEngaged.getY();
        }
        ObjectAnimator glowOut = AnimatorHelper.glowPositionXYAnimation(this, 0.0f, x, f, this.mDefaultGlowEndFlick.getY(), AnimationTimes.Flick.GLOW_OUT_TIME, AnimationInterpolators.Generic.LINEAR_OUT_SLOW_IN);
        this.mAnimOpacity = AnimatorHelper.opacityAnimation(this, 0.0f, AnimationTimes.Flick.GLOW_OUT_TIME, AnimationInterpolators.Linear.LINEAR);
        this.mAnimScale = AnimatorHelper.glowScaleAnimation(this, 1.0f, 0.0f, AnimationTimes.Flick.GLOW_OUT_SCALE_TIME, AnimationInterpolators.Generic.FAST_OUT_SLOW_IN);
        AnimatorSet lineIn = AnimatorHelper.lineAnimation(this, direction == ShaderGlow.FlickDirection.LEFT ? -0.5f : 0.5f, 1.0f, this.mDefaultLineWidthExpanded, AnimationTimes.Flick.LINE_IN_POS_TIME, AnimationInterpolators.Linear.LINEAR);
        AnimatorSet lineOut = AnimatorHelper.lineAnimation(this, 0.0f, this.mDefaultLineWidth, AnimationTimes.Flick.LINE_OUT_TIME, AnimationInterpolators.Generic.LINEAR_OUT_SLOW_IN);
        AnimatorSet animFlickLine = new AnimatorSet();
        animFlickLine.playSequentially(new Animator[]{lineIn, lineOut});
        AnimatorSet animFlickIn = new AnimatorSet();
        animFlickIn.playTogether(new Animator[]{glowOut, this.mAnimScale, this.mAnimOpacity, animFlickLine});
        AnimatorSet mAnimFlick2 = new AnimatorSet();
        mAnimFlick2.playSequentially(new Animator[]{resetToEngagedState(), animFlickIn});
        mAnimFlick2.addListener(new AnimatorListenerReset(this, (AnonymousClass1) null));
        return mAnimFlick2;
    }

    private AnimatorSet omniswipeAnimation() {
        cancelStateAnimations();
        this.mAnimScale = AnimatorHelper.glowScaleAnimation(this, 0.0f, AnimationTimes.Omniswipe.GLOW_OUT_TIME, AnimationInterpolators.Quart.EASE_IN);
        AnimatorSet lineIn = AnimatorHelper.lineAnimation(this, 1.0f, this.mDefaultLineWidth, AnimationTimes.Omniswipe.LINE_IN_ALPHA_TIME, AnimationTimes.Omniswipe.LINE_IN_WIDTH_TIME, AnimationInterpolators.Generic.LINEAR_OUT_SLOW_IN, (Interpolator) null);
        AnimatorSet lineOut = AnimatorHelper.lineAnimation(this, 0.0f, 0.0f, AnimationTimes.Omniswipe.LINE_OUT_ALPHA_TIME, AnimationTimes.Omniswipe.LINE_OUT_WIDTH_TIME, AnimationInterpolators.Linear.LINEAR, AnimationInterpolators.Quart.EASE_IN);
        AnimatorSet animOmniswipe = new AnimatorSet();
        AnimatorSet animOmniswipeOut = new AnimatorSet();
        animOmniswipeOut.playSequentially(new Animator[]{lineIn, lineOut});
        animOmniswipe.playTogether(new Animator[]{this.mAnimScale, animOmniswipeOut});
        AnimatorSet mAnimOmniswipe2 = new AnimatorSet();
        mAnimOmniswipe2.playSequentially(new Animator[]{resetToEngagedState(), animOmniswipe});
        mAnimOmniswipe2.addListener(new AnimatorListenerReset(this, (AnonymousClass1) null));
        return mAnimOmniswipe2;
    }

    private AnimatorSet animateOpacity(float opacity, boolean adjustLineOpacity) {
        Interpolator interpolator;
        long j = opacity == 0.0f ? AnimationTimes.Opacity.OPACITY_OUT_TIME : AnimationTimes.Opacity.OPACITY_IN_TIME;
        if (opacity == 0.0f) {
            interpolator = AnimationInterpolators.Generic.FAST_OUT_SLOW_IN;
        } else {
            interpolator = AnimationInterpolators.Linear.LINEAR;
        }
        return AnimatorHelper.opacityAnimation(this, opacity, j, interpolator, adjustLineOpacity);
    }

    private AnimatorSet animateScale(float initScale, float targetScale, boolean active) {
        return AnimatorHelper.glowScaleAnimation(this, initScale, targetScale, active ? AnimationTimes.Scale.SCALE_IN_TIME : AnimationTimes.Scale.SCALE_OUT_TIME, AnimationInterpolators.Generic.FAST_OUT_SLOW_IN);
    }
}

