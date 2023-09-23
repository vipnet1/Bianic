package com.vippygames.bianic.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.exception_handle.CriticalExceptionHandler;
import com.vippygames.bianic.exception_handle.exceptions.CriticalException;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class EncryptedSharedPreferencesHelper extends SharedPreferencesHelper {
    private static SharedPreferences encryptedPrefs = null;

    public EncryptedSharedPreferencesHelper(Context context) {
        super(context);

        if (encryptedPrefs == null) {
            try {
                String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

                encryptedPrefs = EncryptedSharedPreferences.create(
                        SharedPrefsConsts.SECRET_SHARED_PREFS_FILE,
                        masterKeyAlias,
                        context,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );

            } catch (GeneralSecurityException | IOException e) {
                CriticalExceptionHandler exceptionHandler = new CriticalExceptionHandler(context);
                exceptionHandler.handleException(
                        new CriticalException(e,
                                CriticalException.CriticalExceptionType.FAILED_RETRIEVE_ENCRYPTED_SHARED_PREFS)
                );
            }
        }

        usedPrefsRef = encryptedPrefs;
    }
}
