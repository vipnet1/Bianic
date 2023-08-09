package com.example.binancerebalancinghelper.rebalancing.api.coins_amount;

import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoinsAmountApi {
    public List<CoinAmount> parseCoinsAmount(JSONObject bodyJson) throws CoinsAmountParseException {
        try {
            JSONArray balances = bodyJson.getJSONArray("balances");
            List<CoinAmount> coinAmounts = new ArrayList<>();

            for (int i = 0; i < balances.length(); i++) {
                JSONObject coin = balances.getJSONObject(i);
                String symbol = coin.getString("asset");
                double free = Double.parseDouble(coin.getString("free"));

                if (free > 0) {
                    coinAmounts.add(new CoinAmount(symbol, free));
                }
            }

            return coinAmounts;
        } catch (JSONException e) {
            throw new CoinsAmountParseException(e);
        }
    }
}
