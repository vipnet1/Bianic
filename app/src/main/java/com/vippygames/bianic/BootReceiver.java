package com.vippygames.bianic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.notifications.NotificationsHelper;
import com.vippygames.bianic.permissions.NotificationPermissions;
import com.vippygames.bianic.rebalancing.schedule.RebalancingAlarm;
import com.vippygames.bianic.utils.BootUtils;

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
            RebalancingAlarm rebalancingAlarm = new RebalancingAlarm(context);
            rebalancingAlarm.startAlarm(configurationManager.getValidationInterval());
            rebalancingAlarm.startRebalancingService();
        }
    }

    private boolean shouldStartRebalanceCheck(Context context) {
        // don't if isn't activated according to settings
        ConfigurationManager configurationManager = new ConfigurationManager(context);
        boolean shouldRebalanceCheck = configurationManager.isRebalancingActivated() == 1;
        if (!shouldRebalanceCheck) {
            return false;
        }

        // don't if someone changed settings of whether rebalance or not after boot
        BootUtils bootUtils = new BootUtils(context);
        if (bootUtils.haveCurrentBootTime()) {
            return false;
        }

        // don't if not notification permissions/rebalancing channel permissions
        NotificationPermissions notificationPermissions = new NotificationPermissions();
        if (!notificationPermissions.havePostNotificationsPermission(context)
                || !notificationPermissions.isChannelEnabled(context, NotificationType.REBALANCING_RUNNING)) {
            return false;
        }

        // don't if rebalancing notification active - means already running
        NotificationsHelper notificationsHelper = new NotificationsHelper(context);
        int minId = NotificationType.REBALANCING_RUNNING.getMinNotificationId();
        int maxId = NotificationType.REBALANCING_RUNNING.getMaxNotificationId();

        boolean isRebalancingNotificationActive = false;
        for (int i = minId; i <= maxId; i++) {
            if (notificationsHelper.isNotificationActive(i)) {
                isRebalancingNotificationActive = true;
                break;
            }
        }

        return !isRebalancingNotificationActive;
    }
}