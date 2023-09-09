package com.vippygames.bianic.rebalancing.data_format;

import com.vippygames.bianic.db.threshold_allocation.ThresholdAllocationRecord;
import com.vippygames.bianic.rebalancing.api.coins_amount.CoinAmount;
import com.vippygames.bianic.rebalancing.api.coins_price.CoinPrice;

public class CoinDetails {
    private String symbol;
    private double amount;
    private double price;
    private double totalUsdValue;
    private double desiredPortfolioPercent;

    public CoinDetails(CoinAmount coinAmount, CoinPrice coinPrice, ThresholdAllocationRecord record) {
        this.symbol = coinAmount.getSymbol();
        this.amount = coinAmount.getAmount();
        this.price = coinPrice.getPrice();
        this.totalUsdValue = this.amount * this.price;
        this.desiredPortfolioPercent = record.getDesiredAllocation();
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

    public double getTotalUsdValue() {
        return totalUsdValue;
    }

    public double getDesiredPortfolioPercent() {
        return desiredPortfolioPercent;
    }
}
