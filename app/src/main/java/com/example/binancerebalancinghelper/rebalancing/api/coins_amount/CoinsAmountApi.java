package com.example.binancerebalancinghelper.rebalancing.api.coins_amount;

import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.json.JsonHelper;
import com.example.binancerebalancinghelper.rebalancing.api.common.json.exceptions.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class CoinsAmountApi {
    /**
     *
     * @param responseBody The response body
     * @return The coins amount I have for each coin. If none doesn't count that record
     * @throws CoinsAmountParseException Exception occurred in this function
     */
    public List<CoinAmount> parseCoinsAmount(ResponseBody responseBody) throws CoinsAmountParseException {
        try {
            JsonHelper jsonHelper = new JsonHelper();
            JSONObject jsonBody = jsonHelper.parseResponseJsonObject(responseBody);

            JSONArray balances = jsonBody.getJSONArray("balances");
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
        } catch (JSONException | JsonParseException e) {
            throw new CoinsAmountParseException(e);
        }
    }
}
