package com.vippygames.bianic.rebalancing.schedule;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.vippygames.bianic.R;
import com.vippygames.bianic.notifications.NotificationInfo;
import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.notifications.NotificationsHelper;

public class RebalancingForegroundService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // no need to check notification permission here as called from ConfigureActivity and permission checked there
    @Override
    public void onCreate() {
        super.onCreate();

        NotificationsHelper notificationsHelper = new NotificationsHelper(this);
        NotificationInfo notificationInfo = notificationsHelper.pushNotification(NotificationType.REBALANCING_RUNNING,
                getString(R.string.C_rebchk_notification_checkRunningTitle), getString(R.string.C_rebchk_notification_checkRunningNotChecked), true);
        startForeground(notificationInfo.getNotificationId(), notificationInfo.getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
