package com.vippygames.bianic.notifications;

public enum NotificationType {
    REBALANCING_RUNNING("rebalance_check_running_channel", "Rebalance Check Running"
            , "Indicates that performing rebalance check in background. Required for it to work.", 1),
    REBALANCING_AVAILABLE("rebalance_available_channel", "Rebalance Available",
            "Reached threshold you set you want to rebalance at.", 1),
    NORMAL_EXCEPTION("normal_exception_channel", "Normal Exception",
            "Fixable problem occurred.", 1),
    CRITICAL_EXCEPTION("critical_exception_channel", "Critical Exception",
            "Problem we didn't expect to happen.", 1),
    FATAL_EXCEPTION("fatal_exception_channel", "Fatal Exception",
            "Problem storing other exceptions.", 1);

    private final String channelId;
    private final String channelName;
    private final String channelDescription;
    private final int maxNotificationsCount;

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public int getMaxNotificationsCount() {
        return maxNotificationsCount;
    }

    NotificationType(String channelId, String channelName, String channelDescription, int maxNotificationsCount) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.maxNotificationsCount = maxNotificationsCount;
    }
}
