package com.vippygames.bianic.rebalancing.api.common.exceptions;

public class FailedRequestStatusException extends Exception {
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
}
