package com.vippygames.bianic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

import com.vippygames.bianic.consts.BinanceApiConsts;
import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.db.threshold_allocation.ThresholdAllocationDb;
import com.vippygames.bianic.db.threshold_allocation.ThresholdAllocationRecord;
import com.vippygames.bianic.permissions.BatteryPermissions;
import com.vippygames.bianic.permissions.NotificationPermissions;
import com.vippygames.bianic.rebalancing.validation.BinanceRecordsValidationTask;
import com.vippygames.bianic.shared_preferences.SharedPreferencesHelper;
import com.vippygames.bianic.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String ROOT_TAG_PREFIX = "root_tag_";
    private static final int ROOT_TAG_LENGTH = 10;

    private LayoutInflater layoutInflater;
    private LinearLayout dynamicLinearLayout;

    private View editedRecordRoot = null;
    private String symbolBeforeEdit;
    private String allocationBeforeEdit;

    private AlertDialog binanceRecordsValidationDialog;
    private TextView tvValidateRecords;
    private Button btnValidateRecords;

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
            handleActionRedirectExceptions();
            return true;
        } else if (itemId == R.id.redirect_configure) {
            handleActionRedirectConfigure();
            return true;
        } else if (itemId == R.id.redirect_reports) {
            handleActionRedirectReports();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBinanceRecordsValidationTaskFinished(boolean result) {
        binanceRecordsValidationDialog.dismiss();

        if (!result) {
            return;
        }

        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        sp.setInt(SharedPrefsConsts.ARE_THRESHOLD_ALLOCATION_RECORDS_VALIDATED, 1);

        setValidateRecordsViews();
        Toast.makeText(this, "Records are valid", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dynamicLinearLayout = findViewById(R.id.layout_dynamic_main);

        Button btnAddRecord = findViewById(R.id.btn_add_record);
        btnAddRecord.setOnClickListener(v -> handleAddRecord());

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> handleSave());

        Button btnRevert = findViewById(R.id.btn_revert);
        btnRevert.setOnClickListener(v -> handleRevert());

        btnValidateRecords = findViewById(R.id.btn_validate_records);
        btnValidateRecords.setOnClickListener(v -> handleValidateRecords());

        tvValidateRecords = findViewById(R.id.tv_validate_records);
        setValidateRecordsViews();

        addFirstThresholdRecordIfNeeded();
        loadThresholdAllocationRecords();

        initBinanceRecordsValidationDialog();
    }

    private void checkPermissions() {
        BatteryPermissions batteryPermissions = new BatteryPermissions(this);
        NotificationPermissions notificationPermissions = new NotificationPermissions();

        boolean haveBatteryPermission = batteryPermissions.isIgnoringBatteryOptimizations();
        boolean haveNotificationPermission = notificationPermissions.havePostNotificationsPermission(this);

        if(!haveBatteryPermission || !haveNotificationPermission) {
            showRequestPermissionsDialog();
        }
    }

    private void showRequestPermissionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Why Those Permissions Are Needed");
        builder.setMessage("The permissions we will ask for are crucial for the normal operation of the app." +
                "The battery one to continue doing our job in power saving mode. The notifications to run " +
                "in background & communicate with you.");
        builder.setCancelable(false);
        builder.setPositiveButton("Sure Son", (dialog, which) -> {
            dialog.dismiss();
            requestNeededPermissions();
        });

        AlertDialog requestPermissionsDialog = builder.create();
        requestPermissionsDialog.show();
    }

    private void requestNeededPermissions() {
        BatteryPermissions batteryPermissions = new BatteryPermissions(this);
        NotificationPermissions notificationPermissions = new NotificationPermissions();

        if(!batteryPermissions.isIgnoringBatteryOptimizations()) {
            batteryPermissions.requestIgnoreBatteryOptimizations();
        }

        if(!notificationPermissions.havePostNotificationsPermission(this)) {
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

    private void initBinanceRecordsValidationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Records Validation");
        builder.setMessage("Checking with binance whether the records are valid. Wait a moment please.");
        builder.setCancelable(false);

        binanceRecordsValidationDialog = builder.create();
    }

    private void handleActionRedirectExceptions() {
        Intent intent = new Intent(this, ExceptionsActivity.class);
        this.startActivity(intent);
    }

    private void handleActionRedirectReports() {
        Intent intent = new Intent(this, ReportsActivity.class);
        this.startActivity(intent);
    }

    private void handleActionRedirectConfigure() {
        Intent intent = new Intent(this, ConfigureActivity.class);
        this.startActivity(intent);
    }

    private void handleActionEdit(View recordRoot) {
        if (editedRecordRoot != null) {
            Toast.makeText(this, "Only one concurrent record edit", Toast.LENGTH_SHORT).show();
            return;
        }

        btnValidateRecords.setVisibility(View.INVISIBLE);

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

        symbolBeforeEdit = symbol;
        allocationBeforeEdit = allocation;

        handleActionCancel();
    }

    private void handleActionRemove() {
        dynamicLinearLayout.removeView(editedRecordRoot);
        editedRecordRoot = null;
    }

    private void handleActionCancel() {
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
            Toast.makeText(this, "Only one concurrent record edit", Toast.LENGTH_SHORT).show();
            return;
        }

        btnValidateRecords.setVisibility(View.INVISIBLE);

        addThresholdAllocationRecord();
    }

    private void handleSave() {
        if (!performSaveValidations()) {
            return;
        }
        saveThresholdAllocationRecords();

        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        sp.setInt(SharedPrefsConsts.ARE_THRESHOLD_ALLOCATION_RECORDS_VALIDATED, 0);

        revertRecords();

        btnValidateRecords.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Saved records", Toast.LENGTH_SHORT).show();
    }

    private void handleRevert() {
        if (editedRecordRoot != null) {
            Toast.makeText(this, "Can't revert while editing record", Toast.LENGTH_SHORT).show();
            return;
        }

        revertRecords();
        setValidateRecordsViews();
        Toast.makeText(this, "Restored previous records", Toast.LENGTH_SHORT).show();
    }

    private void handleValidateRecords() {
        if (editedRecordRoot != null) {
            Toast.makeText(this, "Can't validate records while editing record", Toast.LENGTH_SHORT).show();
            return;
        }

        binanceRecordsValidationDialog.show();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new BinanceRecordsValidationTask(this));
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

        StringUtils stringUtils = new StringUtils();
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
        int shouldValidateRecords = sp.getInt(SharedPrefsConsts.ARE_THRESHOLD_ALLOCATION_RECORDS_VALIDATED, 0);

        if (shouldValidateRecords == 1) {
            btnValidateRecords.setVisibility(View.INVISIBLE);
            tvValidateRecords.setVisibility(View.GONE);
        } else {
            btnValidateRecords.setVisibility(View.VISIBLE);
            tvValidateRecords.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateAllocationInput(String allocationInput) {
        if (allocationInput.isEmpty()) {
            Toast.makeText(this, "Allocation cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        int dotIndex = allocationInput.indexOf(".");

        if (dotIndex < 0) {
            float allocation = Float.parseFloat(allocationInput);
            if (allocation > 100) {
                Toast.makeText(this, "Maximal allocation is 100%", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (allocation < 0.1) {
                Toast.makeText(this, "Minimal allocation is 0.1%", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        }

        if (dotIndex == 0) {
            Toast.makeText(this, "Dot cannot be first character in allocation", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dotIndex == allocationInput.length() - 1) {
            Toast.makeText(this, "Dot cannot be last character in allocation", Toast.LENGTH_SHORT).show();
            return false;
        }

        String charsAfterDot = allocationInput.substring(dotIndex + 1);

        if (charsAfterDot.length() > 2) {
            Toast.makeText(this, "up to 2 digits after dot in allocation", Toast.LENGTH_SHORT).show();
            return false;
        }

        float allocation = Float.parseFloat(allocationInput);
        if (allocation > 100) {
            Toast.makeText(this, "Maximal allocation is 100%", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (allocation < 0.1) {
            Toast.makeText(this, "Minimal allocation is 0.1%", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateSymbolInput(String symbolInput) {
        if (symbolInput.isEmpty()) {
            Toast.makeText(this, "Symbol cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        StringUtils stringUtils = new StringUtils();
        if (!stringUtils.isAlphanumeric(symbolInput)) {
            Toast.makeText(this, "Symbol must contain only alphanumeric characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean performSaveValidations() {
        if (editedRecordRoot != null) {
            Toast.makeText(this, "Can't save while editing record", Toast.LENGTH_SHORT).show();
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

            float allocation = Float.parseFloat(edtAllocation.getText().toString());
            totalAllocationsSum += allocation;
        }

        if (totalAllocationsSum != 100.0f) {
            Toast.makeText(this, "Total allocations sum is not 100 - it's " + totalAllocationsSum, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Same symbol name in multiple records - " + symbol, Toast.LENGTH_SHORT).show();
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

    private void saveThresholdAllocationRecords() {
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

    private void revertRecords() {
        setValidateRecordsViews();

        dynamicLinearLayout.removeAllViews();
        loadThresholdAllocationRecords();
    }

    private void loadThresholdAllocationRecords() {
        ThresholdAllocationDb db = new ThresholdAllocationDb(this);
        List<ThresholdAllocationRecord> records = db.loadRecords(db.getRecordsOrderedByDesiredAllocationThenSymbol());

        for (ThresholdAllocationRecord record : records) {
            addThresholdAllocationRecordToUi(record.getSymbol(), record.getDesiredAllocation());
        }
    }

    private void addThresholdAllocationRecordToUi(String symbol, float desiredAllocation) {
        addThresholdAllocationRecord();

        EditText edtSymbol = editedRecordRoot.findViewById(R.id.edt_symbol);
        EditText edtAllocation = editedRecordRoot.findViewById(R.id.edt_allocation);

        edtSymbol.setText(symbol);
        edtAllocation.setText(String.valueOf(desiredAllocation));

        handleActionApply();
    }

    private View addEmptyRecord() {
        View rootView = layoutInflater.inflate(R.layout.portfolio_coin_record, null);
        dynamicLinearLayout.addView(rootView);
        return rootView;
    }
}