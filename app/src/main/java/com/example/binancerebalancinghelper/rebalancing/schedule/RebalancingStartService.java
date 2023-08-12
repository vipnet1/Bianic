package com.example.binancerebalancinghelper.rebalancing.schedule;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.binancerebalancinghelper.notifications.NotificationType;
import com.example.binancerebalancinghelper.notifications.NotificationsHelper;

public class RebalancingStartService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(this);
        Notification notification = notificationsHelper.getPersistentNotification("Rebalancing Check",
                "Checking whether should rebalance");

        int notificationId = notificationsHelper.getNextNotificationId(NotificationType.REBALANCING_RUNNING);
        startForeground(notificationId, notification);

        RebalancingAlarm rebalancingAlarm = new RebalancingAlarm(this);
        rebalancingAlarm.start();

        return START_STICKY;
    }
}
