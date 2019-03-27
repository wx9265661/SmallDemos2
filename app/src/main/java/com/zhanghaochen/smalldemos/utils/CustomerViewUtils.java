package com.zhanghaochen.smalldemos.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;

public class CustomerViewUtils {
    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
     * 0为水平 270是正上方
     *
     * @param radius   半径
     * @param cirAngle 角度
     * @return x，y
     */
    public static float[] getCoordinatePoint(float centerX, float centerY, float radius, float cirAngle) {
        float[] point = new float[2];
        //将角度转换为弧度
        double arcAngle = Math.toRadians(cirAngle);
        if (cirAngle < 90) {
            point[0] = (float) (centerX + Math.cos(arcAngle) * radius);
            point[1] = (float) (centerY + Math.sin(arcAngle) * radius);
        } else if (cirAngle == 90) {
            point[0] = centerX;
            point[1] = centerY + radius;
        } else if (cirAngle > 90 && cirAngle < 180) {
            arcAngle = Math.PI * (180 - cirAngle) / 180.0;
            point[0] = (float) (centerX - Math.cos(arcAngle) * radius);
            point[1] = (float) (centerY + Math.sin(arcAngle) * radius);
        } else if (cirAngle == 180) {
            point[0] = centerX - radius;
            point[1] = centerY;
        } else if (cirAngle > 180 && cirAngle < 270) {
            arcAngle = Math.PI * (cirAngle - 180) / 180.0;
            point[0] = (float) (centerX - Math.cos(arcAngle) * radius);
            point[1] = (float) (centerY - Math.sin(arcAngle) * radius);
        } else if (cirAngle == 270) {
            point[0] = centerX;
            point[1] = centerY - radius;
        } else {
            arcAngle = Math.PI * (360 - cirAngle) / 180.0;
            point[0] = (float) (centerX + Math.cos(arcAngle) * radius);
            point[1] = (float) (centerY - Math.sin(arcAngle) * radius);
        }

        return point;
    }

    /**
     * 获取文字大小混排的文字
     *
     * @param str    str
     * @param indexs 一段文字中可能间隔某几个会大字体，第二维是开始index和字符串长度
     * @return SpannableString
     */
    public static SpannableString getMixedText(String str, int[][] indexs, boolean isBig) {
        if (TextUtils.isEmpty(str) || indexs == null || indexs.length <= 0) {
            return new SpannableString("");
        }
        SpannableString spannableString = new SpannableString(str);
        int fontSizePx1 = SysUtils.convertSpToPixel(GlobalParams.mApplication, isBig ? 20 : 16);
        for (int[] index : indexs) {
            if (index.length < 2) {
                return new SpannableString("");
            }
            if (index[0] >= 0 && (index[0] + index[1]) < spannableString.length()) {
                spannableString.setSpan(new VerticalCenterSpan(fontSizePx1, Color.parseColor("#D6B189")), index[0], index[0] + index[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    /**
     * 使TextView中不同大小字体垂直居中
     */
    public static class VerticalCenterSpan extends ReplacementSpan {

        private float mFontSizePx;

        private int mTextColor;

        private VerticalCenterSpan(float fontSizePx, int textColor) {
            this.mFontSizePx = fontSizePx;
            this.mTextColor = textColor;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            text = text.subSequence(start, end);
            Paint p = getCustomTextPaint(paint);
            return (int) p.measureText(text.toString());
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            text = text.subSequence(start, end);
            Paint p = getCustomTextPaint(paint);
            Paint.FontMetricsInt fm = p.getFontMetricsInt();
            p.setColor(mTextColor);
            // 此处重新计算y坐标，使字体居中
            canvas.drawText(text.toString(), x, y - ((y + fm.descent + y + fm.ascent) / 2 - (bottom + top) / 2), p);
        }

        private TextPaint getCustomTextPaint(Paint srcPaint) {
            TextPaint paint = new TextPaint(srcPaint);
            //设定字体大小, sp转换为px
            paint.setTextSize(mFontSizePx);
            return paint;
        }
    }
}
