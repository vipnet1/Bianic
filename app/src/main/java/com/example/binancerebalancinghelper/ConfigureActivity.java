package com.example.binancerebalancinghelper;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.binancerebalancinghelper.configuration.ConfigurationManager;
import com.example.binancerebalancinghelper.consts.ConfigurationConsts;
import com.example.binancerebalancinghelper.shared_preferences.exceptions.KeyNotFoundException;

public class ConfigureActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtApiKey;
    private EditText edtSecretKey;
    private EditText edtValidationInterval;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.configure_menu, menu);
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
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btn_show_beginning_api) {
            handleActionShowBeginningApi();
        } else if (viewId == R.id.btn_show_beginning_secret) {
            handleActionShowBeginningSecret();
        } else if (viewId == R.id.btn_save) {
            handleActionSave();
        } else if (viewId == R.id.btn_revert) {
            handleActionRevert();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        initOperations();
    }

    private void handleActionShowBeginningApi() {
        String apiKey = edtApiKey.getText().toString();

        if(apiKey.isEmpty()) {
            Toast.makeText(this, "Api key is empty", Toast.LENGTH_LONG).show();
            return;
        }

        String first5ApiCharacters = apiKey.substring(0, Math.min(apiKey.length(), ConfigurationConsts.SHOW_KEY_CHARACTERS_NUMBER));
        Toast.makeText(this, "Api key begins with '" + first5ApiCharacters + "'", Toast.LENGTH_LONG).show();
    }

    private void handleActionShowBeginningSecret() {
        String secretKey = edtSecretKey.getText().toString();

        if(secretKey.isEmpty()) {
            Toast.makeText(this, "Secret key is empty", Toast.LENGTH_LONG).show();
            return;
        }

        String first5SecretCharacters = secretKey.substring(0, Math.min(secretKey.length(), ConfigurationConsts.SHOW_KEY_CHARACTERS_NUMBER));
        Toast.makeText(this, "Secret key begins with '" + first5SecretCharacters + "'", Toast.LENGTH_LONG).show();
    }

    private void handleActionRevert() {
        setConfigurationData();
        Toast.makeText(this, "Reverted configuration", Toast.LENGTH_LONG).show();
    }

    private void handleActionSave() {
        ConfigurationManager configurationManager = new ConfigurationManager(this);

        String apiKey = edtApiKey.getText().toString();
        configurationManager.setApiKey(apiKey);

        String secretKey = edtSecretKey.getText().toString();
        configurationManager.setSecretKey(secretKey);

        int validationInterval = Integer.parseInt(edtValidationInterval.getText().toString());
        configurationManager.setValidationInterval(validationInterval);

        Toast.makeText(this, "Saved configuration", Toast.LENGTH_LONG).show();
    }

    private void initOperations() {
        edtApiKey = findViewById(R.id.edt_api_key);
        edtSecretKey = findViewById(R.id.edt_secret_key);
        edtValidationInterval = findViewById(R.id.edt_validation_interval);
        View btnShowApi = findViewById(R.id.btn_show_beginning_api);
        View btnShowSecret = findViewById(R.id.btn_show_beginning_secret);
        View btnSave = findViewById(R.id.btn_save);
        View btnRevert = findViewById(R.id.btn_revert);

        btnShowApi.setOnClickListener(this);
        btnShowSecret.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnRevert.setOnClickListener(this);

        setConfigurationData();
    }

    private void setConfigurationData() {
        ConfigurationManager configurationManager = new ConfigurationManager(this);

        edtApiKey.setText(configurationManager.getApiKey());
        edtSecretKey.setText(configurationManager.getSecretKey());
        edtValidationInterval.setText(String.valueOf(configurationManager.getValidationInterval()));
    }

    private void handleActionRedirectMain() {
        finish();
    }
}