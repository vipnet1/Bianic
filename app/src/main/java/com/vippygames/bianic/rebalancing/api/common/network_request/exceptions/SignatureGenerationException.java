package com.vippygames.bianic.rebalancing.api.common.network_request.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class SignatureGenerationException extends NormalException {
    public SignatureGenerationException(Exception e) {
        super(e);
    }

    @Override
    public String getExceptionName() {
        return "SignatureGenerationException";
    }

    @Override
    public String getMessage() {
        return "Likely wrong secret key. " + super.getMessage();
    }
}
