package com.google.oslo;

import android.content.Context;
import android.provider.Settings;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Prefs {

    @Retention(RetentionPolicy.SOURCE)
    public @interface Key {
        public static final String DISMISS_TIMER_GESTURE_COUNT = "silence_timer_gesture_count";
        public static final String DISMISS_TIMER_TOUCH_COUNT = "silence_timer_touch_count";
        public static final String MUTE_CALL_GESTURE_COUNT = "silence_call_gesture_count";
        public static final String MUTE_CALL_TOUCH_COUNT = "silence_call_touch_count";
        public static final String SKIP_SONG_GESTURE_COUNT = "skip_gesture_count";
        public static final String SKIP_SONG_TOUCH_COUNT = "skip_touch_count";
        public static final String SNOOZE_ALARM_GESTURE_COUNT = "silence_alarms_gesture_count";
        public static final String SNOOZE_ALARM_TOUCH_COUNT = "silence_alarms_touch_count";
    }

    private Prefs() {
    }
    

    public static int getInt(Context context, String key, int defaultValue) {
        return Settings.Secure.getInt(context.getContentResolver(), key, defaultValue);
    }

    public static void putInt(Context context, String key, int value) {
        Settings.Secure.putInt(context.getContentResolver(), key, value);
    }
}

