package com.vippygames.bianic.consts;

import com.vippygames.bianic.R;

public abstract class SharedPrefsConsts {
    public static final String SHARED_PREFS_FILE = "regular_prefs";
    // if change should change also in backup_rules.xml and data_extraction_rules.xml
    public static final String SECRET_SHARED_PREFS_FILE = "encrypted_prefs";
    public static final String THEME_KEY = "theme";
    public static final String BINANCE_API_KEY = "binance_api_key";
    public static final String BINANCE_SECRET_KEY = "binance_secret_key";
    public static final String VALIDATION_INTERVAL_MINUTES = "validation_interval_minutes";
    public static final String THRESHOLD_REBALANCING_PERCENT = "threshold_rebalancing_percent";
    public static final String IS_REBALANCING_ACTIVATED = "is_rebalancing_activated";
    public static final String ARE_THRESHOLD_ALLOCATION_RECORDS_VALIDATED = "are_threshold_validation_records_validated";
    public static final String SHOULD_ADD_FIRST_THRESHOLD_RECORD = "should_add_first_threshold_record";
    public static final String IS_DETAILED_REPORT_ROTATION_LANDSCAPE = "is_detailed_report_rotation_landscape";
    public static final String NEXT_NOTIFICATION_TYPE_ID_PREFIX = "next_notification_id_";
    public static final String SHOULD_SHOW_CONTRACT = "should_show_contract";
    public static final String SHOULD_WELCOME = "should_welcome";
    public static final String LAST_BOOT_TIME = "last_boot_time";
    public static final String REQUESTED_PERMISSIONS_COUNT_KEY = "requested_permissions_count";
    public static final String DO_NOT_REQUEST_PERMISSIONS_KEY = "do_not_request_permissions";
}
