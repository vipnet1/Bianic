package com.vippygames.bianic.rebalancing.data_format;

import com.vippygames.bianic.rebalancing.api.coins_amount.CoinAmount;
import com.vippygames.bianic.rebalancing.api.coins_price.CoinPrice;

public class CoinDetails {
    private String symbol;
    private double amount;
    private double price;
    private double coinPortfolioUsdValue;

    public CoinDetails(CoinAmount coinAmount, CoinPrice coinPrice) {
        this.symbol = coinAmount.getSymbol();
        this.amount = coinAmount.getAmount();
        this.price = coinPrice.getPrice();
        this.coinPortfolioUsdValue = this.amount * this.price;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public double getCoinPortfolioUsdValue() {
        return coinPortfolioUsdValue;
    }
}
