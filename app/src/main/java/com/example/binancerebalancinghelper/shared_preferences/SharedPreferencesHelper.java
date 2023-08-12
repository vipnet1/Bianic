package com.example.binancerebalancinghelper.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;

public class SharedPreferencesHelper {
    private static SharedPreferences regularPrefs = null;
    protected SharedPreferences usedPrefsRef = null;

    protected SharedPreferencesHelper() {
    }

    public SharedPreferencesHelper(Context context) {
        if (regularPrefs == null) {
            regularPrefs = context.getSharedPreferences(SharedPrefsConsts.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        }

        usedPrefsRef = regularPrefs;
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = usedPrefsRef.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return usedPrefsRef.getInt(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = usedPrefsRef.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return usedPrefsRef.getBoolean(key, defaultValue);
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = usedPrefsRef.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return usedPrefsRef.getString(key, defaultValue);
    }

    public void clear() {
        SharedPreferences.Editor editor = usedPrefsRef.edit();
        editor.clear();
        editor.apply();
    }
}
