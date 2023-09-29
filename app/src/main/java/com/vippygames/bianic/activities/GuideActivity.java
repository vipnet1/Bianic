package com.vippygames.bianic.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.vippygames.bianic.R;
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
        redirectMain();
    }

    private void handleActionRedirectMain() {
        redirectMain();
    }

    private void redirectMain() {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        sp.setInt(SharedPrefsConsts.SHOULD_WELCOME, 0);
        finish();
    }
}