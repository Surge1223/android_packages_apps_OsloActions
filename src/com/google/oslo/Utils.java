package com.google.oslo;

import android.content.Context;
import android.provider.Settings;
import android.util.FeatureFlagUtils;

public class Utils {
    private static boolean isSkipDirectionMutable(Context context) {
        return FeatureFlagUtils.isEnabled(context, "settings_skip_direction_mutable");
    }

    public static boolean isFlickDirectionRightToLeft(Context context) {
        if (isSkipDirectionMutable(context) && Settings.Secure.getIntForUser(context.getContentResolver(), "skip_gesture_direction", 0, -2) != 0) {
            return false;
        }
        return true;
    }
}
