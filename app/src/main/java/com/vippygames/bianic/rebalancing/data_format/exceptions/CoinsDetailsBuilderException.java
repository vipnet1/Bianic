package com.vippygames.bianic.rebalancing.data_format.exceptions;

import android.content.Context;

import com.vippygames.bianic.R;
import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class CoinsDetailsBuilderException extends NormalException {
    private final String customMessage;

    public CoinsDetailsBuilderException(Context context, Exception e) {
        super(context, e);
        customMessage = "";
    }

    public CoinsDetailsBuilderException(Context context, String customMessage) {
        super(context);
        this.customMessage = customMessage;
    }

    @Override
    public String getExceptionName() {
        return "CoinsDetailsBuilderException";
    }

    @Override
    public String getMessage() {
        return customMessage + context.getString(R.string.C_excpdet_validateRecordsAgain);
    }
}
