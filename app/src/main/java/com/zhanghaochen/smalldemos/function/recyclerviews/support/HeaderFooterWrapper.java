package com.zhanghaochen.smalldemos.function.recyclerviews.support;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.zhanghaochen.smalldemos.function.recyclerviews.ViewHolder;

/**
 * 用于给RecyclerView加入Header和Footer<br>
 * <br>
 * 创建时间：2016-08-08 10:13<br>
 * 初始作者：朱凌峰<br>
 * 修改历史：<br>
 * ·[2016-08-08 10:13 朱凌峰] 初始版本<br>
 */
public class HeaderFooterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;
    public static final int BASE_ITEM_TYPE_FOOTER_NO_STOCK_ACCOUNT = 150000;

    //SparseArrayCompat存储的顺序不按照插入顺序排序，需要根据插入的int型key值排序
    private SparseArrayCompat<View> headerViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> footViews = new SparseArrayCompat<>();

    private RecyclerView.Adapter innerAdapter;
    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            innerAdapter.notifyDataSetChanged();
        }
    };

    public HeaderFooterWrapper(RecyclerView.Adapter adapter) {
        innerAdapter = adapter;
        registerAdapterDataObserver(observer);
    }

    public HeaderFooterWrapper(RecyclerView.Adapter adapter, boolean hasStableIds) {
        innerAdapter = adapter;
        setHasStableIds(hasStableIds);
        registerAdapterDataObserver(observer);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (headerViews.get(viewType) != null) {
            return ViewHolder.get(parent.getContext(), headerViews.get(viewType));
        } else if (footViews.get(viewType) != null) {
            return ViewHolder.get(parent.getContext(), footViews.get(viewType));
        }
        return innerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return headerViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return footViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return innerAdapter.getItemViewType(position - getHeadersCount());
    }

    private int getRealItemCount() {
        return innerAdapter.getItemCount();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderViewPos(position)) {
            return;
        }
        if (isFooterViewPos(position)) {
            return;
        }
        innerAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }

    @Override
    public int getItemCount() {
        return getHeadersCount() + getRealItemCount() + getFootersCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(innerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                if (isHeaderViewPos(position)) {
                    return layoutManager.getSpanCount();
                } else if (isFooterViewPos(position)) {
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null) {
                    return oldLookup.getSpanSize(position);
                }
                return 1;
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        innerAdapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            WrapperUtils.setFullSpan(holder);
        }
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }


    public void addHeaderView(View view) {
        headerViews.put(headerViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFootView(View view) {
        footViews.put(footViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public void addFootView(View view, int type) {
        footViews.put(footViews.size() + type, view);
    }

    public boolean removeHeaderView(View view) {
        int index = headerViews.indexOfValue(view);
        if (index >= 0) {
            headerViews.removeAt(index);
            return true;
        }
        return false;
    }

    public boolean removeFootView(View view) {
        int index = footViews.indexOfValue(view);
        if (index >= 0) {
            footViews.removeAt(index);
            return true;
        }
        return false;
    }

    public boolean hasView(View view) {
        return headerViews.indexOfValue(view) >= 0 || footViews.indexOfValue(view) >= 0;
    }

    public int getHeadersCount() {
        return headerViews.size();
    }

    public int getFootersCount() {
        return footViews.size();
    }
}