package com.vippygames.bianic.rebalancing.schedule;

import android.app.IntentService;
import android.content.Intent;

import com.vippygames.bianic.R;
import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.notifications.NotificationsHelper;
import com.vippygames.bianic.rebalancing.Rebalancer;
import com.vippygames.bianic.utils.StringUtils;

public class RebalancingCheckIntentService extends IntentService {

    public RebalancingCheckIntentService() {
        super("RebalancingCheckIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        StringUtils stringUtils = new StringUtils(this);
        NotificationsHelper notificationsHelper = new NotificationsHelper(this);
        notificationsHelper.pushNotification(NotificationType.REBALANCING_RUNNING, getString(R.string.C_rebchk_notification_checkRunningTitle),
                getString(R.string.C_rebchk_notification_checkRunningCheckTime) + stringUtils.getCurrentTime(), true);

        Rebalancer rebalancer = new Rebalancer(this);
        rebalancer.execute();
    }
}