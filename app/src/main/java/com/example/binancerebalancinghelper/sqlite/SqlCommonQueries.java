package com.example.binancerebalancinghelper.sqlite;

public abstract class SqlCommonQueries {
    public static final String CREATE_THRESHOLD_ALLOCATION_TABLE = "" +
            "CREATE TABLE ThresholdAllocation(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "symbol TEXT UNIQUE," +
            "percent_of_portfolio REAL)";
}
