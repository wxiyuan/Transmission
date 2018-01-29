package com.transmission;

import android.content.Context;
import android.support.annotation.NonNull;

public class Utils {

    public static String getStringRes(@NonNull Context context, int resId) {
        return context.getString(resId);
    }

}
