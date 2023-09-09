package com.vippygames.bianic.db.Reports;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vippygames.bianic.sqlite.SqliteDbHelper;
import com.vippygames.bianic.sqlite.consts.ReportsTableConsts;

import java.util.ArrayList;
import java.util.List;

public class ReportsDb {
    private final Context context;

    public ReportsDb(Context context) {
        this.context = context;
    }

    public List<ReportsRecord> loadRecords(Cursor records) {
        int uuidColumnIndex = records.getColumnIndex(ReportsTableConsts.UUID_COLUMN);
        int shouldRebalanceColumnIndex = records.getColumnIndex(ReportsTableConsts.SHOULD_REBALANCE_COLUMN);
        int portfolioUsdColumnIndex = records.getColumnIndex(ReportsTableConsts.PORTFOLIO_USD_VALUE);
        int coinsCountColumnIndex = records.getColumnIndex(ReportsTableConsts.COINS_COUNT);
        int thresholdRebalancingPercentColumnIndex = records.getColumnIndex(ReportsTableConsts.THRESHOLD_REBALANCING_PERCENT);
        int highestDeviationCoinColumnIndex = records.getColumnIndex(ReportsTableConsts.HIGHEST_DEVIATION_COIN);
        int highestDeviationPercentColumnIndex = records.getColumnIndex(ReportsTableConsts.HIGHEST_DEVIATION_PERCENT);
        int createdAtColumnIndex = records.getColumnIndex(ReportsTableConsts.CREATED_AT_COLUMN);

        List<ReportsRecord> results = new ArrayList<>();
        while (records.moveToNext()) {
            String uuid = records.getString(uuidColumnIndex);
            boolean shouldRebalance = records.getInt(shouldRebalanceColumnIndex) > 0;
            double portfolioUsd = records.getDouble(portfolioUsdColumnIndex);
            int coinsCount = records.getInt(coinsCountColumnIndex);
            double thresholdRebalancingPercent = records.getDouble(thresholdRebalancingPercentColumnIndex);
            String highestDeviationCoin = records.getString(highestDeviationCoinColumnIndex);
            double highestDeviationPercent = records.getDouble(highestDeviationPercentColumnIndex);
            String createdAt = records.getString(createdAtColumnIndex);

            ReportsRecord record = new ReportsRecord(
                    uuid, shouldRebalance, portfolioUsd, coinsCount, thresholdRebalancingPercent,
                    highestDeviationCoin, highestDeviationPercent, createdAt
            );
            results.add(record);
        }

        records.close();
        return results;
    }

    public void clearAllReportsFromDb() {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        sqLiteDatabase.delete(ReportsTableConsts.TABLE_NAME, null, null);
    }

    public void clearReportFromDb(String reportUuid) {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        sqLiteDatabase.delete(ReportsTableConsts.TABLE_NAME,
                ReportsTableConsts.UUID_COLUMN + "='" + reportUuid + "'", null);
    }

    public Cursor getRecordsOrderedByCreatedAt() {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);

        return sqLiteDatabase.rawQuery("" +
                        "SELECT * FROM " + ReportsTableConsts.TABLE_NAME
                        + " ORDER BY " + ReportsTableConsts.CREATED_AT_COLUMN + " DESC",
                null);
    }

    public void saveRecord(ReportsRecord record) {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ReportsTableConsts.UUID_COLUMN, record.getUuid());
        contentValues.put(ReportsTableConsts.SHOULD_REBALANCE_COLUMN, record.shouldRebalance());
        contentValues.put(ReportsTableConsts.PORTFOLIO_USD_VALUE, record.getPortfolioUsdValue());
        contentValues.put(ReportsTableConsts.COINS_COUNT, record.getCoinsCount());
        contentValues.put(ReportsTableConsts.THRESHOLD_REBALANCING_PERCENT, record.getThresholdRebalancingPercent());
        contentValues.put(ReportsTableConsts.HIGHEST_DEVIATION_COIN, record.getHighestDeviationCoin());
        contentValues.put(ReportsTableConsts.HIGHEST_DEVIATION_PERCENT, record.getHighestDeviationPercent());

        sqLiteDatabase.insert(ReportsTableConsts.TABLE_NAME, null, contentValues);
    }
}
