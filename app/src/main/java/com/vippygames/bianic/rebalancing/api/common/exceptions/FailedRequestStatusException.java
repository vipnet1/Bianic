package com.vippygames.bianic.rebalancing.api.common.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class FailedRequestStatusException extends NormalException {
    private final int status;
    private final String message;
    private final String customMessage;

    public FailedRequestStatusException(int status, String message) {
        this.status = status;
        this.message = message;
        this.customMessage = generateCustomMessage();
    }

    private String generateCustomMessage() {
        if (message.contains("\"code\":-1022")) {
            return "Likely wrong Secret Key. ";
        }

        return "";
    }

    @Override
    public String getMessage() {
        return "Failed status code - " + status + ". " + customMessage + " The message - " + this.message;
    }

    @Override
    public String getExceptionName() {
        return "FailedRequestStatusException";
    }
}
