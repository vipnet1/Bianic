package com.vippygames.bianic.rebalancing.api.coins_amount.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class CoinsAmountParseException extends NormalException {
    public CoinsAmountParseException(Exception e) {
        super(e);
    }

    @Override
    public String getExceptionName() {
        return "CoinsAmountParseException";
    }
}
