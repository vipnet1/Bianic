package com.example.binancerebalancinghelper;

import android.content.Context;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.binancerebalancinghelper.rebalancing.schedule.RebalancingCheckIntentService;
import com.example.binancerebalancinghelper.utils.StringUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String ROOT_TAG_PREFIX = "root_tag_";
    private static final int ROOT_TAG_LENGTH = 10;

    private LayoutInflater layoutInflater;
    private LinearLayout dynamicLinearLayout;

    private View editedRecordRoot = null;
    private String symbolBeforeEdit;
    private String allocationBeforeEdit;

    // Only for records of dynamic layout
    @Override
    public void onClick(View view) {
        View recordRoot = getRecordRoot(view);
        int viewId = view.getId();

        if (viewId == R.id.btn_edit) {
            handleActionEdit(recordRoot);
        } else if (viewId == R.id.btn_apply) {
            handleActionApply(recordRoot);
        } else if (viewId == R.id.btn_cancel) {
            handleActionCancel(recordRoot);
        } else if (viewId == R.id.btn_remove) {
            handleActionRemove(recordRoot);
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
        }
        else if(itemId == R.id.redirect_configure) {
            handleActionRedirectConfigure();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleActionRedirectExceptions() {
        Intent intent = new Intent(this, ExceptionsActivity.class);
        this.startActivity(intent);
    }

    private void handleActionRedirectConfigure() {
        Intent intent = new Intent(this, ConfigureActivity.class);
        this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BatteryHelper batteryHelper = new BatteryHelper(this);
        batteryHelper.requestIgnoreBatteryOptimizationsIfNeeded();

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dynamicLinearLayout = findViewById(R.id.layout_dynamic_main);

        addBtnAddRecordListener();
    }

    private void handleActionEdit(View recordRoot) {
        if (editedRecordRoot != null) {
            Toast.makeText(this, "Only one concurrent record edit", Toast.LENGTH_SHORT).show();
            return;
        }

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

    private void handleActionApply(View recordRoot) {
        EditText edtSymbol = recordRoot.findViewById(R.id.edt_symbol);
        EditText edtAllocation = recordRoot.findViewById(R.id.edt_allocation);

        String symbol = edtSymbol.getText().toString();
        String allocation = edtAllocation.getText().toString();

        if (!validateSymbolInput(symbol) || !validateAllocationInput(allocation)) {
            return;
        }

        symbolBeforeEdit = symbol;
        allocationBeforeEdit = allocation;

        handleActionCancel(recordRoot);
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

        return true;
    }

    private boolean validateRecordInput(String symbol, String allocation) {
        if (allocation.isEmpty()) {
            Toast.makeText(this, "Allocation cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        int allocationPercent = Integer.parseInt(allocation);
        if (allocationPercent < 1) {
            Toast.makeText(this, "Minimal allocation is 1%", Toast.LENGTH_SHORT).show();
            return false;
        } else if (allocationPercent > 100) {
            Toast.makeText(this, "Maximal allocation is 100%", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (symbol.isEmpty()) {
            Toast.makeText(this, "Symbol cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void handleActionRemove(View recordRoot) {
        dynamicLinearLayout.removeView(recordRoot);
        editedRecordRoot = null;
    }

    private void handleActionCancel(View recordRoot) {
        Button btnRemove = recordRoot.findViewById(R.id.btn_remove);
        Button btnApply = recordRoot.findViewById(R.id.btn_apply);
        Button btnEdit = recordRoot.findViewById(R.id.btn_edit);
        Button btnCancel = recordRoot.findViewById(R.id.btn_cancel);
        EditText edtSymbol = recordRoot.findViewById(R.id.edt_symbol);
        EditText edtAllocation = recordRoot.findViewById(R.id.edt_allocation);

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

    private View getRecordRoot(View view) {
        String rootTag = ((String) view.getTag()).substring(ROOT_TAG_PREFIX.length());
        return dynamicLinearLayout.findViewWithTag(rootTag);
    }

    private void addBtnAddRecordListener() {
        Button btnAddRecord = findViewById(R.id.btn_add_record);
        btnAddRecord.setOnClickListener(v -> {
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
        });
    }

    private View addEmptyRecord() {
        View rootView = layoutInflater.inflate(R.layout.portfolio_coin_record, null);
        dynamicLinearLayout.addView(rootView);
        return rootView;
    }
}