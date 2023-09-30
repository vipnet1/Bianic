package com.vippygames.bianic.rebalancing.validation;

import android.content.Context;

import com.vippygames.bianic.R;
import com.vippygames.bianic.activities.observe.ObserveInfo;
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
    private ObserveInfo observeInfo;
    private final Context context;

    public BinanceRecordsValidation(Context context) {
        this.context = context;
    }

    public ObserveInfo validateRecordsBinance() {
        try {
            observeInfo = new ObserveInfo(ObserveInfo.STATUS.RUNNING, "");

            ThresholdAllocationDb db = new ThresholdAllocationDb(context);
            List<ThresholdAllocationRecord> records = db.loadRecords(db.getRecords());

            BinanceManager binanceManager = new BinanceManager(context);
            Map<String, ExchangeInfo> exchangeInfo = binanceManager.getExchangeInfo();

            validateCoinsInExchangeInfo(records, exchangeInfo);

            observeInfo.setStatus(ObserveInfo.STATUS.FINISHED);
            return observeInfo;

        } catch (NetworkRequestException | FailedRequestStatusException
                 | EmptyResponseBodyException | FailedValidateRecordsException
                 | ExchangeInfoParseException e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(context);
            exceptionHandler.handleException(e);
        } catch (Exception e) {
            CriticalExceptionHandler criticalExceptionHandler = new CriticalExceptionHandler(context);
            criticalExceptionHandler.handleException(
                    new CriticalException(e, CriticalException.CriticalExceptionType.UNLABELED_EXCEPTION)
            );
        }

        observeInfo.setStatus(ObserveInfo.STATUS.FAILED);
        return observeInfo;
    }

    private void validateCoinsInExchangeInfo(List<ThresholdAllocationRecord> records, Map<String, ExchangeInfo> exchangeInfo) throws FailedValidateRecordsException {
        for (ThresholdAllocationRecord record : records) {
            String coinSymbol = record.getSymbol();
            if (!exchangeInfo.containsKey(record.getSymbol())) {
                String message = context.getString(R.string.C_validation_coinNotOnBinance0) + coinSymbol + context.getString(R.string.C_validation_coinNotOnBinance1);
                observeInfo.setMessage(message);
                throw new FailedValidateRecordsException(context, message);
            }
        }
    }
}
