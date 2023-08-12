package com.example.binancerebalancinghelper.rebalancing.api;

import android.content.Context;

import com.example.binancerebalancinghelper.consts.BinanceApiConsts;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.CoinAmount;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.CoinsAmountApi;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.CoinPrice;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.CoinsPriceApi;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.example.binancerebalancinghelper.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.example.binancerebalancinghelper.rebalancing.api.common.json.exceptions.JsonParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.NetworkAuthRequestHelper;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.NetworkRequestHelper;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions.SignatureGenerationException;

import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class BinanceApi {
    private final Context context;

    public BinanceApi(Context context) {
        this.context = context;
    }

    public List<CoinAmount> getCoinsAmount() throws NetworkRequestException,
            FailedRequestStatusException, EmptyResponseBodyException, JsonParseException,
            CoinsAmountParseException, SignatureGenerationException {

        NetworkAuthRequestHelper networkAuthRequestHelper = new NetworkAuthRequestHelper(context);
        CoinsAmountApi coinsAmountApi = new CoinsAmountApi();

        Response response = networkAuthRequestHelper.performRequest(BinanceApiConsts.ACCOUNT_ENDPOINT, "");
        if(!response.isSuccessful()) {
            throw new FailedRequestStatusException(response.code(), response.message());
        }

        ResponseBody responseBody = response.body();
        if(responseBody == null) {
            throw new EmptyResponseBodyException();
        }

        List<CoinAmount> result = coinsAmountApi.parseCoinsAmount(responseBody);
        networkAuthRequestHelper.closeResponseBody(responseBody);
        return result;
    }

    public List<CoinPrice> getCoinsPrice(String[] symbols) throws NetworkRequestException,
            FailedRequestStatusException, EmptyResponseBodyException, JsonParseException,
            CoinsPriceParseException {

        NetworkRequestHelper networkRequestHelper = new NetworkRequestHelper();
        CoinsPriceApi coinsPriceApi = new CoinsPriceApi();

        Response response = networkRequestHelper.performRequest(BinanceApiConsts.TICKER_PRICE_ENDPOINT,
                "?symbols=" + coinsPriceApi.getSymbolsForQuery(symbols));
        if(!response.isSuccessful()) {
            throw new FailedRequestStatusException(response.code(), response.message());
        }

        ResponseBody responseBody = response.body();
        if(responseBody == null) {
            throw new EmptyResponseBodyException();
        }

        List<CoinPrice> result = coinsPriceApi.parseCoinsPrice(responseBody);
        networkRequestHelper.closeResponseBody(responseBody);
        return result;
    }
}
