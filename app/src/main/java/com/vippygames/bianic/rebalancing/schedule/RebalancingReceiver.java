package com.vippygames.bianic.rebalancing.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.permissions.NotificationPermissions;

public class RebalancingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!haveNeededNotificationPermissions(context)) {
            RebalancingAlarm rebalancingAlarm = new RebalancingAlarm(context);
            rebalancingAlarm.cancelAlarm();
            return;
        }

        Intent serviceIntent = new Intent(context, RebalancingCheckIntentService.class);
        context.startService(serviceIntent);
    }

    private boolean haveNeededNotificationPermissions(Context context) {
        NotificationPermissions notificationPermissions = new NotificationPermissions();
        return notificationPermissions.havePostNotificationsPermission(context)
                && notificationPermissions.isChannelEnabled(context, NotificationType.REBALANCING_RUNNING);
    }
}