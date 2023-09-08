package com.vippygames.bianic.rebalancing.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RebalancingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, RebalancingCheckIntentService.class);
        context.startService(serviceIntent);
    }
}