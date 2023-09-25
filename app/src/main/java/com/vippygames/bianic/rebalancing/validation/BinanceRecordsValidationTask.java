package com.vippygames.bianic.rebalancing.validation;

import com.vippygames.bianic.activities.MainActivity;

public class BinanceRecordsValidationTask implements Runnable {
    private final MainActivity mainActivity;

    public BinanceRecordsValidationTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        BinanceRecordsValidation binanceRecordsValidation = new BinanceRecordsValidation(mainActivity);
        boolean result = binanceRecordsValidation.validateRecordsBinance();
        mainActivity.runOnUiThread(() -> mainActivity.onBinanceRecordsValidationTaskFinished(result));
    }
}
