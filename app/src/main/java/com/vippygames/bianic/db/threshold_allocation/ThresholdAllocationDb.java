package com.vippygames.bianic.db.threshold_allocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vippygames.bianic.sqlite.SqliteDbHelper;
import com.vippygames.bianic.sqlite.consts.ThresholdAllocationTableConsts;

import java.util.ArrayList;
import java.util.List;

public class ThresholdAllocationDb {
    private final Context context;

    public ThresholdAllocationDb(Context context) {
        this.context = context;
    }

    public List<ThresholdAllocationRecord> loadRecords(Cursor records) {
        int idColumnIndex = records.getColumnIndex(ThresholdAllocationTableConsts.ID_COLUMN);
        int createdAtColumnIndex = records.getColumnIndex(ThresholdAllocationTableConsts.CREATED_AT_COLUMN);
        int symbolColumnIndex = records.getColumnIndex(ThresholdAllocationTableConsts.SYMBOL_COLUMN);
        int desiredAllocationColumnIndex = records.getColumnIndex(ThresholdAllocationTableConsts.DESIRED_ALLOCATION);

        List<ThresholdAllocationRecord> results = new ArrayList<>();
        while (records.moveToNext()) {
            int id = records.getInt(idColumnIndex);
            String createdAt = records.getString(createdAtColumnIndex);
            String symbol = records.getString(symbolColumnIndex);
            float desiredAllocation = records.getFloat(desiredAllocationColumnIndex);

            ThresholdAllocationRecord record = new ThresholdAllocationRecord(id, createdAt, symbol, desiredAllocation);
            results.add(record);
        }

        records.close();
        return results;
    }

    public Cursor getRecordsOrderedByDesiredAllocationThenSymbol() {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        String orderBy = ThresholdAllocationTableConsts.DESIRED_ALLOCATION + " DESC, "
                + ThresholdAllocationTableConsts.SYMBOL_COLUMN + " ASC";

        return sqLiteDatabase.query(ThresholdAllocationTableConsts.TABLE_NAME, null,
                null, null, null, null, orderBy);
    }

    public Cursor getRecords() {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        return sqLiteDatabase.query(ThresholdAllocationTableConsts.TABLE_NAME, null,
                null, null, null, null, null);
    }

    public void clearAndSaveRecords(List<ThresholdAllocationRecord> records) {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        sqLiteDatabase.beginTransaction();

        sqLiteDatabase.delete(ThresholdAllocationTableConsts.TABLE_NAME, null, null);

        for (ThresholdAllocationRecord record : records) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ThresholdAllocationTableConsts.SYMBOL_COLUMN, record.getSymbol());
            contentValues.put(ThresholdAllocationTableConsts.DESIRED_ALLOCATION, record.getDesiredAllocation());

            sqLiteDatabase.insert(ThresholdAllocationTableConsts.TABLE_NAME, null, contentValues);
        }

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    public void saveRecord(ThresholdAllocationRecord record) {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ThresholdAllocationTableConsts.SYMBOL_COLUMN, record.getSymbol());
        contentValues.put(ThresholdAllocationTableConsts.DESIRED_ALLOCATION, record.getDesiredAllocation());

        sqLiteDatabase.insert(ThresholdAllocationTableConsts.TABLE_NAME, null, contentValues);
    }
}
