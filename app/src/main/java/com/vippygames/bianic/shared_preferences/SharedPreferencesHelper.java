package com.vippygames.bianic.shared_preferences;


import android.content.Context;
import android.content.SharedPreferences;

import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.shared_preferences.exceptions.KeyNotFoundException;

public class SharedPreferencesHelper {
    protected final Context context;
    private static SharedPreferences regularPrefs = null;
    protected SharedPreferences usedPrefsRef;

    public SharedPreferencesHelper(Context context) {
        this.context = context;
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

    public void setFloat(String key, float value) {
        SharedPreferences.Editor editor = usedPrefsRef.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = usedPrefsRef.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public int getInt(String key) throws KeyNotFoundException {
        if (!usedPrefsRef.contains(key)) {
            throw new KeyNotFoundException(context, key);
        }

        return usedPrefsRef.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return usedPrefsRef.getInt(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return usedPrefsRef.getFloat(key, defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return usedPrefsRef.getString(key, defaultValue);
    }

    public String getString(String key) throws KeyNotFoundException {
        if (!usedPrefsRef.contains(key)) {
            throw new KeyNotFoundException(context, key);
        }

        return usedPrefsRef.getString(key, "");
    }

    public void deleteKeyIfExists(String key) {
        if (!usedPrefsRef.contains(key)) {
            return;
        }

        SharedPreferences.Editor editor = usedPrefsRef.edit();
        editor.remove(key);
        editor.apply();
    }
}
