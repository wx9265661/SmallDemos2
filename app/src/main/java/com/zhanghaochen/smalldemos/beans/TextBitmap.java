package com.zhanghaochen.smalldemos.beans;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class TextBitmap {
    //Matrix.MSKEW_X 控制X坐标的线性倾斜系数
    //Matrix.MSKEW_Y 控制Y坐标的线性倾斜系数
    //Matrix.MTRANS_X//左上顶点X坐标
    //Matrix.MTRANS_Y//左上顶点Y坐标
    //Matrix.MSCALE_X//宽度缩放倍数
    //Matrix.MSCALE_Y//高度缩放位数
    public float startDis;// 开始距离
    public PointF midPoint = new PointF();// 中间点
    public RectF rectF;
    public float oldRotation = 0;
    public float rotation = 0;
    public PointF startPoint = new PointF();
    public Matrix matrix = new Matrix();
    public Paint paint;
    private Bitmap bitmap;

    public TextBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        // 初始化画笔
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}
