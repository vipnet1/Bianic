package com.example.binancerebalancinghelper.shared_preferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.Context;

import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class EncryptedSharedPreferencesHelper extends SharedPreferencesHelper {
    public EncryptedSharedPreferencesHelper(Context context) {
        if (prefs == null) {
            try {
                String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

                prefs = EncryptedSharedPreferences.create(
                        SharedPrefsConsts.SECRET_SHARED_PREFS_FILE,
                        masterKeyAlias,
                        context,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
