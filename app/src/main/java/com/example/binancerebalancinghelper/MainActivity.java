package com.example.binancerebalancinghelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.binancerebalancinghelper.rebalancing.schedule.RebalancingCheckIntentService;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BatteryHelper batteryHelper = new BatteryHelper(this);
        batteryHelper.requestIgnoreBatteryOptimizationsIfNeeded();

        Intent serviceIntent = new Intent(this, RebalancingCheckIntentService.class);
        this.startService(serviceIntent);
    }
}