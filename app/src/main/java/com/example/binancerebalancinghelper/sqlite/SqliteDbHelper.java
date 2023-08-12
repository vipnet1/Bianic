package com.example.binancerebalancinghelper.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqliteDbHelper extends SQLiteOpenHelper {
    private static SqliteDbHelper instance = null;
    private static SQLiteDatabase writeableDatabaseInstance = null;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Default.db";

    private SqliteDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteDatabase getWriteableDatabaseInstance(Context context) {
        if(instance == null) {
            instance = new SqliteDbHelper(context);
        }

        if(writeableDatabaseInstance == null) {
            writeableDatabaseInstance = instance.getWritableDatabase();
        }

        return writeableDatabaseInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SqlCommonQueries.CREATE_THRESHOLD_ALLOCATION_TABLE);
        sqLiteDatabase.execSQL(SqlCommonQueries.CREATE_EXCEPTIONS_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
