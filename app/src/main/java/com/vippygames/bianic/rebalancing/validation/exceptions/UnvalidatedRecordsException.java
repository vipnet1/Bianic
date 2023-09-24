package com.vippygames.bianic.rebalancing.validation.exceptions;

import android.content.Context;

import com.vippygames.bianic.R;
import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class UnvalidatedRecordsException extends NormalException {
    public UnvalidatedRecordsException(Context context) {
        super(context);
    }

    @Override
    public String getMessage() {
        return context.getString(R.string.C_excpdet_validateRecordsClickButton);
    }

    @Override
    public String getExceptionName() {
        return "UnvalidatedRecordsException";
    }
}


