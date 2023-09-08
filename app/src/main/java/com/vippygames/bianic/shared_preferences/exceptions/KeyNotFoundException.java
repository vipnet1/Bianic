package com.vippygames.bianic.shared_preferences.exceptions;

public class KeyNotFoundException extends Exception {
    private final String key;

    public KeyNotFoundException(String key) {
        this.key = key;
    }

    @Override
    public String getMessage() {
        return "SharedPreferences key '" + this.key + "' not found";
    }
}
