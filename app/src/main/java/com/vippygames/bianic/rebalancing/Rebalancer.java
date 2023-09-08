package com.vippygames.bianic.rebalancing;

import android.content.Context;

import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.rebalancing.validation.exceptions.UnvalidatedRecordsException;
import com.vippygames.bianic.rebalancing.api.BinanceApi;
import com.vippygames.bianic.rebalancing.api.coins_amount.CoinAmount;
import com.vippygames.bianic.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.vippygames.bianic.rebalancing.api.coins_price.CoinPrice;
import com.vippygames.bianic.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.SignatureGenerationException;
import com.vippygames.bianic.rebalancing.data_format.CoinDetails;
import com.vippygames.bianic.rebalancing.data_format.CoinsDetailsBuilder;
import com.vippygames.bianic.rebalancing.data_format.exceptions.CoinsDetailsBuilderException;
import com.vippygames.bianic.exception_handle.CriticalExceptionHandler;
import com.vippygames.bianic.exception_handle.ExceptionHandler;
import com.vippygames.bianic.exception_handle.exceptions.CriticalException;
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

            List<CoinAmount> coinsAmount = binanceApi.getCoinsAmount();
            List<CoinPrice> coinsPrice = binanceApi.getCoinsPrice(getSymbolsFromCoinsAmount(coinsAmount));

            CoinsDetailsBuilder coinsDetailsBuilder = new CoinsDetailsBuilder();
            List<CoinDetails> coinsDetails = coinsDetailsBuilder.getCoinsDetails(coinsAmount, coinsPrice);

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

        if(areRecordsValidated != 1) {
            throw new UnvalidatedRecordsException();
        }
    }

    private String[] getSymbolsFromCoinsAmount(List<CoinAmount> coinsAmount) {
        String[] results = new String[coinsAmount.size()];

        int index = 0;
        for (CoinAmount coinAmount : coinsAmount) {
            results[index++] = coinAmount.symbol;
        }

        return results;
    }
}
