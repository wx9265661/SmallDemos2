/* Copyright Statement:
 *
 * This software/firmware and related documentation ("Huatai Software) are
 * protected under relevant copyright laws. The information contained herein is
 * confidential and proprietary to Huatai Securities Co.,Ltd. (HUATAI) and/or its licensors. Without
 * the prior written permission of Huatai Securities Co.,Ltd. and/or its licensors, any
 * reproduction, modification, use or disclosure of Huatai Software and
 * information contained herein, in whole or in part, shall be strictly
 * prohibited.
 *
 * Huatai Securities Co.,Ltd. (C) 2018. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("HUATAI SOFTWARE)
 * RECEIVED FROM HUATAI AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 * ON AN "AS-IS" BASIS ONLY. HUATAI EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NONINFRINGEMENT. NEITHER DOES HUATAI PROVIDE ANY WARRANTY WHATSOEVER WITH
 * RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 * INCORPORATED IN, OR SUPPLIED WITH THE HUATAI SOFTWARE AND RECEIVER AGREES
 * TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 * RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 * OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN HUATAI
 * SOFTWARE. HUATAI SHALL ALSO NOT BE RESPONSIBLE FOR ANY HUATAI SOFTWARE
 * RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND HUATAI'S
 * ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE HUATAI SOFTWARE
 * RELEASED HEREUNDER WILL BE, AT HUATAI'S OPTION, TO REVISE OR REPLACE THE
 * HUATAI SOFTWAREAT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 * CHARGE PAID BY RECEIVER TO HUATAI FOR SUCH HUATAI SOFTWAREAT ISSUE.
 *
 * The following software/firmware and/or related documentation ("Huatai
 * Software") have been modified by Huatai Securities Co.,Ltd. All revisions are subject to
 * any receiver's applicable license agreements with Huatai Securities Co.,Ltd.
 */
package com.zhanghaochen.smalldemos.function.indicator;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.function.indicator.abs.CommonNavigatorAdapter;
import com.zhanghaochen.smalldemos.function.indicator.abs.IMeasurablePagerTitleView;
import com.zhanghaochen.smalldemos.function.indicator.abs.IPagerIndicator;
import com.zhanghaochen.smalldemos.function.indicator.abs.IPagerNavigator;
import com.zhanghaochen.smalldemos.function.indicator.abs.IPagerTitleView;
import com.zhanghaochen.smalldemos.function.indicator.support.NavigatorHelper;
import com.zhanghaochen.smalldemos.function.indicator.support.PositionData;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用的ViewPager指示器，包含PagerTitle和PagerIndicator
 */
public class CommonNavigator extends FrameLayout implements IPagerNavigator, NavigatorHelper.OnNavigatorScrollListener {
    private HorizontalScrollView mScrollView;
    private LinearLayout mTitleContainer;
    private LinearLayout mIndicatorContainer;
    private IPagerIndicator mIndicator;

    private CommonNavigatorAdapter mAdapter;
    private NavigatorHelper mNavigatorHelper;

    /**
     * 提供给外部的参数配置
     */
    /****************************************************/
    private boolean mAdjustMode;   // 自适应模式，为true表示title均分宽度，适用于数目固定的、少量的title
    private boolean mEnablePivotScroll; // 启动中心点滚动
    private float mScrollPivotX = 0.5f; // 滚动中心点 0.0f - 1.0f
    private boolean mSmoothScroll = true;   // 是否平滑滚动，适用于 !mAdjustMode && !mFollowTouch
    private boolean mFollowTouch = true;    // 是否手指跟随滚动
    private int mRightPadding;
    private int mLeftPadding;
    private boolean mIndicatorOnTop;    // 指示器是否在title上方，默认为下方
    private boolean mSkimOver;
    private boolean mReselectWhenLayout = true;
    private boolean shouldShowDivider = false;
    private int dividerMode;
    private Drawable dividerDrawable;
    private int dividerPadding;
    /****************************************************/

    // 保存每个title的位置信息，为扩展indicator提供保障
    private List<PositionData> mPositionDataList = new ArrayList<>();

    private DataSetObserver mObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            mNavigatorHelper.setTotalCount(mAdapter.getCount());    // 如果使用helper，应始终保证helper中的totalCount为最新
            init();
        }

        @Override
        public void onInvalidated() {
            // 没什么用，暂不做处理
        }
    };

    public CommonNavigator(Context context) {
        super(context);
        mNavigatorHelper = new NavigatorHelper();
        mNavigatorHelper.setNavigatorScrollListener(this);
    }

    @Override
    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public boolean isAdjustMode() {
        return mAdjustMode;
    }

    public void setAdjustMode(boolean is) {
        mAdjustMode = is;
    }

    public CommonNavigatorAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(CommonNavigatorAdapter adapter) {
        if (mAdapter == adapter) {
            return;
        }
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mObserver);
            adapter.notifyDataSetChanged();
        } else {
            mNavigatorHelper.setTotalCount(0);
            init();
        }
    }

    private void init() {
        removeAllViews();

        View root;
        if (mAdjustMode) {
            root = LayoutInflater.from(getContext()).inflate(R.layout.pager_navigator_layout_no_scroll, this);
        } else {
            root = LayoutInflater.from(getContext()).inflate(R.layout.pager_navigator_layout, this);
        }

        mScrollView = (HorizontalScrollView) root.findViewById(R.id.scroll_view);   // mAdjustMode为true时，mScrollView为null

        mTitleContainer = (LinearLayout) root.findViewById(R.id.title_container);
        mTitleContainer.setPadding(mLeftPadding, 0, mRightPadding, 0);
        if (shouldShowDivider) {
            mTitleContainer.setShowDividers(dividerMode);
            mTitleContainer.setDividerDrawable(dividerDrawable);
            mTitleContainer.setDividerPadding(dividerPadding);
        }

        mIndicatorContainer = (LinearLayout) root.findViewById(R.id.indicator_container);
        if (mIndicatorOnTop) {
            mIndicatorContainer.getParent().bringChildToFront(mIndicatorContainer);
        }

        initTitlesAndIndicator();
    }

    /**
     * 初始化title和indicator
     */
    private void initTitlesAndIndicator() {
        for (int i = 0, j = mNavigatorHelper.getTotalCount(); i < j; i++) {
            IPagerTitleView v = mAdapter.getTitleView(getContext(), i);
            if (v instanceof View) {
                View view = (View) v;
                LinearLayout.LayoutParams lp;
                if (mAdjustMode) {
                    lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.weight = mAdapter.getTitleWeight(getContext(), i);
                } else {
                    lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                }
                mTitleContainer.addView(view, lp);
            }
        }
        if (mAdapter != null) {
            mIndicator = mAdapter.getIndicator(getContext());
            if (mIndicator instanceof View) {
                LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mIndicatorContainer.addView((View) mIndicator, lp);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mAdapter != null) {
            preparePositionData();
            if (mIndicator != null) {
                mIndicator.onPositionDataProvide(mPositionDataList);
            }
            if (mReselectWhenLayout && mNavigatorHelper.getScrollState() == ViewPager.SCROLL_STATE_IDLE) {
                onPageSelected(mNavigatorHelper.getCurrentIndex());
                onPageScrolled(mNavigatorHelper.getCurrentIndex(), 0.0f, 0);
            }
        }
    }

    /**
     * 获取title的位置信息，为打造不同的指示器、各种效果提供可能
     */
    private void preparePositionData() {
        mPositionDataList.clear();
        for (int i = 0, j = mNavigatorHelper.getTotalCount(); i < j; i++) {
            View v = mTitleContainer.getChildAt(i);
            PositionData data = new PositionData();
            data.mLeft = v.getLeft();
            data.mTop = v.getTop();
            data.mRight = v.getRight();
            data.mBottom = v.getBottom();
            if (v instanceof IMeasurablePagerTitleView) {
                IMeasurablePagerTitleView view = (IMeasurablePagerTitleView) v;
                data.mContentLeft = view.getContentLeft();
                data.mContentTop = view.getContentTop();
                data.mContentRight = view.getContentRight();
                data.mContentBottom = view.getContentBottom();
            } else {
                data.mContentLeft = data.mLeft;
                data.mContentTop = data.mTop;
                data.mContentRight = data.mRight;
                data.mContentBottom = data.mBottom;
            }
            mPositionDataList.add(data);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mAdapter != null) {

            mNavigatorHelper.onPageScrolled(position, positionOffset, positionOffsetPixels);
            if (mIndicator != null) {
                mIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            // 手指跟随滚动
            if (mScrollView != null && mPositionDataList.size() > 0) {
                if (mFollowTouch) {
                    int currentPosition = Math.min(mPositionDataList.size() - 1, position);
                    int nextPosition = Math.min(mPositionDataList.size() - 1, position + 1);
                    PositionData current = mPositionDataList.get(currentPosition);
                    PositionData next = mPositionDataList.get(nextPosition);
                    float scrollTo = current.horizontalCenter() - mScrollView.getWidth() * mScrollPivotX;
                    float nextScrollTo = next.horizontalCenter() - mScrollView.getWidth() * mScrollPivotX;
                    mScrollView.scrollTo((int) (scrollTo + (nextScrollTo - scrollTo) * positionOffset), 0);
                } else if (!mEnablePivotScroll) {
                    // TODO 实现待选中项完全显示出来
                }
            }
        }
    }

    public float getScrollPivotX() {
        return mScrollPivotX;
    }

    public void setScrollPivotX(float scrollPivotX) {
        mScrollPivotX = scrollPivotX;
    }

    @Override
    public void onPageSelected(int position) {
        if (mAdapter != null) {
            mNavigatorHelper.onPageSelected(position);
            if (mIndicator != null) {
                mIndicator.onPageSelected(position);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mAdapter != null) {
            mNavigatorHelper.onPageScrollStateChanged(state);
            if (mIndicator != null) {
                mIndicator.onPageScrollStateChanged(state);
            }
        }
    }

    @Override
    public void onAttachToMagicIndicator() {
        init(); // 将初始化延迟到这里
    }

    @Override
    public void onDetachFromMagicIndicator() {
    }

    public IPagerIndicator getPagerIndicator() {
        return mIndicator;
    }

    public boolean isEnablePivotScroll() {
        return mEnablePivotScroll;
    }

    public void setEnablePivotScroll(boolean is) {
        mEnablePivotScroll = is;
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        if (mTitleContainer == null) {
            return;
        }
        View v = mTitleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onEnter(index, totalCount, enterPercent, leftToRight);
        }
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        if (mTitleContainer == null) {
            return;
        }
        View v = mTitleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onLeave(index, totalCount, leavePercent, leftToRight);
        }
    }

    public boolean isSmoothScroll() {
        return mSmoothScroll;
    }

    public void setSmoothScroll(boolean smoothScroll) {
        mSmoothScroll = smoothScroll;
    }

    public boolean isFollowTouch() {
        return mFollowTouch;
    }

    public void setFollowTouch(boolean followTouch) {
        mFollowTouch = followTouch;
    }

    public boolean isSkimOver() {
        return mSkimOver;
    }

    public void setSkimOver(boolean skimOver) {
        mSkimOver = skimOver;
        mNavigatorHelper.setSkimOver(skimOver);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        if (mTitleContainer == null) {
            return;
        }
        View v = mTitleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onSelected(index, totalCount);
        }
        if (!mAdjustMode && !mFollowTouch && mScrollView != null && mPositionDataList.size() > 0) {
            int currentIndex = Math.min(mPositionDataList.size() - 1, index);
            PositionData current = mPositionDataList.get(currentIndex);
            if (mEnablePivotScroll) {
                float scrollTo = current.horizontalCenter() - mScrollView.getWidth() * mScrollPivotX;
                if (mSmoothScroll) {
                    mScrollView.smoothScrollTo((int) (scrollTo), 0);
                } else {
                    mScrollView.scrollTo((int) (scrollTo), 0);
                }
            } else {
                // 如果当前项被部分遮挡，则滚动显示完全
                if (mScrollView.getScrollX() > current.mLeft) {
                    if (mSmoothScroll) {
                        mScrollView.smoothScrollTo(current.mLeft, 0);
                    } else {
                        mScrollView.scrollTo(current.mLeft, 0);
                    }
                } else if (mScrollView.getScrollX() + getWidth() < current.mRight) {
                    if (mSmoothScroll) {
                        mScrollView.smoothScrollTo(current.mRight - getWidth(), 0);
                    } else {
                        mScrollView.scrollTo(current.mRight - getWidth(), 0);
                    }
                }
            }
        }
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        if (mTitleContainer == null) {
            return;
        }
        View v = mTitleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onDeselected(index, totalCount);
        }
    }

    public IPagerTitleView getPagerTitleView(int index) {
        if (mTitleContainer == null) {
            return null;
        }
        return (IPagerTitleView) mTitleContainer.getChildAt(index);
    }

    public int getRightPadding() {
        return mRightPadding;
    }

    public void setRightPadding(int rightPadding) {
        mRightPadding = rightPadding;
    }

    public int getLeftPadding() {
        return mLeftPadding;
    }

    public void setLeftPadding(int leftPadding) {
        mLeftPadding = leftPadding;
    }

    public boolean isIndicatorOnTop() {
        return mIndicatorOnTop;
    }

    public void setIndicatorOnTop(boolean indicatorOnTop) {
        mIndicatorOnTop = indicatorOnTop;
    }

    public boolean isReselectWhenLayout() {
        return mReselectWhenLayout;
    }

    public void setReselectWhenLayout(boolean reselectWhenLayout) {
        mReselectWhenLayout = reselectWhenLayout;
    }

    public void showDivider(int dividerMode, Drawable dividerDrawable, int dividerPadding) {
        shouldShowDivider = true;
        this.dividerMode = dividerMode;
        this.dividerDrawable = dividerDrawable;
        this.dividerPadding = dividerPadding;
    }

    public void hideDivider() {
        shouldShowDivider = false;
    }
}
