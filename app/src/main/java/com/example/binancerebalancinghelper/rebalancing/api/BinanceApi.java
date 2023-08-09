package com.example.binancerebalancinghelper.rebalancing.api;

import android.content.Context;

import com.example.binancerebalancinghelper.consts.BinanceApiConsts;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.CoinAmount;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.CoinsAmountApi;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.CoinPrice;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.CoinsPriceApi;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.json.JsonHelper;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.NetworkAuthRequestHelper;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.example.binancerebalancinghelper.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.example.binancerebalancinghelper.rebalancing.api.common.json.exceptions.JsonParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.NetworkRequestHelper;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions.SignatureGenerationException;

import org.json.JSONObject;

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
        JsonHelper jsonHelper = new JsonHelper();
        CoinsAmountApi coinsAmountApi = new CoinsAmountApi();

        Response response = networkAuthRequestHelper.performRequest(BinanceApiConsts.ACCOUNT_ENDPOINT, "");
        if(!response.isSuccessful()) {
            throw new FailedRequestStatusException(response.code(), response.message());
        }

        ResponseBody responseBody = response.body();
        if(responseBody == null) {
            throw new EmptyResponseBodyException();
        }

        JSONObject jsonBody = jsonHelper.parseResponseJson(responseBody);
        networkAuthRequestHelper.closeResponseBody(responseBody);

        return coinsAmountApi.parseCoinsAmount(jsonBody);
    }

    public List<CoinPrice> getCoinsPrice() throws NetworkRequestException,
            FailedRequestStatusException, EmptyResponseBodyException, JsonParseException,
            CoinsPriceParseException {

        NetworkRequestHelper networkRequestHelper = new NetworkRequestHelper();
        JsonHelper jsonHelper = new JsonHelper();
        CoinsPriceApi coinsPriceApi = new CoinsPriceApi();

        Response response = networkRequestHelper.performRequest(BinanceApiConsts.TICKER_PRICE_ENDPOINT, "");
        if(!response.isSuccessful()) {
            throw new FailedRequestStatusException(response.code(), response.message());
        }

        ResponseBody responseBody = response.body();
        if(responseBody == null) {
            throw new EmptyResponseBodyException();
        }

        JSONObject jsonBody = jsonHelper.parseResponseJson(responseBody);
        networkRequestHelper.closeResponseBody(responseBody);

        return coinsPriceApi.parseCoinsPrice(jsonBody);
    }
}
