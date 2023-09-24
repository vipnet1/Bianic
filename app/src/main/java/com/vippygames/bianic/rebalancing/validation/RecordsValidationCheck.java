package com.vippygames.bianic.rebalancing.validation;

import android.content.Context;

import com.vippygames.bianic.consts.SharedPrefsConsts;
import com.vippygames.bianic.rebalancing.validation.exceptions.UnvalidatedRecordsException;
import com.vippygames.bianic.shared_preferences.SharedPreferencesHelper;

public class RecordsValidationCheck {
    private final Context context;

    public RecordsValidationCheck(Context context) {
        this.context = context;
    }

    public void validateThresholdAllocationRecordsValidated() throws UnvalidatedRecordsException {
        SharedPreferencesHelper sp = new SharedPreferencesHelper(context);
        int areRecordsValidated = sp.getInt(SharedPrefsConsts.ARE_THRESHOLD_ALLOCATION_RECORDS_VALIDATED, 0);

        if (areRecordsValidated != 1) {
            throw new UnvalidatedRecordsException(context);
        }
    }
}
