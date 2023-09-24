package com.vippygames.bianic;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.consts.ConfigurationConsts;

public abstract class ThemeAppCompatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setSelectedTheme();
        super.onCreate(savedInstanceState);
    }

    private void setSelectedTheme() {
        ConfigurationManager configurationManager = new ConfigurationManager(this);
        String theme = configurationManager.getTheme();

        if (theme.equals(ConfigurationConsts.THEME_LIGHT)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (theme.equals(ConfigurationConsts.THEME_DARK)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}