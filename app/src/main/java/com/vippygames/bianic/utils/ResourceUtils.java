package com.vippygames.bianic.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import com.vippygames.bianic.R;

public class ResourceUtils {
    private final Context context;

    public ResourceUtils(Context context) {
        this.context = context;
    }

    public int getColorByAttr(int attr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}
