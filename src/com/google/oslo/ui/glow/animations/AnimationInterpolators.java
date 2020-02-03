package com.google.oslo.ui.glow.animations;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;

public class AnimationInterpolators {

    static class Cubic {
        static final Interpolator EASE_OUT = new PathInterpolator(0.215f, 0.61f, 0.355f, 1.0f);

        Cubic() {
        }
    }

    static class Generic {
        static final Interpolator FAST_OUT_SLOW_IN = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
        static final Interpolator LINEAR_OUT_SLOW_IN = new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);

        Generic() {
        }
    }

    static class Linear {
        static final Interpolator LINEAR = new LinearInterpolator();

        Linear() {
        }
    }

    static class Quart {
        static final Interpolator EASE_IN = new PathInterpolator(0.895f, 0.03f, 0.685f, 0.22f);
        static final Interpolator EASE_IN_OUT = new PathInterpolator(0.77f, 0.0f, 0.175f, 1.0f);
        static final Interpolator EASE_OUT = new PathInterpolator(0.165f, 0.84f, 0.44f, 1.0f);

        Quart() {
        }
    }

    static class Sine {
        static final Interpolator EASE_OUT = new PathInterpolator(0.39f, 0.575f, 0.565f, 1.0f);

        Sine() {
        }
    }
}

