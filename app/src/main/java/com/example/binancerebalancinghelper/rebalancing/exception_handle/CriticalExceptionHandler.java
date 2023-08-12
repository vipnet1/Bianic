package com.example.binancerebalancinghelper.rebalancing.exception_handle;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.binancerebalancinghelper.notifications.NotificationType;
import com.example.binancerebalancinghelper.notifications.NotificationsHelper;
import com.example.binancerebalancinghelper.consts.ExceptionHandleConsts;
import com.example.binancerebalancinghelper.consts.NotificationsConsts;
import com.example.binancerebalancinghelper.rebalancing.exception_handle.exceptions.CriticalException;
import com.example.binancerebalancinghelper.sqlite.SqliteDbHelper;
import com.example.binancerebalancinghelper.sqlite.consts.ExceptionsLogTableConsts;

public class CriticalExceptionHandler {
    private final Context context;

    public CriticalExceptionHandler(Context context) {
        this.context = context;
    }

    public void handleException(CriticalException exception) {
        writeExceptionToDatabase(exception);
        showExceptionNotification(exception);
    }

    private void writeExceptionToDatabase(CriticalException exception) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(context);

        try {
            SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);

            ContentValues contentValues = new ContentValues();
            contentValues.put(ExceptionsLogTableConsts.SEVERITY_COLUMN, ExceptionHandleConsts.SEVERITY_CRITICAL);
            contentValues.put(ExceptionsLogTableConsts.MESSAGE_COLUMN, exception.toString());

            sqLiteDatabase.insert(ExceptionsLogTableConsts.TABLE_NAME, null, contentValues);
        } catch (Exception e) {
            notificationsHelper.pushNotification(ExceptionHandleConsts.SEVERITY_FATAL + " exception occurred",
                    "While writing exception to db", NotificationType.FATAL_EXCEPTION);
        }
    }

    private void showExceptionNotification(CriticalException exception) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(context);

        try {
            String exceptionClassName = exception.getOriginalException().getClass().toString();
            int lastDotIndex = exceptionClassName.lastIndexOf('.');
            String className = exceptionClassName.substring(lastDotIndex + 1);

            notificationsHelper.pushNotification(ExceptionHandleConsts.SEVERITY_CRITICAL + " exception occurred",
                    className, NotificationType.CRITICAL_EXCEPTION);
        } catch (Exception e) {
            notificationsHelper.pushNotification(ExceptionHandleConsts.SEVERITY_FATAL + " exception occurred",
                    "While trying to show exception", NotificationType.FATAL_EXCEPTION);
        }
    }
}
