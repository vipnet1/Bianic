package com.example.binancerebalancinghelper.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;

public class SharedPreferencesHelper {
    protected static SharedPreferences prefs = null;

    protected SharedPreferencesHelper() {}

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

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }
}
