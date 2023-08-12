package com.example.binancerebalancinghelper.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.binancerebalancinghelper.R;
import com.example.binancerebalancinghelper.consts.NotificationsConsts;
import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;
import com.example.binancerebalancinghelper.shared_preferences.SharedPreferencesHelper;

public class NotificationsHelper {
    private final Context context;

    public NotificationsHelper(Context context) {
        this.context = context;
    }

    public void pushNotification(String title, String text, NotificationType notificationType) {
        createNotificationChannel();
        Notification notification = getNotification(title, text, false);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(getNextNotificationId(notificationType), notification);
    }

    public Notification getPersistentNotification(String title, String text) {
        createNotificationChannel();
        return getNotification(title, text, true);
    }

    public int getNextNotificationId(NotificationType notificationType) {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        String sp_key = SharedPrefsConsts.NEXT_NOTIFICATION_TYPE_ID_PREFIX + notificationType.toString();

        int notificationId = sharedPreferencesHelper.getInt(sp_key, notificationType.getMinNotificationId());

        if(notificationId >= notificationType.getMaxNotificationId()) {
            sharedPreferencesHelper.setInt(sp_key, notificationType.getMinNotificationId());
        }
        else {
            sharedPreferencesHelper.setInt(sp_key, notificationId + 1);
        }

        return notificationId;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotificationsConsts.CHANNEL_ID, NotificationsConsts.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NotificationsConsts.CHANNEL_DESCRIPTION);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
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
