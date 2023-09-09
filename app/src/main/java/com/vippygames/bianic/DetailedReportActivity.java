package com.vippygames.bianic;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vippygames.bianic.consts.DetailedReportsConsts;
import com.vippygames.bianic.consts.ReportsConsts;
import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.db.reports.detailed_reports.DetailedReportsDb;
import com.vippygames.bianic.db.reports.detailed_reports.DetailedReportsRecord;
import com.vippygames.bianic.shared_preferences.SharedPreferencesHelper;
import com.vippygames.bianic.utils.StringUtils;

import java.util.List;
import java.util.Random;

public class DetailedReportActivity extends AppCompatActivity {
    private TableLayout detailedReportTable;
    private int[] columnsColors;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailed_report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.back) {
            handleActionBack();
            return true;
        } else if (itemId == R.id.rotate) {
            handleActionRotate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_report);

        detailedReportTable = findViewById(R.id.layout_detailed_report);
        columnsColors = generateColumnsColors();
        setColumnsColors();
        loadDetailedReports();
        applyRotation();
    }

    private void setColumnsColors() {
        TableRow columnsTableRow = findViewById(R.id.columns_table_row);

        for (int i = 0; i < DetailedReportsConsts.COLUMNS_COUNT; i++) {
            View cell = columnsTableRow.getChildAt(i);
            cell.setBackgroundColor(columnsColors[i]);
        }
    }

    private void handleActionRotate() {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        int isRotationLandscape = sp.getInt(SharedPrefsConsts.IS_DETAILED_REPORT_ROTATION_LANDSCAPE,
                DetailedReportsConsts.IS_DEFAULT_ROTATION_LANDSCAPE);
        sp.setInt(SharedPrefsConsts.IS_DETAILED_REPORT_ROTATION_LANDSCAPE, 1 - isRotationLandscape);
        applyRotation();
    }

    private void handleActionBack() {
        finish();
    }

    private void applyRotation() {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        int isRotationLandscape = sp.getInt(SharedPrefsConsts.IS_DETAILED_REPORT_ROTATION_LANDSCAPE,
                DetailedReportsConsts.IS_DEFAULT_ROTATION_LANDSCAPE);

        if (isRotationLandscape == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private int[] generateColumnsColors() {
        int[] possibleColors = {
                Color.parseColor("#FFC107"), // Yellow
                Color.parseColor("#4CAF50"), // Green
                Color.parseColor("#F44336"), // Red
                Color.parseColor("#2196F3"), // Blue
                Color.parseColor("#9C27B0"),  // Purple
                Color.parseColor("#FF99CC"), // Pink
                Color.parseColor("#D68EF0"), // Light Purple
                Color.parseColor("#3DE3E6"), // Light Blue
                Color.parseColor("#9BE87D"), // Light Green
        };

        Random random = new Random();
        int[] columnsColors = new int[DetailedReportsConsts.COLUMNS_COUNT];
        int previousColorIndex = -1;

        for (int i = 0; i < DetailedReportsConsts.COLUMNS_COUNT; i++) {
            int colorIndex;
            if (i == 0) {
                colorIndex = random.nextInt(possibleColors.length);
            } else {
                colorIndex = random.nextInt(possibleColors.length - 1);
                if (previousColorIndex == colorIndex) {
                    colorIndex = possibleColors.length - 1;
                }
            }
            columnsColors[i] = possibleColors[colorIndex];
            previousColorIndex = colorIndex;
        }

        return columnsColors;
    }

    private void loadDetailedReports() {
        String reportRecordUuid = getIntent().getStringExtra(ReportsConsts.REPORT_RECORD_UUID_INTENT_EXTRA);

        DetailedReportsDb db = new DetailedReportsDb(this);
        List<DetailedReportsRecord> records = db.loadRecords(db.getRecordsOrderedByTargetAllocationThenCoin(reportRecordUuid));

        for (DetailedReportsRecord record : records) {
            addDetailedReportRecordToUi(record);
        }
    }

    private void addDetailedReportRecordToUi(DetailedReportsRecord record) {
        TableRow row = new TableRow(this);

        for (int i = 0; i < DetailedReportsConsts.COLUMNS_COUNT; i++) {
            TextView cell = createDetailedReportTableCell();
            cell.setBackgroundColor(columnsColors[i]);
            addDataToCell(cell, record, i);
            row.addView(cell);
        }

        detailedReportTable.addView(row);
    }

    private TextView createDetailedReportTableCell() {
        TextView cell = new TextView(DetailedReportActivity.this);
        cell.setTextSize(18);
        cell.setTextColor(Color.WHITE);
        cell.setPadding(16, 16, 16, 16);
        cell.setGravity(Gravity.CENTER);
        return cell;
    }

    private void addDataToCell(TextView cell, DetailedReportsRecord record, int cellIndex) {
        StringUtils stringUtils = new StringUtils();
        switch (cellIndex) {
            case DetailedReportsConsts.COIN_COLUMN_INDEX:
                cell.setText(record.getCoin());
                break;
            case DetailedReportsConsts.TARGET_ALLOCATION_COLUMN_INDEX:
                cell.setText(stringUtils.convertDoubleToString(record.getTargetAllocation(), 2) + "%");
                break;
            case DetailedReportsConsts.QUANTITY_COLUMN_INDEX:
                cell.setText(stringUtils.convertDoubleToString(record.getQuantity(), 6));
                break;
            case DetailedReportsConsts.PRICE_COLUMN_INDEX:
                cell.setText(stringUtils.convertDoubleToString(record.getPrice(), 4) + "$");
                break;
            case DetailedReportsConsts.CURRENT_VALUE_COLUMN_INDEX:
                cell.setText(stringUtils.convertDoubleToString(record.getCurrentUsdValue(), 1) + "$");
                break;
            case DetailedReportsConsts.CURRENT_ALLOCATION_COLUMN_INDEX:
                cell.setText(stringUtils.convertDoubleToString(record.getCurrentAllocation(), 3) + "%");
                break;
            case DetailedReportsConsts.TARGET_QUANTITY_COLUMN_INDEX:
                double targetQuantity = record.getTargetQuantity();
                String message = "";
                if (targetQuantity >= 0) {
                    message += "Buy ";
                } else {
                    message += "Sell ";
                }

                double absoluteTargetQuantity = Math.abs(targetQuantity);
                message += stringUtils.convertDoubleToString(absoluteTargetQuantity, 6);

                double targetQuantityUsd = absoluteTargetQuantity * record.getPrice();
                message += " " + record.getCoin() + " | "
                        + stringUtils.convertDoubleToString(targetQuantityUsd, 2) + " USD";

                cell.setText(message);
                break;
        }
    }
}