package com.example.binancerebalancinghelper.rebalancing.schedule;

import android.app.IntentService;
import android.content.Intent;

import com.example.binancerebalancinghelper.NotificationsHelper;
import com.example.binancerebalancinghelper.SharedPreferencesHelper;
import com.example.binancerebalancinghelper.consts.NotificationsConsts;

public class RebalancingCheckIntentService extends IntentService {

    public RebalancingCheckIntentService() {
        super("RebalancingCheckIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);

        int count = sharedPreferencesHelper.getInt("count", 0);
        count++;
        sharedPreferencesHelper.setInt("count", count);

        NotificationsHelper notificationsHelper = new NotificationsHelper(this);
        notificationsHelper.pushNotification("My Title", "My Text " + count, NotificationsConsts.MESSAGE_NOTIFICATION_ID);
    }
}