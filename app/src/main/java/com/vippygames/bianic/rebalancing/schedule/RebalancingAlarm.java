package com.vippygames.bianic.rebalancing.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class RebalancingAlarm {
    private final Context context;
    private final AlarmManager alarmManager;

    public RebalancingAlarm(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void startAlarm(int validationIntervalMinutes) {
        PendingIntent pendingIntent = createAlarmPendingIntent(context);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), ((long) validationIntervalMinutes) * 60 * 1000, pendingIntent);
    }

    public void cancelAlarm() {
        PendingIntent pendingIntent = createAlarmPendingIntent(context);
        alarmManager.cancel(pendingIntent);
    }

    public void startRebalancingService() {
        Intent intent = new Intent(context.getApplicationContext(), RebalancingForegroundService.class);
        context.startForegroundService(intent);
    }

    public void stopRebalancingService() {
        Intent intent = new Intent(context.getApplicationContext(), RebalancingForegroundService.class);
        context.stopService(intent);
    }

    private PendingIntent createAlarmPendingIntent(Context context) {
        Context applicationContext = context.getApplicationContext();
        Intent rrIntent = new Intent(applicationContext, RebalancingReceiver.class);
        return PendingIntent.getBroadcast(applicationContext, 0, rrIntent, PendingIntent.FLAG_IMMUTABLE);
    }
}
