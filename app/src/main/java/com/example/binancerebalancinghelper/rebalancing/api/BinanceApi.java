package com.example.binancerebalancinghelper.rebalancing.api;

import android.content.Context;

import com.example.binancerebalancinghelper.consts.BinanceApiConsts;
import com.example.binancerebalancinghelper.rebalancing.api.coins_info.CoinInfo;
import com.example.binancerebalancinghelper.rebalancing.api.coins_info.CoinsInfoApi;
import com.example.binancerebalancinghelper.rebalancing.api.common.json.JsonHelper;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.NetworkRequestHelper;
import com.example.binancerebalancinghelper.rebalancing.api.coins_info.exceptions.CoinsInfoParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.example.binancerebalancinghelper.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.example.binancerebalancinghelper.rebalancing.api.common.json.exceptions.JsonParseException;
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

    protected List<CoinInfo> getCoinsInfo() throws NetworkRequestException,
            FailedRequestStatusException, EmptyResponseBodyException, JsonParseException,
            CoinsInfoParseException, SignatureGenerationException {

        NetworkRequestHelper networkRequestHelper = new NetworkRequestHelper(context);
        JsonHelper jsonHelper = new JsonHelper();
        CoinsInfoApi coinsInfoApi = new CoinsInfoApi();

        Response response = networkRequestHelper.performRequest(BinanceApiConsts.ACCOUNT_ENDPOINT, "");
        if(!response.isSuccessful()) {
            throw new FailedRequestStatusException(response.code(), response.message());
        }

        ResponseBody responseBody = response.body();
        if(responseBody == null) {
            throw new EmptyResponseBodyException();
        }

        JSONObject jsonBody = jsonHelper.parseResponseJson(responseBody);
        networkRequestHelper.closeResponseBody(responseBody);

        return coinsInfoApi.parseCoinsInfo(jsonBody);
    }
}
