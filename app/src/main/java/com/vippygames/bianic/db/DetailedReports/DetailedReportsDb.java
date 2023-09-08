package com.vippygames.bianic.db.DetailedReports;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.vippygames.bianic.sqlite.SqliteDbHelper;
import com.vippygames.bianic.sqlite.consts.DetailedReportsTableConsts;

import java.util.List;

public class DetailedReportsDb {
    private final Context context;

    public DetailedReportsDb(Context context) {
        this.context = context;
    }

    public void saveRecords(List<DetailedReportsRecord> records) {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        sqLiteDatabase.beginTransaction();

        for (DetailedReportsRecord record : records) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DetailedReportsTableConsts.REPORTS_TABLE_UUID_COLUMN, record.getReportsTableUuid());
            contentValues.put(DetailedReportsTableConsts.COIN_COLUMN, record.getCoin());
            contentValues.put(DetailedReportsTableConsts.TARGET_ALLOCATION_COLUMN, record.getTargetAllocation());
            contentValues.put(DetailedReportsTableConsts.QUANTITY_COLUMN, record.getQuantity());
            contentValues.put(DetailedReportsTableConsts.PRICE_COLUMN, record.getPrice());
            contentValues.put(DetailedReportsTableConsts.CURRENT_USD_VALUE_COLUMN, record.getCurrentUsdValue());
            contentValues.put(DetailedReportsTableConsts.CURRENT_ALLOCATION_COLUMN, record.getCurrentAllocation());
            contentValues.put(DetailedReportsTableConsts.TARGET_QUANTITY_COLUMN, record.getTargetQuantity());

            sqLiteDatabase.insert(DetailedReportsTableConsts.TABLE_NAME, null, contentValues);
        }

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }
}
