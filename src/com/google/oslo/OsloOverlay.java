package com.google.oslo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.util.Log;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.InputMonitor;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.OverlayPlugin;
import com.android.systemui.plugins.PluginDependency;
import com.android.systemui.plugins.annotations.Requirements;
import com.android.systemui.plugins.annotations.Requires;
import com.android.systemui.plugins.statusbar.DozeParameters;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.google.oslo.service.serviceinterface.OsloServiceManager;
import com.google.oslo.service.serviceinterface.OsloServiceManager.GestureListener;
import com.google.oslo.service.serviceinterface.OsloStrings;
import com.google.oslo.service.serviceinterface.input.OsloGestureConfig;
import com.google.oslo.service.serviceinterface.output.OsloFlickOutput;
import com.google.oslo.service.serviceinterface.output.OsloReachOutput;
import com.google.oslo.service.serviceinterface.output.OsloStatusOutput;
import com.google.oslo.ui.OsloFeedbackView;
import com.google.oslo.ui.OsloOnboarding;
import com.google.oslo.ui.glow.GlowFeedbackView;
import com.google.oslo.ui.glow.ShaderView;
import com.google.oslo.ui.glow.animations.AnimationTimes;
import com.google.oslo.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Requirements({@Requires(target = OverlayPlugin.class, version = 4), @Requires(target = DarkIconDispatcher.DarkReceiver.class, version = 1), @Requires(target = DarkIconDispatcher.class, version = 1), @Requires(target = StatusBarStateController.StateListener.class, version = 1), @Requires(target = StatusBarStateController.class, version = 1)})
public class OsloOverlay implements OverlayPlugin {
    public static final boolean DEBUG = SystemProperties.getBoolean("persist.sysui.oslofeedback", false);
    private static final boolean ENABLE_OSLOFEEDBACK_VIA_ADB = DEBUG;
    static final float PRESENCE_DEBOUNCE = 0.1f;
    static final float PRESENCE_DETECTION_RADIUS = 1.3f;
    public static final float REACH_DETECTION_RADIUS = 0.2f;
    static final float SWIPE_DETECTION_RADIUS = 0.25f;
    private static final String TAG = "OsloPlugin";
    static final float VIS_FEEDBACK_REACH_SENSITIVITY = 1.0f;
    int VERSION = 4;

    public static DozeParameters mDozeParams;
    private final String ASLEEP;
    private final String DISENGAGED;
    private final String ENGAGED;
    private final String FLICK_LEFT;
    private final String FLICK_RIGHT;
    private final String GLOW;
    private final String HIDE;
    private final String LINE;
    private final String OMNISWIPE;
    private final String OSLO_ACTION_NAME = getClass().getName();
    private final String REACH;
    private final String REACH_OUT;
    private final String RESET_PREFS;
    private final String SCALE_ANIMATION_TIME;
    protected String mActiveFlickSubscriber;
    private boolean mAdbCommandsRegistered;
    private final Runnable mCloseStatusBar;
    private boolean mCollapseDesired;
    private boolean mCollapsed;
    private DarkIconDispatcher mDarkIconDispatcher;

    public boolean mDestroyed;

    public boolean mFullScreen;
    protected int mGatingReason;
    private final OsloFeedbackView.Callback mGestureCallback;
    protected GestureListener mGestureListenerFlickEcho;
    protected final GestureListener mGestureListenerReach;
    protected final GestureListener mGestureListenerReachEcho;
    private boolean mHoldStatusBarOpen;

    public boolean mIsOsloEnabled;
    @VisibleForTesting
    protected Handler mMainThreadHandler;
    private Minimizer mMinimizer;
    protected OsloFeedbackView mOsloFeedbackView;

    public OsloOnboarding mOsloOnboarding;
    protected OsloServiceManager mOsloServiceManager;
    @VisibleForTesting
    protected HashSet mPackagesHidingVisualFeedback;
    protected Context mPluginContext;
    protected final OsloGestureConfig mReachGestureConfig;
    protected boolean mReachRegistered;
    private boolean mRegisteredListeners;
    private OsloSounds mSounds;

    public View mStatusBar;
    protected OverlayPlugin.Callback mStatusBarCallback;
    private View mStatusBarOverlay;
    private StatusBarStateController mStatusBarStateController;
    private final StatusBarStateController.StateListener mStatusBarStateListener;
    protected final OsloServiceManager.StatusListener mStatusListener;

    public List<StatusListener> mStatusListeners;
    protected Context mSysuiContext;
    private BroadcastReceiver mTestGestureReceiver;

    public boolean mUseGlow = true;
    private boolean IS_DEBUGGABLE = true;
    protected WindowManager mWindowManager;

    public class Minimizer {
        private final int mDisplayId;
        private OsloInputEventReceiver mInputEventReceiver;
        private InputMonitor mInputMonitor;

        class OsloInputEventReceiver extends InputEventReceiver {
            OsloInputEventReceiver(InputChannel channel, Looper looper) {
                super(channel, looper);
            }

            public void onInputEvent(InputEvent event) {
                if ((event instanceof MotionEvent) && ((MotionEvent) event).getActionMasked() == 0) {
                    if (OsloOverlay.this.mOsloFeedbackView != null) {
                        OsloOverlay.this.mOsloFeedbackView.onMinimize(true);
                    }
                    if (!(OsloOverlay.this.mOsloOnboarding == null || OsloOverlay.this.mOsloOnboarding.getVisibility() == View.GONE)) {
                        incrementTouchPref(OsloOverlay.this.mActiveFlickSubscriber);
                        OsloOverlay.this.mOsloOnboarding.hide(true, 5);
                    }
                }
                finishInputEvent(event, true);
            }
        }

        public Minimizer(Context context) {
            mDisplayId = context.getDisplayId();
        }

        public void addInteractionListeners() {
            if (mInputMonitor == null || mInputEventReceiver == null) {
                removeInteractionListeners();
                mInputMonitor = InputManager.getInstance().monitorGestureInput("oslo-minimizer", mDisplayId);
                mInputEventReceiver = new OsloInputEventReceiver(mInputMonitor.getInputChannel(), Looper.getMainLooper());
                forceShowStatusBar(true);
            }
        }

        public void removeInteractionListeners() {
            OsloInputEventReceiver osloInputEventReceiver = mInputEventReceiver;
            if (osloInputEventReceiver != null) {
                osloInputEventReceiver.dispose();
                mInputEventReceiver = null;
                forceShowStatusBar(false);
            }
            InputMonitor inputMonitor = mInputMonitor;
            if (inputMonitor != null) {
                inputMonitor.dispose();
                mInputMonitor = null;
            }
        }

        private void forceShowStatusBar(boolean forceNotFullScreen) {
            if (forceNotFullScreen) {
                updateHoldStatusBarOpen(true);
                return;
            }
            if (!(OsloOverlay.this.mOsloOnboarding == null || OsloOverlay.this.mOsloOnboarding.getVisibility() == View.GONE)) {
                OsloOverlay.this.mOsloOnboarding.hide(true, 6);
            }
            closeStatusBarWithDelay( AnimationTimes.Scale.SCALE_OUT_TIME);
        }
    }

    public interface StatusListener {
        void onActiveFlickSubscriberChanged(String str);
    }

    public OsloOverlay() {
        OsloGestureConfig osloGestureConfig = new OsloGestureConfig(OSLO_ACTION_NAME, VIS_FEEDBACK_REACH_SENSITIVITY, 1, 1, 1);
        mReachGestureConfig = osloGestureConfig;
        mDestroyed = false;
        mPackagesHidingVisualFeedback = new HashSet();
        mGatingReason = 0;
        FLICK_LEFT = "FLICKL";
        FLICK_RIGHT = "FLICKR";
        ENGAGED = "ENGAGED";
        REACH = "REACH";
        OMNISWIPE = "OMNI";
        ASLEEP = "ASLEEP";
        HIDE = "HIDE";
        LINE = "LINE";
        DISENGAGED = "DISENGAGED";
        RESET_PREFS = "RESET_PREFS";
        REACH_OUT = "REACH_OUT";
        GLOW = "GLOW";
        SCALE_ANIMATION_TIME = "TIME";
        mStatusListeners = new ArrayList();
        mActiveFlickSubscriber = null;
        mReachRegistered = false;
        mRegisteredListeners = false;
        mStatusBarStateListener = new StatusBarStateController.StateListener() {
            @Override
            public void onStatePreChange(int oldState, int newState) {

            }

            @Override
            public void onStatePostChange() {

            }

            @Override
            public void onStateChanged(int newState) {

            }

            @Override
            public void onDozingChanged(boolean isDozing) {

            }

            @Override
            public void onDozeAmountChanged(float linear, float eased) {

            }

            public void onSystemUiVisibilityChanged(int visibility) {
                boolean unused = OsloOverlay.this.mFullScreen = OsloOverlay.isFullScreen(visibility);
            }

            @Override
            public void onPulsingChanged(boolean pulsing) {

            }
        };

        this.mGestureCallback = new OsloFeedbackView.Callback() {
            public void onAnimationEnd() {
                if (OsloOverlay.this.mIsOsloEnabled) {
                    OsloOverlay osloOverlay = OsloOverlay.this;
                    osloOverlay.hideOsloFeedbackView(osloOverlay.mFullScreen ? null : OsloOverlay.this.mActiveFlickSubscriber);
                    return;
                }
                OsloOverlay.this.removeViews();
            }
        };
        this.mTestGestureReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    OsloOverlay.this.mMainThreadHandler.post( new Runnable(  ) {
                        private final Intent f$1;

                        {
                            Intent r2 = null;
                            this.f$1 = r2;
                        }

                        public final void run() {
                            //  this.lambda$onReceive$0$OsloOverlay$3( this.f$1 );
                        }
                    } );
                }
            }
        };

        this.mCloseStatusBar = new Runnable() {
            public final void run() {
                OsloOverlay.this.lambda$new$0$OsloOverlay();
            }
        };


        this.mGestureListenerFlickEcho = new GestureListener() {
            public void onGestureDetected(Bundle gestureOutput) {
                OsloOverlay.this.mMainThreadHandler.post(new Runnable() {
                    private final Bundle f$1;
                    private GestureListener gestureOutput;

                    {
                        Bundle r2 = null;
                        this.f$1 = r2;                    }

                    public final void run() {
                        onGestureDetectedOslo4(this.f$1);
                    }
                });
            };

            public void onGestureDetectedOslo4(Bundle gestureOutput) {
                OsloFlickOutput flickOutput = new OsloFlickOutput(gestureOutput);
                if (OsloOverlay.DEBUG) {
                    Log.v(OsloOverlay.TAG, flickOutput.toString());
                }

                if (OsloOverlay.this.mActiveFlickSubscriber != null && !OsloOverlay.this.mPackagesHidingVisualFeedback.contains(OsloOverlay.this.mActiveFlickSubscriber)) {
                    if (OsloOverlay.this.mOsloFeedbackView == null) {
                        Log.w(OsloOverlay.TAG, "Received flick gesture but OsloFeedbackView is null");
                        return;
                    }
                    OsloOverlay osloOverlay = OsloOverlay.this;
                    osloOverlay.showOsloFeedbackView(osloOverlay.mActiveFlickSubscriber);
                    int direction = flickOutput.getDirection();
                    if (direction != 0) {
                        if (!(direction == 1 || direction == 2)) {
                            if (direction == 4 || direction == 5 || direction == 6) {
                                OsloOverlay.this.mOsloFeedbackView.onFlickLeft(flickOutput.getDetected());
                                OsloOverlay osloOverlay2 = OsloOverlay.this;
                                osloOverlay2.incrementGesturePref(osloOverlay2.mActiveFlickSubscriber);
                                return;
                            } else if (direction != 8) {
                                return;
                            }
                        }
                        OsloOverlay.this.mOsloFeedbackView.onFlickRight(flickOutput.getDetected());
                        OsloOverlay osloOverlay3 = OsloOverlay.this;
                        osloOverlay3.incrementGesturePref(osloOverlay3.mActiveFlickSubscriber);
                        return;
                    }
                    OsloOverlay.this.mOsloFeedbackView.onOmniswipe(flickOutput.getDetected());
                    OsloOverlay osloOverlay4 = OsloOverlay.this;
                    osloOverlay4.incrementGesturePref(osloOverlay4.mActiveFlickSubscriber);
                }

            }
        };
        this.mGestureListenerReachEcho = new GestureListener() {
            public void onGestureDetected(Bundle gestureOutput) {
                OsloOverlay.this.mMainThreadHandler.post(new Runnable() {
                    private final Bundle f$1;

                    {
                        Bundle r2 = null;
                        this.f$1 = r2;                      }

                    public final void run() {
                        onGestureDetectedOslo(this.f$1);
                    }
                });
            }

            public void onGestureDetectedOslo(Bundle gestureOutput) {
                OsloReachOutput reachOutput = new OsloReachOutput(gestureOutput);
                if (OsloOverlay.DEBUG) {
                    Log.v(OsloOverlay.TAG, reachOutput.toString());
                }
                if (OsloOverlay.this.mOsloFeedbackView == null) {
                    Log.w(OsloOverlay.TAG, "Received reach gesture but OsloFeedbackView is null");
                } else if (OsloOverlay.this.mActiveFlickSubscriber != null) {
                    // OsloOverlay.this.mOsloFeedbackView.onReach(reachOutput.getDetected());
                }
            }
        };
        this.mStatusListener = new OsloServiceManager.StatusListener() {
            public void onStatusChanged(Bundle statusOutput) {
                boolean post = OsloOverlay.this.mMainThreadHandler.post( new Runnable( ) {
                    private final Bundle f$1;

                    {
                        Bundle statusOutput = null;
                        this.f$1 = statusOutput;
                    }

                    public final void run() {
//                        lambda$onStatusChanged$0$OsloOverlay$6( this.f$1 );
                    }
                } );
            }

            public void lambda$onStatusChanged$0$OsloOverlay$6(Bundle statusOutput) {
                OsloStatusOutput osloStatusOutput = new OsloStatusOutput(statusOutput);
                int gatingReason = osloStatusOutput.getGatingReason();
                OsloOverlay.this.updateActiveSubscribers(osloStatusOutput, gatingReason == 0 ? 1 : gatingReason, osloStatusOutput.isEnabled());
                if (OsloOverlay.DEBUG) {
                    Log.v(OsloOverlay.TAG, osloStatusOutput.toString());
                }
            }
        };
        this.mGestureListenerReach = new GestureListener() {
            public void onGestureDetected(Bundle gestureOutput) {
            }
        };
    }

    public void showOsloFeedbackView(String flickSubscriber) {
        boolean showViews = flickSubscriber != null;
        if (DEBUG) {
            Log.d(TAG, "showOsloFeedbackView showViews=" + showViews + " mOsloFeedbackView=" + mOsloFeedbackView);
        }
        if (showViews && mOsloFeedbackView != null) {
            updateHoldStatusBarOpen(showViews);
            setFeedbackViewVisibility(0);
        }
    }


    public void hideOsloFeedbackView(String flickSubscriber) {
        boolean showViews = flickSubscriber != null;
        if (DEBUG) {
            Log.d(TAG, "hideOsloFeedbackView showViews=" + showViews);
        }
        if (!showViews) {
            updateHoldStatusBarOpen(showViews);
            setFeedbackViewVisibility(8);
            OsloOnboarding osloOnboarding = mOsloOnboarding;
            if (osloOnboarding != null) {
                osloOnboarding.hide(true, 1);
            }
        }
    }

    private void setFeedbackViewVisibility(int visibility) {
        OsloFeedbackView osloFeedbackView = mOsloFeedbackView;
        if (osloFeedbackView != null) {
            osloFeedbackView.setVisibility(visibility);
        }
        OsloSounds osloSounds = mSounds;
        if (osloSounds == null) {
            return;
        }
        if (visibility == 0) {
            osloSounds.showFeedback();
        } else {
            osloSounds.hideFeedback();
        }
    }

    public  void lambda$new$0$OsloOverlay() {
        updateHoldStatusBarOpen(false);
    }


    public void closeStatusBarWithDelay(long delay) {
        mMainThreadHandler.removeCallbacks(mCloseStatusBar);
        mMainThreadHandler.postDelayed(mCloseStatusBar, delay);
    }


    public void updateHoldStatusBarOpen(boolean holdStatusBarOpen) {
        mMainThreadHandler.removeCallbacks(mCloseStatusBar);
        if (mHoldStatusBarOpen != holdStatusBarOpen) {
            mHoldStatusBarOpen = holdStatusBarOpen;
            mStatusBarCallback.onHoldStatusBarOpenChange();
            if (DEBUG) {
                Log.d(TAG, " mHoldStatusBarOpen=" + mHoldStatusBarOpen);
            }
        }
    }

    public boolean holdStatusBarOpen() {
        return mHoldStatusBarOpen;
    }

    public void setCollapseDesired(boolean collapseDesired) {
        mCollapseDesired = collapseDesired;
        OsloFeedbackView osloFeedbackView = mOsloFeedbackView;
        if (osloFeedbackView != null) {
            osloFeedbackView.setCollapseDesired(mCollapseDesired);
        }
        OsloOnboarding osloOnboarding = mOsloOnboarding;
        if (osloOnboarding != null) {
            osloOnboarding.setCollapseDesired(mCollapseDesired);
        }
    }

    private boolean registerListener(StatusListener listener) {
        mStatusListeners.add(listener);
        return true;
    }

    private boolean unregisterListener(StatusListener listener) {
        return mStatusListeners.remove(listener);
    }


    public void updateActiveSubscribers(OsloStatusOutput osloStatusOutput, int gating, boolean enabled) {
        String flickSubscriber = osloStatusOutput.getStatusReportDataElement(1).getActiveSubscriberId();
        String reachSubscriber = osloStatusOutput.getStatusReportDataElement(4).getActiveSubscriberId();
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("gating=");
            sb.append(gating);
            sb.append(" activeFlickSubscriber=");
            sb.append(flickSubscriber);
            sb.append(" activeReachSubscriber=");
            sb.append(reachSubscriber);
            sb.append(" isEnabled=");
            sb.append(enabled);
            sb.append(" prevFlickSubscriber=");
            sb.append(mActiveFlickSubscriber);
            sb.append(" prevGatingReason=");
            sb.append(mGatingReason);
            sb.append(" prevIsEnabled=");
            sb.append(mIsOsloEnabled);
            sb.append(" mOsloFeedbackView=");
            sb.append(mOsloFeedbackView);
            sb.append(" isMinimized=");
            OsloFeedbackView osloFeedbackView = mOsloFeedbackView;
            sb.append(osloFeedbackView != null ? Boolean.valueOf(osloFeedbackView.isMinimized()) : "null");
            Log.d(TAG, sb.toString());
        }
        if (mPackagesHidingVisualFeedback.contains(flickSubscriber) || !enabled) {
            flickSubscriber = null;
        }
        if (mOsloFeedbackView == null) {
            if (!enabled || mDestroyed) {
                mActiveFlickSubscriber = null;
                mGatingReason = gating;
                mIsOsloEnabled = enabled;
                return;
            }
            addViews();
        }
        showOsloFeedbackView(flickSubscriber);
        for (StatusListener listener : mStatusListeners) {
            listener.onActiveFlickSubscriberChanged(flickSubscriber);
        }
        OsloFeedbackView osloFeedbackView2 = mOsloFeedbackView;
        if (osloFeedbackView2 == null) {
            Log.w(TAG, "OsloFeedbackView is null. enabled=" + enabled + " mDestroyed=" + mDestroyed + " mStatusBar=" + mStatusBar);
            return;
        }
        osloFeedbackView2.setEnabled(enabled);
        boolean registerReach = !mOsloFeedbackView.isMinimized() && !isAsleep(gating) && flickSubscriber != null;
        if (!enabled || flickSubscriber == null) {
            mOsloFeedbackView.onDisengaged();
        } else if (gating == 1 && isNewlyActive(flickSubscriber, gating)) {
            mOsloFeedbackView.onEngaged();
            registerReach = true;
        } else if (isAsleep(gating)) {
            mOsloFeedbackView.onAsleep();
        }
        if (registerReach) {
            registerReach();
        } else {
            unregisterReach();
        }
        mActiveFlickSubscriber = flickSubscriber;
        mGatingReason = gating;
        mIsOsloEnabled = enabled;
    }

    private boolean isNewlyActive(String newFlickSubscriber, int newGatingReason) {
        boolean shouldBeUnminimized = mOsloFeedbackView.isMinimized() && (Objects.equals(newFlickSubscriber, mActiveFlickSubscriber) ^ true) && !isAsleep(mGatingReason);
        boolean hasNewSubscriber = mActiveFlickSubscriber == null && newFlickSubscriber != null;
        boolean isNewlyUngated = isAsleep(mGatingReason) && !isAsleep(newGatingReason) && !mOsloFeedbackView.isMinimized();
        if (DEBUG) {
            Log.d(TAG, "hasNewSubscriber=" + hasNewSubscriber + " isNewlyUngated=" + isNewlyUngated + " shouldBeUnminimized=" + shouldBeUnminimized);
        }
        if (hasNewSubscriber || isNewlyUngated || shouldBeUnminimized) {
            return true;
        }
        return false;
    }

    private boolean isAsleep(int gating) {
        return gating == 3 || gating == 2;
    }

    @Override
    public int getVersion() {
        return 4;
    }

    public void onCreate(Context sysuiContext, Context pluginContext) {
        if (DEBUG) {
            Log.d(TAG, "onCreate");
        }
        mPluginContext = pluginContext;
        mSysuiContext = sysuiContext;
        mPackagesHidingVisualFeedback = new HashSet(Arrays.asList(mPluginContext.getResources().getStringArray(R.array.config_visFeedbackBlacklist)));
        mWindowManager = (WindowManager) mSysuiContext.getSystemService(Context.WINDOW_SERVICE);
        mMainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public void setup(View statusBar, View navBar) {
    }

    public void setup(View statusBar, View navBar, OverlayPlugin.Callback callback, DozeParameters dozeParams) {
        if (DEBUG) {
            Log.d(TAG, "setup mUseGlow=" + mUseGlow);
        }
        mStatusBar = statusBar;
        mDozeParams = dozeParams;
        mStatusBarCallback = callback;
        mMinimizer = new Minimizer(mSysuiContext);
//        addViews();
        enableOsloFeedbackViaAdb();
        mOsloServiceManager = new OsloServiceManager(mPluginContext, new Runnable() {
            public final void run() {
                lambda$setup$1$OsloOverlay();
            }
        });
        registerListeners();
    }

    public void lambda$setup$1$OsloOverlay() {
        mRegisteredListeners = false;
        registerListeners();
    }

    private void enableOsloFeedbackViaAdb() {
        if (IS_DEBUGGABLE && ENABLE_OSLOFEEDBACK_VIA_ADB) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("FLICKL");
            filter.addAction("ENGAGED");
            filter.addAction("DISENGAGED");
            filter.addAction("REACH");
            filter.addAction("REACH_OUT");
            filter.addAction("FLICKR");
            filter.addAction("HIDE");
            filter.addAction("OMNI");
            filter.addAction("ASLEEP");
            filter.addAction("LINE");
            filter.addAction("RESET_PREFS");
            filter.addAction("GLOW");
            filter.addAction("TIME");
            mPluginContext.registerReceiver(mTestGestureReceiver, filter);
            mAdbCommandsRegistered = true;
        }
    }

    private void registerListeners() {
        mRegisteredListeners = true;
        mOsloServiceManager.registerListener(mGestureListenerFlickEcho, 2, new OsloGestureConfig(OSLO_ACTION_NAME, 0.0f, 0, 0));
        mOsloServiceManager.registerListener(mGestureListenerReachEcho, 5, new OsloGestureConfig(OSLO_ACTION_NAME, 0.0f, 0, 0));
        mOsloServiceManager.registerListener(mStatusListener, 6, new OsloGestureConfig(OSLO_ACTION_NAME, 0.0f, 0, 0));
    }

    public void onDestroy() {
        mMainThreadHandler.post(new Runnable() {
            public final void run() {
//                lambda$onDestroy$2$OsloOverlay();
            }
        });
    }

    public void lambda$onDestroy$2$OsloOverlay() {
        if (DEBUG) {
            Log.d(TAG, "onDestroy mRegisteredListeners=" + mRegisteredListeners);
        }
        mDestroyed = true;
        if (mRegisteredListeners) {
            mRegisteredListeners = false;
            if (mPluginContext != null && IS_DEBUGGABLE && mAdbCommandsRegistered) {
                mPluginContext.unregisterReceiver(mTestGestureReceiver);
                mAdbCommandsRegistered = false;
            }
            mOsloServiceManager.unregisterListener(mGestureListenerFlickEcho);
            mOsloServiceManager.unregisterListener(mGestureListenerReachEcho);
            mOsloServiceManager.unregisterListener(mStatusListener);
            mOsloServiceManager.unbindFromService();
        }
        unregisterReach();
        removeViews();
    }


    public void removeViews() {
        if (DEBUG) {
            Log.d(TAG, "removeViews");
        }
        updateHoldStatusBarOpen(false);
        OsloSounds osloSounds = mSounds;
        if (osloSounds != null) {
            osloSounds.onDestroy();
            mSounds = null;
        }
        OsloFeedbackView osloFeedbackView = mOsloFeedbackView;
        if (osloFeedbackView != null) {
            if (mUseGlow) {
                ShaderView glow = ((GlowFeedbackView) osloFeedbackView).getView();
                removeView((ViewGroup) glow.getParent(), glow);
            }
            removeView((ViewGroup) mOsloFeedbackView.getParent(), mOsloFeedbackView);
            unregisterListener(mOsloFeedbackView);
            mOsloFeedbackView = null;
        }
        OsloOnboarding osloOnboarding = mOsloOnboarding;
        if (osloOnboarding != null) {
            unregisterListener(osloOnboarding);
            removeView((ViewGroup) mOsloOnboarding.getParent(), mOsloOnboarding);
            removeView((ViewGroup) mOsloOnboarding.getView().getParent(), mOsloOnboarding.getView());
            mOsloOnboarding = null;
        }
        Minimizer minimizer = mMinimizer;
        if (minimizer != null) {
            minimizer.removeInteractionListeners();
        }
        mStatusBarStateController.removeCallback(mStatusBarStateListener);
    }

    private void removeView(ViewGroup parentView, View view) {
        if (DEBUG) {
            Log.d(TAG, "removeView parentView=" + parentView + " view=" + view);
        }
        if (parentView != null) {
            parentView.removeView(view);
        }
    }

    private void addViews() {
        if (!(mStatusBar instanceof ViewGroup)) {
            return;
        }
        if (mDestroyed) {
            Log.w(TAG, "This OverlayPlugin was destroyed. mDestroyed=" + mDestroyed + " Don't add views or register listeners.");
            return;
        }
        try {
            mDarkIconDispatcher = (DarkIconDispatcher) PluginDependency.get(this, DarkIconDispatcher.class);
            mStatusBarStateController = (StatusBarStateController) PluginDependency.get(this, StatusBarStateController.class);
            ViewGroup statusBarView = (ViewGroup) mStatusBar;
            if (mUseGlow) {
                mSounds = new OsloSounds(mPluginContext, mSysuiContext);
                mOsloFeedbackView = new GlowFeedbackView(mSysuiContext, mPluginContext, mDarkIconDispatcher, mStatusBarStateController, mCollapseDesired, mMinimizer, mSounds, mStatusBar);
            }
            OsloOnboarding osloOnboarding = new OsloOnboarding(mSysuiContext, mPluginContext, mDarkIconDispatcher, mStatusBarStateController, mCollapseDesired, statusBarView);
            mOsloOnboarding = osloOnboarding;
            registerListener(mOsloOnboarding);
            registerListener(mOsloFeedbackView);
            mOsloFeedbackView.setDisengagedCallback(mGestureCallback);
            hideOsloFeedbackView((String) null);
            if (mUseGlow) {
                statusBarView.addView(((GlowFeedbackView) mOsloFeedbackView).getView(), 1);
            }
            statusBarView.addView(mOsloFeedbackView, 1);
            statusBarView.addView(mOsloOnboarding);
            mStatusBarStateController.addCallback(mStatusBarStateListener);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "This OverlayPlugin was destroyed. Don't add views or register listeners.");
        }
    }

    public static String getGestureCountKey(String id) {
        if (id == null) {
            return null;
        }
        char c = 65535;
        switch (id.hashCode()) {
            case -1448737948:
                if (id.equals(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_DISMISS_TIMER)) {
                    c = 3;
                    break;
                }
                break;
            case -1254455797:
                if (id.equals(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SILENCE_RINGER)) {
                    c = 1;
                    break;
                }
                break;
            case -1059149585:
                if (id.equals(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SKIP_MEDIA)) {
                    c = 0;
                    break;
                }
                break;
            case 1529572962:
                if (id.equals(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SNOOZE_ALARM)) {
                    c = 2;
                    break;
                }
                break;
        }
        if (c == 0) {
            return Prefs.Key.SKIP_SONG_GESTURE_COUNT;
        }
        if (c == 1) {
            return Prefs.Key.MUTE_CALL_GESTURE_COUNT;
        }
        if (c == 2) {
            return Prefs.Key.SNOOZE_ALARM_GESTURE_COUNT;
        }
        if (c != 3) {
            return null;
        }
        return Prefs.Key.DISMISS_TIMER_GESTURE_COUNT;
    }

    public static String getTouchCountKey(String id) {
        if (id == null) {
            return null;
        }
        char c = 65535;
        switch (id.hashCode()) {
            case -1448737948:
                if (id.equals(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_DISMISS_TIMER)) {
                    c = 3;
                    break;
                }
                break;
            case -1254455797:
                if (id.equals(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SILENCE_RINGER)) {
                    c = 1;
                    break;
                }
                break;
            case -1059149585:
                if (id.equals(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SKIP_MEDIA)) {
                    c = 0;
                    break;
                }
                break;
            case 1529572962:
                if (id.equals(OsloStrings.OsloBuiltInActionIds.CONFIG_ID_SNOOZE_ALARM)) {
                    c = 2;
                    break;
                }
                break;
        }
        if (c == 0) {
            return Prefs.Key.SKIP_SONG_TOUCH_COUNT;
        }
        if (c == 1) {
            return Prefs.Key.MUTE_CALL_TOUCH_COUNT;
        }
        if (c == 2) {
            return Prefs.Key.SNOOZE_ALARM_TOUCH_COUNT;
        }
        if (c != 3) {
            return null;
        }
        return Prefs.Key.DISMISS_TIMER_TOUCH_COUNT;
    }


    public void incrementGesturePref(String actionId) {
        String gestureCountKey = getGestureCountKey(actionId);
        if (gestureCountKey != null) {
            Context context = mPluginContext;
            Prefs.putInt(context, gestureCountKey, Prefs.getInt(context, gestureCountKey, 0) + 1);
        } else if (DEBUG) {
            Log.d(TAG, "No contextual tip associated with this actionId=" + actionId);
        }
    }


    public void incrementTouchPref(String actionId) {
        String gestureCountKey = getTouchCountKey(actionId);
        if (gestureCountKey != null) {
            Context context = mPluginContext;
            Prefs.putInt(context, gestureCountKey, Prefs.getInt(context, gestureCountKey, 0) + 1);
        } else if (DEBUG) {
            Log.d(TAG, "No contextual tip associated with this actionId=" + actionId);
        }
    }

    private void registerReach() {
        if (!mReachRegistered) {
            mReachRegistered = true;
            mOsloServiceManager.registerListener(mGestureListenerReach, 4, mReachGestureConfig);
        }
    }

    private void unregisterReach() {
        if (mReachRegistered) {
            mReachRegistered = false;
            mOsloServiceManager.unregisterListener(mGestureListenerReach);
        }
    }

    public static boolean shouldControlScreenOff() {
        if (DEBUG) {
            Log.d(TAG, "shouldControlScreenOff=" + mDozeParams.shouldControlScreenOff());
        }
        return mDozeParams.shouldControlScreenOff();
    }

    public static boolean isFullScreen(int systemUiVisibility) {
        return (systemUiVisibility & 6150) != 0;
    }
}

