package com.vippygames.bianic.permissions;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.permissions.dialogs.NotificationPermissionDialogFragment;

public class NotificationPermissions {
    public boolean havePostNotificationsPermission(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        return notificationManagerCompat.areNotificationsEnabled();
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

    public void requestPostNotificationsPermission(FragmentActivity fragmentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(fragmentActivity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        } else {
            manualRequestNotifications(fragmentActivity);
        }
    }

    private void manualRequestNotifications(FragmentActivity fragmentActivity) {
        NotificationPermissionDialogFragment dialogFragment = new NotificationPermissionDialogFragment();
        dialogFragment.show(fragmentActivity.getSupportFragmentManager(), NotificationPermissionDialogFragment.TAG);
    }
}
