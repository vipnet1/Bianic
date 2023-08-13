package com.example.binancerebalancinghelper.rebalancing.api.common.response_parser;

import com.example.binancerebalancinghelper.rebalancing.api.common.response_parser.exceptions.ResponseParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;

public class ResponseParser {
    public JSONObject parseResponseJsonObject(ResponseBody responseBody) throws ResponseParseException {
        try {
            String responseString = responseBody.string();
            return new JSONObject(responseString);
        } catch (JSONException | IOException e) {
            throw new ResponseParseException(e);
        }
    }

    public JSONArray parseResponseJsonArray(ResponseBody responseBody) throws ResponseParseException {
        try {
            String responseString = responseBody.string();
            return new JSONArray(responseString);
        } catch (JSONException | IOException e) {
            throw new ResponseParseException(e);
        }
    }

    public String parseResponseString(ResponseBody responseBody) throws ResponseParseException {
        try {
            return responseBody.string();
        } catch (IOException e) {
            throw new ResponseParseException(e);
        }
    }
}
