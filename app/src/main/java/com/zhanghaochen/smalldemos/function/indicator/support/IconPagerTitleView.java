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
package com.zhanghaochen.smalldemos.function.indicator.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.function.indicator.abs.IMeasurablePagerTitleView;
import com.zhanghaochen.smalldemos.utils.SysUtils;


/**
 * 文本带icon的指示器标题
 */

@SuppressLint("ViewConstructor")
public class IconPagerTitleView extends RelativeLayout implements IMeasurablePagerTitleView {
    private int mSelectedColor;
    private int mNormalColor;
    private Interpolator mStartInterpolator;
    private Interpolator mEndInterpolator;
    private TextView tabTextView;
    private ImageView tabImageView;

    @Override
    public void onSelected(int index, int totalCount) {
    }

    @Override
    public void onDeselected(int index, int totalCount) {
    }

    public IconPagerTitleView(Context context, @LayoutRes int layoutId) {
        super(context, null);
        init(layoutId);
    }

    private void init(@LayoutRes int layoutId) {
        setStartInterpolator(getStartInterpolator());
        setEndInterpolator(getEndInterpolator());
        inflate(getContext(), layoutId, this);
        tabTextView = (TextView) findViewById(R.id.tab_text);
        tabImageView = (ImageView) findViewById(R.id.tab_image);
        tabTextView.setGravity(Gravity.CENTER);
        int padding = SysUtils.convertDpToPixel(10);
        setPadding(padding, 0, padding, 0);
        tabTextView.setSingleLine();
        tabTextView.setEllipsize(TextUtils.TruncateAt.END);
    }

    private Paint getPaint() {
        return tabTextView.getPaint();
    }

    private CharSequence getText() {
        return tabTextView.getText();
    }

    @Override
    public int getContentLeft() {
        Rect bound = new Rect();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), bound);
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 - contentWidth / 2;
    }

    @Override
    public int getContentTop() {
        Paint.FontMetrics metrics = getPaint().getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 - contentHeight / 2);
    }

    @Override
    public int getContentRight() {
        Rect bound = new Rect();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), bound);
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 + contentWidth / 2;
    }

    @Override
    public int getContentBottom() {
        Paint.FontMetrics metrics = getPaint().getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 + contentHeight / 2);
    }

    public int getSelectedColor() {
        return mSelectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        mSelectedColor = selectedColor;
    }

    public int getNormalColor() {
        return mNormalColor;
    }

    public void setNormalColor(int normalColor) {
        mNormalColor = normalColor;
    }

    public void setTextSize(int unit, float size) {
        tabTextView.setTextSize(unit, size);
    }

    public void setText(CharSequence text) {
        tabTextView.setText(text);
    }

    public void setIcon(Drawable drawable) {
        tabImageView.setImageDrawable(drawable);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        int color = (Integer) ArgbEvaluatorHolder.eval(mEndInterpolator.getInterpolation(leavePercent), getSelectedColor(), getNormalColor());
        tabTextView.setTextColor(color);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        int color = (Integer) ArgbEvaluatorHolder.eval(mStartInterpolator.getInterpolation(enterPercent), getNormalColor(), getSelectedColor());
        tabTextView.setTextColor(color);
    }

    public Interpolator getStartInterpolator() {
        return mStartInterpolator;
    }

    public void setStartInterpolator(Interpolator startInterpolator) {
        mStartInterpolator = startInterpolator;
        if (mStartInterpolator == null) {
            mStartInterpolator = new LinearInterpolator();
        }
    }

    public Interpolator getEndInterpolator() {
        return mEndInterpolator;
    }

    public void setEndInterpolator(Interpolator endInterpolator) {
        mEndInterpolator = endInterpolator;
        if (mEndInterpolator == null) {
            mEndInterpolator = new LinearInterpolator();
        }
    }

}
