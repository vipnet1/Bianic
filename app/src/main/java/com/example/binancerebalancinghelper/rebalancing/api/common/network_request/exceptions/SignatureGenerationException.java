package com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions;

public class SignatureGenerationException extends Exception {
    public SignatureGenerationException(Exception e) {
        super(e);
    }
}
