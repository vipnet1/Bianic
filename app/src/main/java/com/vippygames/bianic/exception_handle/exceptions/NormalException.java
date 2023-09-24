package com.vippygames.bianic.exception_handle.exceptions;

import android.content.Context;

public class NormalException extends Exception {
    protected final Context context;

    public NormalException(Context context) {
        this.context = context;
    }

    public NormalException(Context context, Exception e) {
        super(e);
        this.context = context;
    }

    public String getExceptionName() {
        return "NormalException";
    }
}
