package com.vippygames.bianic.rebalancing.api.common.exceptions;

import android.content.Context;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class EmptyResponseBodyException extends NormalException {
    public EmptyResponseBodyException(Context context) {
        super(context);
    }

    @Override
    public String getExceptionName() {
        return "EmptyResponseBodyException";
    }
}
