package com.vippygames.bianic.rebalancing.report.manual;

import com.vippygames.bianic.ReportsActivity;

public class ManualReportGenerationTask implements Runnable {
    private final ReportsActivity reportsActivity;

    public ManualReportGenerationTask(ReportsActivity reportsActivity) {
        this.reportsActivity = reportsActivity;
    }

    @Override
    public void run() {
        ManualReportGeneration manualReportGeneration = new ManualReportGeneration(reportsActivity);
        boolean result = manualReportGeneration.generateReport();
        reportsActivity.runOnUiThread(() -> reportsActivity.onManualReportGenerationTaskFinished(result));
    }
}
