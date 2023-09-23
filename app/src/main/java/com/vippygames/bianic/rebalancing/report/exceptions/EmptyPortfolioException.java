package com.vippygames.bianic.rebalancing.report.exceptions;

import android.content.Context;

import com.vippygames.bianic.R;
import com.vippygames.bianic.consts.ReportsConsts;
import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class EmptyPortfolioException extends NormalException {
    public EmptyPortfolioException(Context context) {
        super(context);
    }

    @Override
    public String getMessage() {
        return context.getString(R.string.C_excpdet_totalCoinsValueLessThan0) + ReportsConsts.MIN_PORTFOLIO_VALUE_USDT
                + context.getString(R.string.C_excpdet_totalCoinsValueLessThan1);
    }

    @Override
    public String getExceptionName() {
        return "EmptyPortfolioException";
    }
}
