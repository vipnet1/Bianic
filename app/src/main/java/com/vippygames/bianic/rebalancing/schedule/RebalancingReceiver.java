package com.vippygames.bianic.rebalancing.schedule;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vippygames.bianic.consts.ConfigurationConsts;

public class RebalancingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean cancelAlarm = intent.getBooleanExtra(ConfigurationConsts.CANCEL_ALARM_EXTRA, false);
        if (cancelAlarm) {
            cancelAlarm(context);
            return;
        }

        Intent serviceIntent = new Intent(context, RebalancingCheckIntentService.class);
        context.startService(serviceIntent);
    }

    private void cancelAlarm(Context context) {
        Intent rrIntent = new Intent(context, RebalancingReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, rrIntent, PendingIntent.FLAG_IMMUTABLE);

        RebalancingAlarm rebalancingAlarm = new RebalancingAlarm(context);
        rebalancingAlarm.cancel(pendingIntent);
    }
}