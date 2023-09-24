package com.vippygames.bianic.notifications;

import android.content.Context;

import com.vippygames.bianic.R;

public enum NotificationType {
    REBALANCING_RUNNING(1, 1),
    REBALANCING_AVAILABLE(0, 0),
    NORMAL_EXCEPTION(2, 2),
    CRITICAL_EXCEPTION(3, 3),
    FATAL_EXCEPTION(4, 4);
    private final int minNotificationId;
    private final int maxNotificationId;

    public String getChannelId(Context context) {
        switch (this) {
            case REBALANCING_RUNNING:
                return context.getString(R.string.C_notificationType_rebalanceRunningChannelId);
            case REBALANCING_AVAILABLE:
                return context.getString(R.string.C_notificationType_rebalanceAvailableChannelId);
            case NORMAL_EXCEPTION:
                return context.getString(R.string.C_notificationType_normalExceptionChannelId);
            case CRITICAL_EXCEPTION:
                return context.getString(R.string.C_notificationType_criticalExceptionChannelId);
            case FATAL_EXCEPTION:
                return context.getString(R.string.C_notificationType_fatalExceptionChannelId);
            default: // should never get to this case
                return "";
        }
    }

    public String getChannelName(Context context) {
        switch (this) {
            case REBALANCING_RUNNING:
                return context.getString(R.string.C_notificationType_rebalanceRunningChannelName);
            case REBALANCING_AVAILABLE:
                return context.getString(R.string.C_notificationType_rebalanceAvailableChannelName);
            case NORMAL_EXCEPTION:
                return context.getString(R.string.C_notificationType_normalExceptionChannelName);
            case CRITICAL_EXCEPTION:
                return context.getString(R.string.C_notificationType_criticalExceptionChannelName);
            case FATAL_EXCEPTION:
                return context.getString(R.string.C_notificationType_fatalExceptionChannelName);
            default: // should never get to this case
                return "";
        }
    }

    public String getChannelDescription(Context context) {
        switch (this) {
            case REBALANCING_RUNNING:
                return context.getString(R.string.C_notificationType_rebalanceRunningChannelDesc);
            case REBALANCING_AVAILABLE:
                return context.getString(R.string.C_notificationType_rebalanceAvailableChannelDesc);
            case NORMAL_EXCEPTION:
                return context.getString(R.string.C_notificationType_normalExceptionChannelDesc);
            case CRITICAL_EXCEPTION:
                return context.getString(R.string.C_notificationType_criticalExceptionChannelDesc);
            case FATAL_EXCEPTION:
                return context.getString(R.string.C_notificationType_fatalExceptionChannelDesc);
            default: // should never get to this case
                return "";
        }
    }

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
