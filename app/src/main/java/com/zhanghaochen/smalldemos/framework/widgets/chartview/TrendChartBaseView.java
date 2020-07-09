/**
 * @Package: com.zhanghaochen.smalldemos.customer.views
 * @Description:
 * @Author: zhanghaochen
 * @CreateDate: 2019/4/17 下午5:42
 * @Version: 1.0
 */
package com.zhanghaochen.smalldemos.framework.widgets.chartview;

import android.content.Context;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author created by zhanghaochen
 * @date 2019-04-17 下午5:42
 * 描述：折线图基类
 */
public abstract class TrendChartBaseView extends View {
    public Paint mPaint = new Paint();

    public TrendChartBaseView(Context context) {
        super(context);
        setLayerType(LAYER_TYPE_NONE, null);
        initViews();
    }

    public TrendChartBaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_NONE, null);
        initViews();
    }

    public TrendChartBaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_NONE, null);
        initViews();
    }

    public void initPaint() {
        if (mPaint == null) {
            mPaint = new Paint();
        } else {
            mPaint.reset();
        }
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    protected abstract void initViews();
}
