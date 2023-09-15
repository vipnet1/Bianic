package com.vippygames.bianic.exception_handle;

import android.content.Context;

import com.vippygames.bianic.consts.ExceptionHandleConsts;
import com.vippygames.bianic.db.exceptions_log.ExceptionsLogDb;
import com.vippygames.bianic.db.exceptions_log.ExceptionsLogRecord;
import com.vippygames.bianic.exception_handle.exceptions.CriticalException;
import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.notifications.NotificationsHelper;

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
            notificationsHelper.pushNotification(NotificationType.FATAL_EXCEPTION,
                    ExceptionHandleConsts.SEVERITY_FATAL + " exception occurred",
                    "While writing exception to db", false);
        }
    }

    private void showExceptionNotification(CriticalException exception) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(context);

        try {
            String exceptionClassName = exception.getOriginalException().getClass().toString();
            int lastDotIndex = exceptionClassName.lastIndexOf('.');
            String className = exceptionClassName.substring(lastDotIndex + 1);

            notificationsHelper.pushNotification(NotificationType.CRITICAL_EXCEPTION,
                    ExceptionHandleConsts.SEVERITY_CRITICAL + " exception occurred",
                    exception.getCriticalExceptionType() + ": " + className, false);
        } catch (Exception e) {
            notificationsHelper.pushNotification(NotificationType.FATAL_EXCEPTION,
                    ExceptionHandleConsts.SEVERITY_FATAL + " exception occurred",
                    "While trying to show exception", false);
        }
    }
}
