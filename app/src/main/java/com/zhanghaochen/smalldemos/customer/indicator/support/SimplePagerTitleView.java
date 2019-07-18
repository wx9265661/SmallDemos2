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
package com.zhanghaochen.smalldemos.customer.indicator.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;

import com.zhanghaochen.smalldemos.customer.indicator.abs.IMeasurablePagerTitleView;
import com.zhanghaochen.smalldemos.utils.SysUtils;

/**
 * 带文本的指示器标题
 */
@SuppressLint("AppCompatCustomView")
public class SimplePagerTitleView extends TextView implements IMeasurablePagerTitleView {
    /**
     * 被选中的颜色
     */
    private int mSelectedColor;
    /**
     * 普通的颜色
     */
    private int mNormalColor;

    /**
     * 默认字体大小
     */
    protected int mDefaultTextSize;

    /**
     * 选中字体大小
     */
    protected int mSelectedTextSize;

    /**
     * 选中字体是否加粗
     */
    protected boolean mSelectedTextBold = false;

    /**
     * 线条的左右边距
     */
    protected int mLeftRightMargin;

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public SimplePagerTitleView(Context context) {
        super(context, null);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setGravity(Gravity.CENTER);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.END);
    }

    /**
     * 选中回调
     *
     * @param index      索引
     * @param totalCount 总数
     */
    @Override
    public void onSelected(int index, int totalCount) {
        setTextColor(mSelectedColor);
        if (mSelectedTextSize > 0) {
            setTextSize(mSelectedTextSize);
        }
        if (mSelectedTextBold) {
            TextPaint tp = getPaint();
            tp.setFakeBoldText(false);
        }
    }

    /**
     * 未选中回调
     *
     * @param index      索引
     * @param totalCount 总数
     */
    @Override
    public void onDeselected(int index, int totalCount) {
        setTextColor(mNormalColor);
        if (mDefaultTextSize > 0) {
            setTextSize(mDefaultTextSize);
        }
        if (mSelectedTextBold) {
            TextPaint tp = getPaint();
            tp.setFakeBoldText(true);
        }

    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
    }

    /**
     * 获取文本左侧间距
     *
     * @return 左侧距离
     */
    @Override
    public int getContentLeft() {
        Rect bound = new Rect();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), bound);
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 - contentWidth / 2 + mLeftRightMargin;
    }

    /**
     * 获取文本上方间距
     *
     * @return 上方距离
     */
    @Override
    public int getContentTop() {
        Paint.FontMetrics metrics = getPaint().getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 - contentHeight / 2);
    }

    /**
     * 获取文本右侧间距
     *
     * @return 右侧距离
     */
    @Override
    public int getContentRight() {
        Rect bound = new Rect();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), bound);
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 + contentWidth / 2 - mLeftRightMargin;
    }

    /**
     * 获取文本底部间距
     *
     * @return 底部距离
     */
    @Override
    public int getContentBottom() {
        Paint.FontMetrics metrics = getPaint().getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 + contentHeight / 2);
    }

    /**
     * 获取被选中的文本颜色
     *
     * @return 颜色
     */
    public int getSelectedColor() {
        return mSelectedColor;
    }

    /**
     * 设置被选中文本的颜色
     *
     * @param selectedColor 选中颜色
     */
    public void setSelectedColor(int selectedColor) {
        mSelectedColor = selectedColor;
    }

    /**
     * 获取未选中文本颜色
     *
     * @return 文本颜色
     */
    public int getNormalColor() {
        return mNormalColor;
    }

    /**
     * 设置未选中文本颜色
     *
     * @param normalColor 颜色
     */
    public void setNormalColor(int normalColor) {
        mNormalColor = normalColor;
    }

    public void setDefaultTextSize(int mDefaultTextSize) {
        this.mDefaultTextSize = mDefaultTextSize;
    }

    public void setSelectedTextSize(int mSelectedTextSize) {
        this.mSelectedTextSize = mSelectedTextSize;
    }

    public void setSelectedTextBold(boolean mSelectedTextBold) {
        this.mSelectedTextBold = mSelectedTextBold;
    }

    public void setLeftRightMargin(int mLeftRightMargin) {
        this.mLeftRightMargin = mLeftRightMargin;
    }

    public void setPadding(int mPadding) {
        int padding = SysUtils.convertDpToPixel(mPadding);
        setPadding(padding, 0, padding, 0);
    }
}
