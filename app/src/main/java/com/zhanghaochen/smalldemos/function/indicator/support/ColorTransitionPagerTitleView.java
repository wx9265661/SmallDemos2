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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * 两种颜色过渡的指示器标题
 */
public class ColorTransitionPagerTitleView extends SimplePagerTitleView {
    private Interpolator mStartInterpolator = new LinearInterpolator();
    private Interpolator mEndInterpolator = new LinearInterpolator();

    /**
     * 是否要在onselected的时候清除CompoundDrawables
     */
    private boolean mClearCompoundDrawablesOnSelected;

    public ColorTransitionPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        if (mClearCompoundDrawablesOnSelected) {
            clearCompoundDrawables();
        }
    }

    @Override
    public void onDeselected(int index, int totalCount) {
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        int color = (Integer) ArgbEvaluatorHolder.eval(mEndInterpolator.getInterpolation(leavePercent), getSelectedColor(), getNormalColor());
        setTextColor(color);
        if (mDefaultTextSize > 0) {
            setTextSize(mDefaultTextSize);
        }
        if (mSelectedTextBold) {
            TextPaint tp = getPaint();
            tp.setFakeBoldText(false);
        }
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        int color = (Integer) ArgbEvaluatorHolder.eval(mStartInterpolator.getInterpolation(enterPercent), getNormalColor(), getSelectedColor());
        setTextColor(color);
        if (mSelectedTextSize > 0) {
            setTextSize(mSelectedTextSize);
        }
        if (mSelectedTextBold) {
            TextPaint tp = getPaint();
            tp.setFakeBoldText(true);
        }
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

    public void setClearCompoundDrawablesOnSelected(boolean mClearCompoundDrawablesOnSelected) {
        this.mClearCompoundDrawablesOnSelected = mClearCompoundDrawablesOnSelected;
    }

    /**
     *  清除CompoundDrawables
     */
    private void clearCompoundDrawables() {
        boolean needReset = false;
        Drawable[] drawables = getCompoundDrawables();
        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] != null) {
                needReset = true;
            }
        }
        if (needReset) {
            setCompoundDrawables(null, null, null, null);
        }
    }
}
