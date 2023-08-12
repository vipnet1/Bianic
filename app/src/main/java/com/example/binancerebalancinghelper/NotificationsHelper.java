package com.example.binancerebalancinghelper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.binancerebalancinghelper.consts.NotificationsConsts;

public class NotificationsHelper {
    private final Context context;

    public NotificationsHelper(Context context) {
        this.context = context;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationsConsts.CHANNEL_ID, NotificationsConsts.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NotificationsConsts.CHANNEL_DESCRIPTION);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void pushNotification(String title, String text, int notificationId) {
        createNotificationChannel();
        Notification notification = getNotification(title, text, false);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(notificationId, notification);
    }

    public Notification getPersistentNotification(String title, String text) {
        createNotificationChannel();
        return getNotification(title, text, true);
    }

    private Notification getNotification(String title, String text, boolean setOngoing) {
        Intent intent = new Intent(context, NotificationsHelper.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationsConsts.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(setOngoing);

        return builder.build();
    }
}
