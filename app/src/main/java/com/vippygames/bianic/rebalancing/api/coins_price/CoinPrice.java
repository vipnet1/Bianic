package com.vippygames.bianic.rebalancing.api.coins_price;

public class CoinPrice {
    private String symbol;
    private double price;

    public CoinPrice(String symbol, double amount) {
        this.symbol = symbol;
        this.price = amount;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }
}
