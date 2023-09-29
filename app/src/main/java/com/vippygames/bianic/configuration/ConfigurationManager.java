package com.vippygames.bianic.configuration;

import android.content.Context;

import com.vippygames.bianic.consts.ConfigurationConsts;
import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.shared_preferences.EncryptedSharedPreferencesHelper;
import com.vippygames.bianic.shared_preferences.SharedPreferencesHelper;
import com.vippygames.bianic.shared_preferences.exceptions.KeyNotFoundException;

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

    public String getTheme() {
        return sharedPreferencesHelper.getString(SharedPrefsConsts.THEME_KEY, ConfigurationConsts.DEFAULT_THEME);
    }

    public void setTheme(String theme) {
        sharedPreferencesHelper.setString(SharedPrefsConsts.THEME_KEY, theme);
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
        // if key empty delete it so we get exception before starting binance data retrieval logic
        if (apiKey.isEmpty()) {
            encryptedSharedPreferencesHelper.deleteKeyIfExists(SharedPrefsConsts.BINANCE_API_KEY);
            return;
        }

        encryptedSharedPreferencesHelper.setString(SharedPrefsConsts.BINANCE_API_KEY, apiKey);
    }

    public void setSecretKey(String secretKey) {
        // if key empty delete it so we get exception before starting binance data retrieval logic
        if (secretKey.isEmpty()) {
            encryptedSharedPreferencesHelper.deleteKeyIfExists(SharedPrefsConsts.BINANCE_SECRET_KEY);
            return;
        }

        encryptedSharedPreferencesHelper.setString(SharedPrefsConsts.BINANCE_SECRET_KEY, secretKey);
    }

    public void setValidationInterval(int validationInterval) {
        sharedPreferencesHelper.setInt(SharedPrefsConsts.VALIDATION_INTERVAL_MINUTES, validationInterval);
    }

    public void setThresholdRebalancingPercent(float thresholdRebalancingPercent) {
        sharedPreferencesHelper.setFloat(SharedPrefsConsts.THRESHOLD_REBALANCING_PERCENT, thresholdRebalancingPercent);
    }
}
