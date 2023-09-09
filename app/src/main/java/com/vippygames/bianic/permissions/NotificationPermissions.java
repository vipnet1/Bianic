package com.vippygames.bianic.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vippygames.bianic.MainActivity;

public class NotificationPermissions {
    public boolean havePostNotificationsPermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
            return true;
        }

        int permissionState = ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPostNotificationsPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }
}
