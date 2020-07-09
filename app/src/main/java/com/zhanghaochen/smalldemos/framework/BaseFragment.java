package com.zhanghaochen.smalldemos.framework;

import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

public abstract class BaseFragment extends Fragment {

    protected MyHandler mHandler = new MyHandler(this);

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected abstract void handleMessage(Message message);

    protected static class MyHandler extends Handler {

        private final WeakReference<BaseFragment> mFragment;

        public MyHandler(BaseFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseFragment fragment = mFragment.get();
            if (fragment != null && fragment.isAdded()) {
                fragment.handleMessage(msg);
            }
            super.handleMessage(msg);
        }
    }
}
