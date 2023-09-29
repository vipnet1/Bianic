package com.vippygames.bianic.activities.main.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.vippygames.bianic.R;
import com.vippygames.bianic.common.AlertDialogModify;

public class ValidationDialogFragment extends DialogFragment {
    public static final String TAG = "validation_dialog_fragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = requireContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.C_main_dialog_recordsValidationTitle);
        builder.setMessage(R.string.C_main_dialog_recordsValidationMessage);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();

        AlertDialogModify alertDialogModify = new AlertDialogModify(context);
        alertDialogModify.modify(alertDialog);

        return alertDialog;
    }
}
