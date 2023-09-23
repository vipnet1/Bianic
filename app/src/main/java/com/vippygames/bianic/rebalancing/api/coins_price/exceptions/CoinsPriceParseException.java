package com.vippygames.bianic.rebalancing.api.coins_price.exceptions;

import android.content.Context;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class CoinsPriceParseException extends NormalException {
    public CoinsPriceParseException(Context context, Exception e) {
        super(context, e);
    }

    @Override
    public String getExceptionName() {
        return "CoinsPriceParseException";
    }
}
