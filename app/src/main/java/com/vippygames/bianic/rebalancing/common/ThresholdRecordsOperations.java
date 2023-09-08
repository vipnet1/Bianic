package com.vippygames.bianic.rebalancing.common;

import com.vippygames.bianic.consts.BinanceApiConsts;
import com.vippygames.bianic.db.ThresholdAllocation.ThresholdAllocationRecord;

import java.util.List;

public class ThresholdRecordsOperations {
    public boolean areRecordsContainUsdt(List<ThresholdAllocationRecord> records) {
        for (ThresholdAllocationRecord record : records) {
            if (record.getSymbol().equals(BinanceApiConsts.USDT_SYMBOL)) {
                return true;
            }
        }

        return false;
    }

    public String[] getCoinsSymbols(List<ThresholdAllocationRecord> records, boolean areRecordsContainUsdt) {
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
