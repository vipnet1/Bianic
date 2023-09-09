package com.vippygames.bianic.rebalancing.report;

import com.vippygames.bianic.rebalancing.data_format.CoinDetails;

import java.util.List;

public class ReportCalculations {
    public double getAllCoinsUsdValue(List<CoinDetails> coinsDetails) {
        double totalUsdValue = 0;
        for (CoinDetails coinDetails : coinsDetails) {
            totalUsdValue += coinDetails.getTotalUsdValue();
        }
        return totalUsdValue;
    }

    public double getDeviationPercent(double currentPortfolioPercent, double desiredPortfolioPercent) {
        return Math.abs(100 * (currentPortfolioPercent / desiredPortfolioPercent) - 100);
    }

    public double calculateTargetQuantity(double currentPortfolioPercent,
                                          double desiredPortfolioPercent, double coinAmount) {
        double coinAmountAtDesiredPercent = (desiredPortfolioPercent / currentPortfolioPercent) * coinAmount;
        return coinAmountAtDesiredPercent - coinAmount;
    }

    public double getCurrentPortfolioPercent(double coinTotalUsdValue, double portfolioUsdValue) {
        return 100 * coinTotalUsdValue / portfolioUsdValue;
    }

    public boolean shouldRebalance(double thresholdRebalancingPercent, double currentPortfolioPercent,
                                   double desiredPortfolioPercent) {
        double difference = thresholdRebalancingPercent / 100;
        return currentPortfolioPercent < desiredPortfolioPercent * (1 - difference)
                || currentPortfolioPercent > desiredPortfolioPercent * (1 + difference);
    }
}
