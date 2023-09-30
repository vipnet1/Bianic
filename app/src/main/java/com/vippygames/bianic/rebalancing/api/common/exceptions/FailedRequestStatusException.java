package com.vippygames.bianic.rebalancing.api.common.exceptions;

import android.content.Context;

import com.vippygames.bianic.R;
import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class FailedRequestStatusException extends NormalException {
    private final int status;
    private final String message;
    private final String customMessage;

    public FailedRequestStatusException(Context context, int status, String message) {
        super(context);
        this.status = status;
        this.message = message;
        this.customMessage = generateCustomMessage();
    }

    private String generateCustomMessage() {
        if (message.contains("\"code\":-2014") || message.contains("\"code\":-2015")) {
            return context.getString(R.string.C_excpdet_wrongApiKey);
        }

        if (message.contains("\"code\":-1022")) {
            return context.getString(R.string.C_excpdet_wrongSecretKey);
        }

        if (message.contains("\"code\":-1021")) {
            return context.getString(R.string.C_excpdet_likelySlowOrNoNetwork);
        }

        if (message.contains("\"code\":-1121")) {
            return context.getString(R.string.C_excpdet_binanceRemovedCoins);
        }

        return "";
    }

    @Override
    public String getMessage() {
        return "Failed status code - " + status + ". " + customMessage + " The message - " + this.message;
    }

    @Override
    public String getExceptionName() {
        return "FailedRequestStatusException";
    }
}
