package com.example.binancerebalancinghelper.rebalancing.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.binancerebalancinghelper.consts.RebalancingScheduleConsts;

public class RebalancingAlarm {
    private final AlarmManager alarmManager;
    private final PendingIntent pendingIntent;

    public RebalancingAlarm(Context context) {
        Intent intent = new Intent(context, RebalancingReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void start() {
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), RebalancingScheduleConsts.ALARM_INTERVAL_MILLIS, pendingIntent);
    }
}
