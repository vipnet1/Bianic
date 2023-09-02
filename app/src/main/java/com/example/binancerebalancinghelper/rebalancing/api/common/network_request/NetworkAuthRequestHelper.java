package com.example.binancerebalancinghelper.rebalancing.api.common.network_request;

import android.content.Context;

import com.example.binancerebalancinghelper.configuration.ConfigurationManager;
import com.example.binancerebalancinghelper.consts.BinanceApiConsts;
import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions.SignatureGenerationException;
import com.example.binancerebalancinghelper.shared_preferences.EncryptedSharedPreferencesHelper;
import com.example.binancerebalancinghelper.shared_preferences.SharedPreferencesHelper;
import com.example.binancerebalancinghelper.shared_preferences.exceptions.KeyNotFoundException;

import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetworkAuthRequestHelper {
    private final String apiKey;
    private final String secretKey;

    public NetworkAuthRequestHelper(Context context) throws KeyNotFoundException {
        ConfigurationManager configurationManager = new ConfigurationManager(context);

        apiKey = configurationManager.getApiKeyFailOnNotFound();
        secretKey = configurationManager.getSecretKeyFailOnNotFound();
    }

    // additionalQueryParams should finish with '&'
    public Response performRequest(String endpoint, String additionalQueryParams) throws SignatureGenerationException, NetworkRequestException {
        String signatureGenerationParams = getSignatureGenerationParams();
        String signature = generateSignature(signatureGenerationParams, secretKey);

        return performRequestImpl(
                getRequestUrl(signatureGenerationParams, signature, endpoint, additionalQueryParams)
        );
    }

    public void closeResponseBody(ResponseBody responseBody) {
        responseBody.close();
    }

    private String getSignatureGenerationParams() {
        return "recvWindow=" + BinanceApiConsts.RECEIVE_WINDOW
                + "&timestamp=" + getTimestampForRequest();
    }

    // if phone's time is ahead of binance than request will fail, so take some time back to avoid it.
    private long getTimestampForRequest() {
        return System.currentTimeMillis() - BinanceApiConsts.SUBSTRUCTED_TIMESTAMP_FROM_REQUEST;
    }


    private String generateSignature(String params, String secretKey) throws SignatureGenerationException {
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

    private String getQueryParams(String signatureGenerationParams, String signature) {
        return signatureGenerationParams + "&signature=" + signature;
    }

    private String getRequestUrl(String signatureGenerationParams, String signature,
                                 String endpoint, String additionalQueryParams) {
        return BinanceApiConsts.MAIN_ENDPOINT + endpoint + "?" + additionalQueryParams +
                getQueryParams(signatureGenerationParams, signature);
    }

    private Response performRequestImpl(String requestUrl) throws NetworkRequestException {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .addHeader(BinanceApiConsts.API_KEY_HEADER_NAME, apiKey)
                    .build();

            return client.newCall(request).execute();
        } catch (IOException e) {
            throw new NetworkRequestException(e);
        }
    }
}
