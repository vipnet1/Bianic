package com.example.binancerebalancinghelper.rebalancing.api;

import android.content.Context;

import com.example.binancerebalancinghelper.consts.BinanceApiConsts;
import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;
import com.example.binancerebalancinghelper.rebalancing.api.exceptions.CoinsInfoParseException;
import com.example.binancerebalancinghelper.rebalancing.api.exceptions.EmptyResponseBodyException;
import com.example.binancerebalancinghelper.rebalancing.api.exceptions.FailedRequestStatusException;
import com.example.binancerebalancinghelper.rebalancing.api.exceptions.JsonParseException;
import com.example.binancerebalancinghelper.rebalancing.api.exceptions.NetworkRequestException;
import com.example.binancerebalancinghelper.rebalancing.api.exceptions.SignatureGenerationException;
import com.example.binancerebalancinghelper.shared_preferences.EncryptedSharedPreferencesHelper;
import com.example.binancerebalancinghelper.shared_preferences.SharedPreferencesHelper;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BinanceApi {
    private final String apiKey;
    private final String secretKey;

    public BinanceApi(Context context) {
        SharedPreferencesHelper sharedPreferencesHelper = new EncryptedSharedPreferencesHelper(context);

        apiKey = sharedPreferencesHelper.getString(SharedPrefsConsts.BINANCE_API_KEY, "");
        secretKey = sharedPreferencesHelper.getString(SharedPrefsConsts.BINANCE_SECRET_KEY, "");
    }

    private String getSignatureGenerationParams() {
        return "recvWindow=" + BinanceApiConsts.RECEIVE_WINDOW
                + "&timestamp=" + System.currentTimeMillis();
    }

    private String getQueryParams(String signatureGenerationParams, String signature) {
        return signatureGenerationParams + "&signature=" + signature;
    }

    private String getRequestUrl(String signatureGenerationParams, String signature) {
        return BinanceApiConsts.MAIN_ENDPOINT + "/" + BinanceApiConsts.ACCOUNT_ENDPOINT
                + getQueryParams(signatureGenerationParams, signature);
    }

    private Response performRequest(String requestUrl) throws NetworkRequestException {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .addHeader(BinanceApiConsts.API_KEY_HEADER_NAME, apiKey)
                    .build();

            return  client.newCall(request).execute();
        } catch (IOException e) {
            throw new NetworkRequestException(e);
        }
    }

    private JSONObject parseResponseJson(ResponseBody responseBody) throws JsonParseException {
        try {
            String responseString = responseBody.string();
            return new JSONObject(responseString);
        } catch (JSONException | IOException e) {
            throw new JsonParseException(e);
        }
    }

    private void closeResponseBody(ResponseBody responseBody) {
        responseBody.close();
    }

    private List<CoinInfo> parseCoinsInfo(JSONObject bodyJson) throws CoinsInfoParseException {
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


    protected Object getCoinsInfo() throws NetworkRequestException,
            FailedRequestStatusException, EmptyResponseBodyException, JsonParseException,
            CoinsInfoParseException, SignatureGenerationException {
        String signatureGenerationParams = getSignatureGenerationParams();
        String signature = generateSignature(signatureGenerationParams, secretKey);

        Response response = performRequest(getRequestUrl(signatureGenerationParams, signature));
        if(!response.isSuccessful()) {
            throw new FailedRequestStatusException(response.code(), response.message());
        }

        ResponseBody responseBody = response.body();
        if(responseBody == null) {
            throw new EmptyResponseBodyException();
        }

        JSONObject jsonBody = parseResponseJson(responseBody);
        closeResponseBody(responseBody);

        return parseCoinsInfo(jsonBody);
    }

    public static String generateSignature(String params, String secretKey) throws SignatureGenerationException {
        try {
            Mac sha256_hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_hmac.init(secret_key);
            byte[] signatureBytes = sha256_hmac.doFinal(params.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new SignatureGenerationException(e);
        }
    }
}
