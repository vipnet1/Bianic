package com.example.binancerebalancinghelper.exception_handle;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.binancerebalancinghelper.notifications.NotificationType;
import com.example.binancerebalancinghelper.notifications.NotificationsHelper;
import com.example.binancerebalancinghelper.consts.ExceptionHandleConsts;
import com.example.binancerebalancinghelper.exception_handle.exceptions.CriticalException;
import com.example.binancerebalancinghelper.sqlite.SqliteDbHelper;
import com.example.binancerebalancinghelper.sqlite.consts.ExceptionsLogTableConsts;

public class ExceptionHandler {
    private final Context context;

    public ExceptionHandler(Context context) {
        this.context = context;
    }

    public void handleException(Exception exception) {
        writeExceptionToDatabase(exception);
        showExceptionNotification(exception);
    }

    private void writeExceptionToDatabase(Exception exception) {
        try {
            SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(context);

            ContentValues contentValues = new ContentValues();
            contentValues.put(ExceptionsLogTableConsts.SEVERITY_COLUMN, ExceptionHandleConsts.SEVERITY_NORMAL);
            contentValues.put(ExceptionsLogTableConsts.MESSAGE_COLUMN, exception.toString());

            sqLiteDatabase.insert(ExceptionsLogTableConsts.TABLE_NAME, null, contentValues);
        } catch (Exception e) {
            CriticalExceptionHandler criticalExceptionHandler = new CriticalExceptionHandler(context);
            criticalExceptionHandler.handleException(
                    new CriticalException(e, CriticalException.CriticalExceptionType.FAILED_WRITING_EXCEPTION_TO_DB)
            );
        }
    }

    private void showExceptionNotification(Exception exception) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(context);

        try {
            String exceptionClassName = exception.getClass().toString();
            int lastDotIndex = exceptionClassName.lastIndexOf('.');
            String className = exceptionClassName.substring(lastDotIndex + 1);

            notificationsHelper.pushNotification(ExceptionHandleConsts.SEVERITY_NORMAL + " exception occurred",
                    className, NotificationType.NORMAL_EXCEPTION);
        } catch (Exception e) {
            CriticalExceptionHandler criticalExceptionHandler = new CriticalExceptionHandler(context);
            criticalExceptionHandler.handleException(
                    new CriticalException(e, CriticalException.CriticalExceptionType.FAILED_SHOW_EXCEPTION_NOTIFICATION)
            );
        }
    }
}
