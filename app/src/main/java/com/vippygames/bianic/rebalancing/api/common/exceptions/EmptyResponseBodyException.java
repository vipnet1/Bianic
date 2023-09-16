package com.vippygames.bianic.rebalancing.api.common.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class EmptyResponseBodyException extends NormalException {
    public EmptyResponseBodyException() {
        super();
    }

    @Override
    public String getExceptionName() {
        return "EmptyResponseBodyException";
    }
}
