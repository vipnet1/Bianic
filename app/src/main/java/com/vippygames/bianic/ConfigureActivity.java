package com.vippygames.bianic;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.consts.ConfigurationConsts;
import com.vippygames.bianic.consts.ContactConsts;
import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.notifications.NotificationType;
import com.vippygames.bianic.permissions.NotificationPermissions;
import com.vippygames.bianic.shared_preferences.SharedPreferencesHelper;
import com.vippygames.bianic.utils.ExternalAppUtils;
import com.vippygames.bianic.utils.RebalanceActivationUtils;

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
        showContractIfNeeded();
    }

    private void showContractIfNeeded() {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        if (sp.getInt(SharedPrefsConsts.SHOULD_SHOW_CONTRACT, 1) == 0) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Important");
        builder.setCancelable(false);
        builder.setMessage("Storing the keys on your device in Bianic is safe because you should configure them as READ ONLY" +
                " in Binance. Don't ever change the fact that they are read-only after showing it to Bianic. We are not responsible for" +
                " any issues, trades or losses occurred because of not following this simple rule. We are not responsible for your losses " +
                " in general - Bianic is a tool to help you and make your adventure easier, not an investment/trading advisor.");

        builder.setPositiveButton("Accept", (dialog, which) -> {
            SharedPreferencesHelper sp2 = new SharedPreferencesHelper(ConfigureActivity.this);
            sp2.setInt(SharedPrefsConsts.SHOULD_SHOW_CONTRACT, 0);
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
            Toast.makeText(this, "Api key is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String first5ApiCharacters = apiKey.substring(0, Math.min(apiKey.length(), ConfigurationConsts.SHOW_KEY_CHARACTERS_NUMBER));
        Toast.makeText(this, "Api key begins with '" + first5ApiCharacters + "'", Toast.LENGTH_SHORT).show();
    }

    private void handleActionShowBeginningSecret() {
        String secretKey = edtSecretKey.getText().toString();

        if (secretKey.isEmpty()) {
            Toast.makeText(this, "Secret key is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String first5SecretCharacters = secretKey.substring(0, Math.min(secretKey.length(), ConfigurationConsts.SHOW_KEY_CHARACTERS_NUMBER));
        Toast.makeText(this, "Secret key begins with '" + first5SecretCharacters + "'", Toast.LENGTH_SHORT).show();
    }

    private void handleActionRevert() {
        setConfigurationData();
        Toast.makeText(this, "Reverted configuration", Toast.LENGTH_SHORT).show();
    }

    private boolean isThresholdRebalancingPercentInputValid(String thresholdRebalancingPercentText) {
        if (thresholdRebalancingPercentText.isEmpty()) {
            Toast.makeText(this, "Threshold rebalancing percent - cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        int dotIndex = thresholdRebalancingPercentText.indexOf(".");

        if (dotIndex < 0) {
            if (thresholdRebalancingPercentText.length() > 6) {
                Toast.makeText(this, "Threshold rebalancing percent - value must be less than 1000000", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        }

        if (dotIndex == 0) {
            Toast.makeText(this, "Threshold rebalancing percent - dot cannot be first character", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dotIndex == thresholdRebalancingPercentText.length() - 1) {
            Toast.makeText(this, "Threshold rebalancing percent - dot cannot be last character", Toast.LENGTH_SHORT).show();
            return false;
        }

        String charsBeforeDot = thresholdRebalancingPercentText.substring(0, dotIndex);
        String charsAfterDot = thresholdRebalancingPercentText.substring(dotIndex + 1);

        if (charsBeforeDot.length() > 6) {
            Toast.makeText(this, "Threshold rebalancing percent - value must be less than 1000000", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (charsAfterDot.length() > 3) {
            Toast.makeText(this, "Threshold rebalancing percent - up to 3 digits after dot", Toast.LENGTH_SHORT).show();
            return false;
        }

        float value = Float.parseFloat(thresholdRebalancingPercentText);
        if (value < 0.1) {
            Toast.makeText(this, "Threshold rebalancing percent - value cannot be lower than 0.1", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidationIntervalInputValid(String validationIntervalText) {
        if (validationIntervalText.isEmpty()) {
            Toast.makeText(this, "Validation interval - cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        int value = Integer.parseInt(validationIntervalText);
        if (value < 2) {
            Toast.makeText(this, "Validation interval - cannot be lower than 2", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isShouldRebalanceInputValid(boolean shouldRebalance) {
        NotificationPermissions notificationPermissions = new NotificationPermissions();

        if (shouldRebalance) {
            if (!notificationPermissions.havePostNotificationsPermission(this)) {
                Toast.makeText(this, "Need notifications permission to run rebalancing check", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!notificationPermissions.isChannelEnabled(this, NotificationType.REBALANCING_RUNNING)) {
                Toast.makeText(this, "Enable rebalancing check notification channel.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void handleActionSave() {
        ConfigurationManager configurationManager = new ConfigurationManager(this);

        int previousValidationInterval = configurationManager.getValidationInterval();
        int previousIsRebalancingActivated = configurationManager.isRebalancingActivated();
        boolean shouldRebalanceInput = btnDeactivate.getVisibility() == View.VISIBLE;

        String validationIntervalText = edtValidationInterval.getText().toString();
        String thresholdRebalancingPercentText = edtThresholdRebalancingPercent.getText().toString();

        if (!isValidationIntervalInputValid(validationIntervalText)
                || !isThresholdRebalancingPercentInputValid(thresholdRebalancingPercentText)
                || !isShouldRebalanceInputValid(shouldRebalanceInput)) {
            return;
        }

        String apiKey = edtApiKey.getText().toString();
        configurationManager.setApiKey(apiKey);

        String secretKey = edtSecretKey.getText().toString();
        configurationManager.setSecretKey(secretKey);

        int newValidationInterval = Integer.parseInt(validationIntervalText);
        configurationManager.setValidationInterval(newValidationInterval);

        float thresholdRebalancingPercent = Float.parseFloat(thresholdRebalancingPercentText);
        configurationManager.setThresholdRebalancingPercent(thresholdRebalancingPercent);

        int newIsRebalancingActivated = 0;
        if (shouldRebalanceInput) {
            newIsRebalancingActivated = 1;
        }
        configurationManager.setIsRebalancingActivated(newIsRebalancingActivated);

        RebalanceActivationUtils rebalanceActivationUtils = new RebalanceActivationUtils(this);
        rebalanceActivationUtils.changeRebalancerIfNeeded(previousValidationInterval,
                previousIsRebalancingActivated, newValidationInterval, newIsRebalancingActivated);
        Toast.makeText(this, "Saved configuration", Toast.LENGTH_SHORT).show();
        redirectMain();
    }

    private void handleActionRedirectMain() {
        redirectMain();
    }

    private void redirectMain() {
        finish();
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

        initGuideToCreateKeys();
        setConfigurationData();
    }

    private void initGuideToCreateKeys() {
        Button btnGuide = findViewById(R.id.guide_create_keys);
        btnGuide.setOnClickListener(view -> {
            ExternalAppUtils externalAppUtils = new ExternalAppUtils(this);
            externalAppUtils.tryOpenUri(ConfigurationConsts.GUIDE_CREATE_KEYS_URL, "No browser app found");
        });
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
}