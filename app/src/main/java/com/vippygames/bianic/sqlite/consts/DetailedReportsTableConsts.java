package com.vippygames.bianic.sqlite.consts;

public class DetailedReportsTableConsts {
    public static final String TABLE_NAME = "DetailedReports";
    public static final String UUID_COLUMN = "uuid";
    public static final String REPORTS_TABLE_UUID_COLUMN = "reports_table_uuid";
    public static final String COIN_COLUMN = "coin";
    public static final String TARGET_ALLOCATION_COLUMN = "target_allocation";
    public static final String QUANTITY_COLUMN = "quantity";
    public static final String PRICE_COLUMN = "price";
    public static final String CURRENT_USD_VALUE_COLUMN = "current_usd_value";
    public static final String CURRENT_ALLOCATION_COLUMN = "current_allocation";
    //how many of that coin to buy/sell to reach target allocation. When positive need buy when negative sell.
    public static final String TARGET_QUANTITY_COLUMN = "target_quantity";
    public static final String CREATED_AT_COLUMN = "created_at";
}
