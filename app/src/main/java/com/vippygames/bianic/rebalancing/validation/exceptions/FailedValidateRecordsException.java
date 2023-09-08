package com.vippygames.bianic.rebalancing.validation.exceptions;

public class FailedValidateRecordsException extends Exception {
    private final String message;

    public FailedValidateRecordsException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
