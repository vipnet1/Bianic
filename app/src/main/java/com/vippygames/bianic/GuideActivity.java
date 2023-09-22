package com.vippygames.bianic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.shared_preferences.SharedPreferencesHelper;

public class GuideActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.guide_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.redirect_main) {
            handleActionRedirectMain();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
    }

    @Override
    public void onBackPressed() {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        sp.setInt(SharedPrefsConsts.SHOULD_REDIRECT_TO_GUIDE, 0);
        super.onBackPressed();
    }

    private void handleActionRedirectMain() {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        sp.setInt(SharedPrefsConsts.SHOULD_REDIRECT_TO_GUIDE, 0);
        finish();
    }
}