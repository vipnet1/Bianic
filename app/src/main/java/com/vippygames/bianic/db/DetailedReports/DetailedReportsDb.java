package com.vippygames.bianic.db.DetailedReports;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vippygames.bianic.sqlite.SqliteDbHelper;
import com.vippygames.bianic.sqlite.consts.DetailedReportsTableConsts;

import java.util.ArrayList;
import java.util.List;

public class DetailedReportsDb {
    private final Context context;

    public DetailedReportsDb(Context context) {
        this.context = context;
    }

    public List<DetailedReportsRecord> loadRecords(Cursor records) {
        int uuidColumnIndex = records.getColumnIndex(DetailedReportsTableConsts.UUID_COLUMN);
        int reportsTableUuidColumnIndex = records.getColumnIndex(DetailedReportsTableConsts.REPORTS_TABLE_UUID_COLUMN);
        int coinsColumnIndex = records.getColumnIndex(DetailedReportsTableConsts.COIN_COLUMN);
        int targetAllocationColumnIndex = records.getColumnIndex(DetailedReportsTableConsts.TARGET_ALLOCATION_COLUMN);
        int quantityColumnIndex = records.getColumnIndex(DetailedReportsTableConsts.QUANTITY_COLUMN);
        int priceColumnIndex = records.getColumnIndex(DetailedReportsTableConsts.PRICE_COLUMN);
        int currentUsdValueColumnIndex = records.getColumnIndex(DetailedReportsTableConsts.CURRENT_USD_VALUE_COLUMN);
        int currentAllocationColumnIndex = records.getColumnIndex(DetailedReportsTableConsts.CURRENT_ALLOCATION_COLUMN);
        int targetQuantityColumnIndex = records.getColumnIndex(DetailedReportsTableConsts.TARGET_QUANTITY_COLUMN);
        int createdAtColumnIndex = records.getColumnIndex(DetailedReportsTableConsts.CREATED_AT_COLUMN);


        List<DetailedReportsRecord> results = new ArrayList<>();

        while (records.moveToNext()) {
            String uuid = records.getString(uuidColumnIndex);
            String reportsTableUuid = records.getString(reportsTableUuidColumnIndex);
            String coin = records.getString(coinsColumnIndex);
            double targetAllocation = records.getDouble(targetAllocationColumnIndex);
            double quantity = records.getDouble(quantityColumnIndex);
            double price = records.getDouble(priceColumnIndex);
            double currentUsdValue = records.getDouble(currentUsdValueColumnIndex);
            double currentAllocation = records.getDouble(currentAllocationColumnIndex);
            double targetQuantity = records.getDouble(targetQuantityColumnIndex);
            String createdAt = records.getString(createdAtColumnIndex);

            DetailedReportsRecord record = new DetailedReportsRecord(
                    uuid, reportsTableUuid, coin, targetAllocation, quantity, price,
                    currentUsdValue, currentAllocation, targetQuantity, createdAt
            );
            results.add(record);
        }

        records.close();
        return results;
    }

    public Cursor getRecordsOrderedByTargetAllocationThenCoin(String reportsTableUuid) {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);

        return sqLiteDatabase.rawQuery("" +
                        "SELECT * FROM " + DetailedReportsTableConsts.TABLE_NAME
                        + " WHERE " + DetailedReportsTableConsts.REPORTS_TABLE_UUID_COLUMN + "='" + reportsTableUuid
                        + "' ORDER BY " + DetailedReportsTableConsts.TARGET_ALLOCATION_COLUMN + " DESC"
                        + ", " + DetailedReportsTableConsts.COIN_COLUMN + " ASC",
                null);
    }

    public void clearAllDetailedReportsFromDb() {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        sqLiteDatabase.delete(DetailedReportsTableConsts.TABLE_NAME, null, null);
    }

    public void clearDetailedReportsByReportUuid(String reportUuid) {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        sqLiteDatabase.delete(DetailedReportsTableConsts.TABLE_NAME,
                DetailedReportsTableConsts.REPORTS_TABLE_UUID_COLUMN + "='" + reportUuid + "'", null);
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
