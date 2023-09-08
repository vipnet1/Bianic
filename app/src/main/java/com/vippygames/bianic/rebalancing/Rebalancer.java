package com.vippygames.bianic.rebalancing;

import android.content.Context;

import com.vippygames.bianic.consts.BinanceApiConsts;
import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.db.ThresholdAllocation.ThresholdAllocationDb;
import com.vippygames.bianic.db.ThresholdAllocation.ThresholdAllocationRecord;
import com.vippygames.bianic.exception_handle.CriticalExceptionHandler;
import com.vippygames.bianic.exception_handle.ExceptionHandler;
import com.vippygames.bianic.exception_handle.exceptions.CriticalException;
import com.vippygames.bianic.rebalancing.api.BinanceApi;
import com.vippygames.bianic.rebalancing.api.coins_amount.CoinAmount;
import com.vippygames.bianic.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.vippygames.bianic.rebalancing.api.coins_price.CoinPrice;
import com.vippygames.bianic.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.SignatureGenerationException;
import com.vippygames.bianic.rebalancing.common.ThresholdRecordsOperations;
import com.vippygames.bianic.rebalancing.data_format.CoinDetails;
import com.vippygames.bianic.rebalancing.data_format.CoinsDetailsBuilder;
import com.vippygames.bianic.rebalancing.data_format.exceptions.CoinsDetailsBuilderException;
import com.vippygames.bianic.rebalancing.validation.exceptions.UnvalidatedRecordsException;
import com.vippygames.bianic.rebalancing.watch.threshold.ThresholdWatch;
import com.vippygames.bianic.shared_preferences.SharedPreferencesHelper;
import com.vippygames.bianic.shared_preferences.exceptions.KeyNotFoundException;

import java.util.List;

public class Rebalancer {
    private final Context context;

    public Rebalancer(Context context) {
        this.context = context;
    }

    public void execute() {
        try {
            validateThresholdAllocationRecordsValidated();

            BinanceApi binanceApi = new BinanceApi(context);

            ThresholdAllocationDb db = new ThresholdAllocationDb(context);
            List<ThresholdAllocationRecord> records = db.loadRecords(db.getRecords());

            List<CoinAmount> coinsAmount = binanceApi.getCoinsAmount();

            CoinPrice usdtCoinPrice = new CoinPrice(BinanceApiConsts.USDT_SYMBOL, 1);

            ThresholdRecordsOperations thresholdRecordsOperations = new ThresholdRecordsOperations();
            boolean areRecordsContainUsdt = thresholdRecordsOperations.areRecordsContainUsdt(records);
            String[] coinsSymbols = thresholdRecordsOperations.getCoinsSymbols(records, areRecordsContainUsdt);
            List<CoinPrice> coinsPrice = binanceApi.getCoinsPrice(coinsSymbols);

            if (areRecordsContainUsdt) {
                coinsPrice.add(usdtCoinPrice);
            }

            CoinsDetailsBuilder coinsDetailsBuilder = new CoinsDetailsBuilder();
            List<CoinDetails> coinsDetails = coinsDetailsBuilder.getCoinsDetails(records, coinsAmount, coinsPrice);

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

    private void validateThresholdAllocationRecordsValidated() throws UnvalidatedRecordsException {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(context);
        int areRecordsValidated = sp.getInt(SharedPrefsConsts.ARE_THRESHOLD_ALLOCATION_RECORDS_VALIDATED, 0);

        if (areRecordsValidated != 1) {
            throw new UnvalidatedRecordsException();
        }
    }
}
