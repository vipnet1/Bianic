package com.vippygames.bianic.rebalancing.watch.threshold;

import android.content.Context;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.notifications.NotificationsHelper;
import com.vippygames.bianic.rebalancing.data_format.CoinDetails;
import com.vippygames.bianic.rebalancing.report.ReportCalculations;
import com.vippygames.bianic.rebalancing.report.ReportGenerator;

import java.util.List;

public class ThresholdWatch {
    private final Context context;

    public ThresholdWatch(Context context) {
        this.context = context;
    }

    public void check(List<CoinDetails> coinsDetails) {
        CoinDetails coinDetails = getRebalancingCoinDetailsCause(coinsDetails);
        if(coinDetails == null) {
            return;
        }

        ReportGenerator reportGenerator = new ReportGenerator(context);
        showCanRebalanceNotification("Coin " + coinDetails.getSymbol() + " reached threshold to rebalance.");
        reportGenerator.generateReport(coinsDetails);
    }

    // if should rebalance get CoinDetails that is the reason, if not returns null
    private CoinDetails getRebalancingCoinDetailsCause(List<CoinDetails> coinsDetails) {
        ReportCalculations reportCalculations = new ReportCalculations();
        double portfolioUsdValue = reportCalculations.getAllCoinsUsdValue(coinsDetails);

        ConfigurationManager configurationManager = new ConfigurationManager(context);
        float thresholdRebalancingPercent = configurationManager.getThresholdRebalancingPercent();

        for (CoinDetails coinDetails : coinsDetails) {
            double currentPortfolioPercent = reportCalculations.getCurrentPortfolioPercent(
                    coinDetails.getTotalUsdValue(), portfolioUsdValue
            );
            double desiredPortfolioPercent = coinDetails.getDesiredPortfolioPercent();

            if (reportCalculations.shouldRebalance(thresholdRebalancingPercent, currentPortfolioPercent, desiredPortfolioPercent)) {
                return coinDetails;
            }
        }

        return null;
    }

    private void showCanRebalanceNotification(String message) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(context);
        notificationsHelper.pushNotification(NotificationType.REBALANCING_AVAILABLE,
                "Rebalancing available, report generated.", message, false);
    }
}
