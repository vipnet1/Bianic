package com.example.binancerebalancinghelper.rebalancing.exception_handle.exceptions;

public class CriticalException extends Exception {
    private final Exception originalException;
    private final CriticalExceptionType criticalExceptionType;

    public enum CriticalExceptionType {
        FAILED_SHOW_EXCEPTION_NOTIFICATION, FAILED_WRITING_EXCEPTION_TO_DB, UNLABELED_EXCEPTION
    }

    public CriticalException(Exception e, CriticalExceptionType criticalExceptionType) {
        super(e);
        this.originalException = e;
        this.criticalExceptionType = criticalExceptionType;
    }

    public Exception getOriginalException() {
        return this.originalException;
    }

    @Override
    public String getMessage() {
        return "--- " + this.criticalExceptionType.toString() + " ---" + super.getMessage();
    }
}
