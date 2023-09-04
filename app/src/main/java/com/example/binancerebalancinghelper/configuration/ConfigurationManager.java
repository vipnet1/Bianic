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

    public int isRebalancingActivated() {
        return sharedPreferencesHelper.getInt(SharedPrefsConsts.IS_REBALANCING_ACTIVATED, ConfigurationConsts.DEFAULT_IS_REBALANCING_ACTIVATED);
    }

    public float getThresholdRebalancingPercent() {
        return sharedPreferencesHelper.getFloat(SharedPrefsConsts.THRESHOLD_REBALANCING_PERCENT, ConfigurationConsts.DEFAULT_THRESHOLD_REBALANCING_PERCENT);
    }

    public void setIsRebalancingActivated(int isRebalancingActivated) {
        sharedPreferencesHelper.setInt(SharedPrefsConsts.IS_REBALANCING_ACTIVATED, isRebalancingActivated);
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

    public void setThresholdRebalancingPercent(float thresholdRebalancingPercent) {
        sharedPreferencesHelper.setFloat(SharedPrefsConsts.THRESHOLD_REBALANCING_PERCENT, thresholdRebalancingPercent);
    }
}
