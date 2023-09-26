package com.vippygames.bianic.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.vippygames.bianic.R;
import com.vippygames.bianic.common.AlertDialogModify;
import com.vippygames.bianic.common.ThemeApplication;
import com.vippygames.bianic.configuration.ConfigurationManager;
import com.vippygames.bianic.consts.ConfigurationConsts;
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
        builder.setTitle(R.string.C_config_dialog_contractTitle);
        builder.setCancelable(false);
        builder.setMessage(R.string.C_config_dialog_contractMessage);

        builder.setPositiveButton(R.string.C_config_dialog_contractAccept, (dialog, which) -> {
            SharedPreferencesHelper sp2 = new SharedPreferencesHelper(ConfigureActivity.this);
            sp2.setInt(SharedPrefsConsts.SHOULD_SHOW_CONTRACT, 0);
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();

        AlertDialogModify alertDialogModify = new AlertDialogModify(this);
        alertDialogModify.modify(dialog);

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
            Toast.makeText(this, R.string.C_config_toast_apiBeginningEmpty, Toast.LENGTH_SHORT).show();
            return;
        }

        String first5ApiCharacters = apiKey.substring(0, Math.min(apiKey.length(), ConfigurationConsts.SHOW_KEY_CHARACTERS_NUMBER));
        Toast.makeText(this, getString(R.string.C_config_toast_apiBeginsWith) + "'" + first5ApiCharacters + "'", Toast.LENGTH_SHORT).show();
    }

    private void handleActionShowBeginningSecret() {
        String secretKey = edtSecretKey.getText().toString();

        if (secretKey.isEmpty()) {
            Toast.makeText(this, R.string.C_config_toast_secretBeginningEmpty, Toast.LENGTH_SHORT).show();
            return;
        }

        String first5SecretCharacters = secretKey.substring(0, Math.min(secretKey.length(), ConfigurationConsts.SHOW_KEY_CHARACTERS_NUMBER));
        Toast.makeText(this, getString(R.string.C_config_toast_secretBeginsWith) + "'" + first5SecretCharacters + "'", Toast.LENGTH_SHORT).show();
    }

    private void handleActionRevert() {
        setConfigurationData();
        Toast.makeText(this, R.string.C_config_toast_revertedConfiguration, Toast.LENGTH_SHORT).show();
    }


    private boolean isThresholdRebalancingPercentInputValid(String thresholdRebalancingPercentText) {
        try {
            if (thresholdRebalancingPercentText.isEmpty()) {
                Toast.makeText(this, R.string.C_config_toast_thresholdRebalanceCantBeEmpty, Toast.LENGTH_SHORT).show();
                return false;
            }

            int dotIndex = thresholdRebalancingPercentText.indexOf(".");

            if (dotIndex < 0) {
                float trp = Float.parseFloat(thresholdRebalancingPercentText);
                if (trp >= 1000000) {
                    Toast.makeText(this, R.string.C_config_toast_thresholdRebalanceLessThan, Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (trp < 0.1) {
                    Toast.makeText(this, R.string.C_config_toast_thresholdRebalanceMoreThan, Toast.LENGTH_SHORT).show();
                    return false;
                }

                return true;
            }

            if (dotIndex == 0) {
                Toast.makeText(this, R.string.C_config_toast_thresholdRebalanceDotNotFirst, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (dotIndex == thresholdRebalancingPercentText.length() - 1) {
                Toast.makeText(this, R.string.C_config_toast_thresholdRebalanceDotNotLast, Toast.LENGTH_SHORT).show();
                return false;
            }

            String charsAfterDot = thresholdRebalancingPercentText.substring(dotIndex + 1);
            if (charsAfterDot.length() > 3) {
                Toast.makeText(this, R.string.C_config_toast_thresholdRebalanceUpToDigitsAfterDot, Toast.LENGTH_SHORT).show();
                return false;
            }

            float trp = Float.parseFloat(thresholdRebalancingPercentText);
            if (trp >= 1000000) {
                Toast.makeText(this, R.string.C_config_toast_thresholdRebalanceLessThan, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (trp < 0.1) {
                Toast.makeText(this, R.string.C_config_toast_thresholdRebalanceMoreThan, Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        } catch (NumberFormatException e) { //shouldn't ever get to catch clause, added just in case
            Toast.makeText(this, R.string.C_config_toast_thresholdRebalanceFormatInvalid, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isValidationIntervalInputValid(String validationIntervalText) {
        if (validationIntervalText.isEmpty()) {
            Toast.makeText(this, R.string.C_config_toast_validationIntervalNotEmpty, Toast.LENGTH_SHORT).show();
            return false;
        }

        int value = Integer.parseInt(validationIntervalText);
        if (value < 2) {
            Toast.makeText(this, R.string.C_config_toast_validationIntervalNotLowerThan, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isShouldRebalanceInputValid(boolean shouldRebalance) {
        NotificationPermissions notificationPermissions = new NotificationPermissions();

        if (shouldRebalance) {
            if (!notificationPermissions.havePostNotificationsPermission(this)) {
                Toast.makeText(this, R.string.C_config_toast_rebalanceActivateNeedNotificationsPerm, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!notificationPermissions.isChannelEnabled(this, NotificationType.REBALANCING_RUNNING)) {
                Toast.makeText(this, R.string.C_config_toast_rebalanceActivateNeedCheckChannel, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, R.string.C_config_toast_savedConfig, Toast.LENGTH_SHORT).show();
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
        initThemeSpinner();
        setConfigurationData();
    }

    private void setSpinnerInitialSelection(String[] themeOptions) {
        Spinner themeSpinner = findViewById(R.id.theme_spinner);
        ConfigurationManager configurationManager = new ConfigurationManager(ConfigureActivity.this);
        String theme = configurationManager.getTheme();
        for (int i = 0; i < themeOptions.length; i++) {
            if (theme.equals(themeOptions[i])) {
                themeSpinner.setSelection(i);
                break;
            }
        }
    }

    private void initThemeSpinner() {
        Spinner themeSpinner = findViewById(R.id.theme_spinner);
        String[] themeOptions = {ConfigurationConsts.THEME_SYSTEM, ConfigurationConsts.THEME_LIGHT, ConfigurationConsts.THEME_DARK};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.theme_spinner_item, themeOptions);
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean firstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // because called first time on activity enter
                if (firstSelection) {
                    firstSelection = false;
                    setSpinnerInitialSelection(themeOptions);
                    return;
                }

                ConfigurationManager configurationManager = new ConfigurationManager(ConfigureActivity.this);
                if (!themeOptions[position].equals(configurationManager.getTheme())) {
                    configurationManager.setTheme(themeOptions[position]);
                    ThemeApplication app = (ThemeApplication) getApplication();
                    app.setSelectedTheme();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        themeSpinner.setAdapter(adapter);
    }

    private void initGuideToCreateKeys() {
        Button btnGuide = findViewById(R.id.guide_create_keys);
        btnGuide.setOnClickListener(view -> {
            ExternalAppUtils externalAppUtils = new ExternalAppUtils(this);
            externalAppUtils.tryOpenUri(ConfigurationConsts.GUIDE_CREATE_KEYS_URL, getString(R.string.C_config_toast_guideNoBrowserAppFound));
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