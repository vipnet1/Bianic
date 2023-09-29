package com.vippygames.bianic.activities.observe;

public class ObserveInfo {
    public enum STATUS {
        RUNNING, FINISHED, FAILED
    }

    private STATUS status;
    private String message;

    public ObserveInfo() {
        status = STATUS.RUNNING;
        message = "";
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
