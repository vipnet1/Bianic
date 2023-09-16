package com.vippygames.bianic.rebalancing.api.common.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class FailedRequestStatusException extends NormalException {
    private final int status;
    private final String message;

    public FailedRequestStatusException(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "Failed status code - " + status + ". The message - " + this.message;
    }

    @Override
    public String getExceptionName() {
        return "FailedRequestStatusException";
    }
}
