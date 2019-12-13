package com.zhanghaochen.smalldemos.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.utils.SysUtils;

/**
 * @author created by zhanghaochen
 * @date 2019-12-11 3:21 PM
 * 描述：上下翻动的radioBtn
 */
public class FantasticRadioButton extends AppCompatRadioButton {
    private static final long DURATION_TIME = 300;
    private static final float ONE_DP = SysUtils.convertDpToPixel(1);
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mBgColor;

    private int mWidth;
    private int mHeight;

    private int mInnerPaddingX = SysUtils.convertDpToPixel(2);
    private int mInnerPaddingY = SysUtils.convertDpToPixel(2);

    private int mCenterX;
    private int mCenterY;

    /**
     * 底部圆点的画笔
     */
    private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mBottomDotColor;
    private float mBottomStaticRectWidth = SysUtils.convertDpToPixel(15);
    // 宽是高的5倍
    private float mBottomRectWidth;
    private float mBottomRectHeight;

    /**
     * 按钮的图案
     */
    private Bitmap mIconBitmap;
    private int mDrawableWidth;
    private int mDrawableHeight;

    private int mTextColor;
    private int mTextSize;
    private String mTextStr;
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 平移的距离
     */
    private float mTransDistances = 0;
    private float mTargetDistances;

    /**
     * 遮挡层的高度
     */
    private float mLayerHeight = SysUtils.convertDpToPixel(16);
    private boolean mIsTransEnd = true;

    public FantasticRadioButton(Context context) {
        this(context, null, 0);
    }

    public FantasticRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public FantasticRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FantasticRadioButton);

        mBgColor = a.getColor(R.styleable.FantasticRadioButton_bg_color, Color.WHITE);
        mBottomDotColor = a.getColor(R.styleable.FantasticRadioButton_bottom_dot_color, Color.parseColor("#EE82EE"));
        mBottomStaticRectWidth = a.getDimensionPixelOffset(R.styleable.FantasticRadioButton_bottom_dot_width, SysUtils.convertDpToPixel(15));
        mDrawableWidth = a.getDimensionPixelOffset(R.styleable.FantasticRadioButton_icon_width, 0);
        mDrawableHeight = a.getDimensionPixelOffset(R.styleable.FantasticRadioButton_icon_height, 0);
        mTextStr = SysUtils.getSafeString(a.getString(R.styleable.FantasticRadioButton_label));
        mTextColor = a.getColor(R.styleable.FantasticRadioButton_label_color, Color.GREEN);
        mTextSize = a.getDimensionPixelOffset(R.styleable.FantasticRadioButton_label_size, SysUtils.convertDpToPixel(16));

        Drawable drawable = a.getDrawable(R.styleable.FantasticRadioButton_fantastic_drawable);
        if (drawable != null) {
            mIconBitmap = drawableToBitmap(drawable);
        }

        a.recycle();

        mTargetDistances = mDrawableHeight * 3f;

        setButtonDrawable(null);

        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5f);

        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mBottomDotColor);
        mCirclePaint.setStrokeWidth(5f);
        mCirclePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mWidth = w - mInnerPaddingX * 2;
            mHeight = h - mInnerPaddingY * 2;

            // 确定画布的中心点
            mCenterX = w / 2;
            mCenterY = h / 2;

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth <= 0 || mHeight <= 0 || mIconBitmap == null || mIconBitmap.isRecycled()) {
            return;
        }

        // 先画图片和文字切换的部分
        if (mIconBitmap != null) {
            drawIconAndText(canvas);
        }
        // 画一个遮挡层，为了遮住放在图片下面的文字
        float layerHeight = (getHeight() - mIconBitmap.getHeight()) / 3;
        mPaint.setColor(mBgColor);
        canvas.drawRect(0, getHeight() - layerHeight, mWidth, getHeight(), mPaint);

        // 最后画遮挡条
        if (!mIsTransEnd) {
            drawLayerPath(canvas);
        }

        // 最后最后画底部圆圈
        drawBottomDot(canvas);
    }

    @Override
    public void setChecked(boolean checked) {
        boolean isChanged = checked != isChecked();

        super.setChecked(checked);

//        ValueAnimator bottomCircleAni = getCircleAnimation();
        ValueAnimator bottomLineAni = startBottomLineAnimation();
        ValueAnimator iconAni = getIconAndTextAnimation();
        if (isChanged) {
            if (checked) {
//                startCircleAnimator();
                bottomLineAni.start();
                iconAni.start();
                postInvalidate();
            } else {
                bottomLineAni.reverse();
                iconAni.reverse();
                postInvalidate();
            }
        }
    }

    /**
     * 画文字和图片
     */
    private void drawIconAndText(Canvas canvas) {
        canvas.save();
        int left = mCenterX - mIconBitmap.getWidth() / 2;
        int top = mCenterY - mIconBitmap.getHeight() / 2;
        canvas.drawBitmap(mIconBitmap, left, top - mTransDistances, mPaint);

        // 画字
        float scaledSizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
            mTextSize, getResources().getDisplayMetrics());
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(scaledSizeInPixels);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setColor(mTextColor);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        float baseline = (mHeight + mTargetDistances - mTransDistances - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(mTextStr, mCenterX, baseline, mTextPaint);
        canvas.restore();
    }

    /**
     * 画底部的选中圆点
     */
    private void drawBottomDot(Canvas canvas) {
        canvas.save();
        canvas.drawRoundRect(new RectF(mCenterX - mBottomRectWidth / 2, mHeight - mBottomRectHeight - ONE_DP, mCenterX + mBottomRectWidth / 2, mHeight - ONE_DP),
            mBottomRectHeight / 2, mBottomRectHeight / 2, mCirclePaint);
//        canvas.drawCircle(mCenterX, getHeight() - SysUtils.convertDpToPixel(6),
//                mCurrentCircleRadius, mCirclePaint);
        canvas.restore();
    }

    /**
     * 画遮罩层
     */
    private void drawLayerPath(Canvas canvas) {
        canvas.save();
        float upTransDistance = -mLayerHeight / 2;
        Path path = new Path();
        path.moveTo(mCenterX - mWidth / 3, upTransDistance);
        path.lineTo(mCenterX + mWidth / 3, upTransDistance + SysUtils.convertDpToPixel(10));
        path.lineTo(mCenterX + mWidth / 3, upTransDistance + SysUtils.convertDpToPixel(10) + mLayerHeight);
        path.lineTo(mCenterX - mWidth / 3, upTransDistance + mLayerHeight);
        path.lineTo(mCenterX - mWidth / 3, upTransDistance);
        path.close();
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, mPaint);
        Path path2 = new Path();
        float startY = upTransDistance + mLayerHeight * 2;
        path2.moveTo(mCenterX - mWidth / 3, startY);
        path2.lineTo(mCenterX + mWidth / 3, startY + SysUtils.convertDpToPixel(10));
        path2.lineTo(mCenterX + mWidth / 3, startY + SysUtils.convertDpToPixel(10) + mLayerHeight);
        path2.lineTo(mCenterX - mWidth / 3, startY + mLayerHeight);
        path2.lineTo(mCenterX - mWidth / 3, startY);
        path2.close();
        canvas.drawPath(path2, mPaint);
        canvas.restore();
    }

    /**
     * 获取底部小点点的动画
     */
    private ValueAnimator startBottomLineAnimation() {
        ValueAnimator lineAnimation = ValueAnimator.ofFloat(0f, mBottomStaticRectWidth);
        lineAnimation.setDuration(DURATION_TIME);
        lineAnimation.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float v) {
                return 1 - (1 - v) * (1 - v);
            }
        });
        lineAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBottomRectWidth = (float) animation.getAnimatedValue();
                // 高度是宽度的五分之一
                mBottomRectHeight = mBottomRectWidth / 5;
                postInvalidate();
            }
        });
        return lineAnimation;
    }

    /**
     * 获取文字和图片的平移动画
     */
    private ValueAnimator getIconAndTextAnimation() {
        ValueAnimator transAnimation = ValueAnimator.ofFloat(0f, mTargetDistances);
        transAnimation.setDuration(DURATION_TIME);
        transAnimation.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float v) {
                return 1 - (1 - v);
            }
        });
        transAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTransDistances = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        transAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                mIsTransEnd = true;
                postInvalidate();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsTransEnd = true;
                postInvalidate();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mIsTransEnd = false;
                postInvalidate();
            }

            @Override
            public void onAnimationPause(Animator animation) {
                mIsTransEnd = true;
                postInvalidate();
            }
        });
        return transAnimation;
    }

    /**
     * 将Drawable转换为Bitmap
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        //取drawable的宽高
        int width = mDrawableWidth > 0 ? mDrawableWidth : drawable.getIntrinsicWidth();
        int height = mDrawableHeight > 0 ? mDrawableHeight : drawable.getIntrinsicHeight();
        //取drawable的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE
            ? Bitmap.Config.ARGB_8888
            : Bitmap.Config.RGB_565;
        //创建对应的bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        //创建对应的bitmap的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        //把drawable内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

}
