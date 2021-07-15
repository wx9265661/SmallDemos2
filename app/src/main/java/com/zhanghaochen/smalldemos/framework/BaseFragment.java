package com.zhanghaochen.smalldemos.framework;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.Objects;

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

    protected void myToast(final CharSequence msg) {
        try {
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ignored) {}
    }
}
