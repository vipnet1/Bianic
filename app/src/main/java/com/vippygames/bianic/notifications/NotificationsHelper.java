package com.vippygames.bianic.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.vippygames.bianic.R;
import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.permissions.NotificationPermissions;
import com.vippygames.bianic.shared_preferences.SharedPreferencesHelper;

public class NotificationsHelper {
    private final Context context;

    public NotificationsHelper(Context context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    public void pushNotification(NotificationType notificationType, String title, String text) {
        NotificationPermissions notificationPermissions = new NotificationPermissions();
        if(!notificationPermissions.havePostNotificationsPermission(context)) {
            return;
        }

        createNotificationChannel(notificationType);
        Notification notification = getNotification(notificationType, title, text, false);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        manager.notify(getNextNotificationId(notificationType), notification);
    }

    public Notification getPersistentNotification(NotificationType notificationType, String title, String text) {
        createNotificationChannel(notificationType);
        return getNotification(notificationType, title, text, true);
    }

    public int getNextNotificationId(NotificationType notificationType) {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        String sp_key = SharedPrefsConsts.NEXT_NOTIFICATION_TYPE_ID_PREFIX + notificationType.getChannelId();

        int notificationId = sharedPreferencesHelper.getInt(sp_key, 0);

        if(notificationId >= notificationType.getMaxNotificationsCount()) {
            sharedPreferencesHelper.setInt(sp_key, 0);
        }
        else {
            sharedPreferencesHelper.setInt(sp_key, notificationId + 1);
        }

        return notificationId;
    }

    private void createNotificationChannel(NotificationType notificationType) {
        NotificationChannel channel = new NotificationChannel(notificationType.getChannelId(), notificationType.getChannelName(), NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(notificationType.getChannelDescription());
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    private Notification getNotification(NotificationType notificationType, String title, String text, boolean setOngoing) {
        Intent intent = new Intent(context, NotificationsHelper.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        int notificationIcon = getNotificationIcon(notificationType);
        Bitmap largeNotificationIcon = getLargeNotificationIcon(notificationType);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, notificationType.getChannelId())
                .setSmallIcon(notificationIcon)
                .setLargeIcon(largeNotificationIcon)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(setOngoing);

        return builder.build();
    }

    private int getNotificationIcon(NotificationType notificationType) {
        switch(notificationType) {
            case REBALANCING_AVAILABLE:
                return R.mipmap.ic_rebalancing_available;
            case NORMAL_EXCEPTION:
                return R.mipmap.ic_exception;
            case CRITICAL_EXCEPTION:
                return R.mipmap.ic_critical_exception;
            case FATAL_EXCEPTION:
                return R.mipmap.ic_fatal_exception;
            default:
                return R.mipmap.ic_launcher;
        }
    }

    private Bitmap getLargeNotificationIcon(NotificationType notificationType) {
        int largeNotificationNumber = -1;
        switch(notificationType) {
            case REBALANCING_AVAILABLE:
                largeNotificationNumber = R.drawable.ic_rebalancing_available_round;
                break;
            case NORMAL_EXCEPTION:
                largeNotificationNumber = R.drawable.ic_exception_round;
                break;
            case CRITICAL_EXCEPTION:
                largeNotificationNumber = R.drawable.ic_critical_exception_round;
                break;
            case FATAL_EXCEPTION:
                largeNotificationNumber = R.drawable.ic_fatal_exception_round;
                break;
            default:
                largeNotificationNumber = R.drawable.ic_launcher_round;
                break;
        }

        return BitmapFactory.decodeResource(context.getResources() , largeNotificationNumber);
    }
}
