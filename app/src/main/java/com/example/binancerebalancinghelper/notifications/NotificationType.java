package com.example.binancerebalancinghelper.notifications;

public enum NotificationType {
    REBALANCING_RUNNING(1, 1),
    REGULAR_MESSAGE(2, 4),
    NORMAL_EXCEPTION(5, 6),
    CRITICAL_EXCEPTION(7, 8),
    FATAL_EXCEPTION(9, 10),
    LAST_TIME_REBALANCE_WAS_AVAILABLE(11, 11);

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