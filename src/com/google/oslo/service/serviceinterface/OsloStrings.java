package com.google.oslo.service.serviceinterface;

public final class OsloStrings {
    public static final int AIRBRUSH_EL2_IOCTL_VALUE = 100014;
    public static final int AIRBRUSH_POWER_STATE_CHANGE_VALUE = 100013;
    public static final int CAMERA_CALIBRATION_VALUE = 100017;
    public static final int CITADEL_EVENT_VALUE = 100019;
    public static final int CITADEL_VERSION_VALUE = 100018;
    public static final int DARWINN_COMPILATION_FAILURE_VALUE = 100016;
    public static final int DARWINN_WATCHDOG_TIMEOUT_VALUE = 100015;
    public static final int FACE_AUTH_ENROLL_VALUE = 100011;
    public static final int FACE_AUTH_MIGRATE_VALUE = 100020;
    public static final int FACE_AUTH_UNLOCK_VALUE = 100012;
    public static final int IR_LASER_SAFETY_STATUS_VALUE = 100010;
    public static final int OSLO_ENABLED_VALUE = 100001;
    public static final int OSLO_FLICK_OUTPUT_VALUE = 100004;
    public static final int OSLO_GESTURE_CLIENT_REGISTER_VALUE = 100006;
    public static final int OSLO_GESTURE_CLIENT_UNREGISTER_VALUE = 100008;
    public static final int OSLO_PRESENCE_CLIENT_REGISTER_VALUE = 100007;
    public static final int OSLO_PRESENCE_OUTPUT_VALUE = 100002;
    public static final int OSLO_REACH_OUTPUT_VALUE = 100003;
    public static final int OSLO_STATUS_OUTPUT_VALUE = 100009;
    public static final int OSLO_SWIPE_OUTPUT_VALUE = 100005;

    public final class OsloBuiltInActionIds {
        public static final String CONFIG_ID_ATTENUATE_ALARM = "com.google.oslo.service.actions.AttenuateAlarmVolume";
        public static final String CONFIG_ID_ATTENUATE_RINGER = "com.google.oslo.service.actions.AttenuateRingerVolume";
        public static final String CONFIG_ID_ATTENUATE_TIMER = "com.google.oslo.service.actions.AttenuateTimerVolume";
        public static final String CONFIG_ID_DISMISS_TIMER = "com.google.oslo.service.actions.DismissTimer";
        public static final String CONFIG_ID_SILENCE_RINGER = "com.google.oslo.service.actions.SilenceRingerAction";
        public static final String CONFIG_ID_SKIP_MEDIA = "com.google.oslo.service.actions.SkipMediaTrack";
        public static final String CONFIG_ID_SNOOZE_ALARM = "com.google.oslo.service.actions.SnoozeAlarm";
        public static final String CONFIG_ID_SYSTEM_UI = "SystemUI";

        public OsloBuiltInActionIds() {
        }
    }

    public final class OsloSettings {
        public static final String OSLO_ENABLED = "aware_enabled";
        public static final String OSLO_EXTENDED_UNLOCK_ENABLED = "com.google.oslo.service.sensors.serviceinterface.OSLO_EXTENDED_UNLOCK_ENABLED";
        public static final String OSLO_SILENCE_INTERRUPTIONS_ENABLED = "silence_gesture";
        public static final String OSLO_SKIP_MEDIA_TRACK_ENABLED = "skip_gesture";

        public OsloSettings() {
        }
    }
}
