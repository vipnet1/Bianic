package com.example.binancerebalancinghelper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.example.binancerebalancinghelper.consts.SharedPrefsConsts;
import com.example.binancerebalancinghelper.shared_preferences.EncryptedSharedPreferencesHelper;
import com.example.binancerebalancinghelper.shared_preferences.SharedPreferencesHelper;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyAsyncTask extends AsyncTask {
    private Context context;

    public MyAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            SharedPreferencesHelper sharedPreferencesHelper = new EncryptedSharedPreferencesHelper(context);

            String apiKey = sharedPreferencesHelper.getString(SharedPrefsConsts.BINANCE_API_KEY, "");
            String secretKey = sharedPreferencesHelper.getString(SharedPrefsConsts.BINANCE_SECRET_KEY, "");



// 2. Create an OkHttpClient instance
            OkHttpClient client = new OkHttpClient();

// 3. Create a separator for the parameters
            String separator = "&";

// 4. Create a string with the parameters
            String params = "recvWindow=59000" + separator + "timestamp=" + System.currentTimeMillis();

// 5. Generate a signature using the params and your secret key
//            String signature = generateSignature(params, secretKey); // you'll need to implement this method
            String signature = generateSignature(params, secretKey); // you'll need to implement this method

// 6. Append the signature to the params
            params += separator + "signature=" + signature;

// 7. Create a request URL with the params
            String requestUrl = "https://api.binance.com/api/v3/account?" + params;

// 8. Create a request object with the request URL and the API key header
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .addHeader("X-MBX-APIKEY", apiKey)
                    .build();

// 9. Execute the request and get a response object
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            String message = response.message();

// 10. Check if the response was successful
            if (response.isSuccessful()) {
                // 11. Get the response body object
                ResponseBody responseBody = response.body();
                // 12. Check if the response body is not null
                if (responseBody != null) {
                    // 13. Get the response string from the body
                    String responseString = responseBody.string();
                    // 14. Create a JSON object from the response string
                    JSONObject json = new JSONObject(responseString);
                    // 15. Get the balances array from the JSON object
                    JSONArray balances = json.getJSONArray("balances");
                    // 16. Loop through the balances array and print the coins that you have
                    for (int i = 0; i < balances.length(); i++) {
                        // 17. Get the coin object from the array
                        JSONObject coin = balances.getJSONObject(i);
                        // 18. Get the coin symbol and free amount from the object
                        String symbol = coin.getString("asset");
                        String free = coin.getString("free");
                        // 19. Check if the free amount is greater than zero
                        if (Double.parseDouble(free) > 0) {
                            // 20. Print the coin symbol and free amount
                            System.out.println(symbol + ": " + free);
                        }
                    }
                    // 21. Close the response body
                    responseBody.close();
                }
            } else {
                // 22. Print an error message if the response was not successful
                System.out.println("Request failed: " + response.code());
            }
        }
        catch(Exception e) {
            String a = "sdf";
        }

        return null;
    }

    public static String generateSignature(String params, String secretKey) throws Exception {
        Mac sha256_hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        sha256_hmac.init(secret_key);
        byte[] signatureBytes = sha256_hmac.doFinal(params.getBytes("UTF-8"));
        return Hex.encodeHexString(signatureBytes);
    }
}
