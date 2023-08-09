package com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions;

import java.io.IOException;

public class NetworkRequestException extends IOException {
    public NetworkRequestException(IOException e) {
        super(e);
    }
}
