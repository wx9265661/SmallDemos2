package com.zhanghaochen.smalldemos.widget.floatview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.function.recyclerviews.CommonAdapter;
import com.zhanghaochen.smalldemos.function.recyclerviews.MaxHeightRecyclerView;
import com.zhanghaochen.smalldemos.function.recyclerviews.ViewHolder;
import com.zhanghaochen.smalldemos.function.recyclerviews.itemdecoration.Divider;
import com.zhanghaochen.smalldemos.function.recyclerviews.itemdecoration.LinearItemDecoration;
import com.zhanghaochen.smalldemos.framework.widgets.floatview.HtscFloatBase;
import com.zhanghaochen.smalldemos.utils.GlobalParams;
import com.zhanghaochen.smalldemos.utils.SysUtils;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author created by zhanghaochen
 * @date 2019-07-16 1:51 PM
 * 描述：用于实时监控一些变量
 */
public class MonitorFloatView extends HtscFloatBase {
    private float mStartX, mStartY;
    private MaxHeightRecyclerView mRecyclerView;
    private MyAdatper mAdapter;

    private OnRemoveFromWindow mRemoveListener;

    public interface OnRemoveFromWindow {
        void onRemove();
    }

    public MonitorFloatView(OnRemoveFromWindow listener) {
        super();
        mRemoveListener = listener;
    }

    @Override
    public void create() {
        super.create();
        mViewMode = WRAP_CONTENT_TOUCHABLE;

        mGravity = Gravity.RIGHT | Gravity.TOP;

        inflate(R.layout.monitor_float_layout);

        initViews();

        updateDatas();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        mRecyclerView = findView(R.id.monitor_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainView.getContext()) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception ignored) {
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setMaxHeight(SysUtils.getScreenHeight(mMainView.getContext()) / 2);
        mAdapter = new MyAdatper(mMainView.getContext());
        Divider linearDivider = new Divider.Builder().size(1).color(ContextCompat.getColor(GlobalParams.mApplication, R.color.white)).build();
        mRecyclerView.addItemDecoration(new LinearItemDecoration(linearDivider));
        mRecyclerView.setAdapter(mAdapter);

        final TextView showHide = findView(R.id.show_hide);
        showHide.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                float x;
                float y;
                x = event.getRawX();
                y = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = x;
                        mStartY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!(Math.abs(x - mStartX) < 10 && Math.abs(y - mStartY) < 10)) {
                            updateViewPosition((int) x + SysUtils.convertDpToPixel(40), (int) y - SysUtils.convertDpToPixel(50));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(x - mStartX) < 10 && Math.abs(y - mStartY) < 10) {
                            // 单击的事件
                            mRecyclerView.setVisibility(mRecyclerView.isShown() ? View.GONE : View.VISIBLE);
                            showHide.setText(mRecyclerView.getVisibility() == View.VISIBLE ? "收起" : "展开");
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        findView(R.id.m_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove();
                if (mRemoveListener != null) {
                    mRemoveListener.onRemove();
                }
            }
        });
    }

    private void updateViewPosition(int x, int y) {
        // 更新浮动窗口位置参数
        if (mWindowManager == null || mWindowLayoutParams == null || mMainView == null) {
            return;
        }
        mWindowLayoutParams.x = SysUtils.getScreenWidth(GlobalParams.mApplication) - x;
        mWindowLayoutParams.y = y;
        // 刷新显示
        mWindowManager.updateViewLayout(mMainView, mWindowLayoutParams);
    }

    /**
     * 每隔3s进行定时刷新
     */
    private void updateDatas() {
        ScheduledExecutorService timer = new ScheduledThreadPoolExecutor(1);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (mRecyclerView != null) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mAdapter != null) {
                                mAdapter.notifyDataChanged(IMonitorFloat.sBinding.monitorObjs());
                            }
                        }
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 3000, TimeUnit.MILLISECONDS);
    }

    class MyAdatper extends CommonAdapter<MonitorBean> {
        public MyAdatper(Context context) {
            super(context, R.layout.monitor_item, new ArrayList<MonitorBean>());
        }

        @Override
        public void convert(ViewHolder holder, MonitorBean monitorBean) {
            holder.<TextView>getView(R.id.m_label).setText(SysUtils.getSafeString(monitorBean.label));
            holder.<TextView>getView(R.id.m_value).setText(SysUtils.getSafeString(monitorBean.value));
        }

        public void notifyDataChanged(ArrayList<MonitorBean> sourceData) {
            if (sourceData != null && sourceData.size() > 0) {
                mDatas.clear();
                mDatas.addAll(sourceData);
                notifyDataSetChanged();
            } else {
                mDatas.clear();
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAddWindowFailed(Exception e) {

    }

    public static class MonitorBean {
        public String label;
        public String value;
    }

    /**
     * 提供外部实现的接口
     */
    public static class IMonitorFloat {
        public static Binding sBinding;

        public interface Binding {
            ArrayList<MonitorBean> monitorObjs();
        }
    }
}
