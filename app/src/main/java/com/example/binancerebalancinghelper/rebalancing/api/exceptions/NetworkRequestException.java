package com.example.binancerebalancinghelper.rebalancing.api.exceptions;

import java.io.IOException;

public class NetworkRequestException extends IOException {
    public NetworkRequestException(IOException e) {
        super(e);
    }
}
