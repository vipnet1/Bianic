package com.vippygames.bianic.rebalancing.api.coins_amount;

public class CoinAmount {
    private String symbol;
    private double amount;

    public CoinAmount(String symbol, double amount) {
        this.symbol = symbol;
        this.amount = amount;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getAmount() {
        return amount;
    }
}
