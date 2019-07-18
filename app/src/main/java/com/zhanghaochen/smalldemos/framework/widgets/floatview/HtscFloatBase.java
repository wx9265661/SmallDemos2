package com.zhanghaochen.smalldemos.framework.widgets.floatview;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.zhanghaochen.smalldemos.utils.GlobalParams;

/**
 * 基于window的悬浮窗基类
 */
public abstract class HtscFloatBase {
    protected static String TAG = "HtscFloatBase";

    public static final int FULLSCREEN_TOUCHABLE = 1;
    public static final int FULLSCREEN_NOT_TOUCHABLE = 2;
    public static final int WRAP_CONTENT_TOUCHABLE = 3;
    public static final int WRAP_CONTENT_NOT_TOUCHABLE = 4;

    public WindowManager mWindowManager;
    public WindowManager.LayoutParams mWindowLayoutParams;

    public View mMainView;

    /**
     * 是否已经添加过悬浮窗
     */
    private boolean mIsAdded = false;

    public int mGravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
    public int mViewMode = WRAP_CONTENT_TOUCHABLE;
    private boolean mRequestFocus = false;
    private boolean mInvisibleNeed = false;

    public HtscFloatBase() {
        create();
    }

    @CallSuper
    public void create() {
        mWindowManager = (WindowManager) GlobalParams.mApplication.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 悬浮窗是否需要获取焦点，通常获取焦点后，悬浮窗可以和软键盘发生交互，被覆盖的应用失去焦点
     *
     * @param requestFocus boolean
     */
    public void setRequestFocus(boolean requestFocus) {
        this.mRequestFocus = requestFocus;
    }

    @CallSuper
    public synchronized void show() {
        show(0, 0);
    }

    @CallSuper
    public synchronized void show(int offSetX, int offSetY) {
        // 检查悬浮窗权限
        if (!FloatWindowParamManager.checkPermission(GlobalParams.mApplication)) {
            FloatWindowParamManager.tryJumpToPermissionPage(GlobalParams.mApplication);
            return;
        }
        if (mMainView == null) {
            throw new IllegalStateException("FloatView Has Not Create!");
        }

        if (mIsAdded) {
            mMainView.setVisibility(View.VISIBLE);
            return;
        }
        try {
            getLayoutParam(mViewMode, offSetX, offSetY);

            mMainView.setVisibility(View.VISIBLE);

            mWindowManager.addView(mMainView, mWindowLayoutParams);
            mIsAdded = true;
        } catch (Exception e) {
            Log.e(TAG, "添加悬浮窗失败！！！！！！请检查悬浮窗权限");
            onAddWindowFailed(e);
        }
    }

    @CallSuper
    public void hide() {
        if (mMainView != null) {
            mMainView.setVisibility(View.INVISIBLE);
        }
    }

    @CallSuper
    public void gone() {
        if (mMainView != null) {
            mMainView.setVisibility(View.GONE);
        }
    }

    @CallSuper
    public void remove() {
        if (mMainView != null && mWindowManager != null) {
            if (mMainView.isAttachedToWindow()) {
                mWindowManager.removeView(mMainView);
                mMainView = null;
            }
        }
    }

    @CallSuper
    public final View inflate(@LayoutRes int layout) {
        mMainView = View.inflate(GlobalParams.mApplication, layout, null);
        return mMainView;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findView(@IdRes int id) {
        if (mMainView != null) {
            return (T) mMainView.findViewById(id);
        }
        return null;
    }

    /**
     * 获取可见性
     *
     * @return
     */
    public boolean getVisibility() {
        if (mMainView != null && mMainView.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 改变可见性
     */
    public void toggleVisibility() {
        if (mMainView != null) {
            if (getVisibility()) {
                if (mInvisibleNeed) {
                    hide();
                } else {
                    gone();
                }
            } else {
                show();
            }
        }
    }

    public abstract void onAddWindowFailed(Exception e);

    /**
     * 获取悬浮窗LayoutParam
     *
     * @param mode
     */
    public void getLayoutParam(int mode, int x, int y) {
        switch (mode) {
            case FULLSCREEN_TOUCHABLE:
                mWindowLayoutParams = FloatWindowParamManager.getFloatLayoutParam(true, true, x, y);
                break;

            case FULLSCREEN_NOT_TOUCHABLE:
                mWindowLayoutParams = FloatWindowParamManager.getFloatLayoutParam(true, false, x, y);
                break;

            case WRAP_CONTENT_NOT_TOUCHABLE:
                mWindowLayoutParams = FloatWindowParamManager.getFloatLayoutParam(false, false, x, y);
                break;

            case WRAP_CONTENT_TOUCHABLE:
                mWindowLayoutParams = FloatWindowParamManager.getFloatLayoutParam(false, true, x, y);
                break;
            default:
                mWindowLayoutParams = FloatWindowParamManager.getFloatLayoutParam(false, true, x, y);
                break;
        }

        if (mRequestFocus) {
            mWindowLayoutParams.flags = mWindowLayoutParams.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }

        mWindowLayoutParams.gravity = mGravity;
    }
}
