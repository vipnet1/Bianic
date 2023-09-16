package com.vippygames.bianic.rebalancing.api.common.network_request.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

import java.io.IOException;

public class NetworkRequestException extends NormalException {
    public NetworkRequestException(IOException e) {
        super(e);
    }

    @Override
    public String getExceptionName() {
        return "NetworkRequestException";
    }
}
