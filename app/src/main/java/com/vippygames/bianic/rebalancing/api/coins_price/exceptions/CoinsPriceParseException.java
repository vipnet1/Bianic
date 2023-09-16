package com.vippygames.bianic.rebalancing.api.coins_price.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class CoinsPriceParseException extends NormalException {
    public CoinsPriceParseException(Exception e) {
        super(e);
    }

    @Override
    public String getExceptionName() {
        return "CoinsPriceParseException";
    }
}
