package com.zhanghaochen.smalldemos.function.recyclerviews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 可以限定最大高度的RecyclerView<br>
 * <br>
 * 创建时间：2016-08-02 09:14<br>
 * 初始作者：朱凌峰<br>
 * 修改历史：<br>
 * ·[2016-08-02 09:14 朱凌峰] 初始版本<br>
 */
public class MaxHeightRecyclerView extends RecyclerView {

    int maxHeight = 0;

    public MaxHeightRecyclerView(Context context) {
        super(context);
    }

    public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int height = heightSpec;
        if (maxHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthSpec, height);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }
}
