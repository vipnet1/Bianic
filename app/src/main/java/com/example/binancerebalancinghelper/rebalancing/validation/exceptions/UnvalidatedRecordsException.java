package com.example.binancerebalancinghelper.rebalancing.validation.exceptions;

public class UnvalidatedRecordsException extends Exception {
    @Override
    public String getMessage() {
        return "You need to validate records by clicking the button in main page.";
    }
}


