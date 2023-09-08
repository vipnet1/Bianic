package com.vippygames.bianic.sqlite;

import com.vippygames.bianic.sqlite.consts.DetailedReportsTableConsts;
import com.vippygames.bianic.sqlite.consts.ExceptionsLogTableConsts;
import com.vippygames.bianic.sqlite.consts.ReportsTableConsts;
import com.vippygames.bianic.sqlite.consts.ThresholdAllocationTableConsts;

public abstract class SqlCommonQueries {
    public static final String CREATE_THRESHOLD_ALLOCATION_TABLE = "" +
            "CREATE TABLE " + ThresholdAllocationTableConsts.TABLE_NAME + "(" +
            ThresholdAllocationTableConsts.ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ThresholdAllocationTableConsts.SYMBOL_COLUMN + " TEXT UNIQUE NOT NULL," +
            ThresholdAllocationTableConsts.DESIRED_ALLOCATION + " REAL NOT NULL," +
            ThresholdAllocationTableConsts.CREATED_AT_COLUMN + " TEXT DEFAULT CURRENT_TIMESTAMP);";

    public static final String CREATE_EXCEPTIONS_LOG_TABLE = "" +
            "CREATE TABLE "+ ExceptionsLogTableConsts.TABLE_NAME +"(" +
            ExceptionsLogTableConsts.ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ExceptionsLogTableConsts.SEVERITY_COLUMN + " TEXT NOT NULL," +
            ExceptionsLogTableConsts.MESSAGE_COLUMN + " TEXT," +
            ExceptionsLogTableConsts.CREATED_AT_COLUMN + " TEXT DEFAULT CURRENT_TIMESTAMP);";

    public static final String CREATE_REPORTS_TABLE = "" +
            "CREATE TABLE "+ ReportsTableConsts.TABLE_NAME +"(" +
            ReportsTableConsts.UUID_COLUMN + " TEXT PRIMARY KEY," +
            ReportsTableConsts.SHOULD_REBALANCE_COLUMN + " BOOLEAN," +
            ReportsTableConsts.PORTFOLIO_USD_VALUE + " REAL," +
            ReportsTableConsts.COINS_COUNT + " INTEGER," +
            ReportsTableConsts.THRESHOLD_REBALANCING_PERCENT + " REAL," +
            ReportsTableConsts.HIGHEST_DEVIATION_COIN + " TEXT," +
            ReportsTableConsts.HIGHEST_DEVIATION_PERCENT + " REAL," +
            ReportsTableConsts.CREATED_AT_COLUMN + " TEXT DEFAULT CURRENT_TIMESTAMP);";

    public static final String CREATE_DETAILED_REPORTS_TABLE = "" +
            "CREATE TABLE "+ DetailedReportsTableConsts.TABLE_NAME +"(" +
            DetailedReportsTableConsts.UUID_COLUMN + " TEXT PRIMARY KEY," +
            DetailedReportsTableConsts.REPORTS_TABLE_UUID_COLUMN + " TEXT NOT NULL," +
            DetailedReportsTableConsts.COIN_COLUMN + " TEXT," +
            DetailedReportsTableConsts.TARGET_ALLOCATION_COLUMN + " REAL," +
            DetailedReportsTableConsts.QUANTITY_COLUMN + " REAL," +
            DetailedReportsTableConsts.PRICE_COLUMN + " REAL," +
            DetailedReportsTableConsts.CURRENT_USD_VALUE_COLUMN + " REAL," +
            DetailedReportsTableConsts.CURRENT_ALLOCATION_COLUMN + " REAL," +
            DetailedReportsTableConsts.TARGET_QUANTITY_COLUMN + " REAL," +
            DetailedReportsTableConsts.CREATED_AT_COLUMN + " TEXT DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY(" + DetailedReportsTableConsts.REPORTS_TABLE_UUID_COLUMN +
            ") REFERENCES " + ReportsTableConsts.TABLE_NAME + "(" + ReportsTableConsts.UUID_COLUMN + "));";
}
