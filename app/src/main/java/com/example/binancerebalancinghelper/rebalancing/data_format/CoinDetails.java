package com.example.binancerebalancinghelper.rebalancing.data_format;

import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.CoinAmount;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.CoinPrice;

public class CoinDetails {
    public String symbol;
    public double amount;
    public double price;
    public double coinPortfolioUsdValue;
    public double percentOfPortfolio;

    public CoinDetails(CoinAmount coinAmount, CoinPrice coinPrice) {
        this.symbol = coinAmount.symbol;
        this.amount = coinAmount.amount;
        this.price = coinPrice.price;
        this.coinPortfolioUsdValue = coinAmount.amount * coinPrice.price;
        this.percentOfPortfolio = -1;
    }
}
