package com.example.binancerebalancinghelper.rebalancing.api.coins_price;

public class CoinPrice {
    public String symbol;
    public double price;

    public CoinPrice(String symbol, double amount) {
        this.symbol = symbol;
        this.price = amount;
    }
}
