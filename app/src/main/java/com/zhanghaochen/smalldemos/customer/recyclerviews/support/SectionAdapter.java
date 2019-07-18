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
package com.zhanghaochen.smalldemos.customer.recyclerviews.support;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.zhanghaochen.smalldemos.customer.recyclerviews.MultiItemCommonAdapter;
import com.zhanghaochen.smalldemos.customer.recyclerviews.MultiItemTypeSupport;
import com.zhanghaochen.smalldemos.customer.recyclerviews.ViewHolder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 分段RecyclerView的adapter，支持在同一列表中显示不同类型的item，同时根据item类型进行界面初始化<br>
 * 将header的layout、textview和数据进行绑定<br>
 * <br>
 * 创建时间：2016-05-30 16:33<br>
 * 初始作者：朱凌峰<br>
 * 修改历史：<br>
 * ·[2016-05-30 16:33 朱凌峰] 初始版本<br>
 */
public abstract class SectionAdapter<T> extends MultiItemCommonAdapter<T> {
    private static final int TYPE_SECTION = 0;
    private SectionSupport mSectionSupport;
    private LinkedHashMap<String, Integer> mSections;
    final RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            findSections();
        }
    };
    private MultiItemTypeSupport<T> headerItemTypeSupport;

    public SectionAdapter(Context context, int layoutId, List<T> datas, SectionSupport sectionSupport) {
        this(context, layoutId, null, datas, sectionSupport);
    }

    public SectionAdapter(Context context, MultiItemTypeSupport multiItemTypeSupport, List<T> datas, SectionSupport sectionSupport) {
        this(context, -1, multiItemTypeSupport, datas, sectionSupport);
    }

    public SectionAdapter(Context context, int layoutId, MultiItemTypeSupport multiItemTypeSupport, List<T> datas, SectionSupport sectionSupport) {
        super(context, datas, null);
        mLayoutId = layoutId;
        initMultiItemTypeSupport(layoutId, multiItemTypeSupport);
        mMultiItemTypeSupport = headerItemTypeSupport;
        mSectionSupport = sectionSupport;
        mSections = new LinkedHashMap<>();
        findSections();
        registerAdapterDataObserver(observer);
    }

    @Override
    public int getItemViewType(int position) {
        return mMultiItemTypeSupport.getItemViewType(position, null);
    }

    private void initMultiItemTypeSupport(int layoutId, final MultiItemTypeSupport multiItemTypeSupport) {
        if (layoutId != -1) {
            headerItemTypeSupport = new MultiItemTypeSupport<T>() {
                @Override
                public int getLayoutId(int itemType) {
                    if (itemType == TYPE_SECTION) {
                        return mSectionSupport.sectionHeaderLayoutId();
                    } else {
                        return mLayoutId;
                    }
                }

                @Override
                public int getItemViewType(int position, T o) {
                    mDatas.get(getIndexForPosition(position));//用于触发数组越界异常
                    return mSections.values().contains(position) ?
                            TYPE_SECTION :
                            1;
                }
            };
        } else if (multiItemTypeSupport != null) {
            headerItemTypeSupport = new MultiItemTypeSupport<T>() {
                @Override
                public int getLayoutId(int itemType) {
                    if (itemType == TYPE_SECTION) {
                        return mSectionSupport.sectionHeaderLayoutId();
                    } else {
                        return multiItemTypeSupport.getLayoutId(itemType);
                    }
                }

                @Override
                public int getItemViewType(int position, T o) {
                    mDatas.get(getIndexForPosition(position));//用于触发数组越界异常
                    int positionVal = getIndexForPosition(position);
                    return mSections.values().contains(position) ?
                            TYPE_SECTION :
                            multiItemTypeSupport.getItemViewType(positionVal, o);
                }
            };
        } else {
            throw new RuntimeException("layoutId or MultiItemTypeSupport must set one.");
        }

    }

    @Override
    protected boolean isEnabled(int viewType) {
        if (viewType == TYPE_SECTION) {
            return false;
        }
        return super.isEnabled(viewType);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(observer);
    }

    public void findSections() {
        int n = mDatas.size();
        int nSections = 0;
        mSections.clear();

        for (int i = 0; i < n; i++) {
            String sectionName = mSectionSupport.getTitle(mDatas.get(i));

            if (!mSections.containsKey(sectionName)) {
                mSections.put(sectionName, i + nSections);
                nSections++;
            }
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + mSections.size();
    }

    public int getIndexForPosition(int position) {
        int nSections = 0;

        Set<Map.Entry<String, Integer>> entrySet = mSections.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getValue() < position) {
                nSections++;
            }
        }
        return position - nSections;
    }

    @Override
    protected int getPosition(RecyclerView.ViewHolder viewHolder) {
        return getIndexForPosition(viewHolder.getAdapterPosition());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        position = getIndexForPosition(position);
        int itemType = holder.getItemViewType();
        //TODO:加入分段标题动态更新的能力
        if (itemType == TYPE_SECTION) {
            holder.setText(mSectionSupport.sectionTitleTextViewId(), mSectionSupport.getTitle(mDatas.get(position)));
            return;
        }
        super.onBindViewHolder(holder, position);
    }


}
