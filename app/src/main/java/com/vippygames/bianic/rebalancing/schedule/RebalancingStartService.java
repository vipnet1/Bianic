package com.vippygames.bianic.rebalancing.schedule;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.vippygames.bianic.notifications.NotificationInfo;
import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.notifications.NotificationsHelper;

public class RebalancingStartService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationsHelper notificationsHelper = new NotificationsHelper(this);
        NotificationInfo notificationInfo = notificationsHelper.pushNotification(NotificationType.REBALANCING_RUNNING,
                "Rebalancing Check", "Checking whether should rebalance", true);
        startForeground(notificationInfo.getNotificationId(), notificationInfo.getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RebalancingAlarm rebalancingAlarm = new RebalancingAlarm(this);
        rebalancingAlarm.start();

        return START_STICKY;
    }
}
