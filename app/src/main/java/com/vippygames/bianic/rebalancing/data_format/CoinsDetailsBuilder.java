package com.vippygames.bianic.rebalancing.data_format;

import android.content.Context;

import com.vippygames.bianic.db.threshold_allocation.ThresholdAllocationRecord;
import com.vippygames.bianic.rebalancing.api.coins_amount.CoinAmount;
import com.vippygames.bianic.rebalancing.api.coins_price.CoinPrice;
import com.vippygames.bianic.rebalancing.data_format.exceptions.CoinsDetailsBuilderException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoinsDetailsBuilder {
    private final Context context;

    public CoinsDetailsBuilder(Context context) {
        this.context = context;
    }

    public List<CoinDetails> getCoinsDetails(
            List<ThresholdAllocationRecord> records, List<CoinAmount> coinsAmount, List<CoinPrice> coinsPrice
    ) throws CoinsDetailsBuilderException {
        try {
            return buildCoinsDetails(records, coinsAmount, coinsPrice);
        } catch (CoinsDetailsBuilderException e) {
            throw e;
        } catch (Exception e) {
            throw new CoinsDetailsBuilderException(context, e);
        }
    }

    private List<CoinDetails> buildCoinsDetails(
            List<ThresholdAllocationRecord> records, List<CoinAmount> coinsAmount, List<CoinPrice> coinsPrice
    ) throws CoinsDetailsBuilderException {
        List<CoinDetails> coinsDetails = new ArrayList<>();

        Map<String, CoinAmount> coinAmountMap = new HashMap<>();
        for (CoinAmount coinAmount : coinsAmount) {
            coinAmountMap.put(coinAmount.getSymbol(), coinAmount);
        }

        Map<String, CoinPrice> coinPriceMap = new HashMap<>();
        for (CoinPrice coinPrice : coinsPrice) {
            coinPriceMap.put(coinPrice.getSymbol(), coinPrice);
        }

        for (ThresholdAllocationRecord record : records) {
            String symbol = record.getSymbol();

            CoinPrice coinPrice = coinPriceMap.get(symbol);
            CoinAmount coinAmount = null;

            if (coinAmountMap.containsKey(symbol)) {
                coinAmount = coinAmountMap.get(symbol);
            } else {
                coinAmount = new CoinAmount(symbol, 0);
            }

            CoinDetails coinDetails = new CoinDetails(coinAmount, coinPrice, record);
            coinsDetails.add(coinDetails);
        }

        return coinsDetails;
    }
}
