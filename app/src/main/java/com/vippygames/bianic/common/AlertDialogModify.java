package com.vippygames.bianic.common;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.vippygames.bianic.R;
import com.vippygames.bianic.utils.ResourceUtils;

public class AlertDialogModify {
    private final Context context;

    public AlertDialogModify(Context context) {
        this.context = context;
    }

    public void modify(AlertDialog alertDialog) {
        alertDialog.setOnShowListener(dialogInterface -> {
            Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            modifyButton(btnPositive);

            Button btnNegative = alertDialog.getButton(Dialog.BUTTON_NEGATIVE);
            modifyButton(btnNegative);

            Button btnNeutral = alertDialog.getButton(Dialog.BUTTON_NEUTRAL);
            modifyButton(btnNeutral);
        });
    }

    private void modifyButton(Button btn) {
        if (btn == null || btn.getVisibility() != View.VISIBLE) {
            return;
        }

        btn.setTextSize(18);

        ResourceUtils resourceUtils = new ResourceUtils(context);
        btn.setTextColor(resourceUtils.getColorByAttr(R.attr.textColor1));
    }
}
