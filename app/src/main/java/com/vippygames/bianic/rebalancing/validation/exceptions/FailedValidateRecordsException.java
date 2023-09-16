package com.vippygames.bianic.rebalancing.validation.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class FailedValidateRecordsException extends NormalException {
    private final String message;

    public FailedValidateRecordsException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getExceptionName() {
        return "FailedValidateRecordsException";
    }
}
