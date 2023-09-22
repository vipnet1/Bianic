package com.vippygames.bianic.rebalancing.report.exceptions;

import com.vippygames.bianic.consts.ReportsConsts;
import com.vippygames.bianic.exception_handle.exceptions.NormalException;

public class EmptyPortfolioException extends NormalException {
    @Override
    public String getMessage() {
        return "Total value of selected coins is less than " + ReportsConsts.MIN_PORTFOLIO_VALUE_USDT
                + "$. Add some value to them to generate reports.";
    }

    @Override
    public String getExceptionName() {
        return "EmptyPortfolioException";
    }
}
