package com.vippygames.bianic;

import android.app.AlertDialog;
import android.content.Context;
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

import com.vippygames.bianic.db.Reports.ReportsDb;
import com.vippygames.bianic.db.Reports.ReportsRecord;
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
        }
        else if(itemId == R.id.clear_all_reports) {
            handleActionClearAllReports();
            return true;
        }
        else if(itemId == R.id.refresh) {
            handleActionRefresh();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onManualReportGenerationTaskFinished(boolean result) {
        manualReportGenerationDialog.dismiss();

        if (!result) {
            return;
        }

        Toast.makeText(this, "Report generated.", Toast.LENGTH_SHORT).show();
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
            addReportRecordToUi(
                    record.getUuid(), record.shouldRebalance(), record.getPortfolioUsdValue(),
                    record.getCoinsCount(), record.getThresholdRebalancingPercent(),
                    record.getHighestDeviationCoin(), record.getHighestDeviationPercent(), record.getCreatedAt()
            );
        }
    }

    private void addReportRecordToUi(String uuid, boolean shouldRebalance, double portfolioUsdValue,
                                     int coinsCount, double thresholdRebalancingPercent, String highestDeviationCoin,
                                     double highestDeviationPercent, String createdAt) {
        View recordRoot = addEmptyRecord();

        TextView tvRecordDbId = recordRoot.findViewById(R.id.record_db_uuid);
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

        tvRecordDbId.setTag(uuid);
        tvCreatedAt.setText(createdAt);

        if (shouldRebalance) {
            tvPassedThreshold.setVisibility(View.VISIBLE);
            tvDidNotPassThreshold.setVisibility(View.GONE);
            recordRoot.setBackgroundColor(Color.rgb(162, 239, 165));
        } else {
            tvPassedThreshold.setVisibility(View.GONE);
            tvDidNotPassThreshold.setVisibility(View.VISIBLE);
            recordRoot.setBackgroundColor(Color.rgb(170, 187, 204));
        }

        tvTotalUsd.setText(String.valueOf(portfolioUsdValue));
        tvThreshold.setText(String.valueOf(thresholdRebalancingPercent));
        tvCoins.setText(String.valueOf(coinsCount));
        tvHighestDeviationCoin.setText(String.valueOf(highestDeviationCoin));
        tvHighestDeviationPercent.setText(String.valueOf(highestDeviationPercent));

        btnDetails.setOnClickListener(this);
        btnClearReport.setOnClickListener(this);

        StringUtils stringUtils = new StringUtils();
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

        ReportsDb db = new ReportsDb(this);
        db.clearAllReportsFromDb();

        // todo: clear all detailed reports
    }

    private void handleActionClearReport(View recordRoot) {
        TextView recordDbUuid = recordRoot.findViewById(R.id.record_db_uuid);
        dynamicLinearLayout.removeView(recordRoot);

        String uuid = recordDbUuid.getTag().toString();
        ReportsDb db = new ReportsDb(this);
        db.clearReportFromDb(uuid);

        // todo: detailed reports
    }

    private void handleActionDetails(View recordRoot) {

    }

    private void handleActionGenerateReport() {
        try {
            RecordsValidationCheck recordsValidationCheck = new RecordsValidationCheck(this);
            recordsValidationCheck.validateThresholdAllocationRecordsValidated();

            manualReportGenerationDialog.show();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new ManualReportGenerationTask(this));

        } catch (UnvalidatedRecordsException e) {
            Toast.makeText(this, "Unable to generate report: click on 'Validate Records' button in the main page", Toast.LENGTH_LONG).show();
            ExceptionHandler exceptionHandler = new ExceptionHandler(this);
            exceptionHandler.handleException(e);
        }
    }

    private void handleActionRefresh() {
        dynamicLinearLayout.removeAllViews();
        loadReports();
    }

    private void handleActionRedirectMain() {
        finish();
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