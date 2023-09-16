package com.vippygames.bianic.rebalancing.validation.exceptions;

import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class UnvalidatedRecordsException extends NormalException {
    @Override
    public String getMessage() {
        return "You need to validate records by clicking the button in the main page.";
    }

    @Override
    public String getExceptionName() {
        return "UnvalidatedRecordsException";
    }
}


