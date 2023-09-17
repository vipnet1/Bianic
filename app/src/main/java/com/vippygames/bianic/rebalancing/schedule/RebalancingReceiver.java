package com.vippygames.bianic.rebalancing.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.notifications.NotificationsHelper;
import com.vippygames.bianic.permissions.NotificationPermissions;

public class RebalancingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!shouldRebalanceCheck(context)) {
            RebalancingAlarm rebalancingAlarm = new RebalancingAlarm(context);
            rebalancingAlarm.cancelAlarm();
            rebalancingAlarm.stopRebalancingService();

            ConfigurationManager configurationManager = new ConfigurationManager(context);
            configurationManager.setIsRebalancingActivated(0);
            return;
        }

        Intent serviceIntent = new Intent(context, RebalancingCheckIntentService.class);
        context.startService(serviceIntent);
    }

    private boolean shouldRebalanceCheck(Context context) {
        // don't if isn't activated according to settings
        ConfigurationManager configurationManager = new ConfigurationManager(context);
        boolean shouldRebalanceCheck = configurationManager.isRebalancingActivated() == 1;
        if (!shouldRebalanceCheck) {
            return false;
        }

        // don't if not notification permissions/rebalancing channel permissions
        NotificationPermissions notificationPermissions = new NotificationPermissions();
        if (!notificationPermissions.havePostNotificationsPermission(context)
                || !notificationPermissions.isChannelEnabled(context, NotificationType.REBALANCING_RUNNING)) {
            return false;
        }

        // don't if rebalancing notification isn't active - means we should stop
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

        return isRebalancingNotificationActive;
    }
}