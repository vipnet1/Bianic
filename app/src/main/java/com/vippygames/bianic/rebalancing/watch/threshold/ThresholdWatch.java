package com.vippygames.bianic.rebalancing.watch.threshold;

import android.content.Context;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.notifications.NotificationsHelper;
import com.vippygames.bianic.rebalancing.data_format.CoinDetails;

import java.util.List;

public class ThresholdWatch {
    private final Context context;

    public ThresholdWatch(Context context) {
        this.context = context;
    }

    public void check(List<CoinDetails> coinsDetails) {
        double portfolioUsdValue = getAllCoinsUsdValue(coinsDetails);

        ConfigurationManager configurationManager = new ConfigurationManager(context);
        float thresholdRebalancingPercent = configurationManager.getThresholdRebalancingPercent();

        for (CoinDetails coinDetails : coinsDetails) {
            double currentPortfolioPercent = coinDetails.getTotalUsdValue() / portfolioUsdValue;
            double desiredPortfolioPercent = coinDetails.getDesiredPortfolioPercent();
            double diff = thresholdRebalancingPercent / 100;

            if (currentPortfolioPercent < desiredPortfolioPercent * (1 - diff)
                    || currentPortfolioPercent > desiredPortfolioPercent * (1 + diff)) {
                showCanRebalanceNotification("Coin " + coinDetails.getSymbol() + " reached threshold to rebalance.");
                return;
            }
        }
    }

    private double getAllCoinsUsdValue(List<CoinDetails> coinsDetails) {
        float totalUsdValue = 0.0f;
        for (CoinDetails coinDetails : coinsDetails) {
            totalUsdValue += coinDetails.getTotalUsdValue();
        }
        return totalUsdValue;
    }

    private void showCanRebalanceNotification(String message) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(context);
        notificationsHelper.pushNotification(NotificationType.REGULAR_MESSAGE,
                "Rebalancing available, report generated.", message);
    }
}
