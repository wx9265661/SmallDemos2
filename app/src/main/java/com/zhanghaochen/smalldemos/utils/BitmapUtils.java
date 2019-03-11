package com.zhanghaochen.smalldemos.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapUtils {

    //压缩后转字节字符
    public String bitmaptoString(Bitmap bitmap) {
        // 将Bitmap转换成字符串
        String string = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int options = 100;
            //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            while (baos.toByteArray().length / 1024 > 100) {
                //重置baos即清空baos
                baos.reset();
                //这里压缩options%，把压缩后的数据存放到baos中
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                //每次都减少10
                options -= 10;
            }
            byte[] bytes = baos.toByteArray();
            baos.close();
            string = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

    // 将字符串转换成Bitmap类型

    public Bitmap stringtoBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;

    }
}
