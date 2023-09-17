package com.vippygames.bianic.utils;

import android.content.Context;

import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.shared_preferences.SharedPreferencesHelper;

/*
There can be a situation when after boot user plays with settings, activates/deactivates rebalance check,
changing validation time and more before boot receiver receives event. The aim of this class to help handle those cases.
The idea is that once boot receiver or user made some related to rebalance check activation action we set time of last
boot in sp key - and we can check from other places whether we handled first action after boot or not yet.
 */
public class BootUtils {
    private static final long REBOOT_TIMES_ALLOWED_DIFFERENCE = 5000;

    private final Context context;

    public BootUtils(Context context) {
        this.context = context;
    }

    public boolean haveCurrentBootTime() {
        long lastBootTime = getLastBootTime();

        SharedPreferencesHelper sp = new SharedPreferencesHelper(context);
        String storedLastBootTime = sp.getString(SharedPrefsConsts.LAST_BOOT_TIME, "0");
        long longStoredLastBootTime = Long.parseLong(storedLastBootTime);

        // in case one of them moved by a bit accidentally give them up to some millis can be different
        return Math.abs(lastBootTime - longStoredLastBootTime) < REBOOT_TIMES_ALLOWED_DIFFERENCE;
    }

    public void setCurrentBootTime() {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(context);
        sp.setString(SharedPrefsConsts.LAST_BOOT_TIME, String.valueOf(getLastBootTime()));
    }

    private long getLastBootTime() {
        return System.currentTimeMillis() - android.os.SystemClock.elapsedRealtime();
    }
}
