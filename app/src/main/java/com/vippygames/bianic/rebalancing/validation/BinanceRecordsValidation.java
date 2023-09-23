package com.vippygames.bianic.rebalancing.validation;

import android.widget.Toast;

import com.vippygames.bianic.MainActivity;
import com.vippygames.bianic.R;
import com.vippygames.bianic.db.threshold_allocation.ThresholdAllocationDb;
import com.vippygames.bianic.db.threshold_allocation.ThresholdAllocationRecord;
import com.vippygames.bianic.exception_handle.CriticalExceptionHandler;
import com.vippygames.bianic.exception_handle.ExceptionHandler;
import com.vippygames.bianic.exception_handle.exceptions.CriticalException;
import com.vippygames.bianic.rebalancing.BinanceManager;
import com.vippygames.bianic.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.vippygames.bianic.rebalancing.api.exchange_info.ExchangeInfo;
import com.vippygames.bianic.rebalancing.api.exchange_info.exceptions.ExchangeInfoParseException;
import com.vippygames.bianic.rebalancing.validation.exceptions.FailedValidateRecordsException;

import java.util.List;
import java.util.Map;

public class BinanceRecordsValidation {
    private final MainActivity mainActivity;

    public BinanceRecordsValidation(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean validateRecordsBinance() {
        try {
            ThresholdAllocationDb db = new ThresholdAllocationDb(mainActivity);
            List<ThresholdAllocationRecord> records = db.loadRecords(db.getRecords());

            BinanceManager binanceManager = new BinanceManager(mainActivity);
            Map<String, ExchangeInfo> exchangeInfo = binanceManager.getExchangeInfo();

            validateCoinsInExchangeInfo(records, exchangeInfo);

            return true;

        } catch (NetworkRequestException | FailedRequestStatusException
                 | EmptyResponseBodyException | FailedValidateRecordsException
                 | ExchangeInfoParseException e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(mainActivity);
            exceptionHandler.handleException(e);
        } catch (Exception e) {
            CriticalExceptionHandler criticalExceptionHandler = new CriticalExceptionHandler(mainActivity);
            criticalExceptionHandler.handleException(
                    new CriticalException(e, CriticalException.CriticalExceptionType.UNLABELED_EXCEPTION)
            );
        }

        return false;
    }

    private void validateCoinsInExchangeInfo(List<ThresholdAllocationRecord> records, Map<String, ExchangeInfo> exchangeInfo) throws FailedValidateRecordsException {
        for (ThresholdAllocationRecord record : records) {
            String coinSymbol = record.getSymbol();
            if (!exchangeInfo.containsKey(record.getSymbol())) {
                String message = mainActivity.getString(R.string.C_validation_coinNotOnBinance0) + coinSymbol + mainActivity.getString(R.string.C_validation_coinNotOnBinance1);
                showToast(message, Toast.LENGTH_SHORT);
                throw new FailedValidateRecordsException(mainActivity, message);
            }
        }
    }

    public void showToast(String message, int toastLength) {
        mainActivity.runOnUiThread(() -> Toast.makeText(mainActivity, message, toastLength).show());
    }
}
