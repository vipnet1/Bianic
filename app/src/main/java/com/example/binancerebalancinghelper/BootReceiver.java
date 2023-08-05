package com.example.binancerebalancinghelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.binancerebalancinghelper.rebalancing.schedule.RebalancingAlarm;
import com.example.binancerebalancinghelper.rebalancing.schedule.RebalancingStartService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent startIntent = new Intent(context, RebalancingStartService.class);
            context.startService(startIntent);
        }
    }
}