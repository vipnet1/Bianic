package com.example.binancerebalancinghelper.exception_handle;

import android.content.Context;

import com.example.binancerebalancinghelper.consts.ExceptionHandleConsts;
import com.example.binancerebalancinghelper.db.ExceptionsLog.ExceptionsLogDb;
import com.example.binancerebalancinghelper.db.ExceptionsLog.ExceptionsLogRecord;
import com.example.binancerebalancinghelper.exception_handle.exceptions.CriticalException;
import com.example.binancerebalancinghelper.notifications.NotificationType;
import com.example.binancerebalancinghelper.notifications.NotificationsHelper;

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
            ExceptionsLogDb db = new ExceptionsLogDb(context);
            ExceptionsLogRecord record = new ExceptionsLogRecord(ExceptionHandleConsts.SEVERITY_CRITICAL, exception.toString());
            db.saveRecord(record);
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
                    exception.getCriticalExceptionType() + ": " + className, NotificationType.CRITICAL_EXCEPTION);
        } catch (Exception e) {
            notificationsHelper.pushNotification(ExceptionHandleConsts.SEVERITY_FATAL + " exception occurred",
                    "While trying to show exception", NotificationType.FATAL_EXCEPTION);
        }
    }
}
