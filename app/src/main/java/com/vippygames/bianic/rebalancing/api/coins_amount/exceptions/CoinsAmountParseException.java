package com.vippygames.bianic.rebalancing.api.coins_amount.exceptions;

import android.content.Context;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class CoinsAmountParseException extends NormalException {
    public CoinsAmountParseException(Context context, Exception e) {
        super(context, e);
    }

    @Override
    public String getExceptionName() {
        return "CoinsAmountParseException";
    }
}
