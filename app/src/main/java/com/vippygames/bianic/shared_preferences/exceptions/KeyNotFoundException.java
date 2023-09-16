package com.vippygames.bianic.shared_preferences.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class KeyNotFoundException extends NormalException {
    private final String key;

    public KeyNotFoundException(String key) {
        this.key = key;
    }

    @Override
    public String getMessage() {
        return "SharedPreferences key '" + this.key + "' not found";
    }

    @Override
    public String getExceptionName() {
        return "KeyNotFoundException";
    }
}
