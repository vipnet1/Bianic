package com.vippygames.bianic.rebalancing.api;

import android.content.Context;

import com.vippygames.bianic.consts.BinanceApiConsts;
import com.vippygames.bianic.rebalancing.api.coins_amount.CoinAmount;
import com.vippygames.bianic.rebalancing.api.coins_amount.CoinsAmountApi;
import com.vippygames.bianic.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.vippygames.bianic.rebalancing.api.coins_price.CoinPrice;
import com.vippygames.bianic.rebalancing.api.coins_price.CoinsPriceApi;
import com.vippygames.bianic.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.vippygames.bianic.rebalancing.api.common.network_request.NetworkAuthRequestHelper;
import com.vippygames.bianic.rebalancing.api.common.network_request.NetworkRequestHelper;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.SignatureGenerationException;
import com.vippygames.bianic.rebalancing.api.common.response_parser.ResponseParser;
import com.vippygames.bianic.rebalancing.api.common.response_parser.exceptions.ResponseParseException;
import com.vippygames.bianic.shared_preferences.exceptions.KeyNotFoundException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class BinanceApi {
    private final Context context;

    public BinanceApi(Context context) {
        this.context = context;
    }

    public List<CoinAmount> getCoinsAmount() throws NetworkRequestException,
            FailedRequestStatusException, EmptyResponseBodyException,
            CoinsAmountParseException, SignatureGenerationException, KeyNotFoundException {

        NetworkAuthRequestHelper networkAuthRequestHelper = new NetworkAuthRequestHelper(context);
        CoinsAmountApi coinsAmountApi = new CoinsAmountApi();

        Response response = networkAuthRequestHelper.performRequest(BinanceApiConsts.ACCOUNT_ENDPOINT, "");

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new EmptyResponseBodyException();
        }

        if (!response.isSuccessful()) {
            throw new FailedRequestStatusException(response.code(), tryGetFailedRequestMessage(responseBody));
        }

        List<CoinAmount> result = coinsAmountApi.parseCoinsAmount(responseBody);
        networkAuthRequestHelper.closeResponseBody(responseBody);
        return result;
    }

    public List<CoinPrice> getCoinsPrice(String[] symbols) throws NetworkRequestException,
            FailedRequestStatusException, EmptyResponseBodyException,
            CoinsPriceParseException {

        if(symbols.length == 0) {
            return new ArrayList<>();
        }

        NetworkRequestHelper networkRequestHelper = new NetworkRequestHelper();
        CoinsPriceApi coinsPriceApi = new CoinsPriceApi();

        Response response = networkRequestHelper.performRequest(BinanceApiConsts.TICKER_PRICE_ENDPOINT,
                "?symbols=" + coinsPriceApi.getSymbolsForQuery(symbols));

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new EmptyResponseBodyException();
        }

        if (!response.isSuccessful()) {
            throw new FailedRequestStatusException(response.code(), tryGetFailedRequestMessage(responseBody));
        }

        List<CoinPrice> result = coinsPriceApi.parseCoinsPrice(responseBody);
        networkRequestHelper.closeResponseBody(responseBody);
        return result;
    }

    private String tryGetFailedRequestMessage(ResponseBody responseBody) {
        try {
            ResponseParser responseParser = new ResponseParser();
            return responseParser.parseResponseString(responseBody);
        } catch (ResponseParseException e) {
            return "Failed to get reason";
        }
    }
}
