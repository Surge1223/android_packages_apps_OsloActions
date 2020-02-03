package com.google.oslo.ui.glow.animations;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class AnimatorHelper {
    static AnimatorSet glowScaleAnimation(AnimatedAttributes animatedAttr, float scale, long duration, Interpolator interpolator) {
        AnimatorSet glowScale = new AnimatorSet();
        glowScale.play(ObjectAnimator.ofFloat(animatedAttr, "glowScale", new float[]{scale}));
        glowScale.setDuration(duration).setInterpolator(interpolator);
        return glowScale;
    }

    static AnimatorSet glowScaleAnimation(AnimatedAttributes animatedAttr, float from, float to, long duration, Interpolator interpolator) {
        AnimatorSet glowScale = new AnimatorSet();
        glowScale.play(ObjectAnimator.ofFloat(animatedAttr, "glowScale", new float[]{from, to}));
        glowScale.setDuration(duration).setInterpolator(interpolator);
        return glowScale;
    }

    static AnimatorSet opacityAnimation(AnimatedAttributes animAttrs, float opacity, long duration, Interpolator interpolator) {
        return opacityAnimation(animAttrs, opacity, duration, interpolator, false);
    }

    static AnimatorSet opacityAnimation(AnimatedAttributes animAttrs, float opacity, long duration, Interpolator interpolator, boolean adjustLine) {
        AnimatorSet animOpacity = new AnimatorSet();
        ObjectAnimator opGlow = ObjectAnimator.ofFloat(animAttrs, "opacity", new float[]{opacity});
        opGlow.setDuration(duration).setInterpolator(interpolator);
        if (adjustLine) {
            ObjectAnimator opLine = ObjectAnimator.ofFloat(animAttrs, "lineAlpha", new float[]{opacity});
            opLine.setDuration(duration).setInterpolator(interpolator);
            animOpacity.play(opGlow).with(opLine);
        } else {
            animOpacity.play(opGlow);
        }
        return animOpacity;
    }

    static Animator animateColors(AnimatedAttributes animAttrs, Color[] glowColors, Color lineColor, long time) {
        if (glowColors == null || lineColor == null) {
            return null;
        }
        if (time <= 0) {
            animAttrs.setLineColor(lineColor);
            animAttrs.setGlowColors(glowColors);
            return null;
        }
        Color[] initGlowColors = animAttrs.getGlowColors();
        Color initLineColor = Color.valueOf(animAttrs.getLineColor().toArgb());
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        final Color[] colorArr = glowColors;
        final AnimatedAttributes animatedAttributes = animAttrs;
        final Color color = initLineColor;
        final Color color2 = lineColor;
        final Color[] colorArr2 = initGlowColors;
        animator.setDuration(time);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        return animator;
    }

    static AnimatorSet lineAnimation(AnimatedAttributes animAttrs, float alpha, float width, long duration, Interpolator interpolator) {
        AnimatorSet lineAnimation = new AnimatorSet();
        lineAnimation.play(ObjectAnimator.ofFloat(animAttrs, "lineAlpha", new float[]{alpha})).with(ObjectAnimator.ofFloat(animAttrs, "lineWidth", new float[]{width}));
        lineAnimation.setDuration(duration).setInterpolator(interpolator);
        return lineAnimation;
    }

    static AnimatorSet lineAnimation(AnimatedAttributes animAttrs, float posX, float alpha, float width, long duration, Interpolator interpolator) {
        return lineAnimation(animAttrs, posX, alpha, width, duration, duration, duration, interpolator, interpolator, interpolator);
    }

    static ObjectAnimator glowPositionYAnimation(AnimatedAttributes animAttrs, float y, long duration, Interpolator interpolator) {
        return glowPositionAnimator(animAttrs, "y", y, duration, interpolator);
    }

    static ObjectAnimator glowPositionYAnimation(AnimatedAttributes animAttrs, float fromY, float toY, long duration, Interpolator interpolator) {
        return glowPositionFromToAnimator(animAttrs, "y", fromY, toY, duration, interpolator);
    }

    static ObjectAnimator glowPositionXYAnimation(AnimatedAttributes animAttrs, float fromX, float toX, float fromY, float toY, long duration, Interpolator interpolator) {
        return glowPositionFromToAnimator(animAttrs, fromX, toX, fromY, toY, duration, interpolator);
    }

    static ObjectAnimator glowPositionXAnimation(AnimatedAttributes animAttrs, float x, long duration, Interpolator interpolator) {
        return glowPositionAnimator(animAttrs, "x", x, duration, interpolator);
    }

    static ObjectAnimator glowPositionXAnimation(AnimatedAttributes animAttrs, float fromX, float toX, long duration, Interpolator interpolator) {
        return glowPositionFromToAnimator(animAttrs, "x", fromX, toX, duration, interpolator);
    }

    private static ObjectAnimator glowPositionAnimator(AnimatedAttributes animAttrs, String attribute, float value, long duration, Interpolator interpolator) {
        ObjectAnimator glowAnimation = ObjectAnimator.ofFloat(animAttrs.getPosition(), attribute, new float[]{value});
        glowAnimation.setDuration(duration).setInterpolator(interpolator);
        return glowAnimation;
    }

    private static ObjectAnimator glowPositionFromToAnimator(AnimatedAttributes animAttrs, String attribute, float from, float to, long duration, Interpolator interpolator) {
        ObjectAnimator glowAnimation = ObjectAnimator.ofFloat(animAttrs.getPosition(), attribute, new float[]{from, to});
        glowAnimation.setDuration(duration).setInterpolator(interpolator);
        return glowAnimation;
    }

    private static ObjectAnimator glowPositionFromToAnimator(AnimatedAttributes animAttrs, float fromX, float toX, float fromY, float toY, long duration, Interpolator interpolator) {
        PropertyValuesHolder glowX = PropertyValuesHolder.ofFloat("x", new float[]{fromX, toX});
        PropertyValuesHolder glowY = PropertyValuesHolder.ofFloat("y", new float[]{fromY, toY});
        ObjectAnimator glowAnimation = ObjectAnimator.ofPropertyValuesHolder(animAttrs.getPosition(), new PropertyValuesHolder[]{glowX, glowY});
        glowAnimation.setDuration(duration).setInterpolator(interpolator);
        return glowAnimation;
    }

    static AnimatorSet lineAnimation(AnimatedAttributes animAttrs, float alpha, float width, long durationAlpha, long durationWidth, Interpolator interpolatorAlpha, Interpolator interpolatorWidth) {
        ObjectAnimator alphaAnimator = floatAnimator(animAttrs, "lineAlpha", alpha, durationAlpha, interpolatorAlpha);
        ObjectAnimator widthAnimator = floatAnimator(animAttrs, "lineWidth", width, durationWidth, interpolatorWidth);
        AnimatorSet lineAnimation = new AnimatorSet();
        lineAnimation.play(alphaAnimator).with(widthAnimator);
        return lineAnimation;
    }

    static AnimatorSet lineAnimation(AnimatedAttributes animAttrs, float posX, float alpha, float width, long durationPosX, long durationAlpha, long durationWidth, Interpolator interpolatorPosX, Interpolator interpolatorAlpha, Interpolator interpolatorWidth) {
        ObjectAnimator alphaAnimator = floatAnimator(animAttrs, "lineAlpha", alpha, durationAlpha, interpolatorAlpha);
        ObjectAnimator widthAnimator = floatAnimator(animAttrs, "lineWidth", width, durationWidth, interpolatorWidth);
        AnimatedAttributes animatedAttributes = animAttrs;
        float f = posX;
        ObjectAnimator posXAnimator = linePositionXAnimation(animAttrs, posX, durationPosX, interpolatorPosX);
        AnimatorSet lineAnimation = new AnimatorSet();
        lineAnimation.playTogether(new Animator[]{alphaAnimator, widthAnimator, posXAnimator});
        return lineAnimation;
    }

    static ObjectAnimator linePositionXAnimation(AnimatedAttributes animAttrs, float posX, long duration, Interpolator interpolator) {
        return floatAnimator(animAttrs, "linePositionX", posX, duration, interpolator);
    }

    private static ObjectAnimator floatAnimator(AnimatedAttributes animAttrs, String attribute, float value, long duration, Interpolator interpolator) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(animAttrs, attribute, new float[]{value});
        animator.setDuration(duration).setInterpolator(interpolator);
        return animator;
    }

    public static Color lerp(Color cIn1, Color cIn2, float a) {
        return lerp(cIn1, cIn2, a, true);
    }

    public static Color lerp(Color cIn1, Color cIn2, float a, boolean clamp) {
        return Color.valueOf(lerp(cIn1.red(), cIn2.red(), a, clamp), lerp(cIn1.green(), cIn2.green(), a, clamp), lerp(cIn1.blue(), cIn2.blue(), a, clamp), lerp(cIn1.alpha(), cIn2.alpha(), a, clamp));
    }

    public static float lerp(float init, float end, float a) {
        return lerp(init, end, a, true);
    }

    public static float lerp(float init, float end, float a, boolean clamp) {
        if (clamp) {
            float f = 1.0f;
            if (a <= 1.0f) {
                f = a < 0.0f ? 0.0f : a;
            }
            a = f;
        }
        return ((end - init) * a) + init;
    }
}

