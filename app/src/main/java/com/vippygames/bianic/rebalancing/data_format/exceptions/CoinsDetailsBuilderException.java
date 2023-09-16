package com.vippygames.bianic.rebalancing.data_format.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class CoinsDetailsBuilderException extends NormalException {
    private final String customMessage;

    public CoinsDetailsBuilderException(Exception e) {
        super(e);
        customMessage = "";
    }

    public CoinsDetailsBuilderException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }

    @Override
    public String getExceptionName() {
        return "CoinsDetailsBuilderException";
    }

    @Override
    public String getMessage() {
        return customMessage + "Please validate records again, it will likely solve the issue.";
    }
}
