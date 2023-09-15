package com.vippygames.bianic.rebalancing;

import android.content.Context;

import com.vippygames.bianic.exception_handle.CriticalExceptionHandler;
import com.vippygames.bianic.exception_handle.ExceptionHandler;
import com.vippygames.bianic.exception_handle.exceptions.CriticalException;
import com.vippygames.bianic.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.vippygames.bianic.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.SignatureGenerationException;
import com.vippygames.bianic.rebalancing.validation.RecordsValidationCheck;
import com.vippygames.bianic.rebalancing.data_format.CoinDetails;
import com.vippygames.bianic.rebalancing.data_format.exceptions.CoinsDetailsBuilderException;
import com.vippygames.bianic.rebalancing.validation.exceptions.UnvalidatedRecordsException;
import com.vippygames.bianic.rebalancing.watch.threshold.ThresholdWatch;
import com.vippygames.bianic.shared_preferences.exceptions.KeyNotFoundException;

import java.util.List;

public class Rebalancer {
    private final Context context;

    public Rebalancer(Context context) {
        this.context = context;
    }

    public void execute() {
        try {
            RecordsValidationCheck recordsValidationCheck = new RecordsValidationCheck(context);
            recordsValidationCheck.validateThresholdAllocationRecordsValidated();

            BinanceManager binanceManager = new BinanceManager(context);
            List<CoinDetails> coinsDetails = binanceManager.generateCoinsDetails();

            ThresholdWatch thresholdWatch = new ThresholdWatch(context);
            thresholdWatch.check(coinsDetails);

        } catch (NetworkRequestException | FailedRequestStatusException | EmptyResponseBodyException
                 | SignatureGenerationException | CoinsPriceParseException
                 | CoinsAmountParseException | CoinsDetailsBuilderException
                 | KeyNotFoundException | UnvalidatedRecordsException e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(context);
            exceptionHandler.handleException(e);
        } catch (Exception e) {
            CriticalExceptionHandler criticalExceptionHandler = new CriticalExceptionHandler(context);
            criticalExceptionHandler.handleException(
                    new CriticalException(e, CriticalException.CriticalExceptionType.UNLABELED_EXCEPTION)
            );
        }
    }
}
