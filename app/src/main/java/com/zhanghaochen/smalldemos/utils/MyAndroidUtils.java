package com.zhanghaochen.smalldemos.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

public class MyAndroidUtils {

    /**
     * 获取屏幕宽度
     *
     * @param context context
     * @return int
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context context
     * @return int
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 针对全面屏的屏幕高度
     *
     * @param activity activity
     * @return int
     */
    public static int getScreenRealHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        } else {
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        }
        return dm.heightPixels;
    }

    /**
     * 获取状态栏的高度
     *
     * @param context context
     * @return int
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
