package com.zhanghaochen.smalldemos.customer.views;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CountDownTextView extends AppCompatTextView implements View.OnClickListener, LifecycleObserver {
    private CountDownTimer mCountDownTimer;
    private OnCountDownStartListener mOnCountDownStartListener;
    private OnCountDownTickListener mOnCountDownTickListener;
    private OnCountDownFinishListener mOnCountDownFinishListener;
    /**
     * 普通文本信息
     */
    private String mNormalText;
    private String mCountDownText;
    private OnClickListener mOnClickListener;
    //倒计时期间是否允许点击
    private boolean mClickable = false;
    //是否把时间格式化成时分秒
    private boolean mShowFormatTime = false;

    /**
     * 用来保存日，时，分，秒
     */
    public int[][] mTimeUnits;
    /**
     * 用来记录倒计时前面label的长度
     */
    public int mBeforeIndex = 0;

    private boolean mIsShowCompleteTv;

    public CountDownTextView(Context context) {
        this(context, null);
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        autoBindLifecycle(context);
    }

    /**
     * 控件自动绑定生命周期,宿主可以是activity或者fragment
     */
    private void autoBindLifecycle(Context context) {
        if (context instanceof AppCompatActivity) {
            // 宿主是activity
            AppCompatActivity activity = (AppCompatActivity) context;
            FragmentManager fm = activity.getSupportFragmentManager();
            List<Fragment> fragments = fm.getFragments();
            if (fragments == null || fragments.size() <= 0) {
                return;
            }
            for (Fragment fragment : fragments) {
                View parent = fragment.getView();
                if (parent != null) {
                    View find = parent.findViewById(getId());
                    if (find == this) {
                        fragment.getLifecycle().addObserver(this);
                        return;
                    }
                }
            }
        }
        // 宿主是fragment
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mOnCountDownStartListener = null;
        mOnCountDownTickListener = null;
        mOnCountDownFinishListener = null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }

    /**
     * 非倒计时状态文本
     *
     * @param normalText 文本
     */
    public CountDownTextView setNormalText(String normalText) {
        mNormalText = normalText;
        setText(normalText);
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mOnCountDownStartListener = null;
        mOnCountDownTickListener = null;
        mOnCountDownFinishListener = null;
        return this;
    }

    public void recycler() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mOnCountDownStartListener = null;
        mOnCountDownTickListener = null;
        mOnCountDownFinishListener = null;
    }

    /**
     * 设置倒计时文本内容
     *
     * @param front  倒计时文本前部分
     * @param latter 倒计时文本后部分
     */
    public CountDownTextView setCountDownText(String front, String latter) {
        mCountDownText = front + "%1$s" + latter;
        return this;
    }

    /**
     * 顺序计时，非倒计时
     *
     * @param second 计时时间秒
     */
    public void startCount(long second) {
        startCount(second, TimeUnit.SECONDS);
    }

    public void startCount(long time, final TimeUnit timeUnit) {
        count(time, 0, timeUnit, false);
    }

    /**
     * 默认按秒倒计时
     *
     * @param second 多少秒
     */
    public void startCountDown(long second) {
        startCountDown(second, TimeUnit.SECONDS);
    }

    public void startCountDown(long time, final TimeUnit timeUnit) {
        count(time, 0, timeUnit, true);
    }

    /**
     * 是否格式化时间
     *
     * @param formatTime 是否格式化
     */
    public CountDownTextView setShowFormatTime(boolean formatTime) {
        mShowFormatTime = formatTime;
        return this;
    }

    /**
     * 计时方案
     *
     * @param time        计时时长
     * @param timeUnit    时间单位
     * @param isCountDown 是否是倒计时，false正向计时
     */
    private void count(final long time, final long offset, final TimeUnit timeUnit, final boolean isCountDown) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        setEnabled(mClickable);
        // 转换成毫秒
        final long millisInFuture = timeUnit.toMillis(time) + 500;
        // 时间间隔为1s
        long interval = TimeUnit.MILLISECONDS.convert(1, timeUnit);
        if (offset == 0 && mOnCountDownStartListener != null) {
            mOnCountDownStartListener.onStart();
        }
        if (TextUtils.isEmpty(mCountDownText)) {
            mCountDownText = getText().toString();
        }
        mCountDownTimer = new CountDownTimer(millisInFuture, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                long count = isCountDown ? millisUntilFinished : (millisInFuture - millisUntilFinished + offset);
                long l = timeUnit.convert(count, TimeUnit.MILLISECONDS);
                String showTime;
                if (mShowFormatTime) {
                    showTime = generateTime(count, mIsShowCompleteTv);
                } else {
                    showTime = String.valueOf(l);
                }
                if (mOnCountDownTickListener != null) {
                    mOnCountDownTickListener.onTick(l, showTime, CountDownTextView.this);
                } else {
                    setText(String.format(mCountDownText, showTime));
                }
            }

            @Override
            public void onFinish() {
                setEnabled(true);
                mCountDownTimer = null;
                if (!TextUtils.isEmpty(mNormalText)) {
                    setText(mNormalText);
                }
                if (mOnCountDownFinishListener != null) {
                    mOnCountDownFinishListener.onFinish();
                }
            }
        };
        mCountDownTimer.start();
    }

    /**
     * 倒计时期间，点击事件是否响应
     *
     * @param clickable 是否响应
     */
    public CountDownTextView setCountDownClickable(boolean clickable) {
        mClickable = clickable;
        return this;
    }

    public CountDownTextView setOnCountDownStartListener(OnCountDownStartListener onCountDownStartListener) {
        mOnCountDownStartListener = onCountDownStartListener;
        return this;
    }

    public CountDownTextView setOnCountDownTickListener(OnCountDownTickListener onCountDownTickListener) {
        mOnCountDownTickListener = onCountDownTickListener;
        return this;
    }

    public CountDownTextView setOnCountDownFinishListener(OnCountDownFinishListener onCountDownFinishListener) {
        mOnCountDownFinishListener = onCountDownFinishListener;
        return this;
    }

    public CountDownTextView setBeforeIndex(int beforeIndex) {
        this.mBeforeIndex = beforeIndex;
        return this;
    }

    /**
     * 设置是否显示精确到天的完整字段
     */
    public CountDownTextView setIsShowComplete(boolean isShowComplete) {
        this.mIsShowCompleteTv = isShowComplete;
        return this;
    }

    public int[][] getTimeIndexes() {
        return mTimeUnits;
    }

    @Override
    public void onClick(View v) {
        if (mCountDownTimer != null && !mClickable) {
            return;
        }
        if (mOnClickListener != null) {
            mOnClickListener.onClick(v);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mOnClickListener = l;
        super.setOnClickListener(this);
    }

    public interface OnCountDownStartListener {
        void onStart();
    }

    public interface OnCountDownTickListener {
        /**
         * 每秒自己刷新textView
         *
         * @param untilFinished 剩余的时间
         * @param showTime      可以直接使用的显示时间
         * @param tv            控件
         */
        void onTick(long untilFinished, String showTime, CountDownTextView tv);
    }

    public interface OnCountDownFinishListener {
        void onFinish();
    }

    /**
     * 将毫秒转时分秒
     */
    @SuppressLint("DefaultLocale")
    public String generateTime(long time, boolean isShowComplete) {
        String format;
        mTimeUnits = null;
        long totalSeconds = (time / 1000);
        int seconds = (int) (totalSeconds % 60);
        int minutes = (int) ((totalSeconds / 60) % 60);
        int hours = (int) ((totalSeconds / 3600) % 24);
        int days = (int) (totalSeconds / (3600 * 24));
        if (!isShowComplete) {
            if (days > 0) {
                format = String.format("%01d天%02d时%02d分%02d秒", days, hours, minutes, seconds);
                mTimeUnits = new int[4][2];
                mTimeUnits[0][0] = mBeforeIndex;
                mTimeUnits[0][1] = String.format("%01d", days).length();
                mTimeUnits[1][0] = mBeforeIndex + String.format("%01d", days).length() + 1;
                mTimeUnits[1][1] = String.format("%02d", hours).length();
                mTimeUnits[2][0] = mBeforeIndex + String.format("%01d", days).length() + String.format("%02d", hours).length() + 2;
                mTimeUnits[2][1] = String.format("%02d", minutes).length();
                mTimeUnits[3][0] = mBeforeIndex + String.format("%01d", days).length() + String.format("%02d", hours).length() + String.format("%02d", minutes).length() + 3;
                mTimeUnits[3][1] = String.format("%02d", seconds).length();
            } else if (hours > 0) {
                format = String.format("%02d时%02d分%02d秒", hours, minutes, seconds);
                mTimeUnits = new int[3][2];
                mTimeUnits[0][0] = mBeforeIndex;
                mTimeUnits[0][1] = String.format("%02d", hours).length();
                mTimeUnits[1][0] = mBeforeIndex + String.format("%02d", hours).length() + 1;
                mTimeUnits[1][1] = String.format("%02d", minutes).length();
                mTimeUnits[2][0] = mBeforeIndex + String.format("%02d", hours).length() + String.format("%02d", minutes).length() + 2;
                mTimeUnits[2][1] = String.format("%02d", seconds).length();
            } else if (minutes > 0) {
                format = String.format("%02d分%02d秒", minutes, seconds);
                mTimeUnits = new int[2][2];
                mTimeUnits[0][0] = mBeforeIndex;
                mTimeUnits[0][1] = String.format("%02d", minutes).length();
                mTimeUnits[1][0] = mBeforeIndex + String.format("%02d", minutes).length() + 1;
                mTimeUnits[1][1] = String.format("%02d", seconds).length();
            } else {
                format = String.format("%2d秒", seconds);
                mTimeUnits = new int[1][2];
                mTimeUnits[0][0] = mBeforeIndex;
                mTimeUnits[0][1] = String.format("%02d", seconds).length();
            }
            return format;
        } else {
            format = String.format("%01d 天 %01d 时 %01d 分 %01d 秒", days, hours, minutes, seconds);
            mTimeUnits = new int[4][2];
            mTimeUnits[0][0] = mBeforeIndex;
            mTimeUnits[0][1] = String.format("%01d", days).length();
            mTimeUnits[1][0] = mBeforeIndex + String.format("%01d", days).length() + 3;
            mTimeUnits[1][1] = String.format("%01d", hours).length();
            mTimeUnits[2][0] = mBeforeIndex + String.format("%01d", days).length() + String.format("%01d", hours).length() + 6;
            mTimeUnits[2][1] = String.format("%01d", minutes).length();
            mTimeUnits[3][0] = mBeforeIndex + String.format("%01d", days).length() + String.format("%01d", hours).length() + String.format("%01d", minutes).length() + 9;
            mTimeUnits[3][1] = String.format("%01d", seconds).length();
            return format;
        }
    }
}
