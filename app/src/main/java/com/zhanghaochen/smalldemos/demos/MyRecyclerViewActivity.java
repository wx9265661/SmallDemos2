package com.zhanghaochen.smalldemos.demos;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.customer.recyclerviews.itemdecoration.Divider;
import com.zhanghaochen.smalldemos.customer.recyclerviews.itemdecoration.LinearItemDecoration;
import com.zhanghaochen.smalldemos.framework.BaseActivity;
import com.zhanghaochen.smalldemos.views.MyDemoHolder01;

import java.util.ArrayList;
import java.util.Arrays;

public class MyRecyclerViewActivity extends BaseActivity {

    private SwipeRefreshLayout mRefreshLayout;

    private CustomerAdapter mAdapter;

    private RecyclerView mRecyclerView;

    /**
     * 明星投顾
     */
    private static final int HOLDER_STAR = 1;

    /**
     * 本期嘉宾
     */
    private static final int HOLDER_GUESTER = 2;

    /**
     * 首席行业解读
     */
    private static final int HOLDER_CHIEF = 3;

    /**
     * 热门观点
     */
    private static final int HOLDER_HOT_POINT = 4;

    /**
     * 记录页面组成模块
     */
    private ArrayList<Integer> mItemArray = new ArrayList<>();

    private ArrayList<String> mDatas = new ArrayList<>();

    @Override
    protected void handleMessage(Message message) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recycler);

        initViews();
        initFackDatas();

        mAdapter.notifyDataSetChanged();
    }

    private void initViews() {
        mItemArray.clear();
        mItemArray.addAll(Arrays.asList(HOLDER_STAR, HOLDER_GUESTER, HOLDER_CHIEF, HOLDER_HOT_POINT));

        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRecyclerView = findViewById(R.id.coor_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        Divider linearDivider = new Divider.Builder().size(8).color(ContextCompat.getColor(this, R.color.circle_board_dot_shadow)).build();
        mRecyclerView.addItemDecoration(new LinearItemDecoration(linearDivider));
        mAdapter = new CustomerAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setEnabled(false);
    }

    private void initFackDatas() {
        for (int i = 0; i < 3; i++) {
            mDatas.add("这是第" + i + "条数据");
        }
    }

    private class CustomerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = null;
            switch (viewType) {
                case HOLDER_STAR:
                    vh = MyDemoHolder01.newInstance(parent);
                    break;
                case HOLDER_GUESTER:
                    vh = MyDemoHolder01.newInstance(parent);
                    break;
                case HOLDER_CHIEF:
                    vh = MyDemoHolder01.newInstance(parent);
                    break;
                case HOLDER_HOT_POINT:
                    vh = MyDemoHolder01.newInstance(parent);
                    break;
                default:
                    vh = MyDemoHolder01.newInstance(parent);
                    break;
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case HOLDER_STAR:
                    ((MyDemoHolder01) holder).onBindData(mDatas);
                    break;
                case HOLDER_GUESTER:
                    ((MyDemoHolder01) holder).onBindData(mDatas);
                    break;
                case HOLDER_CHIEF:
                    ((MyDemoHolder01) holder).onBindData(mDatas);
                    break;
                case HOLDER_HOT_POINT:
                    ((MyDemoHolder01) holder).onBindData(mDatas);
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mItemArray.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mItemArray.get(position);
        }
    }
}
