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

import androidx.annotation.NonNull;
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
import com.vippygames.bianic.utils.StringUtils;

public class ConfigureActivity extends AppCompatActivity implements View.OnClickListener {
    // value in bundle indicates if button activate/deactivate was seen. if 1 seen deactivate if 0 seen activate.
    private static final String SAVED_ACTIVATE_STATUS_KEY = "saved_activate_status";

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
            handleRedirectMain();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btn_show_beginning_api) {
            handleShowBeginningApi();
        } else if (viewId == R.id.btn_show_beginning_secret) {
            handleShowBeginningSecret();
        } else if (viewId == R.id.btn_save) {
            handleSave();
        } else if (viewId == R.id.btn_revert) {
            handleRevert();
        } else if (viewId == R.id.btn_activate) {
            handleActivate();
        } else if (viewId == R.id.btn_deactivate) {
            handleDeactivate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        initOperations();
        setConfigurationData(savedInstanceState);
        showContractIfNeeded();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        saveActivationStatus(outState);
        super.onSaveInstanceState(outState);
    }

    private void saveActivationStatus(Bundle outState) {
        Button btnActivate = findViewById(R.id.btn_activate);
        if (btnActivate.getVisibility() == View.VISIBLE) {
            outState.putInt(SAVED_ACTIVATE_STATUS_KEY, 0);
        } else {
            outState.putInt(SAVED_ACTIVATE_STATUS_KEY, 1);
        }
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
        });
        AlertDialog dialog = builder.create();

        AlertDialogModify alertDialogModify = new AlertDialogModify(this);
        alertDialogModify.modify(dialog);

        dialog.show();
    }

    private void handleActivate() {
        activateClicked();
    }

    private void activateClicked() {
        btnActivate.setVisibility(View.GONE);
        btnDeactivate.setVisibility(View.VISIBLE);
    }

    private void handleDeactivate() {
        deactivateClicked();
    }

    private void deactivateClicked() {
        btnActivate.setVisibility(View.VISIBLE);
        btnDeactivate.setVisibility(View.GONE);
    }

    private void handleShowBeginningApi() {
        showBeginningApi();
    }

    private void showBeginningApi() {
        String apiKey = edtApiKey.getText().toString();

        if (apiKey.isEmpty()) {
            Toast.makeText(this, R.string.C_config_toast_apiBeginningEmpty, Toast.LENGTH_SHORT).show();
            return;
        }

        String first5ApiCharacters = apiKey.substring(0, Math.min(apiKey.length(), ConfigurationConsts.SHOW_KEY_CHARACTERS_NUMBER));
        Toast.makeText(this, getString(R.string.C_config_toast_apiBeginsWith) + "'" + first5ApiCharacters + "'", Toast.LENGTH_SHORT).show();
    }

    private void handleShowBeginningSecret() {
        showBeginningSecret();
    }

    private void showBeginningSecret() {
        String secretKey = edtSecretKey.getText().toString();

        if (secretKey.isEmpty()) {
            Toast.makeText(this, R.string.C_config_toast_secretBeginningEmpty, Toast.LENGTH_SHORT).show();
            return;
        }

        String first5SecretCharacters = secretKey.substring(0, Math.min(secretKey.length(), ConfigurationConsts.SHOW_KEY_CHARACTERS_NUMBER));
        Toast.makeText(this, getString(R.string.C_config_toast_secretBeginsWith) + "'" + first5SecretCharacters + "'", Toast.LENGTH_SHORT).show();
    }

    private void handleRevert() {
        revert();
    }

    private void revert() {
        setConfigurationData(null);
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

    private boolean isApiKeyValid(String apiKey) {
        StringUtils stringUtils = new StringUtils(this);
        if (!stringUtils.isAlphanumeric(apiKey)) {
            Toast.makeText(this, R.string.C_config_toast_apiKeyNotAlphanumeric, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isSecretKeyValid(String secretKey) {
        StringUtils stringUtils = new StringUtils(this);
        if (!stringUtils.isAlphanumeric(secretKey)) {
            Toast.makeText(this, R.string.C_config_toast_secretKeyNotAlphanumeric, Toast.LENGTH_SHORT).show();
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

    private void handleSave() {
        if (save()) {
            redirectMain();
        }
    }

    // true if success, false if failed
    private boolean save() {
        ConfigurationManager configurationManager = new ConfigurationManager(this);

        int previousValidationInterval = configurationManager.getValidationInterval();
        int previousIsRebalancingActivated = configurationManager.isRebalancingActivated();
        boolean shouldRebalanceInput = btnDeactivate.getVisibility() == View.VISIBLE;

        String apiKey = edtApiKey.getText().toString();
        String secretKey = edtSecretKey.getText().toString();
        String validationIntervalText = edtValidationInterval.getText().toString();
        String thresholdRebalancingPercentText = edtThresholdRebalancingPercent.getText().toString();


        if (!isValidationIntervalInputValid(validationIntervalText)
                || !isApiKeyValid(apiKey) || !isSecretKeyValid(secretKey)
                || !isThresholdRebalancingPercentInputValid(thresholdRebalancingPercentText)
                || !isShouldRebalanceInputValid(shouldRebalanceInput)) {
            return false;
        }

        configurationManager.setApiKey(apiKey);
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
        return true;
    }

    private void handleRedirectMain() {
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

    private void setConfigurationData(Bundle savedInstanceState) {
        ConfigurationManager configurationManager = new ConfigurationManager(this);

        edtApiKey.setText(configurationManager.getApiKey());
        edtSecretKey.setText(configurationManager.getSecretKey());
        edtValidationInterval.setText(String.valueOf(configurationManager.getValidationInterval()));
        edtThresholdRebalancingPercent.setText(String.valueOf(configurationManager.getThresholdRebalancingPercent()));

        int activationStatus = configurationManager.isRebalancingActivated();
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_ACTIVATE_STATUS_KEY)) {
            activationStatus = savedInstanceState.getInt(SAVED_ACTIVATE_STATUS_KEY);
        }

        if (activationStatus == 1) {
            btnActivate.setVisibility(View.GONE);
            btnDeactivate.setVisibility(View.VISIBLE);
        } else {
            btnActivate.setVisibility(View.VISIBLE);
            btnDeactivate.setVisibility(View.GONE);
        }
    }
}