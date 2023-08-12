package com.example.binancerebalancinghelper.rebalancing;

import android.content.Context;

import com.example.binancerebalancinghelper.NotificationsHelper;
import com.example.binancerebalancinghelper.consts.NotificationsConsts;
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

        } catch (NetworkRequestException | FailedRequestStatusException | EmptyResponseBodyException
                | JsonParseException | SignatureGenerationException | CoinsPriceParseException
                | CoinsAmountParseException | CoinsDetailsBuilderException e) {
            showExceptionNotification(e);
        }
    }

    private void showExceptionNotification(Exception exception) {
        NotificationsHelper notificationsHelper = new NotificationsHelper(context);

        try {
            String exceptionClassName = exception.getClass().toString();
            int lastDotIndex = exceptionClassName.lastIndexOf('.');
            String className = exceptionClassName.substring(lastDotIndex + 1);

            notificationsHelper.pushNotification("Exception occurred", className, NotificationsConsts.MESSAGE_NOTIFICATION_ID);
        } catch (Exception e) {
            notificationsHelper.pushNotification("Exception occurred", "While trying to show exception", NotificationsConsts.MESSAGE_NOTIFICATION_ID);
        }
    }
}
