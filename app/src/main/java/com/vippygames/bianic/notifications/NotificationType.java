package com.vippygames.bianic.notifications;

public enum NotificationType {
    REBALANCING_RUNNING(1, 1),
    REBALANCING_AVAILABLE(2, 2),
    NORMAL_EXCEPTION(3, 4),
    CRITICAL_EXCEPTION(5, 6),
    FATAL_EXCEPTION(7, 8);

    private final int minNotificationId;
    private final int maxNotificationId;

    public int getMinNotificationId() {
        return minNotificationId;
    }

    public int getMaxNotificationId() {
        return maxNotificationId;
    }

    NotificationType(int minNotificationId, int maxNotificationId) {
        this.minNotificationId = minNotificationId;
        this.maxNotificationId = maxNotificationId;
    }
}
