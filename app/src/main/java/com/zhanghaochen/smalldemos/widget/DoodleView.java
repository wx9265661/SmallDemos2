package com.zhanghaochen.smalldemos.widget;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.zhanghaochen.smalldemos.beans.TextBitmap;
import com.zhanghaochen.smalldemos.utils.SysUtils;

import java.util.ArrayList;

/**
 * 自定义View.使用Canvas、Paint等来实现图片编辑功能（包括普通涂鸦、画圆、画矩形、画箭头、写字）
 */
@SuppressLint("AppCompatCustomView")
public class DoodleView extends ImageView implements LifecycleObserver {
    private static final String TAG = "DoodleView";
    private DoodleCallback mCallBack;
    private int mViewWidth, mViewHeight;
    /**
     * 暂时的画笔
     */
    private Paint mTempPaint;
    /**
     * 暂时的路径
     */
    private Path mTempPath;
    private Paint mMosaicPaint;
    /**
     * 画笔的颜色
     */
    private int mPaintColor = Color.RED;
    /**
     * 画笔的粗细
     */
    private int mPaintWidth = SysUtils.convertDpToPixel(5);
    private Paint mBitmapPaint;
    private Bitmap mMoasicBitmap;
    private Bitmap mOriginBitmap;
    private MODE mMode = MODE.NONE;
    private MODE mTextMode = MODE.NONE;
    private GRAPH_TYPE mCurrentGraphType = GRAPH_TYPE.RECT;
    private ArrayList<DrawGraphBean> mGraphPath = new ArrayList<>();
    /**
     * 是否可编辑
     */
    private boolean mIsEditable = false;
    /**
     * 所有的路径
     */
    private ArrayList<MODE> mPaths = new ArrayList<>();
    /**
     * 涂鸦的路径
     */
    private ArrayList<DrawPathBean> mDoodlePath = new ArrayList<>();
    /**
     * 马赛克路径
     */
    private ArrayList<DrawPathBean> mMosaicPath = new ArrayList<>();
    private ArrayList<TextBitmap> mTextBitmaps = new ArrayList<>();
    private TextBitmap mCurrentTextBitmap;
    private Matrix mCurrentMatrix = new Matrix();
    private boolean mIsClickOnText = false;
    private float mStartX, mStartY;
    private float mMoveX, mMoveY;

    public DoodleView(Context context) {
        super(context);
        init();
        autoBindLifecycle(context);
    }

    public DoodleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        autoBindLifecycle(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewWidth <= 0 || mViewHeight <= 0) {
            return;
        }

        // 画原始图
        if (mOriginBitmap != null) {
            canvas.drawBitmap(mOriginBitmap, 0, 0, mBitmapPaint);
        }

        // 按照路径和画笔画图
        // 先画马赛克
        drawMosaicPath(canvas);
        // 再画图形
        drawGraphs(canvas);
        // 再画涂鸦
        drawDoodlePath(canvas);
        // 最后画文字
        // todo 关闭绘制文字功能
//        drawTextBitmap(canvas);
    }

    /**
     * 画涂鸦内容
     */
    private void drawDoodlePath(Canvas canvas) {
        if (mDoodlePath.size() > 0) {
            for (DrawPathBean pathBean : mDoodlePath) {
                canvas.drawPath(pathBean.path, pathBean.paint);
            }
        }
    }

    public DoodleView setCallBack(DoodleCallback callBack) {
        this.mCallBack = callBack;
        return this;
    }

    /**
     * 添加文字功能， 暂时关闭
     */
    private void addTextBitmap(TextBitmap textBitmap) {
        if (textBitmap != null && mViewWidth > 0 && mViewHeight > 0) {
            textBitmap.matrix.postTranslate(mViewWidth / 2 - textBitmap.getBitmap().getWidth() / 2,
                    mViewHeight / 2 - textBitmap.getBitmap().getHeight() / 2);
            float[] values = new float[9];
            textBitmap.matrix.getValues(values);

            float transX = values[Matrix.MTRANS_X];
            float transY = values[Matrix.MTRANS_Y];
            float width = values[Matrix.MSCALE_X] * textBitmap.getBitmap().getWidth();
            float height = values[Matrix.MSCALE_Y] * textBitmap.getBitmap().getHeight();

            // 给文字所占的位置框个框
            textBitmap.rectF = new RectF(0, 0, width, height);
            textBitmap.matrix.mapRect(textBitmap.rectF);
            this.mTextBitmaps.add(textBitmap);
        }
    }

    /**
     * 设置原始的截图
     *
     * @param originBitmap drawable
     */
    public void setOriginBitmap(@NonNull Bitmap originBitmap) {
        mOriginBitmap = originBitmap;
        initOriginBitmap();
    }

    private void init() {
        setMode(mMode);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mViewWidth = w;
            mViewHeight = h;

            initOriginBitmap();
        }
    }

    private void initOriginBitmap() {
        if (mOriginBitmap != null && mViewHeight > 0 && mViewWidth > 0) {
//            mOriginBitmap = ((BitmapDrawable) drawable).getBitmap();
            mOriginBitmap = Bitmap.createScaledBitmap(mOriginBitmap, mViewWidth, mViewHeight, true);

            Log.d(TAG, "onSizeChanged:w:" + mViewWidth + "//h:" + mViewHeight);
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);

            mMosaicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mMosaicPaint.setFilterBitmap(false);
            mMosaicPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            makeMosaicBitmap();

            postInvalidate();
        }
    }

    /**
     * 画马赛克内容
     */
    private void drawMosaicPath(Canvas canvas) {
        if (mMosaicPath.size() > 0 && mMoasicBitmap != null) {
            // 保存图层
            int layerCount = canvas.saveLayer(0, 0, mViewWidth, mViewHeight, null, Canvas.ALL_SAVE_FLAG);
            for (DrawPathBean mosaicPath : mMosaicPath) {
                canvas.drawPath(mosaicPath.path, mosaicPath.paint);
            }
            // 进行图层的合并
            canvas.drawBitmap(mMoasicBitmap, 0, 0, mMosaicPaint);
            canvas.restoreToCount(layerCount);
        }
    }

    /**
     * 画图形
     */
    private void drawGraphs(Canvas canvas) {
        if (mGraphPath.size() > 0) {
            for (DrawGraphBean graphBean : mGraphPath) {
                if (graphBean.type == GRAPH_TYPE.RECT) {
                    graphBean.paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, graphBean.paint);
                } else if (graphBean.type == GRAPH_TYPE.OVAL) {
                    graphBean.paint.setStyle(Paint.Style.STROKE);
                    canvas.drawOval(new RectF(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY), graphBean.paint);
                } else if (graphBean.type == GRAPH_TYPE.CIRCLE) {
                    graphBean.paint.setStyle(Paint.Style.STROKE);
                    // 计算半径
                    float radius = Math.min(Math.abs(graphBean.startX - graphBean.endX), Math.abs(graphBean.startY - graphBean.endY)) / 2;
                    float centerX, centerY;
                    centerX = graphBean.endX >= graphBean.startX ? graphBean.startX + radius : graphBean.startX - radius;
                    centerY = graphBean.endY >= graphBean.startY ? graphBean.startY + radius : graphBean.startY - radius;
                    canvas.drawCircle(centerX, centerY, radius, graphBean.paint);
                } else if (graphBean.type == GRAPH_TYPE.ARROW) {
                    graphBean.paint.setStyle(Paint.Style.FILL);
                    drawArrow(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, canvas, graphBean.paint);
                }
            }
        }
    }

    private void drawTextBitmap(Canvas canvas) {
        Paint pp = new Paint();
        pp.setAntiAlias(true);
        pp.setStyle(Paint.Style.STROKE);
        pp.setColor(Color.RED);
        if (mTextBitmaps.size() > 0) {
            for (int i = 0; i < mTextBitmaps.size(); i++) {
                TextBitmap textBitmap = mTextBitmaps.get(i);
                canvas.drawBitmap(textBitmap.getBitmap(), textBitmap.matrix, textBitmap.paint);
                if (i == mTextBitmaps.size() - 1 && textBitmap.rectF != null) {
                    canvas.drawRect(textBitmap.rectF, pp);
                }
            }
        }
    }

    private void setModePaint(MODE mode) {
        if (mode == MODE.DOODLE_MODE) {
            mTempPaint = new Paint();
            mTempPaint.setAntiAlias(true);
            mTempPaint.setColor(mPaintColor);
            mTempPaint.setStyle(Paint.Style.STROKE);
            mTempPaint.setStrokeWidth(mPaintWidth);
            mTempPaint.setStrokeCap(Paint.Cap.ROUND);
            mTempPaint.setStrokeJoin(Paint.Join.ROUND);
        } else if (mode == MODE.MOSAIC_MODE) {
            mTempPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTempPaint.setAntiAlias(true);
            mTempPaint.setDither(true);
            mTempPaint.setStyle(Paint.Style.STROKE);
            mTempPaint.setTextAlign(Paint.Align.CENTER);
            mTempPaint.setStrokeCap(Paint.Cap.ROUND);
            mTempPaint.setStrokeJoin(Paint.Join.ROUND);
            mTempPaint.setStrokeWidth(mPaintWidth * 2);
        } else if (mode == MODE.GRAPH_MODE) {
            mTempPaint = new Paint();
            mTempPaint.setAntiAlias(true);
            mTempPaint.setColor(mPaintColor);
            mTempPaint.setStrokeWidth(mPaintWidth);
            mTempPaint.setStrokeCap(Paint.Cap.ROUND);
            mTempPaint.setStrokeJoin(Paint.Join.ROUND);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsEditable) {
            mMoveX = event.getX();
            mMoveY = event.getY();
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            if (action == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "ACTION_DOWN");
                // 看看有没有点击到文字
                // todo 关闭文字功能
//                if (mCurrentTextBitmap == null && mTextBitmaps.size() > 0) {
//                    mCurrentTextBitmap = mTextBitmaps.get(mTextBitmaps.size() - 1);
//                }
//                // 将点击到的那个bitmap移动到上层
//                boolean isChanged = false;
//                // 倒过来循环文字bitmap列表
//                for (int i = mTextBitmaps.size() - 1; i > -1; i--) {
//                    TextBitmap bitmap = mTextBitmaps.get(i);
//                    if (bitmap != null) {
//                        // 看看点击的点是否在这个框框里
//                        if (bitmap.rectF.contains(mMoveX, mMoveY)) {
//                            mCurrentTextBitmap = bitmap;
//                            HtscLog.d(TAG, "点击到文字啦！" + bitmap.rectF);
//                            mIsClickOnText = true;
//                            break;
//                        }
//                    }
//                }
//
//                // 把在操作的文字添加到栈底
//                if (mIsClickOnText && mCurrentTextBitmap != null) {
//                    mTextMode = MODE.DRAG;
//                    mTextBitmaps.remove(mCurrentTextBitmap);
//                    mTextBitmaps.add(mCurrentTextBitmap);
//
//                    mCurrentMatrix.set(mCurrentTextBitmap.matrix);
//                    mCurrentTextBitmap.matrix.set(mCurrentMatrix);
//                    mCurrentTextBitmap.startPoint.set(mMoveX, mMoveY);
//                }


                if (!mIsClickOnText) {
                    if (mMode == MODE.GRAPH_MODE) {
                        mStartX = event.getX();
                        mStartY = event.getY();
                        setModePaint(mMode);

                        // 添加到队列中
                        DrawGraphBean graphBean = new DrawGraphBean(mStartX, mStartY, mStartX, mStartY, mCurrentGraphType, mTempPaint);

                        mPaths.add(mMode);
                        mGraphPath.add(graphBean);
                    } else if (mMode == MODE.DOODLE_MODE || mMode == MODE.MOSAIC_MODE) {
                        // 设置对应mode的画笔
                        setModePaint(mMode);

                        mTempPath = new Path();

                        mStartX = mMoveX;
                        mStartY = mMoveY;
                        mTempPath.moveTo(mStartX, mStartY);

                        // 把path加到队列中
                        DrawPathBean pathBean = new DrawPathBean(mTempPath, mTempPaint, mMode);
                        mPaths.add(mMode);
                        if (mMode == MODE.DOODLE_MODE) {
                            mDoodlePath.add(pathBean);
                        } else if (mMode == MODE.MOSAIC_MODE) {
                            mMosaicPath.add(pathBean);
                        }
                    }
                }
                if (mCallBack != null) {
                    mCallBack.onDrawStart();
                }
                return true;
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (!mIsClickOnText) {
                    // 使用队列中最后一条path进行操作
                    if (mMode == MODE.DOODLE_MODE && mDoodlePath.size() > 0) {
                        mDoodlePath.get(mDoodlePath.size() - 1).path.lineTo(mMoveX, mMoveY);
                    } else if (mMode == MODE.MOSAIC_MODE && mMosaicPath.size() > 0) {
                        mMosaicPath.get(mMosaicPath.size() - 1).path.lineTo(mMoveX, mMoveY);
                    } else if (mMode == MODE.GRAPH_MODE && mGraphPath.size() > 0) {
                        DrawGraphBean tempBean = mGraphPath.get(mGraphPath.size() - 1);
                        tempBean.endX = mMoveX;
                        tempBean.endY = mMoveY;
                    }
                }
                if (mIsClickOnText && mCurrentTextBitmap != null) {
                    // 如果是拖拽模式
                    if (mTextMode == MODE.DRAG) {
                        float dx = mMoveX - mCurrentTextBitmap.startPoint.x;
                        float dy = mMoveY - mCurrentTextBitmap.startPoint.y;
                        // 在没有进行移动之前的位置基础上进行移动
                        mCurrentTextBitmap.matrix.set(mCurrentMatrix);
                        mCurrentTextBitmap.matrix.postTranslate(dx, dy);
                        Log.d(TAG, "拖动rect" + mCurrentTextBitmap.rectF);
                    } else if (mTextMode == MODE.ZOOM) {
                        // 如果是旋转和缩放模式
                        // 结束距离
                        float endDis = distance(event);
                        mCurrentTextBitmap.rotation = rotation(event) - mCurrentTextBitmap.oldRotation;
                        if (endDis > 10f) {
                            // 获取缩放倍数
                            float scale = endDis / mCurrentTextBitmap.startDis;
                            mCurrentTextBitmap.matrix.set(mCurrentMatrix);
                            mCurrentTextBitmap.matrix.postScale(scale, scale, mCurrentTextBitmap.midPoint.x, mCurrentTextBitmap.midPoint.y);
                            mCurrentTextBitmap.matrix.postRotate(mCurrentTextBitmap.rotation, mCurrentTextBitmap.midPoint.x, mCurrentTextBitmap.midPoint.y);
                            Log.d(TAG, "scale:" + scale);
                        }
                    }
                }

                if (mCallBack != null) {
                    mCallBack.onDrawing();
                }
                postInvalidate();
                return true;
            } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
                Log.d(TAG, "ACTION_POINTER_DOWN");
                // 当屏幕上还有触点（手指），再有一个手指压下屏幕
                if (mIsClickOnText && mCurrentTextBitmap != null) {
                    mTextMode = MODE.ZOOM;
                    mCurrentTextBitmap.oldRotation = rotation(event);
                    mCurrentTextBitmap.startDis = distance(event);
                    if (mCurrentTextBitmap.startDis > 10f) {
                        mCurrentTextBitmap.midPoint = mid(event);
                        // 记录textbitmap当前的缩放倍数
                        mCurrentMatrix.set(mCurrentTextBitmap.matrix);
                    }
                }
                return true;
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                Log.d(TAG, "ACTION_UP");
                if (!mIsClickOnText) {
                    mTempPath = null;
                    mTempPaint = null;
                }
                mIsClickOnText = false;
                mTextMode = MODE.NONE;
                mCurrentTextBitmap = null;
                if (mCallBack != null) {
                    mCallBack.onDrawComplete();
                }
                return true;
            } else if (action == MotionEvent.ACTION_POINTER_UP) {
                Log.d(TAG, "ACTION_POINTER_UP");
                mTextMode = MODE.NONE;
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 撤销操作
     *
     * @return 撤销后剩余可以撤销的步骤
     */
    public int revertPath() {
        int size = mPaths.size();
        if (size > 0) {
            // 根据最后一位数的mode，删除对应path
            MODE lastestMode = mPaths.get(size - 1);
            if (lastestMode == MODE.DOODLE_MODE && mDoodlePath.size() > 0) {
                mDoodlePath.remove(mDoodlePath.size() - 1);
            } else if (lastestMode == MODE.MOSAIC_MODE && mMosaicPath.size() > 0) {
                mMosaicPath.remove(mMosaicPath.size() - 1);
            } else if (lastestMode == MODE.GRAPH_MODE && mGraphPath.size() > 0) {
                mGraphPath.remove(mGraphPath.size() - 1);
            }
            mPaths.remove(size - 1);
        }
        postInvalidate();
        return size;
    }

    /**
     * 获取马赛克的bitmap
     */
    private Bitmap makeMosaicBitmap() {
        if (mMoasicBitmap != null) {
            return mMoasicBitmap;
        }

        int w = Math.round(mViewWidth / 16f);
        int h = Math.round(mViewHeight / 16f);

        if (mOriginBitmap != null) {
            // 先创建小图
            mMoasicBitmap = Bitmap.createScaledBitmap(mOriginBitmap, w, h, false);
            // 再把小图放大
            mMoasicBitmap = Bitmap.createScaledBitmap(mMoasicBitmap, mViewWidth, mViewHeight, false);
        }
        return mMoasicBitmap;
    }

    /**
     * 计算两点之间的距离
     *
     * @return float
     */
    public float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        // a方+b方=c方
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 计算两点之间的中间点
     *
     * @return PointF
     */
    public PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    /**
     * 计算手势对应的角度
     *
     * @return float
     */
    public float rotation(MotionEvent event) {
        double deltaX = (event.getX(0) - event.getX(1));
        double deltaY = (event.getY(0) - event.getY(1));
        // 计算坐标点相对于圆点所对应的弧度
        double radians = Math.atan2(deltaY, deltaX);
        // 把弧度转换成角度
        return (float) Math.toDegrees(radians);
    }

    /**
     * 设置所要画图形的种类
     *
     * @param graphType graphType
     */
    public void setGraphType(GRAPH_TYPE graphType) {
        this.mCurrentGraphType = graphType;
    }

    /**
     * 画箭头
     */
    private void drawArrow(float sx, float sy, float ex, float ey, Canvas canvas, Paint paint) {
        int size = 8;
        int count = 30;
        float x = ex - sx;
        float y = ey - sy;
        double d = x * x + y * y;
        double r = Math.sqrt(d);
        float zx = (float) (ex - (count * x / r));
        float zy = (float) (ey - (count * y / r));
        float xz = zx - sx;
        float yz = zy - sy;
        double zd = xz * xz + yz * yz;
        double zr = Math.sqrt(zd);
        Path triangle = new Path();
        triangle.moveTo(sx, sy);
        triangle.lineTo((float) (zx + size * yz / zr), (float) (zy - size * xz / zr));
        triangle.lineTo((float) (zx + size * 2 * yz / zr), (float) (zy - size * 2 * xz / zr));
        triangle.lineTo(ex, ey);
        triangle.lineTo((float) (zx - size * 2 * yz / zr), (float) (zy + size * 2 * xz / zr));
        triangle.lineTo((float) (zx - size * yz / zr), (float) (zy + size * xz / zr));
        triangle.close();
        canvas.drawPath(triangle, paint);
    }

    /**
     * 设置画笔的颜色
     *
     * @param color 颜色
     */
    public void setPaintColor(int color) {
        this.mPaintColor = color;
        if (mTempPaint != null) {
            mTempPaint.setColor(mPaintColor);
        }
    }

    /**
     * 设置是否可编辑
     *
     * @param editable 能否编辑
     */
    public void setEditable(boolean editable) {
        this.mIsEditable = editable;
    }

    public void setMode(MODE mode) {
        this.mMode = mode;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void clear() {
        if (mOriginBitmap != null && !mOriginBitmap.isRecycled()) {
            mOriginBitmap.recycle();
            mOriginBitmap = null;
        }
        if (mMoasicBitmap != null && !mMoasicBitmap.isRecycled()) {
            mMoasicBitmap.recycle();
            mMoasicBitmap = null;
        }
        mCallBack = null;
    }

    private void autoBindLifecycle(Context context) {
        if (context == null) {
            return;
        }
        if (context instanceof AppCompatActivity) {
            // 宿主是activity
            AppCompatActivity activity = (AppCompatActivity) context;
            ((AppCompatActivity) activity).getLifecycle().addObserver(this);
            return;
        }
        // 宿主是fragment
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver(this);
            return;
        }
    }

    public enum MODE {
        NONE, GRAPH_MODE, DOODLE_MODE, MOSAIC_MODE, DRAG, ZOOM
    }

    public enum GRAPH_TYPE {
        RECT, CIRCLE, OVAL, ARROW
    }

    public interface DoodleCallback {
        void onDrawStart();

        void onDrawing();

        void onDrawComplete();
    }

    /**
     * 记录画笔和画图的路径，主要用来撤销画图的操作
     */
    class DrawPathBean {
        public Path path;
        public Paint paint;
        public MODE mode;

        DrawPathBean(Path path, Paint paint, MODE mode) {
            this.paint = paint;
            this.path = path;
            this.mode = mode;
        }
    }

    class DrawGraphBean {
        public float startX, startY, endX, endY;
        public GRAPH_TYPE type;
        public Paint paint;

        DrawGraphBean(float startX, float startY, float endx, float endY, GRAPH_TYPE type, Paint paint) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endx;
            this.endY = endY;
            this.type = type;
            this.paint = paint;
        }
    }
}
