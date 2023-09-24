package com.vippygames.bianic.rebalancing.api.common.response_parser.exceptions;

import android.content.Context;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class ResponseParseException extends NormalException {
    public ResponseParseException(Context context, Exception e) {
        super(context, e);
    }

    @Override
    public String getExceptionName() {
        return "ResponseParseException";
    }
}
