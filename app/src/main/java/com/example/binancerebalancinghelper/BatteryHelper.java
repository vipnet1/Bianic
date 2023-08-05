package com.example.binancerebalancinghelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;

public class BatteryHelper {
    private final Context context;

    public BatteryHelper(Context context) {
        this.context = context;
    }

    public void requestIgnoreBatteryOptimizationsIfNeeded() {
        if (!isIgnoringBatteryOptimizations()) {
            requestIgnoreBatteryOptimizations();
        }
    }

    private void requestIgnoreBatteryOptimizations() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    private boolean isIgnoringBatteryOptimizations() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return  powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
    }
}
