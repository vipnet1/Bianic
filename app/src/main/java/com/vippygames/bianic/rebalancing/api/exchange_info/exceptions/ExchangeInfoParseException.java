package com.vippygames.bianic.rebalancing.api.exchange_info.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class ExchangeInfoParseException extends NormalException {
    public ExchangeInfoParseException(Exception e) {
        super(e);
    }

    @Override
    public String getExceptionName() {
        return "ExchangeInfoParseException";
    }
}
