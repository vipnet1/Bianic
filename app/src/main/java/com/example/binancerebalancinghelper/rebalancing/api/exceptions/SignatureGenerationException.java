package com.example.binancerebalancinghelper.rebalancing.api.exceptions;

public class SignatureGenerationException extends Exception {
    public SignatureGenerationException(Exception e) {
        super(e);
    }
}
