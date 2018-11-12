package com.zhanghaochen.smalldemos.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class SysUtils {

    /**
     * 将dp转换为px
     *
     * @param dp
     * @return
     */
    public static int convertDpToPixel(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (metrics.density * dp + 0.5f);
    }

    public static int convertSpToPixel(Context context, float sp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, metrics));
    }
}
