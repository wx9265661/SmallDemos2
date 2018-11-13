package com.zhanghaochen.smalldemos.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.utils.SysUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 */
public class FxView extends View {
    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;

    private ArrayList<Double> mResults = new ArrayList<>();
    private ArrayList<MyPoint> mPoints = new ArrayList<>();

    /**
     * 真正画图的区域
     */
    private RectF mInnerRect;

    private int mXMax;
    private double mYMax;
    private double mStep;

    private Paint mPaint;

    public FxView(Context context) {
        super(context);
    }

    public FxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FxView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;

        mInnerRect = new RectF(0, 0, mWidth, (float) (mHeight * 0.9));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCrossLine(canvas);
        drawFunctions(canvas);
        drawText(canvas);
    }

    private void drawCrossLine(Canvas canvas) {
        initPaint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.paint_crossline_color));

        // 画十字线
        canvas.drawLine(0, mCenterY, mWidth, mCenterY, mPaint);
        canvas.drawLine(mCenterX, 0, mCenterX, mHeight, mPaint);
        // 画刻度
        canvas.drawLine(mInnerRect.left + mInnerRect.width() / 4, mCenterY,
                mInnerRect.left + mInnerRect.width() / 4, mCenterY - SysUtils.convertDpToPixel(getContext(), 5), mPaint);
        canvas.drawLine(mInnerRect.right - mInnerRect.width() / 4, mCenterY,
                mInnerRect.right - mInnerRect.width() / 4, mCenterY - SysUtils.convertDpToPixel(getContext(), 5), mPaint);

        //画虚线
        mPaint.setColor(Color.parseColor("#000000"));
        // 3.5实线，2.5空白
        mPaint.setPathEffect(new DashPathEffect(new float[]{SysUtils.convertDpToPixel(getContext(), 3.5f), SysUtils.convertDpToPixel(getContext(), 2.5f)}, 0));
        Path dashPath = new Path();
        dashPath.moveTo(mInnerRect.left, mCenterY + mInnerRect.height() / 4);
        dashPath.lineTo(mInnerRect.right, mCenterY + mInnerRect.height() / 4);
        canvas.drawPath(dashPath, mPaint);
        dashPath.reset();
        dashPath.moveTo(mInnerRect.left, mCenterY - mInnerRect.height() / 4);
        dashPath.lineTo(mInnerRect.right, mCenterY - mInnerRect.height() / 4);
        canvas.drawPath(dashPath, mPaint);
    }

    private void drawFunctions(Canvas canvas) {
        Path path = new Path();
        initPaint();
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.paint_lines_color));
        mPaint.setStyle(Paint.Style.STROKE);
        if (mPoints.size() > 0) {
            for (int i = 0; i < mPoints.size(); i++) {
                MyPoint point = mPoints.get(i);
                if (i == 0) {
                    path.reset();
                    path.moveTo(point.x, point.y);
                } else {
                    path.lineTo(point.x, point.y);
                }
            }
            canvas.drawPath(path, mPaint);
        }
    }

    private void drawText(Canvas canvas) {
        //写文字
        initPaint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.paint_text_color));
        int spSize = 16;
        float scaledSizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spSize, getResources().getDisplayMetrics());
        mPaint.setTextSize(scaledSizeInPixels);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(-mXMax + "", SysUtils.convertDpToPixel(getContext(), 10), mCenterY, mPaint);
        canvas.drawText(mXMax + "", mInnerRect.width() - SysUtils.convertDpToPixel(getContext(), 10), mCenterY, mPaint);

        canvas.drawText(-mYMax / 2 + "", mCenterX, mCenterY + mInnerRect.height() / 4, mPaint);
        canvas.drawText(mYMax / 2 + "", mCenterX, mCenterY - mInnerRect.height() / 4, mPaint);
    }

    private void initPaint() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
        } else {
            mPaint.reset();
        }
    }

    public void setFormula(String formula, String max, String min) {
        mResults.clear();
        mPoints.clear();
        // 获取绝对值最大x，确定x的范围
        int maxValue = SysUtils.parseInt(max);
        int minValue = SysUtils.parseInt(min);
        mXMax = Math.max(Math.abs(maxValue), Math.abs(minValue));
        // 计算一格的宽度
        mStep = (double) mInnerRect.width() / (mXMax * 2);
        for (int i = -mXMax; i < mXMax + 1; i++) {
            // 转化将x替换
            String newFormula = formula.replaceAll("x", i + "");
            // 计算x对应的y的数值
            mResults.add(SysUtils.eval(newFormula));
        }
        // 计算出y的最大最小值
        double minY = Collections.min(mResults);
        double maxY = Collections.max(mResults);
        mYMax = Math.max(Math.abs(minY), Math.abs(maxY));
        for (int i = 0; i < mResults.size(); i++) {
            MyPoint point = new MyPoint();
            point.x = (float) (-mInnerRect.left + mStep * i);
            point.y = (float) (-mResults.get(i) / mYMax * mInnerRect.height() / 2 + mCenterY);
            mPoints.add(point);
        }

        postInvalidate();
    }

    class MyPoint {
        public float x;
        public float y;
    }
}
