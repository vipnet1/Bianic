package com.vippygames.bianic.db.Reports;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.vippygames.bianic.sqlite.SqliteDbHelper;
import com.vippygames.bianic.sqlite.consts.ReportsTableConsts;

public class ReportsDb {
    private final Context context;

    public ReportsDb(Context context) {
        this.context = context;
    }

    public void saveRecord(ReportsRecord record) {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ReportsTableConsts.SHOULD_REBALANCE_COLUMN, record.shouldRebalance());
        contentValues.put(ReportsTableConsts.PORTFOLIO_USD_VALUE, record.getPortfolioUsdValue());
        contentValues.put(ReportsTableConsts.COINS_COUNT, record.getCoinsCount());
        contentValues.put(ReportsTableConsts.THRESHOLD_REBALANCING_PERCENT, record.getThresholdRebalancingPercent());
        contentValues.put(ReportsTableConsts.HIGHEST_DEVIATION_COIN, record.getHighestDeviationCoin());
        contentValues.put(ReportsTableConsts.HIGHEST_DEVIATION_PERCENT, record.getHighestDeviationPercent());

        sqLiteDatabase.insert(ReportsTableConsts.TABLE_NAME, null, contentValues);
    }
}
