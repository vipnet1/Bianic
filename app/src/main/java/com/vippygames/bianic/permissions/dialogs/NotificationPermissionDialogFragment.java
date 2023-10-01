package com.vippygames.bianic.permissions.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.vippygames.bianic.R;
import com.vippygames.bianic.common.AlertDialogModify;

public class NotificationPermissionDialogFragment extends DialogFragment {
    public static final String TAG = "notification_permission_dialog_fragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = requireContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.C_perm_dialog_manualPermReqTitle);
        builder.setMessage(R.string.C_perm_dialog_manualPermReqMessage);
        builder.setPositiveButton(R.string.C_perm_dialog_manualPermReqOpenSettings, (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        });
        builder.setNegativeButton(R.string.C_perm_dialog_manualPermReqCancel, null);

        AlertDialog dialog = builder.create();

        AlertDialogModify alertDialogModify = new AlertDialogModify(context);
        alertDialogModify.modify(dialog);

        setCancelable(false);

        return dialog;
    }
}
