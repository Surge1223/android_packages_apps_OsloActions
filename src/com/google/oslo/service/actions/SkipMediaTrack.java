package com.google.oslo.service.actions;

import android.content.ComponentName;
import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.google.oslo.Utils;
import com.google.oslo.service.OsloDeviceConfig;
import com.google.oslo.service.UserContentObserver;
import com.google.oslo.service.serviceinterface.OsloServiceManager;
import com.google.oslo.service.serviceinterface.OsloStrings;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;
import com.google.oslo.service.serviceinterface.output.OsloFlickOutput;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class SkipMediaTrack extends Action {
    private static final float CONFIG_DETECTION_RADIUS = 1.0f;
    private static final int CONFIG_GRANULARITY = 3;
    private static final int CONFIG_SENSITIVITY = 1;
    private static final String TAG = "Oslo/SkipMediaTrack";
    private static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);
    /* access modifiers changed from: private */
    public static Set<String> sMediaAppWhitelist = OsloDeviceConfig.createSetFromString(OsloDeviceConfig.getMediaAppList());
    private final DeviceConfig.OnPropertiesChangedListener mDeviceConfigListener = new DeviceConfig.OnPropertiesChangedListener() {
        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            if (properties.getKeyset().contains(OsloDeviceConfig.FLAG_MEDIA_APP_LIST)) {
                Set unused = SkipMediaTrack.sMediaAppWhitelist = OsloDeviceConfig.createSetFromString(properties.getString(OsloDeviceConfig.FLAG_MEDIA_APP_LIST, OsloDeviceConfig.DEFAULT_MEDIA_APP_WHITELIST));
                SkipMediaTrack skipMediaTrack = SkipMediaTrack.this;
                skipMediaTrack.scanActiveMediaSessions(skipMediaTrack.mMediaSessionManager.getActiveSessionsForUser((ComponentName) null, -1));
            }
        }
    };
    /* access modifiers changed from: private */
    public final MediaSessionManager mMediaSessionManager = ((MediaSessionManager) getContext().getSystemService(MediaSessionManager.class));
    private final PowerManager mPowerManager = ((PowerManager) getContext().getSystemService(PowerManager.class));
    private final List<OsloMediaControllerCallback> mRegisteredCallbacks = new LinkedList();
    private final List<MediaController> mRegisteredSessions = new LinkedList();
    private final MediaSessionManager.OnActiveSessionsChangedListener mSessionsListener = new MediaSessionManager.OnActiveSessionsChangedListener() {
        public void onActiveSessionsChanged(List<MediaController> controllers) {
            SkipMediaTrack.this.scanActiveMediaSessions(controllers);
        }
    };

    private class OsloMediaControllerCallback extends MediaController.Callback {
        private MediaController mController;

        OsloMediaControllerCallback(MediaController controller) {
            this.mController = controller;
        }

        public void onPlaybackStateChanged(PlaybackState state) {
            super.onPlaybackStateChanged(state);
            SkipMediaTrack.this.updateListenerState();
        }

        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            SkipMediaTrack.this.removeRegisteredSession(this, this.mController);
            SkipMediaTrack.this.updateListenerState();
        }

        public void unregisterCallback() {
            SkipMediaTrack.this.removeRegisteredSession(this, this.mController);
        }
    }

    private boolean isPlaybackActive(int state) {
        return state == 3;
    }

    private boolean isSkipNextPrevSupported(long actions) {
        return (48 & actions) != 0;
    }

    private boolean isSupportedAppUseCase(String appPackageName, long actions) {
        if (TextUtils.isEmpty(appPackageName)) {
            return true;
        }
        char c = 65535;
        if (appPackageName.hashCode() == 1316692951 && appPackageName.equals("com.clearchannel.iheartradio.controller")) {
            c = 0;
        }
        if (c == 0 && !isSkipNextPrevSupported(actions)) {
            return false;
        }
        return true;
    }

    private boolean isSupportedApp(String appPackageName) {
        if (!TextUtils.isEmpty(appPackageName)) {
            return sMediaAppWhitelist.contains(appPackageName);
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void updateListenerState() {
        for (MediaController controller : this.mRegisteredSessions) {
            PlaybackState state = controller.getPlaybackState();
            if (state == null) {
                if (DEBUG) {
                    String tag = getTag();
                    Log.d(tag, "ignoring session from " + controller.getPackageName() + " with no state");
                }
            } else if (isPlaybackActive(state.getState()) && isSupportedAppUseCase(controller.getPackageName(), state.getActions())) {
                if (DEBUG) {
                    String tag2 = getTag();
                    Log.d(tag2, "Register for flick; session from " + controller.getPackageName() + " is active");
                }
                registerListener();
                return;
            }
        }
        if (DEBUG) {
            Log.d(getTag(), "Unregistering for flick");
        }
        unregisterListener();
    }

    private void removeRegisteredCallbacks() {
        Iterator<OsloMediaControllerCallback> iterator = this.mRegisteredCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().unregisterCallback();
            iterator.remove();
        }
    }

    /* access modifiers changed from: private */
    public void removeRegisteredSession(MediaController.Callback cb, MediaController controller) {
        Iterator<MediaController> iterator = this.mRegisteredSessions.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().controlsSameSession(controller)) {
                if (DEBUG) {
                    String tag = getTag();
                    Log.d(tag, "removing session from " + controller.getPackageName());
                }
                controller.unregisterCallback(cb);
                iterator.remove();
                return;
            }
        }
    }

    private void updateRegisteredSession(MediaController controller) {
        for (MediaController controlsSameSession : this.mRegisteredSessions) {
            if (controlsSameSession.controlsSameSession(controller)) {
                return;
            }
        }
        if (isSupportedApp(controller.getPackageName())) {
            if (DEBUG) {
                String tag = getTag();
                Log.d(tag, "adding session from " + controller.getPackageName());
            }
            OsloMediaControllerCallback controllerCallback = new OsloMediaControllerCallback(controller);
            controller.registerCallback(controllerCallback);
            this.mRegisteredCallbacks.add(controllerCallback);
            this.mRegisteredSessions.add(controller);
        }
    }

    /* access modifiers changed from: private */
    public void scanActiveMediaSessions(List<MediaController> controllers) {
        for (MediaController controller : controllers) {
            updateRegisteredSession(controller);
        }
        updateListenerState();
    }

    public SkipMediaTrack(Context context, OsloServiceManager osloServiceManager) {
        super(context, osloServiceManager);
        updateActionDetectorRegistration();
        this.mSettingsObserver = new UserContentObserver(getContext(), Settings.Secure.getUriFor(getSettingName()), new Consumer() {
            public final void accept(Object obj) {
                SkipMediaTrack.this.lambda$new$0$SkipMediaTrack((Uri) obj);
            }
        });
        DeviceConfig.addOnPropertiesChangedListener(OsloDeviceConfig.NAMESPACE, getContext().getMainExecutor(), this.mDeviceConfigListener);
    }

    public /* synthetic */ void lambda$new$0$SkipMediaTrack(Uri u) {
        updateActionDetectorRegistration();
    }

    /* access modifiers changed from: protected */
    public String getSettingName() {
        return OsloStrings.OsloSettings.OSLO_SKIP_MEDIA_TRACK_ENABLED;
    }

    /* access modifiers changed from: protected */
    public void unregisterActionDetector() {
        this.mMediaSessionManager.removeOnActiveSessionsChangedListener(this.mSessionsListener);
        removeRegisteredCallbacks();
    }

    /* access modifiers changed from: protected */
    public void registerActionDetector() {
        scanActiveMediaSessions(this.mMediaSessionManager.getActiveSessionsForUser((ComponentName) null, -1));
        this.mMediaSessionManager.addOnActiveSessionsChangedListener(this.mSessionsListener, (ComponentName) null, -1, (Handler) null);
    }

    /* access modifiers changed from: protected */
    public void onTrigger(Bundle gestureOutput) {
        OsloFlickOutput flickOutput = new OsloFlickOutput(gestureOutput);
        boolean rightToLeftNext = Utils.isFlickDirectionRightToLeft(getContext());
        if (flickOutput.getDetected()) {
            int direction = flickOutput.getDirection();
            if (!(direction == 1 || direction == 2)) {
                if (direction == 4 || direction == 5 || direction == 6) {
                    advance(rightToLeftNext);
                    return;
                } else if (direction != 8) {
                    return;
                }
            }
            advance(!rightToLeftNext);
        }
    }

    private void advance(boolean next) {
        long now = SystemClock.uptimeMillis();
        for (MediaController controller : this.mRegisteredSessions) {
            PlaybackState state = controller.getPlaybackState();
            if (state == null) {
                if (DEBUG) {
                    String tag = getTag();
                    Log.d(tag, "Can not control session from " + controller.getPackageName() + " with no state");
                }
            } else if (isPlaybackActive(state.getState()) && isSupportedAppUseCase(controller.getPackageName(), state.getActions())) {
                MediaController.TransportControls sessionControls = controller.getTransportControls();
                if (next) {
                    sessionControls.skipToNext();
                    if (DEBUG) {
                        String tag2 = getTag();
                        Log.d(tag2, "Send skip next to: " + controller.getPackageName());
                    }
                } else {
                    sessionControls.skipToPrevious();
                    if (DEBUG) {
                        String tag3 = getTag();
                        Log.d(tag3, "Send skip prev to: " + controller.getPackageName());
                    }
                }
            }
        }
        this.mPowerManager.userActivity(now, 2, 0);
    }

    /* access modifiers changed from: protected */
    public String getTag() {
        return TAG;
    }

    /* access modifiers changed from: protected */
    public int getListenerType() {
        return 1;
    }

    /* access modifiers changed from: protected */
    public OsloGestureConfig getGestureConfig() {
        return new OsloGestureConfig(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SKIP_MEDIA, CONFIG_DETECTION_RADIUS, 1, 3);
    }
}
