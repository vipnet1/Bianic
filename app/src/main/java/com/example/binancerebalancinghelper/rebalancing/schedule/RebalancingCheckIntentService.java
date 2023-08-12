package com.example.binancerebalancinghelper.rebalancing.schedule;

import android.app.IntentService;
import android.content.Intent;

import com.example.binancerebalancinghelper.rebalancing.Rebalancer;

public class RebalancingCheckIntentService extends IntentService {

    public RebalancingCheckIntentService() {
        super("RebalancingCheckIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        Rebalancer rebalancer = new Rebalancer(this);
        rebalancer.execute();
    }
}