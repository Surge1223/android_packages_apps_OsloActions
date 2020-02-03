package com.google.oslo.service;

import android.content.Context;
import com.google.oslo.service.actions.Action;
import com.google.oslo.service.actions.AttenuateAlarmVolume;
import com.google.oslo.service.actions.AttenuateRingerVolume;
import com.google.oslo.service.actions.AttenuateTimerVolume;
import com.google.oslo.service.actions.DismissTimer;
import com.google.oslo.service.actions.SilenceRinger;
import com.google.oslo.service.actions.SkipMediaTrack;
import com.google.oslo.service.actions.SnoozeAlarm;
import com.google.oslo.service.serviceinterface.OsloServiceManager;
import java.util.ArrayList;
import java.util.List;

public class OsloBuiltInActions {
    private static final String TAG = "Oslo/OsloBuiltInActions";
    private final List<Action> mBuiltInActions = new ArrayList();
    private final Context mContext;
    private final OsloServiceManager mOsloServiceManager;

    public OsloBuiltInActions(Context context) {
        this.mContext = context;
        this.mOsloServiceManager = new OsloServiceManager(context);
        this.mBuiltInActions.add(new AttenuateAlarmVolume(context, this.mOsloServiceManager));
        this.mBuiltInActions.add(new AttenuateRingerVolume(context, this.mOsloServiceManager));
        this.mBuiltInActions.add(new AttenuateTimerVolume(context, this.mOsloServiceManager));
        this.mBuiltInActions.add(new DismissTimer(context, this.mOsloServiceManager));
        this.mBuiltInActions.add(new SilenceRinger(context, this.mOsloServiceManager));
        this.mBuiltInActions.add(new SkipMediaTrack(context, this.mOsloServiceManager));
        this.mBuiltInActions.add(new SnoozeAlarm(context, this.mOsloServiceManager));
    }
}
