package com.zhanghaochen.smalldemos.widget.trend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.zhanghaochen.smalldemos.beans.TrendDataBean;
import com.zhanghaochen.smalldemos.framework.widgets.chartview.TrendChartBaseView;
import com.zhanghaochen.smalldemos.utils.ColorUtils;
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
    private static final float VOL_DISTANCES = SysUtils.convertDpToPixel(1f);


    private static final float DP_10 = SysUtils.convertDpToPixel(10);
    private static final float DP_5 = SysUtils.convertDpToPixel(5);
    private static final float DP_3 = SysUtils.convertDpToPixel(3);

    private static final float CHART_LEFT_MARGIN = SysUtils.convertDpToPixel(2);
    private static final float TOP_MARGIN = SysUtils.convertDpToPixel(10);
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
            mChartRect = new RectF(LEFT_TEXT_WIDTH, TOP_MARGIN, width - RIGHT_TEXT_WIDTH, height * 0.7f);
            mVolRect = new RectF(LEFT_TEXT_WIDTH, height * 0.8f, width - RIGHT_TEXT_WIDTH, height - BOTTOM_MARGIN);
            mChartMiddleY = mChartRect.top + mChartRect.height() / 2;
            mWidth = width;
            mHeight = height;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawFrame(canvas);
        drawText(canvas);
        drawLines(canvas);
        drawVol(canvas);
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
        if (mMaxMin[0] != Float.MIN_VALUE && mMaxMin[1] != Float.MAX_VALUE) {
            initPaint();
            // 基准的最大值要比数据的最大值大才行，同理最小值也一样
            mPaint.setStyle(Paint.Style.FILL);
            int spSize = 9;
            float scaledSizeInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spSize,
                    getResources().getDisplayMetrics());
            mPaint.setTextSize(scaledSizeInPx);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            mPaint.setColor(COLOR_LABEL_TEXT);

            canvas.drawText(mMaxMin[1] + "", mChartRect.left - DP_5, mChartRect.bottom + DP_3, mPaint);
            canvas.drawText(mMaxMin[0] + "", mChartRect.left - DP_5, mChartRect.top + DP_3, mPaint);
            canvas.drawText((mMaxMin[0] + mMaxMin[1]) / 2f + "", mChartRect.left - DP_5, mChartRect.top + mChartRect.height() / 2f + DP_3, mPaint);

            // 画延x轴的几个数字，使用数据的数量作为x轴
            mPaint.setTextAlign(Paint.Align.CENTER);
            int size = mTrendDatas.size();
            canvas.drawText("0", mChartRect.left, mChartRect.bottom + DP_10, mPaint);
            canvas.drawText(size / 2 + "", mChartRect.left + mChartRect.width() / 2, mChartRect.bottom + DP_10, mPaint);
            canvas.drawText(size + "", mChartRect.right, mChartRect.bottom + DP_10, mPaint);
        }
    }

    private void drawLines(Canvas canvas) {
        if (mMaxMin[0] != Float.MIN_VALUE && mMaxMin[1] != Float.MAX_VALUE && mTrendDatas.size() > 0) {
            initPaint();
            mPaint.setStrokeWidth(SysUtils.convertDpToPixel(1));
            mPaint.setColor(COLOR_TREND_LINE);
            // 来个曲线
            Path path = new Path();
            ArrayList<TrendDataBean> tempBeans = new ArrayList<>(mTrendDatas);
            for (int i = 0; i < tempBeans.size(); i++) {
                TrendDataBean bean = tempBeans.get(i);
                if (i == 0) {
                    // 移动到第一个点
                    path.moveTo(mChartRect.left, mChartRect.bottom - getOffsetY(bean.newValue));
                } else {
                    path.lineTo(mChartRect.left + mStep * i, mChartRect.bottom - getOffsetY(bean.newValue));
                }
            }
            canvas.drawPath(path, mPaint);
            // 绘制阴影
            if (tempBeans.size() > 1) {
                path.lineTo(mChartRect.left + (tempBeans.size() - 1) * mStep, mChartRect.bottom);
                path.lineTo(mChartRect.left, mChartRect.bottom);
                path.lineTo(mChartRect.left, mChartRect.bottom - getOffsetY(tempBeans.get(0).newValue));
                path.close();
                initPaint();
                mPaint.setStyle(Paint.Style.FILL);
                LinearGradient linearGradient = new LinearGradient(mChartRect.left, mHighestY, mChartRect.left, mChartRect.bottom,
                        COLOR_GRADIENT_TOP, COLOR_GRADIENT_BOTTOM, Shader.TileMode.CLAMP);
                mPaint.setShader(linearGradient);
                canvas.drawPath(path, mPaint);
            }
        }
    }

    private void drawVol(Canvas canvas) {
        if (mStep <= 0 || mTrendDatas.size() < 1) {
            return;
        }
        initPaint();
        ArrayList<TrendDataBean> tempBeans = new ArrayList<>(mTrendDatas);
        for (int i = 0; i < tempBeans.size(); i++) {
            TrendDataBean bean = tempBeans.get(i);
            // 假设第一条是绿色的
            if (i == 0) {
                mPaint.setColor(ColorUtils.upPriceColor);
                // 两根成交量线之间距离0.5dp
                // 第一条线的粗细是正常的一半
                mPaint.setStrokeWidth((mStep - VOL_DISTANCES) / 2f);
                canvas.drawLine(mVolRect.left + mPaint.getStrokeWidth() / 2, mVolRect.bottom,
                        mVolRect.left + mPaint.getStrokeWidth() / 2, mVolRect.bottom - getVolOffsetY(bean.vol), mPaint);

            } else {
                // 和前一个价格比较，得出红绿
                TrendDataBean preBean = tempBeans.get(i - 1);
                if (preBean != null) {
                    mPaint.setColor(bean.newValue >= preBean.newValue ? ColorUtils.upPriceColor : ColorUtils.downPriceColor);
                    // 画线，
                    // 如果是最后一根线，与第一根画法类似
                    if (i == tempBeans.size() - 1) {
                        mPaint.setStrokeWidth((mStep - VOL_DISTANCES) / 2f);
                        canvas.drawLine(mVolRect.left + i * mStep - mPaint.getStrokeWidth() / 2, mVolRect.bottom,
                                mVolRect.left + i * mStep - mPaint.getStrokeWidth() / 2, mVolRect.bottom - getVolOffsetY(bean.vol), mPaint);
                    } else {
                        // 两根线之间的距离为VOL_DISTANCES
                        mPaint.setStrokeWidth(mStep - VOL_DISTANCES);
                        canvas.drawLine(mVolRect.left + i * mStep, mVolRect.bottom,
                                mVolRect.left + i * mStep, mVolRect.bottom - getVolOffsetY(bean.vol), mPaint);
                    }
                }
            }
        }
    }

    public void setData(List<TrendDataBean> data) {
        if (data != null && data.size() > 0) {
            this.mTrendDatas.clear();
            mTrendDatas.addAll(data);

            mMaxMin = getMaxMin(mTrendDatas);

            // 使用数据的数量作为x轴
            try {
                mStep = (float) mChartRect.width() / (data.size() - 1);
            } catch (Exception e) {
                mStep = mChartRect.width();
            }

            postInvalidate();
        }
    }

    private float getOffsetY(float value) {
        float result = 0f;
        // 确保差值在最大值之内
        float sub = mMaxMin[0] - mMaxMin[1];
        if (value <= mMaxMin[0] && mChartRect.height() != 0 && sub > 0) {
            return (value - mMaxMin[1]) / sub * (mChartRect.height());
        }
        return result;
    }

    private float getVolOffsetY(float vol) {
        float result = 0f;
        if (vol >= 0 && mMaxMin[2] > 0 && vol <= mMaxMin[2]) {
            return vol / mMaxMin[2] * mVolRect.height();
        }
        return result;
    }

    /**
     * 获取数据的最大值，最小值，和成交最大值
     */
    private float[] getMaxMin(List<TrendDataBean> trendDataBeans) {
        float[] maxMin = {Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE};
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
