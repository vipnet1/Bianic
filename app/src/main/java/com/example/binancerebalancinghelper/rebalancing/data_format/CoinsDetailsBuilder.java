package com.example.binancerebalancinghelper.rebalancing.data_format;

import com.example.binancerebalancinghelper.consts.CoinsDetailsBuilderConsts;
import com.example.binancerebalancinghelper.rebalancing.api.coins_amount.CoinAmount;
import com.example.binancerebalancinghelper.rebalancing.api.coins_price.CoinPrice;
import com.example.binancerebalancinghelper.rebalancing.data_format.exceptions.CoinsDetailsBuilderException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoinsDetailsBuilder {
    public List<CoinDetails> getCoinsDetails(
            List<CoinAmount> coinsAmount, List<CoinPrice> coinsPrice
    ) throws CoinsDetailsBuilderException {
        try {
            List<CoinDetails> coinsDetails = buildCoinsDetails(coinsAmount, coinsPrice);
            double portfolioUsdValue = getPortfolioUsdValue(coinsDetails);

            for (CoinDetails coinDetails : coinsDetails) {
                coinDetails.percentOfPortfolio = getCoinPercentOfPortfolio(
                        coinDetails.coinPortfolioUsdValue, portfolioUsdValue
                );
            }

            return coinsDetails;
        } catch (Exception e) {
            throw new CoinsDetailsBuilderException(e);
        }
    }

    private double getCoinPercentOfPortfolio(double coinPortfolioUsdValue, double portfolioUsdValue) {
        return coinPortfolioUsdValue * 100 / portfolioUsdValue;
    }

    private double getPortfolioUsdValue(List<CoinDetails> coinsDetails) {
        double totalUsdValue = 0;

        for (CoinDetails coinDetail : coinsDetails) {
            totalUsdValue += coinDetail.coinPortfolioUsdValue;
        }

        return totalUsdValue;
    }


    private List<CoinDetails> buildCoinsDetails(
            List<CoinAmount> coinsAmount, List<CoinPrice> coinsPrice
    ) {
        List<CoinDetails> coinsDetails = new ArrayList<>();

        Map<String, CoinPrice> coinPriceMap = new HashMap<>();
        for (CoinPrice coinPrice : coinsPrice) {
            coinPriceMap.put(coinPrice.symbol, coinPrice);
        }

        for (CoinAmount coinAmount : coinsAmount) {
            CoinPrice coinPrice = coinPriceMap.get(coinAmount.symbol);
            CoinDetails coinDetails = new CoinDetails(coinAmount, coinPrice);

            if (coinDetails.coinPortfolioUsdValue > CoinsDetailsBuilderConsts.MIN_BUILT_COIN_PORTFOLIO_USD) {
                coinsDetails.add(coinDetails);
            }
        }

        return coinsDetails;
    }
}
