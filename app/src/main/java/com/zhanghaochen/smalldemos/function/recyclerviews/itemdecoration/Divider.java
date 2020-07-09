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
package com.zhanghaochen.smalldemos.function.recyclerviews.itemdecoration;

import android.graphics.Color;
import androidx.annotation.ColorInt;

import com.zhanghaochen.smalldemos.utils.SysUtils;

public class Divider {

    @ColorInt
    public int color;
    public int color2; //白色主题的divider，原因是RecyclerView的ItemDecoration使用这个Divider会有叠加效应
    public int size;
    public int marginStart;
    public int marginEnd;
    public boolean isNeedLast = true;

    private Divider() {
    }

    public static class Builder {
        private int color = Color.GRAY;
        private float sizeInDp = 0.5f;
        private int sizeInPx = 1;
        private int marginStartInDp = 0;
        private int marginEndInDp = 0;
        private boolean isNeedLastDivider = true;

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder needLast(boolean isNeedLast) {
            this.isNeedLastDivider = isNeedLast;
            return this;
        }

        public Builder size(float sizeInDp) {
            this.sizeInDp = sizeInDp;
            return this;
        }

        public Builder sizeInPx(int sizeInPx) {
            this.sizeInPx = sizeInPx;
            return this;
        }

        public Builder margin(int marginStartInDp, int marginEndInDp) {
            this.marginStartInDp = marginStartInDp;
            this.marginEndInDp = marginEndInDp;
            return this;
        }

        public Divider build() {
            Divider divider = new Divider();
            divider.size = SysUtils.convertDpToPixel(this.sizeInDp);
            divider.color = this.color;
            divider.marginStart = SysUtils.convertDpToPixel(this.marginStartInDp);
            divider.marginEnd = SysUtils.convertDpToPixel(this.marginEndInDp);
            divider.isNeedLast = this.isNeedLastDivider;
            return divider;
        }

        public Divider buildSizeInPx() {
            Divider divider = new Divider();
            divider.size = this.sizeInPx;
            divider.color = this.color;
            divider.marginStart = SysUtils.convertDpToPixel(this.marginStartInDp);
            divider.marginEnd = SysUtils.convertDpToPixel(this.marginEndInDp);
            divider.isNeedLast = this.isNeedLastDivider;
            return divider;
        }
    }
}
