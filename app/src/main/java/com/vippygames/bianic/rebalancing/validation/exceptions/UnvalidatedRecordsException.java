package com.vippygames.bianic.rebalancing.validation.exceptions;

public class UnvalidatedRecordsException extends Exception {
    @Override
    public String getMessage() {
        return "You need to validate records by clicking the button in the main page.";
    }
}

