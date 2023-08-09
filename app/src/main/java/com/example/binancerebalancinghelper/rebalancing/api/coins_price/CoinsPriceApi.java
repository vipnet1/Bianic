package com.example.binancerebalancinghelper.rebalancing.api.coins_price;

import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.CoinAmount;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoinsPriceApi {
    public List<CoinPrice> parseCoinsPrice(JSONObject bodyJson) throws CoinsPriceParseException {
        try {
            JSONArray jsonArray = new JSONArray(bodyJson);
            List<CoinPrice> coinPrices = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String symbol = jsonObject.getString("symbol");
                double price = Double.parseDouble(jsonObject.getString("price"));

                coinPrices.add(new CoinPrice(symbol, price));
            }

            return coinPrices;
        } catch (JSONException e) {
            throw new CoinsPriceParseException(e);
        }
    }
}
