package com.vippygames.bianic.rebalancing.schedule;

import android.app.IntentService;
import android.content.Intent;

import com.vippygames.bianic.rebalancing.Rebalancer;

public class RebalancingCheckIntentService extends IntentService {

    public RebalancingCheckIntentService() {
        super("RebalancingCheckIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        Rebalancer rebalancer = new Rebalancer(this);
        rebalancer.execute();
    }
}