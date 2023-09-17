package com.vippygames.bianic.utils;

import android.content.Context;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.notifications.NotificationsHelper;
import com.vippygames.bianic.permissions.NotificationPermissions;

/*
Holds tests that other classes use to decide what actions to take in relation with rebalance check
 */
public class RebalanceTestUtils {
    private final Context context;

    public RebalanceTestUtils(Context context) {
        this.context = context;
    }

    public boolean testIsRebalancingSettingOn() {
        ConfigurationManager configurationManager = new ConfigurationManager(context);
        return configurationManager.isRebalancingActivated() == 1;
    }

    public boolean testHaveCurrentBootTime() {
        BootUtils bootUtils = new BootUtils(context);
        return bootUtils.haveCurrentBootTime();
    }

    public boolean testRebalanceCheckNotificationsPermissionsOn() {
        NotificationPermissions notificationPermissions = new NotificationPermissions();
        return notificationPermissions.havePostNotificationsPermission(context)
                && notificationPermissions.isChannelEnabled(context, NotificationType.REBALANCING_RUNNING);
    }

    public boolean testRebalanceCheckNotificationActive() {
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
