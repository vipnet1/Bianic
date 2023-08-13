package com.example.binancerebalancinghelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.binancerebalancinghelper.rebalancing.schedule.RebalancingCheckIntentService;
import com.example.binancerebalancinghelper.rebalancing.schedule.RebalancingStartService;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LayoutInflater layoutInflater;
    private ScrollView recordsScrollView;
    private LinearLayout dynamicLinearLayout;

    private String symbolBeforeEdit;
    private String allocationBeforeEdit;

    // Only for records of dynamic layout
    @Override
    public void onClick(View view) {
        View recordRoot = getRecordRoot(view);
        int viewId = view.getId();

        if(viewId == R.id.btn_edit) {
            handleActionEdit(recordRoot);
        }
        else if(viewId == R.id.btn_apply) {
            handleActionApply(recordRoot);
        }
        else if(viewId == R.id.btn_cancel) {
            handleActionCancel(recordRoot);
        }
        else if(viewId == R.id.btn_remove) {
            handleActionRemove(recordRoot);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BatteryHelper batteryHelper = new BatteryHelper(this);
        batteryHelper.requestIgnoreBatteryOptimizationsIfNeeded();

//        Intent serviceIntent = new Intent(this, RebalancingCheckIntentService.class);
//        this.startService(serviceIntent);

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recordsScrollView = (ScrollView) findViewById(R.id.sv_coins_allocation);
        dynamicLinearLayout = (LinearLayout) findViewById(R.id.layout_dynamic);

        addBtnAddRecordListener();
    }

    private void handleActionEdit(View recordRoot) {
        Button btnRemove = (Button) recordRoot.findViewById(R.id.btn_remove);
        Button btnApply = (Button) recordRoot.findViewById(R.id.btn_apply);
        Button btnEdit = (Button) recordRoot.findViewById(R.id.btn_edit);
        Button btnCancel = (Button) recordRoot.findViewById(R.id.btn_cancel);
        EditText edtSymbol = (EditText) recordRoot.findViewById(R.id.edt_symbol);
        EditText edtAllocation = (EditText) recordRoot.findViewById(R.id.edt_allocation);

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
        EditText edtSymbol = (EditText) recordRoot.findViewById(R.id.edt_symbol);
        EditText edtAllocation = (EditText) recordRoot.findViewById(R.id.edt_allocation);

        symbolBeforeEdit = edtSymbol.getText().toString();
        allocationBeforeEdit = edtAllocation.getText().toString();

        handleActionCancel(recordRoot);
    }

    private void handleActionRemove(View recordRoot) {
        recordsScrollView.removeView(recordRoot);
    }

    private void handleActionCancel(View recordRoot) {
        Button btnRemove = (Button) recordRoot.findViewById(R.id.btn_remove);
        Button btnApply = (Button) recordRoot.findViewById(R.id.btn_apply);
        Button btnEdit = (Button) recordRoot.findViewById(R.id.btn_edit);
        Button btnCancel = (Button) recordRoot.findViewById(R.id.btn_cancel);
        EditText edtSymbol = (EditText) recordRoot.findViewById(R.id.edt_symbol);
        EditText edtAllocation = (EditText) recordRoot.findViewById(R.id.edt_allocation);

        btnRemove.setVisibility(View.INVISIBLE);
        btnApply.setVisibility(View.GONE);
        btnEdit.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);

        edtSymbol.setText(symbolBeforeEdit);
        edtAllocation.setText(allocationBeforeEdit);

        edtSymbol.setEnabled(false);
        edtAllocation.setEnabled(false);
    }

    private View getRecordRoot(View view) {
        return dynamicLinearLayout.findViewWithTag(view.getTag());
    }

    private void addBtnAddRecordListener() {
        Button btnAddRecord = (Button) findViewById(R.id.btn_add_record);
        btnAddRecord.setOnClickListener((View.OnClickListener) v -> {
            View recordRoot = addEmptyRecord();

            Button btnRemove = (Button) recordRoot.findViewById(R.id.btn_remove);
            Button btnApply = (Button) recordRoot.findViewById(R.id.btn_apply);
            Button btnEdit = (Button) recordRoot.findViewById(R.id.btn_edit);
            Button btnCancel = (Button) recordRoot.findViewById(R.id.btn_cancel);

            btnRemove.setOnClickListener(this);
            btnApply.setOnClickListener(this);
            btnEdit.setOnClickListener(this);
            btnCancel.setOnClickListener(this);

            String recordTag = generateRandomString(10);
            String childrenTag = "rootTag_" + recordTag;

            recordRoot.setTag(recordTag);
            btnRemove.setTag(childrenTag);
            btnApply.setTag(childrenTag);
            btnEdit.setTag(childrenTag);
            btnCancel.setTag(childrenTag);
        });
    }

    private String generateRandomString(int length)
    {
        Random rng = new Random();
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    private View addEmptyRecord() {
        return layoutInflater.inflate(R.layout.portfolio_coin_record, dynamicLinearLayout);
    }
}