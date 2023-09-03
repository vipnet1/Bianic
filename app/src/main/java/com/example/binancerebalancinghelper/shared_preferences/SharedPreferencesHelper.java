package com.example.binancerebalancinghelper.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;
import com.example.binancerebalancinghelper.shared_preferences.exceptions.KeyNotFoundException;

public class SharedPreferencesHelper {
    private static SharedPreferences regularPrefs = null;
    protected SharedPreferences usedPrefsRef;

    protected SharedPreferencesHelper() {
        usedPrefsRef = null;
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

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = usedPrefsRef.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return usedPrefsRef.getInt(key, defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return usedPrefsRef.getString(key, defaultValue);
    }

    public String getString(String key) throws KeyNotFoundException {
        if(!usedPrefsRef.contains(key)) {
            throw new KeyNotFoundException(key);
        }

        return usedPrefsRef.getString(key, "");
    }
}
