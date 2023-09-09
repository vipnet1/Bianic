package com.vippygames.bianic.rebalancing;

import android.content.Context;

import com.vippygames.bianic.consts.BinanceApiConsts;
import com.vippygames.bianic.db.ThresholdAllocation.ThresholdAllocationDb;
import com.vippygames.bianic.db.ThresholdAllocation.ThresholdAllocationRecord;
import com.vippygames.bianic.rebalancing.api.BinanceApi;
import com.vippygames.bianic.rebalancing.api.coins_amount.CoinAmount;
import com.vippygames.bianic.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.vippygames.bianic.rebalancing.api.coins_price.CoinPrice;
import com.vippygames.bianic.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.SignatureGenerationException;
import com.vippygames.bianic.rebalancing.data_format.CoinDetails;
import com.vippygames.bianic.rebalancing.data_format.CoinsDetailsBuilder;
import com.vippygames.bianic.rebalancing.data_format.exceptions.CoinsDetailsBuilderException;
import com.vippygames.bianic.shared_preferences.exceptions.KeyNotFoundException;

import java.util.List;

public class BinanceManager {
    private final Context context;

    public BinanceManager(Context context) {
        this.context = context;
    }

    public List<CoinDetails> generateCoinsDetails() throws EmptyResponseBodyException,
            FailedRequestStatusException, NetworkRequestException, CoinsAmountParseException,
            SignatureGenerationException, KeyNotFoundException, CoinsPriceParseException, CoinsDetailsBuilderException {
        ThresholdAllocationDb db = new ThresholdAllocationDb(context);
        List<ThresholdAllocationRecord> records = db.loadRecords(db.getRecords());

        List<CoinAmount> coinsAmount = getCoinsAmount();
        List<CoinPrice> coinsPrice = getCoinsPrice(records);

        CoinsDetailsBuilder coinsDetailsBuilder = new CoinsDetailsBuilder();
        return coinsDetailsBuilder.getCoinsDetails(records, coinsAmount, coinsPrice);
    }

    public List<CoinAmount> getCoinsAmount() throws EmptyResponseBodyException,
            FailedRequestStatusException, NetworkRequestException, SignatureGenerationException,
            CoinsAmountParseException, KeyNotFoundException {
        BinanceApi binanceApi = new BinanceApi(context);
        return binanceApi.getCoinsAmount();
    }

    public List<CoinPrice> getCoinsPrice(List<ThresholdAllocationRecord> records) throws NetworkRequestException,
            EmptyResponseBodyException, CoinsPriceParseException, FailedRequestStatusException {
        BinanceApi binanceApi = new BinanceApi(context);
        CoinPrice usdtCoinPrice = new CoinPrice(BinanceApiConsts.USDT_SYMBOL, 1);

        boolean areRecordsContainUsdt = areRecordsContainUsdt(records);
        String[] coinsSymbols = getCoinsSymbols(records, areRecordsContainUsdt);
        List<CoinPrice> coinsPrice = binanceApi.getCoinsPrice(coinsSymbols);

        if (areRecordsContainUsdt) {
            coinsPrice.add(usdtCoinPrice);
        }

        return coinsPrice;
    }

    private boolean areRecordsContainUsdt(List<ThresholdAllocationRecord> records) {
        for (ThresholdAllocationRecord record : records) {
            if (record.getSymbol().equals(BinanceApiConsts.USDT_SYMBOL)) {
                return true;
            }
        }

        return false;
    }

    private String[] getCoinsSymbols(List<ThresholdAllocationRecord> records, boolean areRecordsContainUsdt) {
        String[] symbols;
        if (areRecordsContainUsdt) {
            symbols = new String[records.size() - 1];
        } else {
            symbols = new String[records.size()];
        }

        int indexToFill = 0;
        for (ThresholdAllocationRecord record : records) {
            String symbol = record.getSymbol();

            if (!symbol.equals(BinanceApiConsts.USDT_SYMBOL)) {
                symbols[indexToFill++] = symbol;
            }
        }

        return symbols;
    }
}
