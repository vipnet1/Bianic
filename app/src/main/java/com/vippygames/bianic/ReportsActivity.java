package com.vippygames.bianic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vippygames.bianic.consts.NotificationConsts;
import com.vippygames.bianic.consts.ReportsConsts;
import com.vippygames.bianic.db.reports.ReportsDb;
import com.vippygames.bianic.db.reports.ReportsRecord;
import com.vippygames.bianic.db.reports.detailed_reports.DetailedReportsDb;
import com.vippygames.bianic.exception_handle.ExceptionHandler;
import com.vippygames.bianic.rebalancing.report.manual.ManualReportGenerationTask;
import com.vippygames.bianic.rebalancing.validation.RecordsValidationCheck;
import com.vippygames.bianic.rebalancing.validation.exceptions.UnvalidatedRecordsException;
import com.vippygames.bianic.utils.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String ROOT_TAG_PREFIX = "root_tag_";
    private static final int ROOT_TAG_LENGTH = 10;

    private LayoutInflater layoutInflater;
    private LinearLayout dynamicLinearLayout;

    private AlertDialog manualReportGenerationDialog;

    @Override
    public void onClick(View view) {
        View recordRoot = getRecordRoot(view);
        int viewId = view.getId();

        if (viewId == R.id.btn_clear_report) {
            handleActionClearReport(recordRoot);
        } else if (viewId == R.id.btn_details) {
            handleActionDetails(recordRoot);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reports_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.redirect_main) {
            handleActionRedirectMain();
            return true;
        } else if (itemId == R.id.generate_report) {
            handleActionGenerateReport();
            return true;
        } else if (itemId == R.id.clear_all_reports) {
            handleActionClearAllReports();
            return true;
        } else if (itemId == R.id.refresh) {
            handleActionRefresh();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onManualReportGenerationTaskFinished(boolean result) {
        if (result) {
            refreshRecords();
            Toast.makeText(this, "Report generated.", Toast.LENGTH_SHORT).show();
        }
        manualReportGenerationDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dynamicLinearLayout = findViewById(R.id.layout_dynamic_reports);

        loadReports();
        initManualReportGenerationDialog();
    }

    private void loadReports() {
        ReportsDb db = new ReportsDb(this);
        List<ReportsRecord> records = db.loadRecords(db.getRecordsOrderedByCreatedAt());

        for (ReportsRecord record : records) {
            addReportRecordToUi(record);
        }
    }

    private void addReportRecordToUi(ReportsRecord record) {
        View recordRoot = addEmptyRecord();

        TextView tvRecordDbUuid = recordRoot.findViewById(R.id.record_db_uuid);
        TextView tvCreatedAt = recordRoot.findViewById(R.id.tv_created_at);
        TextView tvPassedThreshold = recordRoot.findViewById(R.id.tv_passed_threshold);
        TextView tvDidNotPassThreshold = recordRoot.findViewById(R.id.tv_did_not_pass_threshold);
        TextView tvTotalUsd = recordRoot.findViewById(R.id.tv_total_usd);
        TextView tvThreshold = recordRoot.findViewById(R.id.tv_threshold);
        TextView tvCoins = recordRoot.findViewById(R.id.tv_coins_count);
        TextView tvHighestDeviationCoin = recordRoot.findViewById(R.id.tv_highest_deviation_coin);
        TextView tvHighestDeviationPercent = recordRoot.findViewById(R.id.tv_highest_deviation_percent);
        Button btnDetails = recordRoot.findViewById(R.id.btn_details);
        Button btnClearReport = recordRoot.findViewById(R.id.btn_clear_report);

        StringUtils stringUtils = new StringUtils();
        tvRecordDbUuid.setTag(record.getUuid());
        tvCreatedAt.setText(stringUtils.convertUtcToLocalTime(record.getCreatedAt()));

        if (record.shouldRebalance()) {
            tvPassedThreshold.setVisibility(View.VISIBLE);
            tvDidNotPassThreshold.setVisibility(View.GONE);
            recordRoot.setBackgroundColor(Color.rgb(162, 239, 165));
        } else {
            tvPassedThreshold.setVisibility(View.GONE);
            tvDidNotPassThreshold.setVisibility(View.VISIBLE);
            recordRoot.setBackgroundColor(Color.rgb(209, 219, 230));
        }

        tvTotalUsd.setText(stringUtils.convertDoubleToString(record.getPortfolioUsdValue(), 1) + "$");
        tvThreshold.setText(stringUtils.convertDoubleToString(record.getThresholdRebalancingPercent(), 3) + "%");
        tvCoins.setText(String.valueOf(record.getCoinsCount()));
        tvHighestDeviationCoin.setText(record.getHighestDeviationCoin());
        tvHighestDeviationPercent.setText(stringUtils.convertDoubleToString(record.getHighestDeviationPercent(), 3) + "%");

        btnDetails.setOnClickListener(this);
        btnClearReport.setOnClickListener(this);

        String recordTag = stringUtils.generateRandomString(ROOT_TAG_LENGTH);
        String childrenTag = ROOT_TAG_PREFIX + recordTag;

        recordRoot.setTag(recordTag);
        btnDetails.setTag(childrenTag);
        btnClearReport.setTag(childrenTag);
    }

    private void initManualReportGenerationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Report Generation");
        builder.setMessage("Generating report. Wait a moment please.");
        builder.setCancelable(false);

        manualReportGenerationDialog = builder.create();
    }

    private void handleActionClearAllReports() {
        dynamicLinearLayout.removeAllViews();

        ReportsDb reportsDb = new ReportsDb(this);
        reportsDb.clearAllReportsFromDb();

        DetailedReportsDb detailedReportsDb = new DetailedReportsDb(this);
        detailedReportsDb.clearAllDetailedReportsFromDb();
    }

    private void handleActionClearReport(View recordRoot) {
        TextView recordDbUuid = recordRoot.findViewById(R.id.record_db_uuid);
        dynamicLinearLayout.removeView(recordRoot);

        String uuid = recordDbUuid.getTag().toString();

        ReportsDb reportsDb = new ReportsDb(this);
        reportsDb.clearReportFromDb(uuid);

        DetailedReportsDb detailedReportsDb = new DetailedReportsDb(this);
        detailedReportsDb.clearDetailedReportsByReportUuid(uuid);
    }

    private void handleActionDetails(View recordRoot) {
        Intent intent = new Intent(this, DetailedReportActivity.class);

        TextView recordDbUuid = recordRoot.findViewById(R.id.record_db_uuid);
        intent.putExtra(ReportsConsts.REPORT_RECORD_UUID_INTENT_EXTRA, recordDbUuid.getTag().toString());

        this.startActivity(intent);
    }

    private void handleActionGenerateReport() {
        try {
            RecordsValidationCheck recordsValidationCheck = new RecordsValidationCheck(this);
            recordsValidationCheck.validateThresholdAllocationRecordsValidated();

            manualReportGenerationDialog.show();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new ManualReportGenerationTask(this));

        } catch (UnvalidatedRecordsException e) {
            Toast.makeText(this, "Click on 'Validate Records' button in main page", Toast.LENGTH_SHORT).show();
            ExceptionHandler exceptionHandler = new ExceptionHandler(this);
            exceptionHandler.handleException(e);
        }
    }

    private void handleActionRefresh() {
        refreshRecords();
    }

    private void handleActionRedirectMain() {
        if (!getIntent().getBooleanExtra(NotificationConsts.LAUNCHED_FROM_NOTIFICATION_EXTRA, false)) {
            finish();
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }

    private void refreshRecords() {
        dynamicLinearLayout.removeAllViews();
        loadReports();
    }

    private View addEmptyRecord() {
        View rootView = layoutInflater.inflate(R.layout.report_record, null);
        dynamicLinearLayout.addView(rootView);
        return rootView;
    }

    private View getRecordRoot(View view) {
        String rootTag = ((String) view.getTag()).substring(ROOT_TAG_PREFIX.length());
        return dynamicLinearLayout.findViewWithTag(rootTag);
    }
}