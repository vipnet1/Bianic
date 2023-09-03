package com.example.binancerebalancinghelper.configuration;

import android.content.Context;

import com.example.binancerebalancinghelper.consts.ConfigurationConsts;
import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;
import com.example.binancerebalancinghelper.shared_preferences.EncryptedSharedPreferencesHelper;
import com.example.binancerebalancinghelper.shared_preferences.SharedPreferencesHelper;
import com.example.binancerebalancinghelper.shared_preferences.exceptions.KeyNotFoundException;

public class ConfigurationManager {
    private final SharedPreferencesHelper sharedPreferencesHelper;
    private final SharedPreferencesHelper encryptedSharedPreferencesHelper;

    public ConfigurationManager(Context context) {
        sharedPreferencesHelper = new SharedPreferencesHelper(context);
        encryptedSharedPreferencesHelper = new EncryptedSharedPreferencesHelper(context);
    }

    public String getApiKey() {
        return encryptedSharedPreferencesHelper.getString(SharedPrefsConsts.BINANCE_API_KEY, "");
    }

    public String getApiKeyFailOnNotFound() throws KeyNotFoundException {
        return encryptedSharedPreferencesHelper.getString(SharedPrefsConsts.BINANCE_API_KEY);
    }

    public String getSecretKey() {
        return encryptedSharedPreferencesHelper.getString(SharedPrefsConsts.BINANCE_SECRET_KEY, "");
    }

    public String getSecretKeyFailOnNotFound() throws KeyNotFoundException {
        return encryptedSharedPreferencesHelper.getString(SharedPrefsConsts.BINANCE_SECRET_KEY);
    }

    public int getValidationInterval() {
        return sharedPreferencesHelper.getInt(SharedPrefsConsts.VALIDATION_INTERVAL_MINUTES, ConfigurationConsts.DEFAULT_VALIDATION_INTERVAL_MINUTES);
    }

    public void setApiKey(String apiKey) {
        encryptedSharedPreferencesHelper.setString(SharedPrefsConsts.BINANCE_API_KEY, apiKey);
    }

    public void setSecretKey(String secretKey) {
        encryptedSharedPreferencesHelper.setString(SharedPrefsConsts.BINANCE_SECRET_KEY, secretKey);
    }

    public void setValidationInterval(int validationInterval) {
        sharedPreferencesHelper.setInt(SharedPrefsConsts.VALIDATION_INTERVAL_MINUTES, validationInterval);
    }
}
