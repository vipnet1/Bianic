package com.vippygames.bianic.db.exceptions_log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vippygames.bianic.consts.ExceptionsLogConsts;
import com.vippygames.bianic.sqlite.SqliteDbHelper;
import com.vippygames.bianic.sqlite.consts.ExceptionsLogTableConsts;

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

        String whereClause = ExceptionsLogTableConsts.ID_COLUMN + "=?";
        String[] whereArgs = {exceptionId};

        sqLiteDatabase.delete(ExceptionsLogTableConsts.TABLE_NAME, whereClause, whereArgs);
    }

    public Cursor getRecordsOrderedByCreatedAt() {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        String orderBy = ExceptionsLogTableConsts.CREATED_AT_COLUMN + " DESC";

        return sqLiteDatabase.query(ExceptionsLogTableConsts.TABLE_NAME, null, null,
                null, null, null, orderBy);
    }

    public void saveRecord(ExceptionsLogRecord record) {
        if (getRecordsCount() >= ExceptionsLogConsts.MAX_STORED_EXCEPTION_COUNT) {
            freeSpace();
        }

        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ExceptionsLogTableConsts.SEVERITY_COLUMN, record.getSeverity());
        contentValues.put(ExceptionsLogTableConsts.MESSAGE_COLUMN, record.getMessage());

        sqLiteDatabase.insert(ExceptionsLogTableConsts.TABLE_NAME, null, contentValues);
    }

    private int getRecordsCount() {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        String[] columns = new String[]{"COUNT (*)"};

        Cursor cursor = sqLiteDatabase.query(ExceptionsLogTableConsts.TABLE_NAME, columns,
                null, null, null, null, null, null);

        cursor.moveToFirst();
        int recordsCount = cursor.getInt(0);
        cursor.close();

        return recordsCount;
    }

    private void freeSpace() {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);
        String whereClause = ExceptionsLogTableConsts.ID_COLUMN + " IN (SELECT "
                + ExceptionsLogTableConsts.ID_COLUMN + " FROM " + ExceptionsLogTableConsts.TABLE_NAME
                + " ORDER BY " + ExceptionsLogTableConsts.CREATED_AT_COLUMN + " LIMIT "
                + ExceptionsLogConsts.STORED_EXCEPTIONS_DELETE_PER_QUERY + ")";

        sqLiteDatabase.delete(ExceptionsLogTableConsts.TABLE_NAME, whereClause, null);
    }
}
