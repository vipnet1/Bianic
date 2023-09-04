package com.example.binancerebalancinghelper.consts;

public abstract class SharedPrefsConsts {
    public static final String SHARED_PREFS_FILE = "DefaultPrefs";
    public static final String SECRET_SHARED_PREFS_FILE = "SecretPrefs";

    public static final String BINANCE_API_KEY = "binance_api_key";
    public static final String BINANCE_SECRET_KEY = "binance_secret_key";

    public static final String VALIDATION_INTERVAL_MINUTES = "validation_interval_minutes";
    public static final String THRESHOLD_REBALANCING_PERCENT = "threshold_rebalancing_percent";
    public static final String IS_REBALANCING_ACTIVATED = "is_rebalancing_activated";

    public static final String NEXT_NOTIFICATION_TYPE_ID_PREFIX = "next_notification_id_";
}
