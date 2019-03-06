package com.zhanghaochen.smalldemos.utils;

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
}
