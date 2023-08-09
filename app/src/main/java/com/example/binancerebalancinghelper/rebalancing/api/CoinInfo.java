package com.example.binancerebalancinghelper.rebalancing.api;

public class CoinInfo {
    public String symbol;
//    public double price;
    public String amount;

    public CoinInfo(String symbol, String amount) {
        this.symbol = symbol;
        this.amount = amount;
    }
}
