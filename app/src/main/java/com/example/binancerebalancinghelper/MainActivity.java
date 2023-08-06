package com.example.binancerebalancinghelper;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BatteryHelper batteryHelper = new BatteryHelper(this);
        batteryHelper.requestIgnoreBatteryOptimizationsIfNeeded();

        new MyAsyncTask(this).execute("sdf");
    }
}