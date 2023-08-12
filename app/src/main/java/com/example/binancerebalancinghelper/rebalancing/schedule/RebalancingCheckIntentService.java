package com.example.binancerebalancinghelper.rebalancing.schedule;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.example.binancerebalancinghelper.NotificationsHelper;
import com.example.binancerebalancinghelper.rebalancing.api.BinanceApi;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.CoinAmount;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.CoinPrice;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.example.binancerebalancinghelper.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.example.binancerebalancinghelper.rebalancing.api.common.json.exceptions.JsonParseException;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.example.binancerebalancinghelper.rebalancing.api.common.network_request.exceptions.SignatureGenerationException;
import com.example.binancerebalancinghelper.shared_preferences.SharedPreferencesHelper;
import com.example.binancerebalancinghelper.consts.NotificationsConsts;

import java.util.List;

public class RebalancingCheckIntentService extends IntentService {

    public RebalancingCheckIntentService() {
        super("RebalancingCheckIntentService");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(Intent intent) {
        BinanceApi binanceApi = new BinanceApi(this);
        try {
//            List<CoinPrice> coinsPrice = binanceApi.getCoinsPrice(new String[]{"BTC"});
            List<CoinAmount> coinsAmount = binanceApi.getCoinsAmount();
            String a = "sdf";
        } catch (NetworkRequestException e) {
            e.printStackTrace();
        } catch (FailedRequestStatusException e) {
            e.printStackTrace();
        } catch (EmptyResponseBodyException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (CoinsAmountParseException e) {
            e.printStackTrace();
        } catch (SignatureGenerationException e) {
            e.printStackTrace();
        }

        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);

        int count = sharedPreferencesHelper.getInt("count", 0);
        count++;
        sharedPreferencesHelper.setInt("count", count);

        NotificationsHelper notificationsHelper = new NotificationsHelper(this);
        notificationsHelper.pushNotification("My Title", "My Text " + count, NotificationsConsts.MESSAGE_NOTIFICATION_ID);
    }
}