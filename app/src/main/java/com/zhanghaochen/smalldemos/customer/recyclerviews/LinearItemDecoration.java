package com.zhanghaochen.smalldemos.customer.recyclerviews;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class LinearItemDecoration extends RecyclerView.ItemDecoration {
    protected Paint paint;
    protected Divider divider;

    public LinearItemDecoration(Divider divider) {
        paint = new Paint();
        this.divider = divider;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int drawDividerCount = divider.isNeedLast ? childCount : childCount - 1;
        for (int i = 0; i < drawDividerCount; i++) {
            View child = parent.getChildAt(i);
            drawTopDivider(c, child, divider);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = divider.size;
        }
    }

    protected void drawTopDivider(Canvas c, View child, Divider bd) {
        paint.setColor(bd.color);
        c.drawRect(
            child.getLeft() + bd.marginStart,
            child.getTop() - bd.size,
            child.getRight() - bd.marginEnd,
            child.getTop(),
            paint
        );
    }
}
