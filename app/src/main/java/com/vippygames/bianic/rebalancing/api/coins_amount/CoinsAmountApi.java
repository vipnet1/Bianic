package com.vippygames.bianic.rebalancing.api.coins_amount;

import com.vippygames.bianic.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.vippygames.bianic.rebalancing.api.common.response_parser.ResponseParser;
import com.vippygames.bianic.rebalancing.api.common.response_parser.exceptions.ResponseParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class CoinsAmountApi {
    /**
     * @param responseBody The response body
     * @return The coins amount I have for each coin. If none doesn't count that record
     * @throws CoinsAmountParseException Exception occurred in this function
     */
    public List<CoinAmount> parseCoinsAmount(ResponseBody responseBody) throws CoinsAmountParseException {
        try {
            ResponseParser responseParser = new ResponseParser();
            JSONObject jsonBody = responseParser.parseResponseJsonObject(responseBody);

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
        } catch (JSONException | ResponseParseException e) {
            throw new CoinsAmountParseException(e);
        }
    }
}
