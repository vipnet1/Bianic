package com.vippygames.bianic.rebalancing.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.vippygames.bianic.consts.ScheduleConsts;

public class RebalancingAlarm {
    private final Context applicationContext;
    private final AlarmManager alarmManager;

    public RebalancingAlarm(Context context) {
        this.applicationContext = context.getApplicationContext();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void startAlarm(int validationIntervalMinutes) {
        PendingIntent pendingIntent = createAlarmPendingIntent();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + ScheduleConsts.DELAY_TO_START_ALARM,
                ((long) validationIntervalMinutes) * 60 * 1000, pendingIntent);
    }

    public void cancelAlarm() {
        PendingIntent pendingIntent = createAlarmPendingIntent();
        alarmManager.cancel(pendingIntent);
    }

    public void startRebalancingService() {
        Intent intent = new Intent(applicationContext, RebalancingForegroundService.class);
        applicationContext.startForegroundService(intent);
    }

    public void stopRebalancingService() {
        Intent intent = new Intent(applicationContext, RebalancingForegroundService.class);
        applicationContext.stopService(intent);
    }

    private PendingIntent createAlarmPendingIntent() {
        Intent rrIntent = new Intent(applicationContext, RebalancingReceiver.class);
        return PendingIntent.getBroadcast(applicationContext, 0, rrIntent, PendingIntent.FLAG_IMMUTABLE);
    }
}
