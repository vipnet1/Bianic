package com.vippygames.bianic.rebalancing.api.common.network_request.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

import java.io.IOException;

public class NetworkRequestException extends NormalException {
    private final String customMessage;

    public NetworkRequestException(IOException e) {
        super(e);
        this.customMessage = generateCustomMessage();
    }

    @Override
    public String getExceptionName() {
        return "NetworkRequestException";
    }

    private String generateCustomMessage() {
        String message = super.getMessage();
        if (message == null) {
            return "";
        }

        if (message.contains("java.net.SocketTimeoutException")) {
            return "Likely too bad/slow internet connection. ";
        }

        if (message.contains("No address associated with hostname") || message.contains("Failed to connect to")) {
            return "Likely no internet connection. ";
        }

        if (message.contains("java.net.")) {
            return "Likely or slow network connection or no network at all. ";
        }

        return "";
    }

    @Override
    public String getMessage() {
        return customMessage + super.getMessage();
    }
}
