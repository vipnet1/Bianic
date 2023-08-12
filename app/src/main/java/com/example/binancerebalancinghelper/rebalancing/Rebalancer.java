package com.example.binancerebalancinghelper.rebalancing;

import android.content.Context;

import com.example.binancerebalancinghelper.rebalancing.api.BinanceApi;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.CoinAmount;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.CoinPrice;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.example.binancerebalancinghelper.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.example.binancerebalancinghelper.rebalancing.api.common.json.exceptions.JsonParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions.SignatureGenerationException;
import com.example.binancerebalancinghelper.rebalancing.data_format.CoinDetails;
import com.example.binancerebalancinghelper.rebalancing.data_format.CoinsDetailsBuilder;
import com.example.binancerebalancinghelper.rebalancing.data_format.exceptions.CoinsDetailsBuilderException;
import com.example.binancerebalancinghelper.rebalancing.exception_handle.CriticalExceptionHandler;
import com.example.binancerebalancinghelper.rebalancing.exception_handle.ExceptionHandler;
import com.example.binancerebalancinghelper.rebalancing.exception_handle.exceptions.CriticalException;
import com.example.binancerebalancinghelper.rebalancing.watch.threshold.ThresholdWatch;

import java.util.List;

public class Rebalancer {
    private final Context context;

    public Rebalancer(Context context) {
        this.context = context;
    }

    public void execute() {
        try {
            BinanceApi binanceApi = new BinanceApi(context);

            List<CoinAmount> coinsAmount = binanceApi.getCoinsAmount();
            List<CoinPrice> coinsPrice = binanceApi.getCoinsPrice(new String[]{"BTC"});

            CoinsDetailsBuilder coinsDetailsBuilder = new CoinsDetailsBuilder();
            List<CoinDetails> coinsDetails = coinsDetailsBuilder.getCoinsDetails(coinsAmount, coinsPrice);

            ThresholdWatch thresholdWatch = new ThresholdWatch(context);
            thresholdWatch.check(coinsDetails);

        } catch (NetworkRequestException | FailedRequestStatusException | EmptyResponseBodyException
                | JsonParseException | SignatureGenerationException | CoinsPriceParseException
                | CoinsAmountParseException | CoinsDetailsBuilderException e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(context);
            exceptionHandler.handleException(e);
        }
        catch(Exception e) {
            CriticalExceptionHandler criticalExceptionHandler = new CriticalExceptionHandler(context);
            criticalExceptionHandler.handleException(
                    new CriticalException(e, CriticalException.CriticalExceptionType.UNLABELED_EXCEPTION)
            );
        }
    }
}
