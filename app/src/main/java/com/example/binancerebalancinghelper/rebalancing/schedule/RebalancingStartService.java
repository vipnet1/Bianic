package com.example.binancerebalancinghelper.rebalancing.schedule;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.binancerebalancinghelper.NotificationsHelper;
import com.example.binancerebalancinghelper.consts.NotificationsConsts;

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

        startForeground(NotificationsConsts.REBALANCING_RUNNING_NOTIFICATION_ID, notification);

        RebalancingAlarm rebalancingAlarm = new RebalancingAlarm(this);
        rebalancingAlarm.start();

        return START_STICKY;
    }
}
