package com.vippygames.bianic.activities.main.validation_observer;

public class ValidationObserveInfo {
    public enum STATUS {
        RUNNING, FINISHED, FAILED
    }

    private STATUS status;
    private String message;

    public ValidationObserveInfo() {
        status = STATUS.RUNNING;
        message = "";
    }

    public ValidationObserveInfo(STATUS status, String message) {
        this.status = status;
        this.message = message;
    }

    public STATUS getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
