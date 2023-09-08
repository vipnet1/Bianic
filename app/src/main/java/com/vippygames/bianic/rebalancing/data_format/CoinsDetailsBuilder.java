package com.vippygames.bianic.rebalancing.data_format;

import com.vippygames.bianic.rebalancing.api.coins_amount.CoinAmount;
import com.vippygames.bianic.rebalancing.api.coins_price.CoinPrice;
import com.vippygames.bianic.rebalancing.data_format.exceptions.CoinsDetailsBuilderException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoinsDetailsBuilder {
    public List<CoinDetails> getCoinsDetails(
            List<CoinAmount> coinsAmount, List<CoinPrice> coinsPrice
    ) throws CoinsDetailsBuilderException {
        try {
            return buildCoinsDetails(coinsAmount, coinsPrice);
        } catch (Exception e) {
            throw new CoinsDetailsBuilderException(e);
        }
    }

    private List<CoinDetails> buildCoinsDetails(
            List<CoinAmount> coinsAmount, List<CoinPrice> coinsPrice
    ) {
        List<CoinDetails> coinsDetails = new ArrayList<>();

        Map<String, CoinAmount> coinAmountMap = new HashMap<>();
        for (CoinAmount coinAmount : coinsAmount) {
            coinAmountMap.put(coinAmount.getSymbol(), coinAmount);
        }

        for (CoinPrice coinPrice : coinsPrice) {
            CoinAmount coinAmount = coinAmountMap.get(coinPrice.getSymbol());
            CoinDetails coinDetails = new CoinDetails(coinAmount, coinPrice);

            coinsDetails.add(coinDetails);
        }

        return coinsDetails;
    }
}
