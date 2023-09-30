package com.vippygames.bianic.rebalancing.report.manual;

import android.content.Context;

import com.vippygames.bianic.R;
import com.vippygames.bianic.activities.observe.ObserveInfo;
import com.vippygames.bianic.exception_handle.CriticalExceptionHandler;
import com.vippygames.bianic.exception_handle.ExceptionHandler;
import com.vippygames.bianic.exception_handle.exceptions.CriticalException;
import com.vippygames.bianic.rebalancing.BinanceManager;
import com.vippygames.bianic.rebalancing.api.coins_amount.exceptions.CoinsAmountParseException;
import com.vippygames.bianic.rebalancing.api.coins_price.exceptions.CoinsPriceParseException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.EmptyResponseBodyException;
import com.vippygames.bianic.rebalancing.api.common.exceptions.FailedRequestStatusException;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.NetworkRequestException;
import com.vippygames.bianic.rebalancing.api.common.network_request.exceptions.SignatureGenerationException;
import com.vippygames.bianic.rebalancing.data_format.CoinDetails;
import com.vippygames.bianic.rebalancing.data_format.exceptions.CoinsDetailsBuilderException;
import com.vippygames.bianic.rebalancing.report.ReportGenerator;
import com.vippygames.bianic.rebalancing.report.exceptions.EmptyPortfolioException;
import com.vippygames.bianic.shared_preferences.exceptions.KeyNotFoundException;

import java.util.List;

public class ManualReportGeneration {
    private ObserveInfo observeInfo;
    private final Context context;

    public ManualReportGeneration(Context context) {
        this.context = context;
    }

    public ObserveInfo generateReport() {
        try {
            observeInfo = new ObserveInfo(ObserveInfo.STATUS.RUNNING, "");

            BinanceManager binanceManager = new BinanceManager(context);
            List<CoinDetails> coinsDetails = binanceManager.generateCoinsDetails();

            ReportGenerator reportGenerator = new ReportGenerator(context);
            reportGenerator.generateReport(coinsDetails);

            observeInfo.setStatus(ObserveInfo.STATUS.FINISHED);
            return observeInfo;

        } catch (NetworkRequestException | FailedRequestStatusException |
                 EmptyResponseBodyException | SignatureGenerationException |
                 CoinsPriceParseException | CoinsAmountParseException |
                 CoinsDetailsBuilderException | KeyNotFoundException | EmptyPortfolioException e) {
            observeInfo.setMessage(context.getString(R.string.C_manrepgen_exceptionOccurred));

            ExceptionHandler exceptionHandler = new ExceptionHandler(context);
            exceptionHandler.handleException(e);
        } catch (Exception e) {
            observeInfo.setMessage(context.getString(R.string.C_manrepgen_criticalExceptionOccurred));

            CriticalExceptionHandler criticalExceptionHandler = new CriticalExceptionHandler(context);
            criticalExceptionHandler.handleException(
                    new CriticalException(e, CriticalException.CriticalExceptionType.UNLABELED_EXCEPTION)
            );
        }

        observeInfo.setStatus(ObserveInfo.STATUS.FAILED);
        return observeInfo;
    }
}
