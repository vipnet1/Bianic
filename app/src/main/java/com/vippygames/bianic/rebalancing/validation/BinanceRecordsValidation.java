package com.vippygames.bianic.rebalancing.validation;

import android.widget.Toast;

import com.vippygames.bianic.MainActivity;
import com.vippygames.bianic.consts.BinanceApiConsts;
import com.vippygames.bianic.consts.BinanceRecordsValidationConsts;
import com.vippygames.bianic.db.ThresholdAllocation.ThresholdAllocationDb;
import com.vippygames.bianic.db.ThresholdAllocation.ThresholdAllocationRecord;
import com.vippygames.bianic.exception_handle.CriticalExceptionHandler;
import com.vippygames.bianic.exception_handle.ExceptionHandler;
import com.vippygames.bianic.exception_handle.exceptions.CriticalException;
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
import com.vippygames.bianic.rebalancing.validation.exceptions.FailedValidateRecordsException;
import com.vippygames.bianic.shared_preferences.exceptions.KeyNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BinanceRecordsValidation {
    private final MainActivity mainActivity;

    public BinanceRecordsValidation(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean validateRecordsBinance() {
        try {
            BinanceApi binanceApi = new BinanceApi(mainActivity);

            ThresholdAllocationDb db = new ThresholdAllocationDb(mainActivity);
            List<ThresholdAllocationRecord> records = db.loadRecords(db.getRecords());

            List<CoinAmount> coinsAmount = binanceApi.getCoinsAmount();
            validateCoinsFoundInPortfolio(records, coinsAmount);

            CoinPrice usdtCoinPrice = new CoinPrice(BinanceApiConsts.USDT_SYMBOL, 1);

            boolean areRecordsContainUsdt = areRecordsContainUsdt(records);
            String[] coinsSymbols = getCoinsSymbols(records, areRecordsContainUsdt);
            List<CoinPrice> coinsPrice = binanceApi.getCoinsPrice(coinsSymbols);

            if (areRecordsContainUsdt) {
                coinsPrice.add(usdtCoinPrice);
            }

            CoinsDetailsBuilder coinsDetailsBuilder = new CoinsDetailsBuilder();
            List<CoinDetails> coinsDetails = coinsDetailsBuilder.getCoinsDetails(coinsAmount, coinsPrice);

            validateCoinsAboveMinUsd(coinsDetails);

            return true;

        } catch (NetworkRequestException | FailedRequestStatusException | EmptyResponseBodyException
                | SignatureGenerationException | CoinsPriceParseException
                | CoinsAmountParseException | CoinsDetailsBuilderException
                | KeyNotFoundException | FailedValidateRecordsException e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(mainActivity);
            exceptionHandler.handleException(e);
        } catch (Exception e) {
            CriticalExceptionHandler criticalExceptionHandler = new CriticalExceptionHandler(mainActivity);
            criticalExceptionHandler.handleException(
                    new CriticalException(e, CriticalException.CriticalExceptionType.UNLABELED_EXCEPTION)
            );
        }

        return false;
    }

    private void validateCoinsFoundInPortfolio(List<ThresholdAllocationRecord> records, List<CoinAmount> coinsAmount) throws FailedValidateRecordsException {
        Set<String> foundSymbols = new HashSet<>();
        for (CoinAmount coinAmount : coinsAmount) {
            foundSymbols.add(coinAmount.getSymbol());
        }

        for (ThresholdAllocationRecord record : records) {
            String coinSymbol = record.getSymbol();
            if (!foundSymbols.contains(record.getSymbol())) {
                String message = "You don't have the coin '" + coinSymbol + "' in your portfolio";
                showToast(message);
                throw new FailedValidateRecordsException(message);
            }
        }
    }

    private void validateCoinsAboveMinUsd(List<CoinDetails> coinsDetails) throws FailedValidateRecordsException {
        for (CoinDetails coinDetails : coinsDetails) {
            if (coinDetails.getCoinPortfolioUsdValue() < BinanceRecordsValidationConsts.MIN_COIN_VALUE_USDT) {
                String message = "Coin value in USD in your portfolio is too small - min is "
                        + BinanceRecordsValidationConsts.MIN_COIN_VALUE_USDT + " but your is "
                        + coinDetails.getCoinPortfolioUsdValue();
                showToast(message);
                throw new FailedValidateRecordsException(message);
            }

        }
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

    public void showToast(String message) {
        mainActivity.runOnUiThread(() -> Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show());
    }
}
