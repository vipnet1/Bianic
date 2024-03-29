package com.vippygames.bianic.activities.main;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.vippygames.bianic.BuildConfig;
import com.vippygames.bianic.R;
import com.vippygames.bianic.activities.ConfigureActivity;
import com.vippygames.bianic.activities.ExceptionsActivity;
import com.vippygames.bianic.activities.GuideActivity;
import com.vippygames.bianic.activities.main.dialogs.ValidationDialogFragment;
import com.vippygames.bianic.activities.observe.ObserveInfo;
import com.vippygames.bianic.activities.observe.ObserveViewModel;
import com.vippygames.bianic.activities.reports.ReportsActivity;
import com.vippygames.bianic.common.AlertDialogModify;
import com.vippygames.bianic.consts.BinanceApiConsts;
import com.vippygames.bianic.consts.ContactConsts;
import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.db.threshold_allocation.ThresholdAllocationDb;
import com.vippygames.bianic.db.threshold_allocation.ThresholdAllocationRecord;
import com.vippygames.bianic.permissions.BatteryPermissions;
import com.vippygames.bianic.permissions.NotificationPermissions;
import com.vippygames.bianic.rebalancing.validation.BinanceRecordsValidation;
import com.vippygames.bianic.shared_preferences.SharedPreferencesHelper;
import com.vippygames.bianic.utils.ExternalAppUtils;
import com.vippygames.bianic.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ObserveViewModel validationObserveViewModel;
    private Observer<ObserveInfo> validationObserver;
    // means shown in this activity instance
    private static final String SHOWN_PERMISSIONS_KEY = "shown_permissions_info";
    private static final String COINS_SAVED_INFO_KEY = "coins_saved_info";
    private static final String ROOT_TAG_PREFIX = "root_tag_";
    private static final int ROOT_TAG_LENGTH = 10;
    private static final int NEVER_SHOW_PERMISSIONS_BUTTON_REQUEST_COUNT = 2;

    private LayoutInflater layoutInflater;
    private LinearLayout dynamicLinearLayout;

    private View editedRecordRoot = null;
    private String symbolBeforeEdit;
    private String allocationBeforeEdit;
    private boolean alreadyShownPermissions;

    // Only for records of dynamic layout
    @Override
    public void onClick(View view) {
        View recordRoot = getRecordRoot(view);
        int viewId = view.getId();

        if (viewId == R.id.btn_edit) {
            handleActionEdit(recordRoot);
        } else if (viewId == R.id.btn_apply) {
            handleActionApply();
        } else if (viewId == R.id.btn_cancel) {
            handleActionCancel();
        } else if (viewId == R.id.btn_remove) {
            handleActionRemove();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.redirect_exceptions) {
            handleRedirectExceptions();
            return true;
        } else if (itemId == R.id.redirect_configure) {
            handleRedirectConfigure();
            return true;
        } else if (itemId == R.id.redirect_reports) {
            handleRedirectReports();
            return true;
        } else if (itemId == R.id.about) {
            handleAbout();
            return true;
        } else if (itemId == R.id.guide) {
            handleRedirectGuide();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBinanceRecordsValidationFinished(ObserveInfo info) {
        ObserveInfo.STATUS status = info.getStatus();
        if (status == null || status == ObserveInfo.STATUS.IDLE || status == ObserveInfo.STATUS.RUNNING) {
            return;
        }

        ValidationDialogFragment existingDialogFragment = getValidationDialogFragment();
        if (existingDialogFragment != null) {
            existingDialogFragment.dismiss();
        }

        if (status == ObserveInfo.STATUS.FAILED) {
            String message = info.getMessage();
            if (message.isEmpty()) {
                Toast.makeText(this, R.string.C_main_toast_failedValidateRecords, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } else {
            SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
            sp.setInt(SharedPrefsConsts.ARE_THRESHOLD_ALLOCATION_RECORDS_VALIDATED, 1);

            setValidateRecordsViews();
            Toast.makeText(this, R.string.C_main_toast_recordsValid, Toast.LENGTH_SHORT).show();
        }

        validationObserveViewModel.setObserveInfo(new ObserveInfo(ObserveInfo.STATUS.IDLE, ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        initValidationObserver();
        registerValidationViewModelObserver();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alreadyShownPermissions = savedInstanceState != null
                && savedInstanceState.getBoolean(SHOWN_PERMISSIONS_KEY, false);

        if (!shouldWelcome()) {
            checkPermissions();
        }

        initViews(savedInstanceState);
        welcomeIfNeeded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterValidationViewModelObserver();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        saveThresholdAllocationRecordsToBundle(outState);
        outState.putBoolean(SHOWN_PERMISSIONS_KEY, alreadyShownPermissions);
        super.onSaveInstanceState(outState);
    }

    private void initValidationObserver() {
        validationObserveViewModel = new ViewModelProvider(this).get(ObserveViewModel.class);
        validationObserver = this::onBinanceRecordsValidationFinished;
    }

    private void registerValidationViewModelObserver() {
        validationObserveViewModel.getTaskFinishedLiveData().observe(this, validationObserver);
    }

    private void unregisterValidationViewModelObserver() {
        validationObserveViewModel.getTaskFinishedLiveData().removeObserver(validationObserver);
    }

    private void initViews(Bundle savedInstanceState) {
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dynamicLinearLayout = findViewById(R.id.layout_dynamic_main);

        Button btnAddRecord = findViewById(R.id.btn_add_record);
        btnAddRecord.setOnClickListener(v -> handleAddRecord());

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> handleSave());

        Button btnRevert = findViewById(R.id.btn_revert);
        btnRevert.setOnClickListener(v -> handleRevert());

        Button btnValidateRecords = findViewById(R.id.btn_validate_records);
        btnValidateRecords.setOnClickListener(v -> handleValidateRecords());

        setValidateRecordsViews();
        addFirstThresholdRecordIfNeeded();

        if (savedInstanceState != null && savedInstanceState.containsKey(COINS_SAVED_INFO_KEY)) {
            loadRecordsFromBundle(savedInstanceState);
        } else {
            loadThresholdAllocationRecordsFromDb();
        }
    }

    private void welcomeIfNeeded() {
        if (!shouldWelcome()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.C_main_dialog_welcomeTitle);
        builder.setMessage(R.string.C_main_dialog_welcomeMessage);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.C_main_dialog_welcomeBtnOkay, (dialog, which) -> {
            redirectGuide();

            Handler handler = new Handler();
            handler.postDelayed(this::checkPermissions, 500);
        });

        AlertDialog welcomeDialog = builder.create();

        AlertDialogModify alertDialogModify = new AlertDialogModify(this);
        alertDialogModify.modify(welcomeDialog);

        welcomeDialog.show();
    }

    private void checkPermissions() {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        int do_not_request_permissions = sp.getInt(SharedPrefsConsts.DO_NOT_REQUEST_PERMISSIONS_KEY, 0);
        if (do_not_request_permissions == 1 || alreadyShownPermissions) {
            return;
        }

        BatteryPermissions batteryPermissions = new BatteryPermissions(this);
        NotificationPermissions notificationPermissions = new NotificationPermissions();

        boolean haveBatteryPermission = batteryPermissions.isIgnoringBatteryOptimizations();
        boolean haveNotificationPermission = notificationPermissions.havePostNotificationsPermission(this);

        if (!haveBatteryPermission || !haveNotificationPermission) {
            showRequestPermissionsDialog();
        }
    }

    private void showRequestPermissionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.C_main_dialog_requestPermissionsTitle);
        builder.setMessage(R.string.C_main_dialog_requestPermissionsMessage);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.C_main_dialog_requestPermissionsSure, (dialog, which) -> {
            SharedPreferencesHelper sph = new SharedPreferencesHelper(this);
            int count = sph.getInt(SharedPrefsConsts.REQUESTED_PERMISSIONS_COUNT_KEY, 0);
            sph.setInt(SharedPrefsConsts.REQUESTED_PERMISSIONS_COUNT_KEY, count + 1);
            alreadyShownPermissions = true;

            requestNeededPermissions();
        });

        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);
        int requestedPermissionsCount = sharedPreferencesHelper.getInt(SharedPrefsConsts.REQUESTED_PERMISSIONS_COUNT_KEY, 0);
        if (requestedPermissionsCount >= NEVER_SHOW_PERMISSIONS_BUTTON_REQUEST_COUNT) {
            builder.setNegativeButton(R.string.C_main_dialog_requestPermissionsNeverAgain, (dialog, which) -> {
                SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
                sp.setInt(SharedPrefsConsts.DO_NOT_REQUEST_PERMISSIONS_KEY, 1);
            });
        }

        AlertDialog requestPermissionsDialog = builder.create();

        AlertDialogModify alertDialogModify = new AlertDialogModify(this);
        alertDialogModify.modify(requestPermissionsDialog);

        requestPermissionsDialog.show();
    }

    private void requestNeededPermissions() {
        BatteryPermissions batteryPermissions = new BatteryPermissions(this);
        NotificationPermissions notificationPermissions = new NotificationPermissions();

        if (!batteryPermissions.isIgnoringBatteryOptimizations()) {
            batteryPermissions.requestIgnoreBatteryOptimizations();
        }

        if (!notificationPermissions.havePostNotificationsPermission(this)) {
            notificationPermissions.requestPostNotificationsPermission(this);
        }
    }

    private void addFirstThresholdRecordIfNeeded() {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        int shouldAddRecord = sp.getInt(SharedPrefsConsts.SHOULD_ADD_FIRST_THRESHOLD_RECORD, 1);

        if (shouldAddRecord == 1) {
            ThresholdAllocationDb db = new ThresholdAllocationDb(this);

            ThresholdAllocationRecord record = new ThresholdAllocationRecord(BinanceApiConsts.BTC_SYMBOL, 100);
            db.saveRecord(record);

            sp.setInt(SharedPrefsConsts.SHOULD_ADD_FIRST_THRESHOLD_RECORD, 0);
        }
    }

    private void handleAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.C_main_dialog_aboutTitle);
        builder.setMessage(getString(R.string.C_main_dialog_aboutMessage) + BuildConfig.VERSION_NAME);
        builder.setPositiveButton(R.string.C_main_dialog_aboutGetHelp, (dialog, which) -> {
            ExternalAppUtils externalAppUtils = new ExternalAppUtils(this);
            externalAppUtils.tryOpenUri("mailto:" + ContactConsts.CONTACT_EMAIL, getString(R.string.C_main_toast_noEmailAppFound));
        });
        builder.setNegativeButton(R.string.C_main_dialog_aboutReview, (dialog, which) -> startInAppReview());
        builder.setNeutralButton(R.string.C_main_dialog_aboutCancel, null);
        AlertDialog dialog = builder.create();

        AlertDialogModify alertDialogModify = new AlertDialogModify(this);
        alertDialogModify.modify(dialog);

        dialog.show();
    }

    private void startInAppReview() {
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(reviewTask -> Toast.makeText(this, R.string.C_main_toast_reviewThankYou, Toast.LENGTH_LONG).show());
            } else {
                // There was some problem, log or handle the error code.
                //  @ReviewErrorCode int reviewErrorCode = ((ReviewException) task.getException()).getErrorCode();
                //  Toast.makeText(this, "Couldn't review. Error code " + reviewErrorCode, Toast.LENGTH_SHORT).show();
                openAppInPlayStore();
            }
        });
    }

    private void openAppInPlayStore() {
        final String appPackageName = getPackageName();
        ExternalAppUtils externalAppUtils = new ExternalAppUtils(this);
        try {
            externalAppUtils.openUri(ContactConsts.PLAY_STORE_MARKET_BASE_URI + appPackageName);
        } catch (ActivityNotFoundException ignored) {
            externalAppUtils.tryOpenUri(ContactConsts.PLAY_STORE_WEBSITE_BASE_URI + appPackageName, getString(R.string.C_main_toast_reviewNoBrowserApp));
        }
    }

    private void handleRedirectExceptions() {
        redirectExceptions();
    }

    private void redirectExceptions() {
        Intent intent = new Intent(this, ExceptionsActivity.class);
        this.startActivity(intent);
    }

    private void handleRedirectReports() {
        redirectReports();
    }

    private void redirectReports() {
        Intent intent = new Intent(this, ReportsActivity.class);
        this.startActivity(intent);
    }

    private void handleRedirectGuide() {
        redirectGuide();
    }

    private void redirectGuide() {
        Intent intent = new Intent(this, GuideActivity.class);
        this.startActivity(intent);
    }

    private void handleRedirectConfigure() {
        redirectConfigure();
    }

    private void redirectConfigure() {
        Intent intent = new Intent(this, ConfigureActivity.class);
        this.startActivity(intent);
    }

    private void handleActionEdit(View recordRoot) {
        if (editedRecordRoot != null) {
            Toast.makeText(this, R.string.C_main_toast_onlyOneRecordEdit, Toast.LENGTH_SHORT).show();
            return;
        }
        editRecord(recordRoot);
    }

    private void editRecord(View recordRoot) {
        hideBtnValidateRecords();
        editedRecordRoot = recordRoot;

        Button btnRemove = recordRoot.findViewById(R.id.btn_remove);
        Button btnApply = recordRoot.findViewById(R.id.btn_apply);
        Button btnEdit = recordRoot.findViewById(R.id.btn_edit);
        Button btnCancel = recordRoot.findViewById(R.id.btn_cancel);
        EditText edtSymbol = recordRoot.findViewById(R.id.edt_symbol);
        EditText edtAllocation = recordRoot.findViewById(R.id.edt_allocation);

        btnRemove.setVisibility(View.VISIBLE);
        btnApply.setVisibility(View.VISIBLE);
        btnEdit.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);

        symbolBeforeEdit = edtSymbol.getText().toString();
        allocationBeforeEdit = edtAllocation.getText().toString();

        edtSymbol.setEnabled(true);
        edtAllocation.setEnabled(true);
    }

    private void handleActionApply() {
        EditText edtSymbol = editedRecordRoot.findViewById(R.id.edt_symbol);
        EditText edtAllocation = editedRecordRoot.findViewById(R.id.edt_allocation);

        String symbol = edtSymbol.getText().toString();
        String allocation = edtAllocation.getText().toString();

        if (!validateSymbolInput(symbol) || !validateAllocationInput(allocation)) {
            return;
        }

        applyRecord(symbol, allocation);
    }

    private void applyRecord(String symbol, String allocation) {
        symbolBeforeEdit = symbol.toUpperCase();
        allocationBeforeEdit = allocation;

        handleActionCancel();
    }

    private void handleActionRemove() {
        removeRecord();
    }

    private void removeRecord() {
        dynamicLinearLayout.removeView(editedRecordRoot);
        editedRecordRoot = null;
    }

    private void handleActionCancel() {
        cancelRecord();
    }

    private void cancelRecord() {
        Button btnRemove = editedRecordRoot.findViewById(R.id.btn_remove);
        Button btnApply = editedRecordRoot.findViewById(R.id.btn_apply);
        Button btnEdit = editedRecordRoot.findViewById(R.id.btn_edit);
        Button btnCancel = editedRecordRoot.findViewById(R.id.btn_cancel);
        EditText edtSymbol = editedRecordRoot.findViewById(R.id.edt_symbol);
        EditText edtAllocation = editedRecordRoot.findViewById(R.id.edt_allocation);

        btnRemove.setVisibility(View.INVISIBLE);
        btnApply.setVisibility(View.GONE);
        btnEdit.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);

        edtSymbol.setText(symbolBeforeEdit);
        edtAllocation.setText(allocationBeforeEdit);

        edtSymbol.setEnabled(false);
        edtAllocation.setEnabled(false);

        editedRecordRoot = null;
    }

    private void handleAddRecord() {
        if (editedRecordRoot != null) {
            Toast.makeText(this, R.string.C_main_toast_onlyOneRecordEdit, Toast.LENGTH_SHORT).show();
            return;
        }
        addRecord();
    }

    private void addRecord() {
        hideBtnValidateRecords();
        addThresholdAllocationRecord();
    }

    private void handleSave() {
        if (!performSaveValidations()) {
            return;
        }

        saveRecords();
        Toast.makeText(this, R.string.C_main_toast_savedRecords, Toast.LENGTH_SHORT).show();
    }

    private void saveRecords() {
        saveThresholdAllocationRecordsToDb();

        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        sp.setInt(SharedPrefsConsts.ARE_THRESHOLD_ALLOCATION_RECORDS_VALIDATED, 0);

        revertRecords();
    }

    private void handleRevert() {
        if (editedRecordRoot != null) {
            Toast.makeText(this, R.string.C_main_toast_cantRevertWhileEditing, Toast.LENGTH_SHORT).show();
            return;
        }

        revertRecords();
        Toast.makeText(this, R.string.C_main_toast_restoredRecords, Toast.LENGTH_SHORT).show();
    }

    private void handleValidateRecords() {
        if (editedRecordRoot != null) {
            Toast.makeText(this, R.string.C_main_toast_cantValidateWhileEditing, Toast.LENGTH_SHORT).show();
            return;
        }
        validateRecords();
    }

    private void validateRecords() {
        ValidationDialogFragment dialogFragment = new ValidationDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), ValidationDialogFragment.TAG);

        validationObserveViewModel.setObserveInfo(new ObserveInfo(ObserveInfo.STATUS.RUNNING, ""));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            BinanceRecordsValidation binanceRecordsValidation = new BinanceRecordsValidation(MainActivity.this);
            ObserveInfo observeInfo = binanceRecordsValidation.validateRecordsBinance();
            validationObserveViewModel.postObserveInfo(observeInfo);
        });
    }

    private void addThresholdAllocationRecord() {
        View recordRoot = addEmptyRecord();

        Button btnRemove = recordRoot.findViewById(R.id.btn_remove);
        Button btnApply = recordRoot.findViewById(R.id.btn_apply);
        Button btnEdit = recordRoot.findViewById(R.id.btn_edit);
        Button btnCancel = recordRoot.findViewById(R.id.btn_cancel);

        btnRemove.setOnClickListener(this);
        btnApply.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        StringUtils stringUtils = new StringUtils(this);
        String recordTag = stringUtils.generateRandomString(ROOT_TAG_LENGTH);
        String childrenTag = ROOT_TAG_PREFIX + recordTag;

        recordRoot.setTag(recordTag);
        btnRemove.setTag(childrenTag);
        btnApply.setTag(childrenTag);
        btnEdit.setTag(childrenTag);
        btnCancel.setTag(childrenTag);

        editedRecordRoot = recordRoot;
    }

    private void setValidateRecordsViews() {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        int areRecordsValidated = sp.getInt(SharedPrefsConsts.ARE_THRESHOLD_ALLOCATION_RECORDS_VALIDATED, 0);

        TextView tvValidateRecords = findViewById(R.id.tv_validate_records);

        if (areRecordsValidated == 1) {
            hideBtnValidateRecords();
            tvValidateRecords.setVisibility(View.GONE);
        } else {
            showBtnValidateRecords();
            tvValidateRecords.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateAllocationInput(String allocationInput) {
        try {
            if (allocationInput.isEmpty()) {
                Toast.makeText(this, R.string.C_main_toast_allocationCantBeEmpty, Toast.LENGTH_SHORT).show();
                return false;
            }

            int dotIndex = allocationInput.indexOf(".");

            if (dotIndex < 0) {
                float allocation = Float.parseFloat(allocationInput);
                if (allocation > 100) {
                    Toast.makeText(this, R.string.C_main_toast_allocationMaximalIs, Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (allocation < 0.1) {
                    Toast.makeText(this, R.string.C_main_toast_allocationMinimalIs, Toast.LENGTH_SHORT).show();
                    return false;
                }

                return true;
            }

            if (dotIndex == 0) {
                Toast.makeText(this, R.string.C_main_toast_allocationDotNotFirstCharacter, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (dotIndex == allocationInput.length() - 1) {
                Toast.makeText(this, R.string.C_main_toast_allocationDotNotLastCharacter, Toast.LENGTH_SHORT).show();
                return false;
            }

            String charsAfterDot = allocationInput.substring(dotIndex + 1);
            if (charsAfterDot.length() > 2) {
                Toast.makeText(this, R.string.C_main_toast_allocationUpToDigitsAfterDot, Toast.LENGTH_SHORT).show();
                return false;
            }

            float allocation = Float.parseFloat(allocationInput);
            if (allocation > 100) {
                Toast.makeText(this, R.string.C_main_toast_allocationMaximalIs, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (allocation < 0.1) {
                Toast.makeText(this, R.string.C_main_toast_allocationMinimalIs, Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        } catch (NumberFormatException e) { //shouldn't ever get to catch clause, added just in case
            Toast.makeText(this, R.string.C_main_toast_allocationCoinInvalidFormat, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean validateSymbolInput(String symbolInput) {
        if (symbolInput.isEmpty()) {
            Toast.makeText(this, R.string.C_main_toast_symbolNotEmpty, Toast.LENGTH_SHORT).show();
            return false;
        }

        StringUtils stringUtils = new StringUtils(this);
        if (!stringUtils.isAlphanumeric(symbolInput)) {
            Toast.makeText(this, R.string.C_main_toast_symbolOnlyAlphanumeric, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean performSaveValidations() {
        if (editedRecordRoot != null) {
            Toast.makeText(this, R.string.C_main_toast_cantSaveWhileEditing, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validateSymbolNamesDistinct()) {
            return false;
        }

        return validateSumRecords100();
    }

    private boolean validateSumRecords100() {
        float totalAllocationsSum = 0.0f;
        for (int i = 0; i < dynamicLinearLayout.getChildCount(); i++) {
            View view = dynamicLinearLayout.getChildAt(i);
            EditText edtAllocation = view.findViewById(R.id.edt_allocation);

            try {
                // for some reason got crash that empty, even when have validations for some reason in pre-launch tests
                float allocation = Float.parseFloat(edtAllocation.getText().toString());
                totalAllocationsSum += allocation;
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.C_main_toast_allocationCoinInvalidFormat, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (totalAllocationsSum != 100.0f) {
            Toast.makeText(this, getString(R.string.C_main_toast_allocationsSumNot100) + totalAllocationsSum, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateSymbolNamesDistinct() {
        Set<String> existingAllocations = new HashSet<>();
        for (int i = 0; i < dynamicLinearLayout.getChildCount(); i++) {
            View view = dynamicLinearLayout.getChildAt(i);
            EditText edtSymbol = view.findViewById(R.id.edt_symbol);

            String symbol = edtSymbol.getText().toString();
            if (existingAllocations.contains(symbol)) {
                Toast.makeText(this, getString(R.string.C_main_toast_duplicatedSymbol) + symbol, Toast.LENGTH_SHORT).show();
                return false;
            }

            existingAllocations.add(symbol);
        }

        return true;
    }

    private View getRecordRoot(View view) {
        String rootTag = ((String) view.getTag()).substring(ROOT_TAG_PREFIX.length());
        return dynamicLinearLayout.findViewWithTag(rootTag);
    }

    private void saveThresholdAllocationRecordsToDb() {
        List<ThresholdAllocationRecord> records = new ArrayList<>();

        for (int i = 0; i < dynamicLinearLayout.getChildCount(); i++) {
            View view = dynamicLinearLayout.getChildAt(i);

            EditText edtSymbol = view.findViewById(R.id.edt_symbol);
            EditText edtAllocation = view.findViewById(R.id.edt_allocation);

            String symbol = edtSymbol.getText().toString();
            float allocation = Float.parseFloat(edtAllocation.getText().toString());

            ThresholdAllocationRecord record = new ThresholdAllocationRecord(symbol, allocation);
            records.add(record);
        }

        ThresholdAllocationDb db = new ThresholdAllocationDb(this);
        db.clearAndSaveRecords(records);
    }

    private void saveThresholdAllocationRecordsToBundle(Bundle bundle) {
        int recordsCount = dynamicLinearLayout.getChildCount();
        if (recordsCount == 0) {
            bundle.putParcelableArray(COINS_SAVED_INFO_KEY, null);
            return;
        }

        CoinSavedInfo[] coinSavedInfos = new CoinSavedInfo[recordsCount];
        for (int i = 0; i < recordsCount; i++) {
            View view = dynamicLinearLayout.getChildAt(i);

            EditText edtSymbol = view.findViewById(R.id.edt_symbol);
            EditText edtAllocation = view.findViewById(R.id.edt_allocation);

            String symbol = edtSymbol.getText().toString();
            String allocation = edtAllocation.getText().toString();

            CoinSavedInfo.EDIT_STATUS editStatus = CoinSavedInfo.EDIT_STATUS.NOT_EDITED;
            String symbolCancelData = "";
            String allocationCancelData = "";
            if (editedRecordRoot == view) {
                editStatus = CoinSavedInfo.EDIT_STATUS.ADDED_RECORD;
                Button btnCancel = view.findViewById(R.id.btn_cancel);
                if (btnCancel.getVisibility() == View.VISIBLE) {
                    editStatus = CoinSavedInfo.EDIT_STATUS.EDITED_RECORD;
                }
                symbolCancelData = symbolBeforeEdit;
                allocationCancelData = allocationBeforeEdit;
            }

            CoinSavedInfo coinSavedInfo = new CoinSavedInfo(symbol, allocation, editStatus, symbolCancelData, allocationCancelData);
            coinSavedInfos[i] = coinSavedInfo;
        }

        bundle.putParcelableArray(COINS_SAVED_INFO_KEY, coinSavedInfos);
    }

    private void revertRecords() {
        setValidateRecordsViews();
        dynamicLinearLayout.removeAllViews();
        loadThresholdAllocationRecordsFromDb();
    }

    private void loadThresholdAllocationRecordsFromDb() {
        ThresholdAllocationDb db = new ThresholdAllocationDb(this);
        List<ThresholdAllocationRecord> records = db.loadRecords(db.getRecordsOrderedByDesiredAllocationThenSymbol());

        for (ThresholdAllocationRecord record : records) {
            addThresholdAllocationRecordToUi(record.getSymbol(), record.getDesiredAllocation());
        }
    }

    private void loadRecordsFromBundle(Bundle savedInstanceState) {
        Parcelable[] parcelables = savedInstanceState.getParcelableArray(COINS_SAVED_INFO_KEY);
        if (parcelables == null) {
            return;
        }

        View editedRecord = null;
        for (Parcelable parcelable : parcelables) {
            CoinSavedInfo coinSavedInfo = (CoinSavedInfo) parcelable;
            View record = addCoinSavedInfoToUi(coinSavedInfo);
            if (record != null) {
                editedRecord = record;
            }
        }

        editedRecordRoot = editedRecord;
    }

    // returns edited record if edited, null otherwise
    private View addCoinSavedInfoToUi(CoinSavedInfo coinSavedInfo) {
        CoinSavedInfo.EDIT_STATUS editStatus = coinSavedInfo.getEditStatus();
        if (editStatus == CoinSavedInfo.EDIT_STATUS.ADDED_RECORD) {
            addRecord();
            EditText edtSymbol = editedRecordRoot.findViewById(R.id.edt_symbol);
            EditText edtAllocation = editedRecordRoot.findViewById(R.id.edt_allocation);
            edtSymbol.setText(coinSavedInfo.getSymbolEdtData());
            edtAllocation.setText(coinSavedInfo.getAllocationEdtData());
            return editedRecordRoot;
        } else if (editStatus == CoinSavedInfo.EDIT_STATUS.EDITED_RECORD) {
            addThresholdAllocationRecord();
            EditText edtSymbol = editedRecordRoot.findViewById(R.id.edt_symbol);
            EditText edtAllocation = editedRecordRoot.findViewById(R.id.edt_allocation);
            edtSymbol.setText(coinSavedInfo.getSymbolCancelEdtData());
            edtAllocation.setText(coinSavedInfo.getAllocationCancelEdtData());

            editRecord(editedRecordRoot);

            edtSymbol.setText(coinSavedInfo.getSymbolEdtData());
            edtAllocation.setText(coinSavedInfo.getAllocationEdtData());
            return editedRecordRoot;
        } else {
            addThresholdAllocationRecord();
            applyRecord(coinSavedInfo.getSymbolEdtData(), coinSavedInfo.getAllocationEdtData());
            return null;
        }
    }

    private void addThresholdAllocationRecordToUi(String symbol, float desiredAllocation) {
        addThresholdAllocationRecord();
        applyRecord(symbol, String.valueOf(desiredAllocation));
    }

    private View addEmptyRecord() {
        View rootView = layoutInflater.inflate(R.layout.portfolio_coin_record, null);
        dynamicLinearLayout.addView(rootView);
        return rootView;
    }

    private void hideBtnValidateRecords() {
        Button btnValidateRecords = findViewById(R.id.btn_validate_records);
        btnValidateRecords.setVisibility(View.INVISIBLE);
    }

    private void showBtnValidateRecords() {
        Button btnValidateRecords = findViewById(R.id.btn_validate_records);
        btnValidateRecords.setVisibility(View.VISIBLE);
    }

    private boolean shouldWelcome() {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        return sp.getInt(SharedPrefsConsts.SHOULD_WELCOME, 1) == 1;
    }

    // null if not found
    private ValidationDialogFragment getValidationDialogFragment() {
        return (ValidationDialogFragment) getSupportFragmentManager().findFragmentByTag(ValidationDialogFragment.TAG);
    }
}