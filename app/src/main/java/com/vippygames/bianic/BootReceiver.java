package com.vippygames.bianic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.utils.BootUtils;
import com.vippygames.bianic.utils.RebalanceActivationUtils;
import com.vippygames.bianic.utils.RebalanceTestUtils;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if (!shouldStartRebalanceCheck(context)) {
                return;
            }

            BootUtils bootUtils = new BootUtils(context);
            bootUtils.setCurrentBootTime();

            ConfigurationManager configurationManager = new ConfigurationManager(context);
            RebalanceActivationUtils rebalanceActivationUtils = new RebalanceActivationUtils(context);
            // if passed shouldStartRebalanceCheck means newIsRebalancingActivated is 1
            rebalanceActivationUtils.changeRebalancerAfterBoot(configurationManager.getValidationInterval(), 1);
        }
    }

    private boolean shouldStartRebalanceCheck(Context context) {
        RebalanceTestUtils rebalanceTestUtils = new RebalanceTestUtils(context);

        return rebalanceTestUtils.testIsRebalancingSettingOn() // setting activated
                && !rebalanceTestUtils.testHaveCurrentBootTime() // first time after boot
                && rebalanceTestUtils.testRebalanceCheckNotificationsPermissionsOn() // have permissions
                && !rebalanceTestUtils.testRebalanceCheckNotificationActive(); // Not already running
    }
}