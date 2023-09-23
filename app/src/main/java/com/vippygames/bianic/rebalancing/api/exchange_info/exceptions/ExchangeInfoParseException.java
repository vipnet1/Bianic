package com.vippygames.bianic.rebalancing.api.exchange_info.exceptions;

import android.content.Context;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class ExchangeInfoParseException extends NormalException {
    public ExchangeInfoParseException(Context context, Exception e) {
        super(context, e);
    }

    @Override
    public String getExceptionName() {
        return "ExchangeInfoParseException";
    }
}
