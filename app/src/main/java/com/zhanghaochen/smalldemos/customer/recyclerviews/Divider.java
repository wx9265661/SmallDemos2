package com.zhanghaochen.smalldemos.customer.recyclerviews;

import android.graphics.Color;
import android.support.annotation.ColorInt;

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
