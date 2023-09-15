package com.vippygames.bianic.notifications;

public enum NotificationType {
    REBALANCING_RUNNING("rebalance_check_running_channel", "Rebalance Check Running"
            , "Indicates that performing rebalance check in background. Required for it to work.", 1, 1),
    REBALANCING_AVAILABLE("rebalance_available_channel", "Rebalance Available",
            "Reached threshold you set you want to rebalance at.", 0, 0),
    NORMAL_EXCEPTION("normal_exception_channel", "Normal Exception",
            "Fixable problem occurred.", 2, 2),
    CRITICAL_EXCEPTION("critical_exception_channel", "Critical Exception",
            "Problem we didn't expect to happen.", 3, 3),
    FATAL_EXCEPTION("fatal_exception_channel", "Fatal Exception",
            "Problem storing other exceptions.", 4, 4);

    private final String channelId;
    private final String channelName;
    private final String channelDescription;
    private final int minNotificationId;
    private final int maxNotificationId;

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public int getMinNotificationId() {
        return minNotificationId;
    }

    public int getMaxNotificationId() {
        return maxNotificationId;
    }

    NotificationType(String channelId, String channelName, String channelDescription, int minNotificationId, int maxNotificationId) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.minNotificationId = minNotificationId;
        this.maxNotificationId = maxNotificationId;
    }
}
