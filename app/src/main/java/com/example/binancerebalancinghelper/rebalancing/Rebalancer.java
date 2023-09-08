package com.example.binancerebalancinghelper.rebalancing;

import android.content.Context;

import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;
import com.example.binancerebalancinghelper.exceptions.UnvalidatedRecordsException;
import com.example.binancerebalancinghelper.rebalancing.api.BinanceApi;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.CoinAmount;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.CoinPrice;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.example.binancerebalancinghelper.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions.SignatureGenerationException;
import com.example.binancerebalancinghelper.rebalancing.data_format.CoinDetails;
import com.example.binancerebalancinghelper.rebalancing.data_format.CoinsDetailsBuilder;
import com.example.binancerebalancinghelper.rebalancing.data_format.exceptions.CoinsDetailsBuilderException;
import com.example.binancerebalancinghelper.exception_handle.CriticalExceptionHandler;
import com.example.binancerebalancinghelper.exception_handle.ExceptionHandler;
import com.example.binancerebalancinghelper.exception_handle.exceptions.CriticalException;
import com.example.binancerebalancinghelper.rebalancing.watch.threshold.ThresholdWatch;
import com.example.binancerebalancinghelper.shared_preferences.SharedPreferencesHelper;
import com.example.binancerebalancinghelper.shared_preferences.exceptions.KeyNotFoundException;

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
