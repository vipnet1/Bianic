package com.vippygames.bianic.rebalancing.api.common.network_request;

import android.content.Context;

import com.vippygames.bianic.consts.BinanceApiConsts;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.NetworkRequestException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetworkRequestHelper {
    private final Context context;

    public NetworkRequestHelper(Context context) {
        this.context = context;
    }

    public Response performRequest(String endpoint, String queryParams) throws NetworkRequestException {
        return performRequestImpl(
                getRequestUrl(endpoint, queryParams)
        );
    }

    public void closeResponseBody(ResponseBody responseBody) {
        responseBody.close();
    }

    private String getRequestUrl(String endpoint, String queryParams) {
        return BinanceApiConsts.MAIN_ENDPOINT + endpoint + queryParams;
    }

    private Response performRequestImpl(String requestUrl) throws NetworkRequestException {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .build();

            return client.newCall(request).execute();
        } catch (IOException e) {
            throw new NetworkRequestException(context, e);
        }
    }
}
