package com.vippygames.bianic.common;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.consts.ConfigurationConsts;

public class ThemeApplication extends Application {

    // this method is called before any activity
    @Override
    public void onCreate() {
        super.onCreate();
        setSelectedTheme();
    }

    public void setSelectedTheme() {
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
