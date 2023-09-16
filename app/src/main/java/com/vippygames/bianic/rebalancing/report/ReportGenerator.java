package com.vippygames.bianic.rebalancing.report;

import android.content.Context;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.db.reports.detailed_reports.DetailedReportsDb;
import com.vippygames.bianic.db.reports.detailed_reports.DetailedReportsRecord;
import com.vippygames.bianic.db.reports.ReportsDb;
import com.vippygames.bianic.db.reports.ReportsRecord;
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

        ReportCalculations reportCalculations = new ReportCalculations();

        double portfolioUsdValue = reportCalculations.getAllCoinsUsdValue(coinsDetails);
        double absHighestDeviationPercent = -1;
        double highestDeviationPercent = -1;
        String highestDeviationSymbol = "";
        boolean shouldRebalance = false;

        List<DetailedReportsRecord> detailedReportsRecords = new ArrayList<>();
        for (CoinDetails coinDetails : coinsDetails) {
            double currentPortfolioPercent = reportCalculations.getCurrentPortfolioPercent(
                    coinDetails.getTotalUsdValue(), portfolioUsdValue
            );
            double desiredPortfolioPercent = coinDetails.getDesiredPortfolioPercent();
            double deviationPercent = reportCalculations.getDeviationPercent(currentPortfolioPercent, desiredPortfolioPercent);
            double absDeviationPercent = Math.abs(deviationPercent);

            if (absDeviationPercent > absHighestDeviationPercent) {
                absHighestDeviationPercent = absDeviationPercent;
                highestDeviationPercent = deviationPercent;
                highestDeviationSymbol = coinDetails.getSymbol();
            }

            if (reportCalculations.shouldRebalance(thresholdRebalancingPercent, currentPortfolioPercent, desiredPortfolioPercent)) {
                shouldRebalance = true;
            }

            double coinAmount = coinDetails.getAmount();
            double targetQuantity = reportCalculations.calculateTargetQuantity(currentPortfolioPercent,
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

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
