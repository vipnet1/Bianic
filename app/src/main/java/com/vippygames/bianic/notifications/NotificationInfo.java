package com.vippygames.bianic.notifications;

import android.app.Notification;

public class NotificationInfo {
    private final Notification notification;
    private final int notificationId;

    public NotificationInfo(Notification notification, int notificationId) {
        this.notification = notification;
        this.notificationId = notificationId;
    }

    public Notification getNotification() {
        return notification;
    }

    public int getNotificationId() {
        return notificationId;
    }
}
