package com.vippygames.bianic.rebalancing.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.utils.RebalanceActivationUtils;
import com.vippygames.bianic.utils.RebalanceTestUtils;

public class RebalancingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!shouldRebalanceCheck(context)) {
            RebalanceActivationUtils rebalanceActivationUtils = new RebalanceActivationUtils(context);
            rebalanceActivationUtils.stopServiceAndAlarm();

            ConfigurationManager configurationManager = new ConfigurationManager(context);
            configurationManager.setIsRebalancingActivated(0);
            return;
        }

        Intent serviceIntent = new Intent(context, RebalancingCheckIntentService.class);
        context.startService(serviceIntent);
    }

    private boolean shouldRebalanceCheck(Context context) {
        RebalanceTestUtils rebalanceTestUtils = new RebalanceTestUtils(context);

        return rebalanceTestUtils.testIsRebalancingSettingOn() // setting activated
                && rebalanceTestUtils.testRebalanceCheckNotificationsPermissionsOn() // have permissions
                && rebalanceTestUtils.testRebalanceCheckNotificationActive(); // Showing indication that running
    }
}