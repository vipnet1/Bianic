package com.example.binancerebalancinghelper.sqlite;

import com.example.binancerebalancinghelper.sqlite.consts.ExceptionsLogTableConsts;
import com.example.binancerebalancinghelper.sqlite.consts.ThresholdAllocationTableConsts;

public abstract class SqlCommonQueries {
    public static final String CREATE_THRESHOLD_ALLOCATION_TABLE = "" +
            "CREATE TABLE " + ThresholdAllocationTableConsts.TABLE_NAME + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            ThresholdAllocationTableConsts.SYMBOL_COLUMN + " TEXT UNIQUE NOT NULL," +
            ThresholdAllocationTableConsts.PERCENT_OF_PORTFOLIO_COLUMN + " REAL NOT NULL," +
            ThresholdAllocationTableConsts.CREATED_AT_COLUMN + " TEXT DEFAULT CURRENT_TIMESTAMP);";

    public static final String CREATE_EXCEPTIONS_LOG_TABLE = "" +
            "CREATE TABLE "+ ExceptionsLogTableConsts.TABLE_NAME +"(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            ExceptionsLogTableConsts.SEVERITY_COLUMN + " TEXT NOT NULL," +
            ExceptionsLogTableConsts.MESSAGE_COLUMN + " TEXT," +
            ExceptionsLogTableConsts.CREATED_AT_COLUMN + " TEXT DEFAULT CURRENT_TIMESTAMP);";
}
