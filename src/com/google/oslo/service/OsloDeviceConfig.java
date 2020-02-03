package com.google.oslo.service;

import android.provider.DeviceConfig;
import android.text.TextUtils;
import java.util.Arrays;
import java.util.HashSet;

public class OsloDeviceConfig {
    public static final String DEFAULT_MCC_WHITELIST = "as,au,at,be,bg,ca,hr,cy,cz,dk,ee,fi,fr,gf,pf,de,gr,gp,gu,hu,ie,it,kr,lv,lt,lu,mt,mq,yt,nl,nc,mp,no,pl,pt,pr,ro,re,bl,mf,pm,sg,sk,si,es,se,ch,tw,ae,gb,us,vi,wf";
    public static final String DEFAULT_MEDIA_APP_WHITELIST = "com.amazon.mp3,com.anghami,com.apple.android.music,com.aspiro.tidal,com.bsbportal.music,com.clearchannel.iheartradio.controller,com.ezpeer.ezpeerplus.v4,com.gaana,com.google.android.apps.youtube.music,com.google.android.music,com.hungama.myplay.activity,com.jio.media.jiobeats,com.ktmusic.geniemusic,com.neowiz.android.bugs,com.pandora.android,com.rhapsody,com.shazam.android,com.sirius,com.skysoft.kkbox.android,com.spotify.music,com.spotify.zerotap,deezer.android.app,fm.awa.liverpool";
    public static final String FLAG_MCC_LIST = "mcc_whitelist";
    public static final String FLAG_MEDIA_APP_LIST = "media_app_whitelist";
    public static final String NAMESPACE = "oslo";

    public static String getMccList() {
        return DeviceConfig.getString(NAMESPACE, FLAG_MCC_LIST, DEFAULT_MCC_WHITELIST);
    }

    public static String getMediaAppList() {
        return DeviceConfig.getString(NAMESPACE, FLAG_MEDIA_APP_LIST, DEFAULT_MEDIA_APP_WHITELIST);
    }

    public static HashSet<String> createSetFromString(String list) {
        String[] stringArray = new String[0];
        if (!TextUtils.isEmpty(list)) {
            stringArray = list.split(",");
        }
        return new HashSet<>(Arrays.asList(stringArray));
    }
}
