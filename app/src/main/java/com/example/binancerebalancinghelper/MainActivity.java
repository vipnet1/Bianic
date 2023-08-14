package com.example.binancerebalancinghelper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

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
        dynamicLinearLayout = findViewById(R.id.layout_dynamic);

        addBtnAddRecordListener();
    }

    private void handleActionEdit(View recordRoot) {
        if(editedRecordRoot != null) {
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

        symbolBeforeEdit = edtSymbol.getText().toString();
        allocationBeforeEdit = edtAllocation.getText().toString();

        handleActionCancel(recordRoot);
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
        String rootTag = ((String)view.getTag()).substring(ROOT_TAG_PREFIX.length());
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

            String recordTag = generateRandomString(ROOT_TAG_LENGTH);
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

    private String generateRandomString(int length)
    {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rng = new Random();
        char[] text = new char[length];

        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }

        return new String(text);
    }
}