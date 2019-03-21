package com.zhanghaochen.smalldemos.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ReplacementSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhanghaochen.smalldemos.R;
import com.zhanghaochen.smalldemos.customer.recyclerviews.OnDataChangedListener;
import com.zhanghaochen.smalldemos.utils.GlobalParams;
import com.zhanghaochen.smalldemos.utils.SysUtils;

import java.util.ArrayList;

public class MyDemoHolder01 extends RecyclerView.ViewHolder implements OnDataChangedListener<ArrayList<String>> {

    private static final int MAX_COUNT = 4;

    private LinearLayout mContentLayout;

    public static MyDemoHolder01 newInstance(ViewGroup parent) {
        return new MyDemoHolder01(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_demo01, parent, false));
    }

    public MyDemoHolder01(View itemView) {
        super(itemView);

        mContentLayout = itemView.findViewById(R.id.chief_content_layout);
    }

    @Override
    public void onBindData(ArrayList<String> strs) {
        if (strs == null || strs.size() <= 0) {
            return;
        }
        int size = strs.size();
        initItems(size);
        if (mContentLayout.getChildCount() <= size) {
            for (int i = 0; i < mContentLayout.getChildCount(); i++) {
                MyItem item = (MyItem) mContentLayout.getChildAt(i).getTag();
                final String str = strs.get(i);
                if (TextUtils.isEmpty(str)) {
                    return;
                }
                // 赋值
                item.chiefWhoTv.setText(String.format("%s %s", "研究员", "小李"));
                item.clickNum.setText(String.format("%s次", "1111"));
                item.chiefTitleTv.setText(getChiefTitle(str, true));
                item.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        }
    }

    /**
     * 添加自定义item
     *
     * @param count item数量
     */
    private void initItems(int count) {
        if (mContentLayout == null || count < 0) {
            return;
        }
        // 最多显示4条
        count = count >= MAX_COUNT ? MAX_COUNT : count;
        int childCount = mContentLayout.getChildCount();
        if (childCount != count) {
            mContentLayout.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(mContentLayout.getContext());
            for (int i = 0; i < count; i++) {
                View childView = inflater.inflate(R.layout.holder_item_demo01, mContentLayout, false);
                childView.setTag(new MyItem(childView));
                mContentLayout.addView(childView);
            }
        }
    }

    private class MyItem {
        ImageView imageView;
        TextView chiefTitleTv;
        TextView chiefWhoTv;
        TextView clickNum;
        View itemView;

        MyItem(View view) {
            imageView = view.findViewById(R.id.chief_img);
            chiefTitleTv = view.findViewById(R.id.chief_title);
            chiefWhoTv = view.findViewById(R.id.chief_name);
            clickNum = view.findViewById(R.id.chief_click_num);
            itemView = view;
        }
    }

    /**
     * 带限免标签的String
     *
     * @param title  标题
     * @param isFree 是否带标签
     * @return SpannableString
     */
    private SpannableString getChiefTitle(String title, boolean isFree) {
        if (TextUtils.isEmpty(title)) {
            return new SpannableString("");
        }
        SpannableString stringBuilder = null;
        if (isFree) {
            stringBuilder = new SpannableString(" 限免  " + title);
            stringBuilder.setSpan(new AbsoluteSizeSpan(SysUtils.convertSpToPixel(GlobalParams.mApplication, 10)),
                0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.setSpan(new RoundBackgroundColorSpan(Color.parseColor("#FF5353"), Color.parseColor("#FFFFFF")),
                0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            stringBuilder = new SpannableString(title);
        }
        return stringBuilder;
    }

    /**
     * 自定义带圆角和背景色的Span
     */
    public class RoundBackgroundColorSpan extends ReplacementSpan {
        private int bgColor;
        private int textColor;

        public RoundBackgroundColorSpan(int bgColor, int textColor) {
            super();
            this.bgColor = bgColor;
            this.textColor = textColor;
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            // 所占空间大小
            return ((int) paint.measureText(text, start, end));
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            int color1 = paint.getColor();
            paint.setColor(this.bgColor);
            canvas.drawRoundRect(new RectF(x, top + SysUtils.convertDpToPixel(4),
                    x + SysUtils.convertDpToPixel(26), top + SysUtils.convertDpToPixel(19)),
                SysUtils.convertDpToPixel(1), SysUtils.convertDpToPixel(1), paint);
            paint.setColor(this.textColor);
            canvas.drawText(text, start, end, x + SysUtils.convertDpToPixel(1), y - SysUtils.convertDpToPixel(1), paint);
            paint.setColor(color1);
        }
    }
}
