package com.example.binancerebalancinghelper.rebalancing.api.coins_price;

import com.example.binancerebalancinghelper.consts.BinanceApiConsts;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.response_parser.ResponseParser;
import com.example.binancerebalancinghelper.rebalancing.api.common.response_parser.exceptions.ResponseParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class CoinsPriceApi {
    public List<CoinPrice> parseCoinsPrice(ResponseBody jsonBody) throws CoinsPriceParseException {
        try {
            ResponseParser responseParser = new ResponseParser();
            JSONArray jsonArray = responseParser.parseResponseJsonArray(jsonBody);

            List<CoinPrice> coinPrices = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String symbol = jsonObject.getString("symbol");
                double price = Double.parseDouble(jsonObject.getString("price"));

                String symbolWithoutUsdt = symbol.substring(
                        0, symbol.length() - BinanceApiConsts.USDT_SYMBOL.length()
                );
                coinPrices.add(new CoinPrice(symbolWithoutUsdt, price));
            }

            return coinPrices;
        } catch (JSONException | ResponseParseException e) {
            throw new CoinsPriceParseException(e);
        }
    }

    public String getSymbolsForQuery(String[] symbols) {
        StringBuilder resultBuilder = new StringBuilder("[");

        for (String symbol : symbols) {
            resultBuilder.append('"').append(symbol).append(BinanceApiConsts.USDT_SYMBOL)
                    .append('"').append(",");
        }

        String result = resultBuilder.toString();
        int lastCharIndex = result.length() - 1;

        if (result.charAt(lastCharIndex) == ',') {
            result = result.substring(0, lastCharIndex);
        }

        return result + "]";
    }
}
