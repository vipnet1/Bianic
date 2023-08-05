package com.example.binancerebalancinghelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;

public class SharedPreferencesHelper {
    private static SharedPreferences prefs = null;

    public SharedPreferencesHelper(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(SharedPrefsConsts.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        }
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }
}
