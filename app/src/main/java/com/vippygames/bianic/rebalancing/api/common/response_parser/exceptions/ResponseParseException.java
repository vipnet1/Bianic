package com.vippygames.bianic.rebalancing.api.common.response_parser.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class ResponseParseException extends NormalException {
    public ResponseParseException(Exception e) {
        super(e);
    }

    @Override
    public String getExceptionName() {
        return "ResponseParseException";
    }
}
