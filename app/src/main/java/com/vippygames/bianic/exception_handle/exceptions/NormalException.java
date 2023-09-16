package com.vippygames.bianic.exception_handle.exceptions;

public class NormalException extends Exception {
    public NormalException() {
    }

    public NormalException(Exception e) {
        super(e);
    }

    public String getExceptionName() {
        return "NormalException";
    }
}
