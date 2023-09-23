package com.vippygames.bianic.rebalancing.validation.exceptions;

import android.content.Context;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class FailedValidateRecordsException extends NormalException {
    private final String message;

    public FailedValidateRecordsException(Context context, String message) {
        super(context);
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
