package com.zhanghaochen.smalldemos.utils;

import androidx.core.content.ContextCompat;

import com.zhanghaochen.smalldemos.R;

/**
 * @author created by zhanghaochen
 * @date 2019-08-22 5:17 PM
 * 描述：
 */
public class ColorUtils {
    public static int pingPanColor = ContextCompat.getColor(GlobalParams.mApplication, R.color.hq_pingpan_color);
    public static int upPriceColor = ContextCompat.getColor(GlobalParams.mApplication, R.color.hq_up_color);
    public static int downPriceColor = ContextCompat.getColor(GlobalParams.mApplication, R.color.hq_down_color);
    public static int fontColor = ContextCompat.getColor(GlobalParams.mApplication, R.color.hq_font_color);
    public static int numColor = ContextCompat.getColor(GlobalParams.mApplication, R.color.hq_num_color);
}
