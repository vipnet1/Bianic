package com.example.binancerebalancinghelper.rebalancing.api.coins_amount;

public class CoinAmount {
    public String symbol;
    public double amount;

    public CoinAmount(String symbol, double amount) {
        this.symbol = symbol;
        this.amount = amount;
    }
}
