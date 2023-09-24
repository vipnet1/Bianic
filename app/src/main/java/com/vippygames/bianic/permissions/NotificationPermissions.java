package com.vippygames.bianic.permissions;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vippygames.bianic.notifications.NotificationType;

public class NotificationPermissions {
    public boolean havePostNotificationsPermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
            return true;
        }

        int permissionState = ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isChannelEnabled(Context context, NotificationType notificationType) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = manager.getNotificationChannel(notificationType.getChannelId(context));

        return channel == null || channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
    }

    public boolean isChannelExists(Context context, NotificationType notificationType) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return manager.getNotificationChannel(notificationType.getChannelId(context)) != null;
    }

    public void requestPostNotificationsPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }
}
