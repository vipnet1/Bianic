package com.vippygames.bianic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.rebalancing.schedule.RebalancingStartService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            ConfigurationManager configurationManager = new ConfigurationManager(context);
            if(configurationManager.isRebalancingActivated() == 0) {
                return;
            }

            Intent startIntent = new Intent(context.getApplicationContext(), RebalancingStartService.class);
            context.startForegroundService(startIntent);
        }
    }
}