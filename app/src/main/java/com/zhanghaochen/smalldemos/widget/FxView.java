package com.zhanghaochen.smalldemos.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.utils.SysUtils;

/**
 *
 */
public class FxView extends View {
    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;

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
    }

    private void drawFunctions(Canvas canvas) {
        RectF rectF = new RectF(0, 0, mWidth, (float) (mHeight * 0.8));
        // 画到2pai 就是正负360度
        // 设置1°是一格
        double step = (double) rectF.width() / 720;
        // 设置y轴的刻度,正负1
        Path path = new Path();
        double start = -360;
        for (int i = 0; i < 720; i++) {
            double x = i * step;
            double radius = Math.toRadians(start + i);
            double y = Math.sin(radius) / 1 * rectF.height() / 2 + mCenterY;
            if (i == 0) {
                path.moveTo(0, (float) y);
            } else {
                path.lineTo((float) x, (float) y);
            }
        }
        initPaint();
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.paint_lines_color));
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, mPaint);
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
        canvas.drawText("y=sin(x)", mCenterX, mHeight - SysUtils.convertDpToPixel(getContext(), 10), mPaint);
    }

    private void initPaint() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
        } else {
            mPaint.reset();
        }
    }
}
