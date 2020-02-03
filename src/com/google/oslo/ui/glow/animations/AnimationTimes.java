package com.google.oslo.ui.glow.animations;

public class AnimationTimes {
    static final long ANDROID_LONG_ANIM_TIME;
    static final long ANDROID_MEDIUM_ANIM_TIME;
    static final long ANDROID_SHORT_ANIM_TIME;
    static final long COLOR_ASLEEP_TIME;
    static final long COLOR_DARK_TIME;
    public static boolean DEBUG = false;
    static long DEBUG_MULTIPLIER = (DEBUG ? 3 : 1);

    static class Flick {
        static final long GLOW_OUT_SCALE_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 500);
        static final long GLOW_OUT_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 150);
        static final long GLOW_SCALE_START_DELAY = (AnimationTimes.DEBUG_MULTIPLIER * 10);
        static final long LINE_IN_POS_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 200);
        static final long LINE_OUT_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 700);

        Flick() {
        }
    }

    static class Omniswipe {
        static final long GLOW_IN_START_DELAY = (AnimationTimes.DEBUG_MULTIPLIER * 300);
        static final long GLOW_OUT_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 500);
        static final long LINE_IN_ALPHA_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 150);
        static final long LINE_IN_WIDTH_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 0);
        static final long LINE_OUT_ALPHA_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 550);
        static final long LINE_OUT_WIDTH_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 450);

        Omniswipe() {
        }
    }

    static class Opacity {
        static final long OPACITY_IN_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 500);
        static final long OPACITY_OUT_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 700);

        Opacity() {
        }
    }

    static class PartialFlick {
        static final long GLOW_IN_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 700);
        static final long GLOW_OUT_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 200);

        PartialFlick() {
        }
    }

    static class PartialOmniswipe {
        static final long GLOW_IN_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 700);
        static final long GLOW_OUT_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 200);

        PartialOmniswipe() {
        }
    }

    public static class Scale {
        static final long SCALE_IN_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 500);
        public static final long SCALE_OUT_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 700);
    }

    static class StateDefault {
        static final long TIME = (AnimationTimes.DEBUG_MULTIPLIER * 700);

        StateDefault() {
        }
    }

    static class StateEngaged {
        static final long GLOW_IN_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 200);
        static final long GLOW_OUT_START_DELAY = (AnimationTimes.DEBUG_MULTIPLIER * 30);
        static final long GLOW_OUT_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 500);
        static final long LINE_IN_START_DELAY = (AnimationTimes.DEBUG_MULTIPLIER * 100);
        static final long LINE_IN_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 200);
        static final long LINE_OUT_START_DELAY = (AnimationTimes.DEBUG_MULTIPLIER * 100);
        static final long LINE_OUT_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 600);
        static final long SOFT_GLOW_IN_TIME = (AnimationTimes.DEBUG_MULTIPLIER * 700);

        StateEngaged() {
        }
    }

    static {
        long j = DEBUG_MULTIPLIER;
        ANDROID_SHORT_ANIM_TIME = j * 200;
        ANDROID_MEDIUM_ANIM_TIME = 400 * j;
        ANDROID_LONG_ANIM_TIME = 500 * j;
        COLOR_DARK_TIME = j * 200;
        COLOR_ASLEEP_TIME = j * 200;
    }
}

