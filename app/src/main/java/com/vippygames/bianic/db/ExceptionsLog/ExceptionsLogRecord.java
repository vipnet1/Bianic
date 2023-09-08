package com.vippygames.bianic.db.ExceptionsLog;

public class ExceptionsLogRecord {
    private int id;
    private String createdAt;
    private String severity;
    private String message;

    public ExceptionsLogRecord(int id, String createdAt, String severity, String message) {
        this.id = id;
        this.createdAt = createdAt;
        this.severity = severity;
        this.message = message;
    }

    public ExceptionsLogRecord(String severity, String message) {
        this.severity = severity;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }
}
