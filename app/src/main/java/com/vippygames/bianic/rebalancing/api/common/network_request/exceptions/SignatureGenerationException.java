package com.vippygames.bianic.rebalancing.api.common.network_request.exceptions;

import android.content.Context;

import com.vippygames.bianic.R;
import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class SignatureGenerationException extends NormalException {
    public SignatureGenerationException(Context context, Exception e) {
        super(context, e);
    }

    @Override
    public String getExceptionName() {
        return "SignatureGenerationException";
    }

    @Override
    public String getMessage() {
        return context.getString(R.string.C_excpdet_wrongSecretKey) + super.getMessage();
    }
}
