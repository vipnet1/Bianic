package com.vippygames.bianic.rebalancing.api.exchange_info;

import android.content.Context;

import com.vippygames.bianic.consts.BinanceApiConsts;
import com.vippygames.bianic.rebalancing.api.common.response_parser.exceptions.ResponseParseException;
import com.vippygames.bianic.rebalancing.api.exchange_info.exceptions.ExchangeInfoParseException;
import com.vippygames.bianic.rebalancing.api.common.response_parser.ResponseParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

public class ExchangeInfoApi {
    private final Context context;

    public ExchangeInfoApi(Context context) {
        this.context = context;
    }

    public Map<String, ExchangeInfo> parseExchangeInfo(ResponseBody jsonBody) throws ExchangeInfoParseException {
        try {
            ResponseParser responseParser = new ResponseParser(context);

            JSONObject jsonObject = responseParser.parseResponseJsonObject(jsonBody);
            JSONArray pairsArray = jsonObject.getJSONArray("symbols");

            Map<String, ExchangeInfo> exchangeInfos = new HashMap<>();
            exchangeInfos.put(BinanceApiConsts.USDT_SYMBOL, new ExchangeInfo(BinanceApiConsts.USDT_SYMBOL));

            for (int i = 0; i < pairsArray.length(); i++) {
                JSONObject pairInfo = pairsArray.getJSONObject(i);
                String coinPair = pairInfo.getString("symbol");

                if (coinPair.endsWith(BinanceApiConsts.USDT_SYMBOL)) {
                    String coinSymbol = coinPair.substring(0, coinPair.length() - BinanceApiConsts.USDT_SYMBOL.length());
                    exchangeInfos.put(coinSymbol, new ExchangeInfo(coinSymbol));
                } else if (coinPair.startsWith(BinanceApiConsts.USDT_SYMBOL)) {
                    String coinSymbol = coinPair.substring(BinanceApiConsts.USDT_SYMBOL.length());
                    exchangeInfos.put(coinSymbol, new ExchangeInfo(coinSymbol));
                }
            }

            return exchangeInfos;
        } catch (JSONException | ResponseParseException e) {
            throw new ExchangeInfoParseException(context, e);
        }
    }
}
