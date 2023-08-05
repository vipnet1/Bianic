package com.example.binancerebalancinghelper.rebalancing;

import android.content.Context;
import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;
import com.example.binancerebalancinghelper.shared_preferences.EncryptedSharedPreferencesHelper;
import com.example.binancerebalancinghelper.shared_preferences.SharedPreferencesHelper;

public class BinanceApi {
    public BinanceApi(Context context) {
        SharedPreferencesHelper sharedPreferencesHelper = new EncryptedSharedPreferencesHelper(context);

        String apiKey = sharedPreferencesHelper.getString(SharedPrefsConsts.BINANCE_API_KEY, "");
        String secretKey = sharedPreferencesHelper.getString(SharedPrefsConsts.BINANCE_SECRET_KEY, "");
    }
}
