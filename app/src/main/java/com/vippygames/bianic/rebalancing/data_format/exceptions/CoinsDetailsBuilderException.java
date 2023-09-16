package com.vippygames.bianic.rebalancing.data_format.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class CoinsDetailsBuilderException extends NormalException {
    public CoinsDetailsBuilderException(Exception e) {
        super(e);
    }

    @Override
    public String getExceptionName() {
        return "CoinsDetailsBuilderException";
    }
}
