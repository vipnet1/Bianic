package com.vippygames.bianic;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vippygames.bianic.exception_handle.ExceptionHandler;
import com.vippygames.bianic.rebalancing.report.manual.ManualReportGenerationTask;
import com.vippygames.bianic.rebalancing.validation.RecordsValidationCheck;
import com.vippygames.bianic.rebalancing.validation.exceptions.UnvalidatedRecordsException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportsActivity extends AppCompatActivity {
    private AlertDialog manualReportGenerationDialog;

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

        initManualReportGenerationDialog();
    }

    private void initManualReportGenerationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Report Generation");
        builder.setMessage("Generating report. Wait a moment please.");
        builder.setCancelable(false);

        manualReportGenerationDialog = builder.create();
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

    private void handleActionRedirectMain() {
        finish();
    }
}