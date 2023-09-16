package com.vippygames.bianic.exception_handle;

import android.content.Context;

import com.vippygames.bianic.consts.ExceptionHandleConsts;
import com.vippygames.bianic.db.exceptions_log.ExceptionsLogDb;
import com.vippygames.bianic.db.exceptions_log.ExceptionsLogRecord;
import com.vippygames.bianic.exception_handle.exceptions.CriticalException;
import com.vippygames.bianic.exception_handle.exceptions.NormalException;
import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.notifications.NotificationsHelper;

public class ExceptionHandler {
    private final Context context;

    public ExceptionHandler(Context context) {
        this.context = context;
    }

    public void handleException(NormalException exception) {
        writeExceptionToDatabase(exception);
        showExceptionNotification(exception);
    }

    private void writeExceptionToDatabase(NormalException exception) {
        try {
            ExceptionsLogDb db = new ExceptionsLogDb(context);
            ExceptionsLogRecord record = new ExceptionsLogRecord(ExceptionHandleConsts.SEVERITY_NORMAL,
                    exception.getExceptionName() + ": " + exception.getMessage());
            db.saveRecord(record);
        } catch (Exception e) {
            CriticalExceptionHandler criticalExceptionHandler = new CriticalExceptionHandler(context);
            criticalExceptionHandler.handleException(
                    new CriticalException(e, CriticalException.CriticalExceptionType.FAILED_WRITING_EXCEPTION_TO_DB)
            );
        }
    }

    private void showExceptionNotification(NormalException exception) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(context);

        try {
            notificationsHelper.pushNotification(NotificationType.NORMAL_EXCEPTION,
                    ExceptionHandleConsts.SEVERITY_NORMAL + " exception occurred", exception.getExceptionName(), false);
        } catch (Exception e) {
            CriticalExceptionHandler criticalExceptionHandler = new CriticalExceptionHandler(context);
            criticalExceptionHandler.handleException(
                    new CriticalException(e, CriticalException.CriticalExceptionType.FAILED_SHOW_EXCEPTION_NOTIFICATION)
            );
        }
    }
}
