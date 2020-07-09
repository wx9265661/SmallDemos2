package com.zhanghaochen.smalldemos.framework;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public abstract class BaseActivity extends AppCompatActivity {

    protected MyHandler mHandler = new MyHandler(this);

    protected abstract void handleMessage(Message message);

    protected static class MyHandler extends Handler {

        private final WeakReference<BaseActivity> mActivity;

        public MyHandler(BaseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity activity = mActivity.get();
            if (BaseActivity.isValidContext(activity)) {
                activity.handleMessage(msg);
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 用于判读activity context是否已经被销毁
     *
     * @param context
     * @return
     */
    public static boolean isValidContext(Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            boolean isDestroyed = Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN && activity.isDestroyed();
            return !(isDestroyed || activity.isFinishing());
        }
        return true;
    }
}
