package com.vippygames.bianic.rebalancing.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.vippygames.bianic.configuration.ConfigurationManager;

public class RebalancingAlarm {
    private final Context context;
    private final AlarmManager alarmManager;
    private final PendingIntent pendingIntent;

    public RebalancingAlarm(Context context) {
        this.context = context;

        Intent intent = new Intent(context, RebalancingReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void start() {
        ConfigurationManager configurationManager = new ConfigurationManager(context);
        int validationIntervalMinutes = configurationManager.getValidationInterval();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), validationIntervalMinutes * 60 * 1000, pendingIntent);
    }
}
