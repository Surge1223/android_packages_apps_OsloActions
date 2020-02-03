package com.google.oslo.service.actions;

import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.util.Log;
import java.util.List;

public class AudioUtils {
    private static final boolean DEBUG = true;
    private static final String TAG = "OsloAudioUtils";
    private static final float VOLUME_SCALE_FACTOR = 0.125f;

    public static void attenuateVolume(AudioManager audioManager) {
        attenuateVolume(audioManager, VOLUME_SCALE_FACTOR);
    }

    public static void attenuateVolume(AudioManager audioManager, float scaleFactor) {
        List<AudioPlaybackConfiguration> configs = audioManager.getActivePlaybackConfigurations();
        for (int i = 0; i < configs.size(); i++) {
            AudioAttributes audioAttributes = configs.get(i).getAudioAttributes();
            int audioUsage = audioAttributes.getUsage();
            if ((audioUsage == 4 || audioUsage == 6) && audioAttributes.getContentType() == 4) {
                configs.get(i).getPlayerProxy().setVolume(scaleFactor);
                Log.d(TAG, "Attenuated volume");
            }
        }
    }

    public static boolean isMusicPlaying(List<AudioPlaybackConfiguration> configs) {
        for (int i = 0; i < configs.size(); i++) {
            AudioPlaybackConfiguration config = configs.get(i);
            int playerType = config.getPlayerType();
            boolean isActive = config.isActive();
            int usage = config.getAudioAttributes().getUsage();
            int contentType = config.getAudioAttributes().getContentType();
            if (isActive && playerType != 3 && usage == 1 && contentType == 2) {
                return DEBUG;
            }
        }
        return false;
    }

    public static boolean isLocalSpeakerActive(AudioManager audioManager, List<AudioPlaybackConfiguration> configs) {
        for (AudioPlaybackConfiguration config : configs) {
            if (config.isActive()) {
                for (AudioDeviceInfo adi : audioManager.getDevices(2)) {
                    if (adi.getType() == 8 || adi.getType() == 22) {
                        Log.d(TAG, "not reporting audio playing; found type " + adi.getType());
                        return false;
                    }
                }
                return DEBUG;
            }
        }
        return false;
    }
}
