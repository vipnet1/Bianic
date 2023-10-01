package com.vippygames.bianic.activities.reports.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.vippygames.bianic.R;
import com.vippygames.bianic.common.AlertDialogModify;

public class ManualReportDialogFragment extends DialogFragment {
    public static final String TAG = "manual_report_dialog_fragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = requireContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.C_reports_dialog_reportGenerationTitle);
        builder.setMessage(R.string.C_reports_dialog_reportGenerationMessage);

        AlertDialog alertDialog = builder.create();

        AlertDialogModify alertDialogModify = new AlertDialogModify(context);
        alertDialogModify.modify(alertDialog);

        setCancelable(false);

        return alertDialog;
    }
}
