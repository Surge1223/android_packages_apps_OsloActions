package com.google.oslo.ui;

import android.app.StatusBarManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.google.oslo.Assert;
import com.google.oslo.OsloOverlay;
import com.google.oslo.Prefs;
import com.google.oslo.R;
import com.google.oslo.service.serviceinterface.OsloStrings;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OsloOnboarding extends OsloView implements OsloOverlay.StatusListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final boolean DEBUG = OsloOverlay.DEBUG;
    private static final int DOZING_CHANGE = 3;
    private static final int FLICK_SUBSCRIBER_CHANGE = 1;
    private static final int FULLSCREEN_CHANGE = 5;
    private static final int GESTURE_COUNT = 3;
    private static final long HIDE_ANIM_DURATION_MS = 100;
    private static final int NOT_DOZING = 4;
    private static final int NOT_FULLSCREEN = 2;
    public static final int NO_ACTIVE_SUBSCRIBERS = 1;
    public static final int ONBOARDING_GESTURE_THRESHOLD = 2;
    public static final int ONBOARDING_TOUCH_THRESHOLD = 3;
    private static final int SETTINGS_CHANGE = 4;
    private static final long SHOW_ANIM_DELAY_MS = 0;
    private static final long SHOW_ANIM_DURATION_MS = 500;
    private static final String TAG = "OsloOnboarding";
    public static final int TIMEOUT = 6;
    @VisibleForTesting
    public static final int UNKNOWN = 0;
    public static final int USER_TOUCH = 5;
    private static final Binder sToken = new OsloOnboardingToken();
    protected String mActiveFlickSubscriber = null;
    private int mBurnInOffset;
    @VisibleForTesting
    protected boolean mFullScreen;
    @VisibleForTesting
    protected Handler mHandler;
    private  Context mPluginContext;
    private  SettingsContentObserver mSettingsContentObserver;
    private  StatusBarManager mStatusBarManager;
    private  IStatusBarService mStatusBarService;
    private  ViewGroup mStatusBarView;
    private ImageView mTooltipIcon;
    private TextView mTooltipTextView;
    private int mTooltipTint = -1;
    private View mTooltipView;
    private int mVisibility = 8;

    public class H extends Handler {
        public static final int HIDE = 2;
        public static final int SHOW = 1;

        public H() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                OsloOnboarding.this.showH(msg.arg1);
            } else if (i == 2) {
                OsloOnboarding.this.hideH(msg.arg1, msg.arg2);
            }
        }
    }

    private static final class OsloOnboardingToken extends Binder {
        private OsloOnboardingToken() {
        }
    }

    public class SettingsContentObserver extends ContentObserver {
        private final List<String> mKeysToObserve = new ArrayList();

        public SettingsContentObserver(Handler handler) {
            super(handler);
            this.mKeysToObserve.add(Prefs.Key.SKIP_SONG_GESTURE_COUNT);
            this.mKeysToObserve.add(Prefs.Key.SNOOZE_ALARM_GESTURE_COUNT);
            this.mKeysToObserve.add(Prefs.Key.MUTE_CALL_GESTURE_COUNT);
            this.mKeysToObserve.add(Prefs.Key.DISMISS_TIMER_GESTURE_COUNT);
        }

        public void register(Context context) {
            ContentResolver contentResolver = context.getContentResolver();
            for (int i = 0; i < this.mKeysToObserve.size(); i++) {
                contentResolver.registerContentObserver(Settings.Secure.getUriFor(this.mKeysToObserve.get(i)), false, this);
            }
        }

        public void unregister(Context context) {
            context.getContentResolver().unregisterContentObserver(this);
        }

        public void onChange(boolean selfChange, Uri uri) {
            Assert.isMainThread();
            OsloOnboarding.this.updateText(4);
        }
    }

    public /* bridge */ /* synthetic */ int getBurnInOffset() {
        return super.getBurnInOffset();
    }

    public /* bridge */ /* synthetic */ boolean inFullScreen() {
        return super.inFullScreen();
    }

    public /* bridge */ /* synthetic */ void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public /* bridge */ /* synthetic */ void onDarkChanged(Rect rect, float f, int i) {
        super.onDarkChanged(rect, f, i);
    }

    public /* bridge */ /* synthetic */ void registerDarkIconDispatcher() {
        super.registerDarkIconDispatcher();
    }

    public /* bridge */ /* synthetic */ void setCollapseDesired(boolean z) {
        super.setCollapseDesired(z);
    }

    public /* bridge */ /* synthetic */ void unregisterDarkIconDispatcher() {
        super.unregisterDarkIconDispatcher();
    }

    public /* bridge */ /* synthetic */ void updateConfiguration() {
        super.updateConfiguration();
    }

    @VisibleForTesting
    public OsloOnboarding(Context context, StatusBarManager statusBarManager, IStatusBarService statusBarService) {
        super(context);
        this.mPluginContext = context;
        this.mTooltipIcon = null;
        this.mSettingsContentObserver = null;
        this.mTooltipView = null;
        this.mTooltipTextView = null;
        this.mStatusBarManager = statusBarManager;
        this.mStatusBarService = statusBarService;
        this.mStatusBarView = null;
    }

    public OsloOnboarding(Context sysuiContext, Context pluginContext, DarkIconDispatcher darkIconDispatcher, StatusBarStateController statusBarStateController, boolean collapsed, ViewGroup statusBarView) {
        super(sysuiContext, pluginContext, darkIconDispatcher, statusBarStateController, collapsed);
        this.mStatusBarView = statusBarView;
        this.mPluginContext = pluginContext;
        this.mHandler = new H();
        this.mSettingsContentObserver = new SettingsContentObserver(this.mHandler);
        this.mSettingsContentObserver.register(this.mPluginContext);
        reinflateTooltipView();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
 //           this.mStatusBarManager = (StatusBarManager) ServiceManager.getService("statusbar");
   //     }
        this.mStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
    }

    private void reinflateTooltipView() {
        Assert.isMainThread();
        View view = this.mTooltipView;
        if (!(view == null || view.getParent() == null)) {
            ((ViewGroup) this.mTooltipView.getParent()).removeView(this.mTooltipView);
        }
        this.mTooltipView = LayoutInflater.from(this.mPluginContext).inflate(R.layout.oslo_onboarding_tooltip, (ViewGroup) null);
        this.mStatusBarView.addView(this.mTooltipView);
        this.mTooltipTextView = (TextView) this.mTooltipView.findViewById(R.id.onboarding_text);
        this.mTooltipIcon = (ImageView) this.mTooltipView.findViewById(R.id.tooltip_icon);
        setColor(this.mTooltipTint);
        setVisibility(getVisibility());
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.mVisibility = visibility;
        View view = this.mTooltipView;
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        if (this.mVisibility == 8) {
            unregisterDarkIconDispatcher();
        } else {
            registerDarkIconDispatcher();
        }
    }

    public int getVisibility() {
        return View.VISIBLE;
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SettingsContentObserver settingsContentObserver = this.mSettingsContentObserver;
        if (settingsContentObserver != null) {
            settingsContentObserver.unregister(this.mPluginContext);
        }
    }

    public void dozeTimeTick() {
        int offset = getBurnInOffset();
        if (DEBUG) {
            Log.d(TAG, "dozeTimeTick offset=" + offset);
        }
        if (offset != this.mBurnInOffset) {
            this.mBurnInOffset = offset;
            this.mTooltipView.setTranslationY((float) this.mBurnInOffset);
        }
    }

    public void cancelAllAnimations() {
        this.mTooltipView.animate().cancel();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Assert.isMainThread();
        updateText(4);
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

    public void onDozingChanged(boolean isDozing) {
        Assert.isMainThread();
        if (this.mDozing != isDozing) {
            super.onDozingChanged(isDozing);
            updateDozing(isDozing);
        }
    }

    @Override
    public void onDozeAmountChanged(float linear, float eased) {

    }

    public void onPulsingChanged(boolean isPulsing) {
        super.onPulsingChanged(isPulsing);
        this.mTooltipView.setSelected(!inAOD1());
    }

    public void updateColor(int newColor, float darkIntensity, String reason) {
        if (newColor != this.mTooltipTint) {
            if (DEBUG) {
                Log.d(TAG, "Updating tooltip color newColor=" + newColor + " oldColor=" + this.mTooltipTint + " darkIntensity=" + darkIntensity + " reason=" + reason);
            }
            setColor(newColor);
        }
    }

    public void onActiveFlickSubscriberChanged(String activeFlickSubscriber) {
        if (!Objects.equals(this.mActiveFlickSubscriber, activeFlickSubscriber)) {
            if (DEBUG) {
                Log.d(TAG, "onChange activeFlickSubscriber=" + activeFlickSubscriber);
            }
            this.mActiveFlickSubscriber = activeFlickSubscriber;
            updateText(1);
        }
    }

    public View getView() {
        return this.mTooltipView;
    }

    @VisibleForTesting
    public void updateText(int reason) {
        if (DEBUG) {
            Log.d(TAG, "updateText mActiveFlickSubscriber=" + this.mActiveFlickSubscriber + " reason=" + reason);
        }
        String str = this.mActiveFlickSubscriber;
        if (str != null) {
            show(str);
        } else {
            hide(false, 1);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        reinflateTooltipView();
    }

    public void onSystemUiVisibilityChanged(int visibility) {
        super.onSystemUiVisibilityChanged(visibility);
        boolean fullscreen = inFullScreen();
        if (fullscreen != this.mFullScreen) {
            this.mFullScreen = fullscreen;
            updateText(5);
        }
    }

    private void show(String actionId) {
        int stringId;
        String gestureCountKey = OsloOverlay.getGestureCountKey(actionId);
        String touchCountKey = OsloOverlay.getTouchCountKey(actionId);
        if (gestureCountKey != null && touchCountKey != null) {
            boolean gestureCountAboveThreshold = Prefs.getInt(this.mPluginContext, gestureCountKey, 0) >= 2;
            boolean touchCountAboveThreshold = Prefs.getInt(this.mPluginContext, touchCountKey, 0) >= 3;
            boolean hide = gestureCountAboveThreshold || touchCountAboveThreshold || (!this.mDozing && !this.mFullScreen);
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("checkShouldShow=");
                sb.append(!hide);
                sb.append(" actionId=");
                sb.append(actionId);
                sb.append(" mDozing=");
                sb.append(this.mDozing);
                sb.append(" gestureCountAboveThreshold=");
                sb.append(gestureCountAboveThreshold);
                sb.append(" touchCountAboveThreshold=");
                sb.append(touchCountAboveThreshold);
                sb.append(" fullscreen=");
                sb.append(this.mFullScreen);
                Log.d(TAG, sb.toString());
            }
            if (hide) {
                int reason = 4;
                if (gestureCountAboveThreshold) {
                    reason = 3;
                } else if (touchCountAboveThreshold) {
                    reason = 5;
                }
                if (this.mDozing) {
                    reason = 2;
                }
                hide(true, reason);
                return;
            }
            char c = 65535;
            switch (actionId.hashCode()) {
                case -1448737948:
                    if (actionId.equals(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_DISMISS_TIMER)) {
                        c = 3;
                        break;
                    }
                    break;
                case -1254455797:
                    if (actionId.equals(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SILENCE_RINGER)) {
                        c = 1;
                        break;
                    }
                    break;
                case -1059149585:
                    if (actionId.equals(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SKIP_MEDIA)) {
                        c = 0;
                        break;
                    }
                    break;
                case 1529572962:
                    if (actionId.equals(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SNOOZE_ALARM)) {
                        c = 2;
                        break;
                    }
                    break;
            }
            if (c != 0) {
                if (c == 1) {
                    stringId = R.string.air_swipe_calls;
                } else if (c == 2) {
                    stringId = R.string.air_swipe_alarm;
                } else if (c == 3) {
                    stringId = R.string.air_swipe_timer;
                } else if (DEBUG) {
                    Log.d(TAG, "actionId unrecognized " + actionId);
                    return;
                } else {
                    return;
                }
            } else if (!this.mDozing) {
                hide(true, 4);
                return;
            } else {
                stringId = R.string.air_swipe_media;
            }
            this.mHandler.obtainMessage(1, stringId, 0).sendToTarget();
        } else if (DEBUG) {
            Log.d(TAG, "Don't show nor hide tip.  No contextual tip associated with actionId=" + actionId);
        }
    }

    public void hide(boolean animate, int reason) {
        this.mHandler.obtainMessage(2, 0, reason).sendToTarget();
    }

    private void setColor(int color) {
        this.mTooltipTint = color;
        this.mTooltipTextView.setTextColor(this.mTooltipTint);
        this.mTooltipIcon.setImageTintList(ColorStateList.valueOf(this.mTooltipTint));
    }

    private void updateDozing(boolean isDozing) {
        if (DEBUG) {
            Log.d(TAG, "updateDozing mDozing=" + this.mDozing);
        }
        updateText(3);
    }

    private String resIdToActionId(int resId) {
        switch (resId) {
            case R.string.air_swipe_alarm:
                return OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SNOOZE_ALARM;
            case R.string.air_swipe_calls:
                return OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SILENCE_RINGER;
            case R.string.air_swipe_media:
                return OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SKIP_MEDIA;
            case R.string.air_swipe_timer:
                return OsloStrings.OsloBuiltInActionIds.CONFIG_ID_DISMISS_TIMER;
            default:
                return null;
        }
    }

    /* access modifiers changed from: private */
    public void showH(int stringResId) {
        this.mHandler.removeMessages(1);
        if (!Objects.equals(this.mTooltipView.getTag(), Integer.valueOf(stringResId)) || getVisibility() != View.VISIBLE) {
            if (DEBUG) {
                Log.d(TAG, "showH actionId=" + resIdToActionId(stringResId) + " mBurnInOffset=" + this.mBurnInOffset);
            }
            this.mTooltipView.animate().cancel();
            this.mTooltipView.setAlpha(0.0f);
            setVisibility(View.VISIBLE);
            this.mTooltipView.animate().alpha(1.0f).withLayer().setStartDelay(SHOW_ANIM_DELAY_MS).setDuration(500).withStartAction(new Runnable() {
                private /* synthetic */ int f$1;

                {
                    int r2 = 0;
                    this.f$1 = r2;
                }

                public final void run() {
                    OsloOnboarding.this.lambda$showH$0$OsloOnboarding(this.f$1);
                }
            }).withEndAction(new Runnable() {
                public final void run() {
                    OsloOnboarding.this.lambda$showH$1$OsloOnboarding();
                }
            }).setInterpolator(new DecelerateInterpolator()).start();
        } else if (DEBUG) {
            Log.d(TAG, "showH - Don't reshow. This tooltip is already showing.");
        }
    }

    public /* synthetic */ void lambda$showH$0$OsloOnboarding(int stringResId) {
        this.mTooltipView.setTag(Integer.valueOf(stringResId));
        this.mTooltipTextView.setText(stringResId);
    }

    public /* synthetic */ void lambda$showH$1$OsloOnboarding() {
        this.mTooltipView.setSelected(!inAOD1());
    }

    /* access modifiers changed from: private */
    public void hideH(int animate, int reason) {
        this.mHandler.removeMessages(2);
        if (getVisibility() == View.VISIBLE) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("hideH animate=");
                sb.append(animate == 0 ? "false" : "true");
                sb.append(" reason=");
                sb.append(reason);
                Log.d(TAG, sb.toString());
            }
            try {
                this.mStatusBarService.disable(0, sToken, this.mPluginContext.getPackageName());
            } catch (RemoteException e) {
                Log.w(TAG, "hideH RemoteException - Unable to disable icons from status bar.");
            }
            this.mTooltipView.animate().cancel();
            if (animate != 0) {
                this.mTooltipView.animate().alpha(0.0f).withLayer().setDuration(HIDE_ANIM_DURATION_MS).setInterpolator(new AccelerateInterpolator()).withStartAction(new Runnable() {
                    public final void run() {
                        OsloOnboarding.this.lambda$hideH$2$OsloOnboarding();
                    }
                }).withEndAction(new Runnable() {
                    public final void run() {
                        OsloOnboarding.this.lambda$hideH$3$OsloOnboarding();
                    }
                }).start();
            } else {
                setVisibility(View.GONE);
            }
        }
    }

    public /* synthetic */ void lambda$hideH$2$OsloOnboarding() {
        this.mTooltipView.setTag((Object) null);
    }

    public /* synthetic */ void lambda$hideH$3$OsloOnboarding() {
        setVisibility(View.GONE);
    }
}

