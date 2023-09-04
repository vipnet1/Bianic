package com.example.binancerebalancinghelper;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.binancerebalancinghelper.configuration.ConfigurationManager;
import com.example.binancerebalancinghelper.consts.ConfigurationConsts;

public class ConfigureActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtApiKey;
    private EditText edtSecretKey;
    private EditText edtValidationInterval;
    private EditText edtThresholdRebalancingPercent;
    private Button btnActivate;
    private Button btnDeactivate;

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
        } else if (viewId == R.id.btn_activate) {
            handleActionActivate();
        } else if (viewId == R.id.btn_deactivate) {
            handleActionDeactivate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        initOperations();
    }

    private void handleActionActivate() {
        btnActivate.setVisibility(View.GONE);
        btnDeactivate.setVisibility(View.VISIBLE);
    }

    private void handleActionDeactivate() {
        btnActivate.setVisibility(View.VISIBLE);
        btnDeactivate.setVisibility(View.GONE);
    }

    private void handleActionShowBeginningApi() {
        String apiKey = edtApiKey.getText().toString();

        if (apiKey.isEmpty()) {
            Toast.makeText(this, "Api key is empty", Toast.LENGTH_LONG).show();
            return;
        }

        String first5ApiCharacters = apiKey.substring(0, Math.min(apiKey.length(), ConfigurationConsts.SHOW_KEY_CHARACTERS_NUMBER));
        Toast.makeText(this, "Api key begins with '" + first5ApiCharacters + "'", Toast.LENGTH_LONG).show();
    }

    private void handleActionShowBeginningSecret() {
        String secretKey = edtSecretKey.getText().toString();

        if (secretKey.isEmpty()) {
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

    private boolean isThresholdRebalancingPercentInputValid(String thresholdRebalancingPercentText) {
        if (thresholdRebalancingPercentText.isEmpty()) {
            Toast.makeText(this, "Threshold rebalancing percent - cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        int dotIndex = thresholdRebalancingPercentText.indexOf(".");

        if (dotIndex < 0) {
            if (thresholdRebalancingPercentText.length() > 6) {
                Toast.makeText(this, "Threshold rebalancing percent - value must be less than 1000000", Toast.LENGTH_LONG).show();
                return false;
            }

            return true;
        }

        if (dotIndex == 0) {
            Toast.makeText(this, "Threshold rebalancing percent - dot cannot be first character", Toast.LENGTH_LONG).show();
            return false;
        }

        if (dotIndex == thresholdRebalancingPercentText.length() - 1) {
            Toast.makeText(this, "Threshold rebalancing percent - dot cannot be last character", Toast.LENGTH_LONG).show();
            return false;
        }

        String charsBeforeDot = thresholdRebalancingPercentText.substring(0, dotIndex);
        String charsAfterDot = thresholdRebalancingPercentText.substring(dotIndex + 1);

        if (charsBeforeDot.length() > 6) {
            Toast.makeText(this, "Threshold rebalancing percent - value must be less than 1000000", Toast.LENGTH_LONG).show();
            return false;
        }

        if (charsAfterDot.length() > 5) {
            Toast.makeText(this, "Threshold rebalancing percent - up to 5 digits after dot", Toast.LENGTH_LONG).show();
            return false;
        }

        float value = Float.parseFloat(thresholdRebalancingPercentText);
        if(value < 0.1) {
            Toast.makeText(this, "Threshold rebalancing percent - value cannot be lower than 0.1", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean isValidationIntervalInputValid(String validationIntervalText) {
        if (validationIntervalText.isEmpty()) {
            Toast.makeText(this, "Validation interval - cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        int value = Integer.parseInt(validationIntervalText);
        if(value < 1) {
            Toast.makeText(this, "Validation interval - cannot be lower than 1", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void handleActionSave() {
        ConfigurationManager configurationManager = new ConfigurationManager(this);

        String validationIntervalText = edtValidationInterval.getText().toString();
        String thresholdRebalancingPercentText = edtThresholdRebalancingPercent.getText().toString();

        if (!isValidationIntervalInputValid(validationIntervalText) || !isThresholdRebalancingPercentInputValid(thresholdRebalancingPercentText)) {
            return;
        }

        String apiKey = edtApiKey.getText().toString();
        configurationManager.setApiKey(apiKey);

        String secretKey = edtSecretKey.getText().toString();
        configurationManager.setSecretKey(secretKey);

        int validationInterval = Integer.parseInt(validationIntervalText);
        configurationManager.setValidationInterval(validationInterval);

        float thresholdRebalancingPercent = Float.parseFloat(thresholdRebalancingPercentText);
        configurationManager.setThresholdRebalancingPercent(thresholdRebalancingPercent);

        if (btnActivate.getVisibility() == View.VISIBLE) {
            configurationManager.setIsRebalancingActivated(0);
        } else {
            configurationManager.setIsRebalancingActivated(1);
        }

        Toast.makeText(this, "Saved configuration", Toast.LENGTH_LONG).show();
    }

    private void initOperations() {
        edtApiKey = findViewById(R.id.edt_api_key);
        edtSecretKey = findViewById(R.id.edt_secret_key);
        edtValidationInterval = findViewById(R.id.edt_validation_interval);
        edtThresholdRebalancingPercent = findViewById(R.id.edt_threshold_rebalancing_percent);
        btnActivate = findViewById(R.id.btn_activate);
        btnDeactivate = findViewById(R.id.btn_deactivate);

        View btnShowApi = findViewById(R.id.btn_show_beginning_api);
        View btnShowSecret = findViewById(R.id.btn_show_beginning_secret);
        View btnSave = findViewById(R.id.btn_save);
        View btnRevert = findViewById(R.id.btn_revert);

        btnShowApi.setOnClickListener(this);
        btnShowSecret.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnRevert.setOnClickListener(this);
        btnActivate.setOnClickListener(this);
        btnDeactivate.setOnClickListener(this);

        setConfigurationData();
    }

    private void setConfigurationData() {
        ConfigurationManager configurationManager = new ConfigurationManager(this);

        edtApiKey.setText(configurationManager.getApiKey());
        edtSecretKey.setText(configurationManager.getSecretKey());
        edtValidationInterval.setText(String.valueOf(configurationManager.getValidationInterval()));
        edtThresholdRebalancingPercent.setText(String.valueOf(configurationManager.getThresholdRebalancingPercent()));

        if (configurationManager.isRebalancingActivated() == 1) {
            btnActivate.setVisibility(View.GONE);
            btnDeactivate.setVisibility(View.VISIBLE);
        } else {
            btnActivate.setVisibility(View.VISIBLE);
            btnDeactivate.setVisibility(View.GONE);
        }

    }

    private void handleActionRedirectMain() {
        finish();
    }
}