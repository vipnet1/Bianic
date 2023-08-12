package com.example.binancerebalancinghelper.rebalancing.api.common.json;

import com.example.binancerebalancinghelper.rebalancing.api.common.json.exceptions.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;

public class JsonHelper {
    public JSONObject parseResponseJsonObject(ResponseBody responseBody) throws JsonParseException {
        try {
            String responseString = responseBody.string();
            return new JSONObject(responseString);
        } catch (JSONException | IOException e) {
            throw new JsonParseException(e);
        }
    }

    public JSONArray parseResponseJsonArray(ResponseBody responseBody) throws JsonParseException {
        try {
            String responseString = responseBody.string();
            return new JSONArray(responseString);
        } catch (JSONException | IOException e) {
            throw new JsonParseException(e);
        }
    }
}
