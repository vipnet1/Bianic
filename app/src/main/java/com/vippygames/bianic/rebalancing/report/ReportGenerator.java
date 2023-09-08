package com.vippygames.bianic.rebalancing.report;

import android.content.Context;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.db.DetailedReports.DetailedReportsDb;
import com.vippygames.bianic.db.DetailedReports.DetailedReportsRecord;
import com.vippygames.bianic.db.Reports.ReportsDb;
import com.vippygames.bianic.db.Reports.ReportsRecord;
import com.vippygames.bianic.rebalancing.data_format.CoinDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportGenerator {
    private final Context context;

    public ReportGenerator(Context context) {
        this.context = context;
    }

    public void generateReport(List<CoinDetails> coinsDetails) {
        String uuid = generateUuid();
        int coinsCount = coinsDetails.size();

        ConfigurationManager configurationManager = new ConfigurationManager(context);
        float thresholdRebalancingPercent = configurationManager.getThresholdRebalancingPercent();

        double portfolioUsdValue = getAllCoinsUsdValue(coinsDetails);
        double highestDeviationPercent = -1;
        String highestDeviationSymbol = "";
        boolean shouldRebalance = false;

        List<DetailedReportsRecord> detailedReportsRecords = new ArrayList<>();
        for (CoinDetails coinDetails : coinsDetails) {
            double currentPortfolioPercent = 100 * coinDetails.getTotalUsdValue() / portfolioUsdValue;
            double desiredPortfolioPercent = coinDetails.getDesiredPortfolioPercent();
            double deviationPercent = getDeviationPercent(currentPortfolioPercent, desiredPortfolioPercent);

            if(deviationPercent > highestDeviationPercent) {
                highestDeviationPercent = deviationPercent;
                highestDeviationSymbol = coinDetails.getSymbol();
            }

            if (shouldRebalance(thresholdRebalancingPercent, currentPortfolioPercent, desiredPortfolioPercent)) {
                shouldRebalance = true;
            }

            double coinAmount = coinDetails.getAmount();
            double targetQuantity = calculateTargetQuantity(currentPortfolioPercent,
                    desiredPortfolioPercent, coinAmount);

            DetailedReportsRecord detailedReportsRecord = new DetailedReportsRecord(
                    generateUuid(), uuid, coinDetails.getSymbol(), coinDetails.getDesiredPortfolioPercent(),
                    coinAmount, coinDetails.getPrice(), coinDetails.getTotalUsdValue(),
                    currentPortfolioPercent, targetQuantity);
            detailedReportsRecords.add(detailedReportsRecord);
        }

        ReportsRecord record = new ReportsRecord(uuid, shouldRebalance, portfolioUsdValue, coinsCount,
                thresholdRebalancingPercent, highestDeviationSymbol, highestDeviationPercent);

        ReportsDb reportsDb = new ReportsDb(context);
        reportsDb.saveRecord(record);

        DetailedReportsDb detailedReportsDb = new DetailedReportsDb(context);
        detailedReportsDb.saveRecords(detailedReportsRecords);
    }

    public double getAllCoinsUsdValue(List<CoinDetails> coinsDetails) {
        float totalUsdValue = 0.0f;
        for (CoinDetails coinDetails : coinsDetails) {
            totalUsdValue += coinDetails.getTotalUsdValue();
        }
        return totalUsdValue;
    }

    public double calculateTargetQuantity(double currentPortfolioPercent,
                                          double desiredPortfolioPercent, double coinAmount) {
        double coinAmountAtDesiredPercent = (desiredPortfolioPercent / currentPortfolioPercent) * coinAmount;
        return coinAmountAtDesiredPercent - coinAmount;
    }

    public boolean shouldRebalance(double thresholdRebalancingPercent, double currentPortfolioPercent,
                                   double desiredPortfolioPercent) {
        double difference = thresholdRebalancingPercent / 100;
        return currentPortfolioPercent < desiredPortfolioPercent * (1 - difference)
                || currentPortfolioPercent > desiredPortfolioPercent * (1 + difference);
    }

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }

    private double getDeviationPercent(double currentPortfolioPercent, double desiredPortfolioPercent) {
        return Math.abs(100 * (currentPortfolioPercent / desiredPortfolioPercent) - 100);
    }
}
