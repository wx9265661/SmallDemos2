package com.zhanghaochen.smalldemos.widget.trend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.zhanghaochen.smalldemos.beans.TrendDataBean;
import com.zhanghaochen.smalldemos.framework.widgets.chartview.TrendChartBaseView;
import com.zhanghaochen.smalldemos.utils.SysUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author created by zhanghaochen
 * @date 2019-08-07 3:31 PM
 * 描述：折线图
 */
public class TrendView extends TrendChartBaseView {
    private static final float LEFT_TEXT_WIDTH = SysUtils.convertDpToPixel(30);
    private static final float RIGHT_TEXT_WIDTH = SysUtils.convertDpToPixel(35);

    private static final float CHART_LEFT_MARGIN = SysUtils.convertDpToPixel(2);
    private static final float TOP_MARGIN = SysUtils.convertDpToPixel(2);
    private static final float BOTTOM_MARGIN = SysUtils.convertDpToPixel(3);

    private static final float RECT_LINE_WIDTH = SysUtils.convertDpToPixel(0.5f);

    private static final int COLOR_BG = Color.parseColor("#FEFEFE");
    private static final int COLOR_LINE = Color.parseColor("#D8D8D8");
    private static final int COLOR_LABEL_TEXT = Color.parseColor("#AAA8A8");
    private static final int COLOR_TREND_LINE = Color.parseColor("#FC3539");
    private static final int COLOR_GRADIENT_TOP = Color.parseColor("#B2FF9C9D");
    private static final int COLOR_GRADIENT_BOTTOM = Color.parseColor("#B2FFF3EF");

    /**
     * 分时图的矩形框
     */
    private RectF mChartRect;

    /**
     * 成交量的矩形框
     */
    private RectF mVolRect;

    /**
     * 一小格的长度
     */
    private float mStep = 0;

    private int mWidth, mHeight;

    /**
     * 折线图中线的y坐标
     */
    private float mChartMiddleY;

    private int mMaxSub;

    /**
     * 总共的数据量，
     */
    private int mMaxCount;

    /**
     * 记录绘制折线图中最高的y的坐标，用来绘制折线下面的渐变阴影，没有渐变的话，不需要这个值
     */
    private float mHighestY;

    /**
     * 记录分时的最大值，最小值，成交量最大值
     */
    private float[] mMaxMin = {Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE};

    private float mMoveX, mMoveY;

    /**
     * 画图时的基准值，最低和最高，可以按需修改
     */
    private float mBaseLow, mBaseHigh;

    private List<TrendDataBean> mTrendDatas = new ArrayList<>();

    public TrendView(Context context) {
        super(context);
    }

    public TrendView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TrendView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        if (width > 0 && height > 0) {
            mChartRect = new RectF(LEFT_TEXT_WIDTH, TOP_MARGIN, width - RIGHT_TEXT_WIDTH, height * 0.6f);
            mVolRect = new RectF(LEFT_TEXT_WIDTH, height * 0.7f, width - RIGHT_TEXT_WIDTH, height - BOTTOM_MARGIN);
            mChartMiddleY = mChartRect.top + mChartRect.height() / 2;
            mWidth = width;
            mHeight = height;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawFrame(canvas);
        drawText(canvas);
    }

    /**
     * 画基础框框
     */
    private void drawFrame(Canvas canvas) {
        initPaint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(COLOR_BG);
        mPaint.setStrokeWidth(RECT_LINE_WIDTH);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);

        // 画线条框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(COLOR_LINE);
        canvas.drawRect(mChartRect, mPaint);
        canvas.drawRect(mVolRect, mPaint);
        // 画横线
        canvas.drawLine(mChartRect.left, (mChartRect.top + mChartMiddleY) / 2, mChartRect.right, (mChartRect.top + mChartMiddleY) / 2, mPaint);
        canvas.drawLine(mChartRect.left, mChartMiddleY, mChartRect.right, mChartMiddleY, mPaint);
        canvas.drawLine(mChartRect.left, (mChartRect.bottom + mChartMiddleY) / 2, mChartRect.right, (mChartRect.bottom + mChartMiddleY) / 2, mPaint);
        canvas.drawLine(mVolRect.left, mVolRect.top + mVolRect.height() / 2, mVolRect.right, mVolRect.top + mVolRect.height() / 2, mPaint);
        // 画竖线
        canvas.drawLine(mChartRect.left + mChartRect.width() / 4, mChartRect.top, mChartRect.left + mChartRect.width() / 4, mChartRect.bottom, mPaint);
        canvas.drawLine(mChartRect.left + mChartRect.width() / 2, mChartRect.top, mChartRect.left + mChartRect.width() / 2, mChartRect.bottom, mPaint);
        canvas.drawLine(mChartRect.right - mChartRect.width() / 4, mChartRect.top, mChartRect.right - mChartRect.width() / 4, mChartRect.bottom, mPaint);
        canvas.drawLine(mVolRect.left + mVolRect.width() / 4, mVolRect.top, mVolRect.left + mVolRect.width() / 4, mVolRect.bottom, mPaint);
        canvas.drawLine(mVolRect.left + mVolRect.width() / 2, mVolRect.top, mVolRect.left + mVolRect.width() / 2, mVolRect.bottom, mPaint);
        canvas.drawLine(mVolRect.right - mVolRect.width() / 4, mVolRect.top, mVolRect.right - mVolRect.width() / 4, mVolRect.bottom, mPaint);
    }

    private void drawText(Canvas canvas) {
        initPaint();
        // 基准的最大值要比数据的最大值大才行，同理最小值也一样
        mPaint.setStyle(Paint.Style.FILL);
        int spSize = 9;
        float scaledSizeInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spSize,
                getResources().getDisplayMetrics());
        mPaint.setTextSize(scaledSizeInPx);
        mPaint.setTextAlign(Paint.Align.RIGHT);
        mPaint.setColor(COLOR_LABEL_TEXT);
        canvas.drawText(mBaseLow + "", mChartRect.left, mChartRect.bottom, mPaint);

    }

    /**
     * 获取数据的最大值，最小值，和成交最大值
     */
    private float[] getMaxMin(List<TrendDataBean> trendDataBeans) {
        float[] maxMin = {Long.MIN_VALUE, Long.MAX_VALUE, Long.MIN_VALUE};
        if (trendDataBeans == null || trendDataBeans.size() < 1) {
            return maxMin;
        }
        for (int i = 0; i < trendDataBeans.size(); i++) {
            TrendDataBean bean = trendDataBeans.get(i);
            maxMin[0] = Math.max(maxMin[0], bean.newValue);
            maxMin[1] = Math.min(maxMin[1], bean.newValue);
            maxMin[2] = Math.max(maxMin[2], bean.vol);
        }
        return maxMin;
    }
}
