package com.example.binancerebalancinghelper.exception_handle;

import android.content.Context;

import com.example.binancerebalancinghelper.consts.ExceptionHandleConsts;
import com.example.binancerebalancinghelper.db.ExceptionsLog.ExceptionsLogDb;
import com.example.binancerebalancinghelper.db.ExceptionsLog.ExceptionsLogRecord;
import com.example.binancerebalancinghelper.exception_handle.exceptions.CriticalException;
import com.example.binancerebalancinghelper.notifications.NotificationType;
import com.example.binancerebalancinghelper.notifications.NotificationsHelper;

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
            ExceptionsLogDb db = new ExceptionsLogDb(context);
            ExceptionsLogRecord record = new ExceptionsLogRecord(ExceptionHandleConsts.SEVERITY_NORMAL, exception.toString());
            db.saveRecord(record);
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
