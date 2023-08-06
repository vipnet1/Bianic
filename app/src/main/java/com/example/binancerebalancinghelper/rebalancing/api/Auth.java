package com.example.binancerebalancinghelper.rebalancing.api;

import android.content.Context;
import android.util.Base64;

import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;
import com.example.binancerebalancinghelper.shared_preferences.EncryptedSharedPreferencesHelper;
import com.example.binancerebalancinghelper.shared_preferences.SharedPreferencesHelper;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Auth {
    private Context context;

    public Auth(Context context) {
        this.context = context;
    }

    public String generateSignature() {
//        try {
//            SharedPreferencesHelper sharedPreferencesHelper = new EncryptedSharedPreferencesHelper(context);
//            String secretKey = sharedPreferencesHelper.getString(SharedPrefsConsts.BINANCE_SECRET_KEY, "");
//
//            Mac mac = Mac.getInstance("HmacSHA256");
//            mac.init(new SecretKeySpec(secretKey.getBytes(), "HmacSHA256"));
//            byte[] signature = mac.doFinal(query.getBytes());
//            return Base64.encodeToString(signature, Base64.DEFAULT);
//        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
//            e.printStackTrace();
//        }
        return "";
    }
}
