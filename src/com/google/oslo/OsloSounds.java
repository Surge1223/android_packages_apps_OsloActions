package com.google.oslo;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import com.google.oslo.actions.R;
import android.view.accessibility.AccessibilityManager;

public class OsloSounds implements AccessibilityManager.AccessibilityStateChangeListener {
    private final boolean CHECK_TALKBACK = true;
    private final String TAG = "OsloSounds";
    private final AccessibilityManager mAccessibilityManager;
    private final AudioManager mAudioManager;
    public int mDismissSoundId;
    public int mNavigateNextSoundId;
    public int mNavigatePreviousSoundId;
    private final Context mPluginContext;
    private int mSoundStreamId;
    private final int mSoundStreamType;
    private SoundPool mSounds;
    private final Context mSysuiContext;

    public OsloSounds(Context pluginContext, Context sysuiContext) {
        mPluginContext = pluginContext;
        mSysuiContext = sysuiContext;
        mAccessibilityManager = (AccessibilityManager) mPluginContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
        mAudioManager = (AudioManager) mPluginContext.getSystemService(Context.AUDIO_SERVICE);
        mSoundStreamType = mAudioManager.getUiSoundsStreamType();
    }

    public void showFeedback() {
        mAccessibilityManager.addAccessibilityStateChangeListener(this);
        if (mAccessibilityManager.isEnabled()) {
            setupSoundPool();
        }
    }

    public void hideFeedback() {
        mAccessibilityManager.removeAccessibilityStateChangeListener(this);
        releaseSoundPool();
    }

    private void setupSoundPool() {
        if (mSounds == null) {
            mSounds = new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(new AudioAttributes.Builder().setUsage(13).setContentType(4).build()).build();
            mNavigateNextSoundId = mSounds.load(mPluginContext, R.raw.next, 1);
            mNavigatePreviousSoundId = mSounds.load(mPluginContext, R.raw.previous, 1);
            mDismissSoundId = mSounds.load(mPluginContext, R.raw.dismiss, 1);
        }
    }

    private void releaseSoundPool() {
        SoundPool soundPool = mSounds;
        if (soundPool != null) {
            soundPool.release();
            mSounds = null;
        }
    }

    public void onAccessibilityStateChanged(boolean enabled) {
        if (enabled) {
            setupSoundPool();
        } else {
            releaseSoundPool();
        }
    }

    public void playFlickLeftSound() {
        if (Utils.isFlickDirectionRightToLeft(mSysuiContext)) {
            playSound(mNavigateNextSoundId);
        } else {
            playSound(mNavigatePreviousSoundId);
        }
    }

    public void playFlickRightSound() {
        if (Utils.isFlickDirectionRightToLeft(mSysuiContext)) {
            playSound(mNavigatePreviousSoundId);
        } else {
            playSound(mNavigateNextSoundId);
        }
    }

    public void playOmniswipeSound() {
        playSound(mDismissSoundId);
    }

    private boolean isTalkbackEnabled() {
        return true ^ mAccessibilityManager.getEnabledAccessibilityServiceList(1).isEmpty();
    }

    private void playSound(int soundId) {
        if (mSounds != null && isTalkbackEnabled() && !mAudioManager.isStreamMute(mSoundStreamType)) {
            mSounds.stop(mSoundStreamId);
            mSoundStreamId = mSounds.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        } else if (mSounds == null) {
            Log.w("OsloSounds", "cannot play soundId=" + soundId + ". mSounds is null.");
        }
    }

    public void onDestroy() {
        mAccessibilityManager.removeAccessibilityStateChangeListener(this);
        releaseSoundPool();
    }
}

