package com.zhanghaochen.smalldemos.customer.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Interpolator;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.utils.CustomerViewUtils;
import com.zhanghaochen.smalldemos.utils.SysUtils;

/**
 * 仪表盘的图表
 */
public class CircleBoardView extends View {

    /**
     * 右下角y
     */
    private float mRightDownY = 0;
    /**
     * 右下角x
     */
    private float mRightDownX = 0;

    private Paint mPaint;

    /**
     * 画圆弧的起始点
     */
    private float mStartX = SysUtils.convertDpToPixel(2);

    private float mStartY = SysUtils.convertDpToPixel(2);

    /**
     * 圆的半径
     */
    private float mCircleRadius = 0;

    /**
     * 圆心坐标
     */
    private float mCenterX, mCenterY;

    /**
     * 超过半圆的角度
     */
    private final float CONNER = 10;

    private final float PADDING = SysUtils.convertDpToPixel(4);

    private String mText = "--";

    private float mTotalValue;

    /**
     * 指针的目标角度，就是动画结束后的角度（相对应整个圆的角度）
     * 270度
     * *
     * *
     * *
     * 180度********** 360度(0)
     * *
     * *
     * *
     * 90度
     */
    private float mFinalAngle = 170;

    /**
     * 记录当前的角度
     */
    private float mCurrentAngle = 170;

    public CircleBoardView(Context context) {
        super(context);
    }

    public CircleBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        if (width > 0 && height > 0) {
            mRightDownX = width;
            mRightDownY = height;
            // 根据被赋予的画布大小，计算半径
            // 判断高度，高度的分界线为r+sinConner*r(此时的半径为width/2)
            if (mRightDownY >= ((1 + Math.sin(CONNER * Math.PI / 180)) * (mRightDownX / 2))) {
                mCircleRadius = mRightDownX / 2 - PADDING;
            } else {
                // 获取与要画圆的正切的正方形的边长
                float minLength = Math.min(mRightDownX, mRightDownY) - PADDING;
                // 获取圆的半径
                // 由于不是半圆，要多画10度，需要算出这10度的y的高度(高度/(1+sin10))
                mCircleRadius = (float) ((minLength / (1 + Math.sin(CONNER * Math.PI / 180))));
            }
            mCenterX = mStartX + mCircleRadius;
            mCenterY = mStartY + mCircleRadius;
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOutArc(canvas);
        drawCenter(canvas);
        drawGradientArc(canvas);
        drawOutDot(canvas);
    }

    private void initPaint() {
        if (mPaint == null) {
            mPaint = new Paint();
        } else {
            mPaint.reset();
        }
    }

    /**
     * 仪表盘基础界面,几个圆弧和刻度，文字
     *
     * @param canvas 画布
     */
    private void drawOutArc(Canvas canvas) {
        initPaint();
        // 有阴影的大圆弧
        float radiusWidth = mCircleRadius / 2;
        mPaint.setStrokeWidth(radiusWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(SysUtils.getColor(R.color.circle_board_bg));
        canvas.drawArc(new RectF(mStartX + radiusWidth / 2, mStartY + radiusWidth / 2,
                mCircleRadius * 2 + mStartX - radiusWidth / 2, mCircleRadius * 2 + mStartY - radiusWidth / 2),
            180 - CONNER, 180 + CONNER * 2, false, mPaint);
        // 最外面圆弧线条
        float strokeWidth = SysUtils.convertDpToPixel(1);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(SysUtils.getColor(R.color.circle_board_default_radius));
        canvas.drawArc(new RectF(mStartX, mStartY, mCircleRadius * 2 + mStartX,
            mCircleRadius * 2 + mStartY), 180 - CONNER, 180 + CONNER * 2, false, mPaint);
        // 中间的刻度
        mPaint.setColor(SysUtils.getColor(R.color.circle_board_middle_line));
        canvas.drawLine(mCenterX, mStartY + SysUtils.convertDpToPixel(2), mCenterX, mStartY + SysUtils.convertDpToPixel(7), mPaint);
        //写文字
        initPaint();
        int spSize = 13;
        float scaledSizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            spSize, getResources().getDisplayMetrics());
        mPaint.setTextSize(scaledSizeInPixels);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(SysUtils.getColor(R.color.circle_text_color));
        // 7是刻度底端，13是行高
        canvas.drawText("均值" + mText, mCenterX, mStartY + SysUtils.convertDpToPixel(7 + 13), mPaint);
    }

    /**
     * 画中间的点和指针
     *
     * @param canvas 画布
     */
    private void drawCenter(Canvas canvas) {
        initPaint();
        // 中间圆点的半径
        int radius = SysUtils.convertDpToPixel(4);
        mPaint.setStrokeWidth(SysUtils.convertDpToPixel(10));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(SysUtils.getColor(R.color.circle_board_dot_red));
        // 画指针
        Path path = new Path();
        // 设计到三个点，组成三角指针的形状(因指针头要圆头，需要四个点定位)
        float fingureLength = mCircleRadius - SysUtils.convertDpToPixel(15);
        float[] point1 = CustomerViewUtils.getCoordinatePoint(mCenterX, mCenterY, fingureLength, mCurrentAngle - 0.4f);
        float[] point11 = CustomerViewUtils.getCoordinatePoint(mCenterX, mCenterY, fingureLength, mCurrentAngle + 0.4f);
        path.lineTo(point1[0], point1[1]);
        float[] point2 = CustomerViewUtils.getCoordinatePoint(mCenterX, mCenterY, radius / 2, mCurrentAngle - 90);
        path.lineTo(point2[0], point2[1]);
        float[] point3 = CustomerViewUtils.getCoordinatePoint(mCenterX, mCenterY, radius / 2, mCurrentAngle + 90);
        path.lineTo(point3[0], point3[1]);
        path.lineTo(point11[0], point11[1]);
        path.lineTo(point1[0], point1[1]);
        path.close();
        canvas.drawPath(path, mPaint);
        // 先画背景色的圆
        mPaint.setColor(SysUtils.getColor(R.color.circle_board_dot_out));
        canvas.drawCircle(mCenterX, mCenterY, radius + SysUtils.convertDpToPixel(2), mPaint);
        // 画中心红色的圆
        mPaint.setColor(SysUtils.getColor(R.color.circle_board_dot_red));
        canvas.drawCircle(mCenterX, mCenterY, radius, mPaint);

        mPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    /**
     * 外层的渐变色圆弧
     *
     * @param canvas 画布
     */
    private void drawGradientArc(Canvas canvas) {
        initPaint();
        mPaint.setStrokeWidth(SysUtils.convertDpToPixel(1.5f));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        int[] colors = {SysUtils.getColor(R.color.circle_start_color), SysUtils.getColor(R.color.circle_end_color)};
        // SweepGradient默认是从3点钟位置开始渐变的,需要旋转一哈
        SweepGradient gradient = new SweepGradient(mCenterX, mCenterY, colors, null);
        Matrix matrix = new Matrix();
        matrix.setRotate(180 - CONNER, mCenterX, mCenterY);
        gradient.setLocalMatrix(matrix);
        mPaint.setShader(gradient);
        // 当前圆弧对应的展开角度，就是指针的角度-170度
        canvas.drawArc(new RectF(mStartX, mStartY, mCircleRadius * 2 + mStartX,
            mCircleRadius * 2 + mStartY), 180 - CONNER, mCurrentAngle - 170, false, mPaint);
    }

    /**
     * 画在圆弧上的点
     */
    private void drawOutDot(Canvas canvas) {
        initPaint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        int radius = SysUtils.convertDpToPixel(2);
        float[] dotPoint = CustomerViewUtils.getCoordinatePoint(mCenterX, mCenterY, mCircleRadius, mCurrentAngle);
//        // 画阴影
//        RadialGradient gradient = new RadialGradient(dotPoint[0], dotPoint[1], radius + SysUtils.convertDpToPixel(2),
//                new int[]{HtscColorUtil.getColorFromXML(R.color.hq_circle_board_dot_shadow), Color.parseColor("#00000000")},
//                new float[]{0.2f, 1}, Shader.TileMode.REPEAT);
//        mPaint.setShader(gradient);
//        canvas.drawCircle(dotPoint[0], dotPoint[1], radius + SysUtils.convertDpToPixel(2), mPaint);

        // 画圆点
        initPaint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(SysUtils.getColor(R.color.circle_board_dot_red));
        canvas.drawCircle(dotPoint[0], dotPoint[1], radius, mPaint);
    }

    /**
     * 设置均值
     *
     * @param deGree 均值
     */
    public void setMiddleValue(String deGree) {
        if (TextUtils.isEmpty(deGree)) {
            return;
        }
        float degree = SysUtils.parseFloat(deGree);
        if (degree > 0.0f) {
            mTotalValue = degree * 2;
            mText = deGree + "%";
            postInvalidate();
        } else if ("0".equals(deGree)) {
            mTotalValue = 0;
            mText = "0%";
            postInvalidate();
        }
    }

    /**
     * 设置当前数值
     *
     * @param value 当前数值
     */
    public void setCurrentValue(String value) {
        // 如果非法，直接设置为170度
        if (TextUtils.isEmpty(value)) {
            mFinalAngle = 180 - CONNER;
        } else {
            float v = SysUtils.parseFloat(value);
            // 计算数值对应的角度
            if (v <= 0.0f) {
                // 0对应180度
                mFinalAngle = 180;
            } else if (v > mTotalValue) {
                // 如果大于均值的两倍，直接拉满
                mFinalAngle = CONNER + 360;
            } else {
                if (mTotalValue > 0.0f) {
                    float degree = 180 * (v / mTotalValue);
                    mFinalAngle = 180 + degree;
                } else {
                    // 总值小于等于0，设置的值大于0，应该直接拉满
                    mFinalAngle = CONNER + 360;
                }
            }
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mCurrentAngle, mFinalAngle);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float v) {
                return 1 - (1 - v) * (1 - v) * (1 - v);
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentAngle = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.start();
    }
}
