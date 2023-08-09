package com.example.binancerebalancinghelper.rebalancing.api.coins_info;

import com.example.binancerebalancinghelper.rebalancing.api.coins_info.exceptions.CoinsInfoParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoinsInfoApi {
    public List<CoinInfo> parseCoinsInfo(JSONObject bodyJson) throws CoinsInfoParseException {
        try {
            JSONArray balances = bodyJson.getJSONArray("balances");
            List<CoinInfo> coinInfos = new ArrayList<>();

            for (int i = 0; i < balances.length(); i++) {
                JSONObject coin = balances.getJSONObject(i);
                String symbol = coin.getString("asset");
                String free = coin.getString("free");

                if (Double.parseDouble(free) > 0) {
                    coinInfos.add(new CoinInfo(symbol, free));
                }
            }

            return coinInfos;
        } catch (JSONException e) {
            throw new CoinsInfoParseException(e);
        }
    }
}
