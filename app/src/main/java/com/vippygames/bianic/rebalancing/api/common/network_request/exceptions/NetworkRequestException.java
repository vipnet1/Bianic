package com.vippygames.bianic.rebalancing.api.common.network_request.exceptions;

import android.content.Context;

import com.vippygames.bianic.R;
import com.vippygames.bianic.exception_handle.exceptions.NormalException;

import java.io.IOException;

public class NetworkRequestException extends NormalException {
    private final String customMessage;

    public NetworkRequestException(Context context, IOException e) {
        super(context, e);
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

        if (message.contains("No address associated with hostname") || message.contains("Failed to connect to")) {
            return context.getString(R.string.C_excpdet_noInternetConnection);
        }

        if (message.contains("java.net.")) {
            return context.getString(R.string.C_excpdet_likelySlowOrNoNetwork);
        }

        return "";
    }

    @Override
    public String getMessage() {
        return customMessage + super.getMessage();
    }
}
