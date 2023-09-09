package com.vippygames.bianic.db.reports;

public class ReportsRecord {
    private String uuid;
    private boolean shouldRebalance;
    private double portfolioUsdValue;
    private int coinsCount;
    private double thresholdRebalancingPercent;
    private String highestDeviationCoin;
    private double highestDeviationPercent;
    private String createdAt;

    public ReportsRecord(String uuid, boolean shouldRebalance, double portfolioUsdValue,
                         int coinsCount, double thresholdRebalancingPercent,
                         String highestDeviationCoin, double highestDeviationPercent, String createdAt) {
        this.uuid = uuid;
        this.shouldRebalance = shouldRebalance;
        this.portfolioUsdValue = portfolioUsdValue;
        this.coinsCount = coinsCount;
        this.thresholdRebalancingPercent = thresholdRebalancingPercent;
        this.highestDeviationCoin = highestDeviationCoin;
        this.highestDeviationPercent = highestDeviationPercent;
        this.createdAt = createdAt;
    }

    public ReportsRecord(String uuid, boolean shouldRebalance, double portfolioUsdValue,
                         int coinsCount, double thresholdRebalancingPercent,
                         String highestDeviationCoin, double highestDeviationPercent) {
        this.uuid = uuid;
        this.shouldRebalance = shouldRebalance;
        this.portfolioUsdValue = portfolioUsdValue;
        this.coinsCount = coinsCount;
        this.thresholdRebalancingPercent = thresholdRebalancingPercent;
        this.highestDeviationCoin = highestDeviationCoin;
        this.highestDeviationPercent = highestDeviationPercent;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean shouldRebalance() {
        return shouldRebalance;
    }

    public double getPortfolioUsdValue() {
        return portfolioUsdValue;
    }

    public int getCoinsCount() {
        return coinsCount;
    }

    public double getThresholdRebalancingPercent() {
        return thresholdRebalancingPercent;
    }

    public String getHighestDeviationCoin() {
        return highestDeviationCoin;
    }

    public double getHighestDeviationPercent() {
        return highestDeviationPercent;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
