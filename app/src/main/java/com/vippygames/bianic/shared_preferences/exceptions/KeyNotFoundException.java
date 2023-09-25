package com.vippygames.bianic.shared_preferences.exceptions;

import android.content.Context;

import com.vippygames.bianic.R;
import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class KeyNotFoundException extends NormalException {
    private final String key;

    public KeyNotFoundException(Context context, String key) {
        super(context);
        this.key = key;
    }

    @Override
    public String getMessage() {
        return context.getString(R.string.C_excpdet_notProvideBinanceKeys) + " SharedPreferences key '" + this.key + "' not found";
    }

    @Override
    public String getExceptionName() {
        return "KeyNotFoundException";
    }
}
