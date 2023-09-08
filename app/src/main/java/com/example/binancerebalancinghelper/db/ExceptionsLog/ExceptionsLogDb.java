package com.example.binancerebalancinghelper.db.ExceptionsLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.binancerebalancinghelper.consts.ExceptionHandleConsts;
import com.example.binancerebalancinghelper.db.ThresholdAllocation.ThresholdAllocationRecord;
import com.example.binancerebalancinghelper.exception_handle.CriticalExceptionHandler;
import com.example.binancerebalancinghelper.exception_handle.exceptions.CriticalException;
import com.example.binancerebalancinghelper.sqlite.SqliteDbHelper;
import com.example.binancerebalancinghelper.sqlite.consts.ExceptionsLogTableConsts;
import com.example.binancerebalancinghelper.sqlite.consts.ThresholdAllocationTableConsts;

import java.util.ArrayList;
import java.util.List;

public class ExceptionsLogDb {
    private final Context context;

    public ExceptionsLogDb(Context context) {
        this.context = context;
    }

    public List<ExceptionsLogRecord> loadRecords(Cursor records) {
        int idColumnIndex = records.getColumnIndex(ExceptionsLogTableConsts.ID_COLUMN);
        int createdAtColumnIndex = records.getColumnIndex(ExceptionsLogTableConsts.CREATED_AT_COLUMN);
        int severityColumnIndex = records.getColumnIndex(ExceptionsLogTableConsts.SEVERITY_COLUMN);
        int messageColumnIndex = records.getColumnIndex(ExceptionsLogTableConsts.MESSAGE_COLUMN);

        List<ExceptionsLogRecord> results = new ArrayList<>();
        while (records.moveToNext()) {
            int id = records.getInt(idColumnIndex);
            String createdAt = records.getString(createdAtColumnIndex);
            String severity = records.getString(severityColumnIndex);
            String message = records.getString(messageColumnIndex);

            ExceptionsLogRecord record = new ExceptionsLogRecord(id, createdAt, severity, message);
            results.add(record);
        }

        records.close();
        return results;
    }

    public void clearAllExceptionsFromDb() {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        sqLiteDatabase.delete(ExceptionsLogTableConsts.TABLE_NAME, null, null);
    }

    public void clearExceptionFromDb(String exceptionId) {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        sqLiteDatabase.delete(ExceptionsLogTableConsts.TABLE_NAME, "id=" + exceptionId, null);
    }

    public Cursor getRecordsOrderedByCreatedAt() {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);

        return sqLiteDatabase.rawQuery("" +
                        "SELECT * FROM " + ExceptionsLogTableConsts.TABLE_NAME
                        + " ORDER BY " + ExceptionsLogTableConsts.CREATED_AT_COLUMN + " DESC",
                null);
    }

    public void saveRecord(ExceptionsLogRecord record) {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ExceptionsLogTableConsts.SEVERITY_COLUMN, record.getSeverity());
        contentValues.put(ExceptionsLogTableConsts.MESSAGE_COLUMN, record.getMessage());

        sqLiteDatabase.insert(ExceptionsLogTableConsts.TABLE_NAME, null, contentValues);
    }
}
