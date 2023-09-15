package com.vippygames.bianic.rebalancing.schedule;

import android.app.IntentService;
import android.content.Intent;

import com.vippygames.bianic.notifications.NotificationsHelper;
import com.vippygames.bianic.rebalancing.Rebalancer;
import com.vippygames.bianic.utils.StringUtils;

import java.util.Date;

public class RebalancingCheckIntentService extends IntentService {

    public RebalancingCheckIntentService() {
        super("RebalancingCheckIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        StringUtils stringUtils = new StringUtils();
        NotificationsHelper notificationsHelper = new NotificationsHelper(this);
        notificationsHelper.updateRebalancingCheckNotification("Rebalancing Check", "Checked " + stringUtils.getCurrentTime());

        Rebalancer rebalancer = new Rebalancer(this);
        rebalancer.execute();
    }
}