package com.vippygames.bianic.rebalancing.report.manual;

import android.widget.Toast;

import com.vippygames.bianic.R;
import com.vippygames.bianic.ReportsActivity;
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
    private final ReportsActivity reportsActivity;

    public ManualReportGeneration(ReportsActivity reportsActivity) {
        this.reportsActivity = reportsActivity;
    }

    public boolean generateReport() {
        try {
            BinanceManager binanceManager = new BinanceManager(reportsActivity);
            List<CoinDetails> coinsDetails = binanceManager.generateCoinsDetails();

            ReportGenerator reportGenerator = new ReportGenerator(reportsActivity);
            reportGenerator.generateReport(coinsDetails);

            return true;

        } catch (NetworkRequestException | FailedRequestStatusException |
                 EmptyResponseBodyException | SignatureGenerationException |
                 CoinsPriceParseException | CoinsAmountParseException |
                 CoinsDetailsBuilderException | KeyNotFoundException | EmptyPortfolioException e) {
            showToast(reportsActivity.getString(R.string.C_manrepgen_exceptionOccurred));

            ExceptionHandler exceptionHandler = new ExceptionHandler(reportsActivity);
            exceptionHandler.handleException(e);
        } catch (Exception e) {
            showToast(reportsActivity.getString(R.string.C_manrepgen_criticalExceptionOccurred));

            CriticalExceptionHandler criticalExceptionHandler = new CriticalExceptionHandler(reportsActivity);
            criticalExceptionHandler.handleException(
                    new CriticalException(e, CriticalException.CriticalExceptionType.UNLABELED_EXCEPTION)
            );
        }

        return false;
    }

    public void showToast(String message) {
        reportsActivity.runOnUiThread(() -> Toast.makeText(reportsActivity, message, Toast.LENGTH_SHORT).show());
    }
}
