package com.zhanghaochen.smalldemos.customer.views;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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

import com.zhanghaochen.smalldemos.utils.CustomerViewUtils;
import com.zhanghaochen.smalldemos.utils.SysUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义View.使用Canvas、Paint等来实现图片编辑功能（包括普通涂鸦、画圆、画矩形、画箭头、写字）
 */
@SuppressLint("AppCompatCustomView")
public class DoodleView extends ImageView implements LifecycleObserver {
    private static final String TAG = "DoodleView";

    private float mValidRadius = SysUtils.convertDpToPixel(2);

    private DoodleCallback mCallBack;

    private int mViewWidth, mViewHeight;
    /**
     * 可拖动的点的半径
     */
    private float mDotRadius = SysUtils.convertDpToPixel(8);
    /**
     * 暂时的涂鸦画笔
     */
    private Paint mTempPaint;
    /**
     * 暂时的涂鸦路径
     */
    private Path mTempPath;
    /**
     * 暂时的马赛克路径
     */
    private Path mTempMosaicPath;
    private Paint mTempMosaicPaint;
    /**
     * 暂时的图形实例，用来move时实时画路径
     */
    private DrawGraphBean mTempGraphBean;
    /**
     * 画笔的颜色
     */
    private int mPaintColor = Color.RED;

    private Paint mMosaicPaint;
    /**
     * 画笔的粗细
     */
    private int mPaintWidth = SysUtils.convertDpToPixel(3);
    /**
     * 图形的当前操作模式
     */
    private MODE mGraphMode = MODE.NONE;

    private Paint mBitmapPaint;
    private Paint mGraphRectPaint;
    private Paint mDotPaint;

    private Bitmap mMoasicBitmap;
    private Bitmap mOriginBitmap;

    private MODE mMode = MODE.NONE;

    private GRAPH_TYPE mCurrentGraphType = GRAPH_TYPE.RECT;
    private ArrayList<DrawGraphBean> mGraphPath = new ArrayList<>();

    /**
     * 是否可编辑
     */
    private boolean mIsEditable = false;

    /**
     * 涂鸦的路径
     */
    private ArrayList<DrawPathBean> mDoodlePath = new ArrayList<>();
    /**
     * 马赛克路径
     */
    private ArrayList<DrawPathBean> mMosaicPath = new ArrayList<>();
    /**
     * 当前选中的图形
     */
    private DrawGraphBean mCurrentGraphBean;
    /**
     * 是否点击到图形了
     */
    private boolean mIsClickOnGraph = false;
    /**
     * 区分点击和滑动
     */
    private float mDelaX, mDelaY;

    private float mStartX, mStartY;
    private float mMoveX, mMoveY;

    public DoodleView setCallBack(DoodleCallback callBack) {
        this.mCallBack = callBack;
        return this;
    }

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
        // 再画涂鸦
        drawDoodlePath(canvas);
        // 再画图形
        drawGraphs(canvas);
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
        if (mTempPath != null && mTempPaint != null) {
            canvas.drawPath(mTempPath, mTempPaint);
        }
    }

    /**
     * 画马赛克内容
     */
    private void drawMosaicPath(Canvas canvas) {
        if (mMoasicBitmap != null) {
            // 保存图层
            int layerCount = canvas.saveLayer(0, 0, mViewWidth, mViewHeight, null, Canvas.ALL_SAVE_FLAG);
            if (mMosaicPath.size() > 0) {
                for (DrawPathBean mosaicPath : mMosaicPath) {
                    canvas.drawPath(mosaicPath.path, mosaicPath.paint);
                }
            }
            if (mTempMosaicPath != null && mTempMosaicPaint != null) {
                canvas.drawPath(mTempMosaicPath, mTempMosaicPaint);
            }
            // 进行图层的合并
            canvas.drawBitmap(mMoasicBitmap, 0, 0, mMosaicPaint);
            canvas.restoreToCount(layerCount);
        }
    }

    /**
     * 画所有图形
     */
    private void drawGraphs(Canvas canvas) {
        if (mGraphPath.size() > 0) {
            for (int i = 0; i < mGraphPath.size(); i++) {
                DrawGraphBean graphBean = mGraphPath.get(i);
                if (graphBean.isPass) {
                    drawGraph(canvas, graphBean);

                    // 给最后一个画个框(直线除外）
                    if (mIsClickOnGraph && i == mGraphPath.size() - 1) {
                        if (graphBean.type == GRAPH_TYPE.LINE) {
                            canvas.drawPath(getLineRectPath(graphBean), mGraphRectPaint);
                            // 再给起点终点画圆
                            canvas.drawCircle(graphBean.startX, graphBean.startY, mDotRadius, mDotPaint);
                            canvas.drawCircle(graphBean.endX, graphBean.endY, mDotRadius, mDotPaint);
                        } else {
                            canvas.drawRect(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, mGraphRectPaint);
                            // 再给起点终点画个圆
                            canvas.drawCircle(graphBean.startX, graphBean.startY, mDotRadius, mDotPaint);
                            canvas.drawCircle(graphBean.endX, graphBean.endY, mDotRadius, mDotPaint);
                        }
                    }
                }
            }
        }
        if (mTempGraphBean != null) {
            drawGraph(canvas, mTempGraphBean);
        }
    }

    /**
     * 画某个图形
     *
     * @param canvas    canvas
     * @param graphBean graphBean
     */
    private void drawGraph(Canvas canvas, DrawGraphBean graphBean) {
        if (graphBean.isPass) {
            if (graphBean.type == GRAPH_TYPE.RECT) {
                // 矩形
                graphBean.paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, graphBean.paint);
            } else if (graphBean.type == GRAPH_TYPE.OVAL) {
                // 椭圆
                graphBean.paint.setStyle(Paint.Style.STROKE);
                canvas.drawOval(new RectF(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY), graphBean.paint);
            } else if (graphBean.type == GRAPH_TYPE.ARROW) {
                // 箭头
                graphBean.paint.setStyle(Paint.Style.FILL);
                drawArrow(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, canvas, graphBean.paint);
            } else if (graphBean.type == GRAPH_TYPE.LINE) {
                // 直线
                graphBean.paint.setStyle(Paint.Style.FILL);
                canvas.drawLine(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, graphBean.paint);
            } else if (graphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                // 垂直或水平的直线
                graphBean.paint.setStyle(Paint.Style.FILL);
                canvas.drawLine(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY, graphBean.paint);
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
            mTempMosaicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTempMosaicPaint.setAntiAlias(true);
            mTempMosaicPaint.setDither(true);
            mTempMosaicPaint.setStyle(Paint.Style.STROKE);
            mTempMosaicPaint.setTextAlign(Paint.Align.CENTER);
            mTempMosaicPaint.setStrokeCap(Paint.Cap.ROUND);
            mTempMosaicPaint.setStrokeJoin(Paint.Join.ROUND);
            mTempMosaicPaint.setStrokeWidth(mPaintWidth * 2);
        } else if (mode == MODE.GRAPH_MODE) {
            mTempPaint = new Paint();
            mTempPaint.setAntiAlias(true);
            mTempPaint.setColor(mPaintColor);
            mTempPaint.setStrokeWidth(mPaintWidth);
            mTempPaint.setStrokeCap(Paint.Cap.ROUND);
            mTempPaint.setStrokeJoin(Paint.Join.ROUND);
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
            mOriginBitmap = Bitmap.createScaledBitmap(mOriginBitmap, mViewWidth, mViewHeight, true);

            Log.d(TAG, "onSizeChanged:w:" + mViewWidth + "//h:" + mViewHeight);
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);

            mMosaicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mMosaicPaint.setFilterBitmap(false);
            mMosaicPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            makeMosaicBitmap();

            mGraphRectPaint = new Paint();
            mGraphRectPaint.setAntiAlias(true);
            mGraphRectPaint.setColor(Color.RED);
            mGraphRectPaint.setStyle(Paint.Style.STROKE);
            mGraphRectPaint.setStrokeWidth(1);
            mGraphRectPaint.setStrokeCap(Paint.Cap.ROUND);
            mGraphRectPaint.setStrokeJoin(Paint.Join.ROUND);

            mDotPaint = new Paint();
            mDotPaint.setAntiAlias(true);
            mDotPaint.setColor(Color.RED);
            mDotPaint.setStyle(Paint.Style.FILL);
            mDotPaint.setStrokeCap(Paint.Cap.ROUND);
            mDotPaint.setStrokeJoin(Paint.Join.ROUND);

            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsEditable) {
            mMoveX = event.getX();
            mMoveY = event.getY();
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "ACTION_DOWN");
                mStartX = mMoveX;
                mStartY = mMoveY;
                mDelaX = 0;
                mDelaY = 0;

                // 正常的画图操作
                if (!mIsClickOnGraph) {
                    touchDownNormalPath();
                } else if (mCurrentGraphBean != null && mGraphPath.size() > 0) {
                    touchDownInitGraphOperate();
                }
                if (mCallBack != null) {
                    mCallBack.onDrawStart();
                }
                return true;
            } else if (action == MotionEvent.ACTION_MOVE) {
                mDelaX += Math.abs(mMoveX - mStartX);
                mDelaY += Math.abs(mMoveY - mStartY);
                if (!mIsClickOnGraph) {
                    touchMoveNormalDraw();
                } else if (mCurrentGraphBean != null && mGraphPath.size() > 0) {
                    touchMoveGraphOperate();
                }

                if (mCallBack != null) {
                    mCallBack.onDrawing();
                }
                postInvalidate();
                return true;
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                Log.d(TAG, "ACTION_UP");
                if (mDelaX < mValidRadius && mDelaY < mValidRadius) {
                    // 是点击事件
                    judgeGraphClick();
                    mTempPath = null;
                    mTempPaint = null;
                    mTempMosaicPath = null;
                    mTempMosaicPaint = null;
                    mTempGraphBean = null;
                    postInvalidate();
                    if (mCallBack != null) {
                        mCallBack.onDrawComplete();
                    }
                    return false;
                }
                // 非点击，正常Up
                if (!mIsClickOnGraph) {
                    if (mMode == MODE.DOODLE_MODE) {
                        // 把path加到队列中
                        DrawPathBean pathBean = new DrawPathBean(mTempPath, mTempPaint, MODE.DOODLE_MODE);
                        mDoodlePath.add(pathBean);
                    } else if (mMode == MODE.MOSAIC_MODE) {
                        // 把path加到队列中
                        DrawPathBean pathBean = new DrawPathBean(mTempMosaicPath, mTempMosaicPaint, MODE.MOSAIC_MODE);
                        mMosaicPath.add(pathBean);
                    } else if (mMode == MODE.GRAPH_MODE) {
                        mGraphPath.add(mTempGraphBean);
                    }
                    mTempPath = null;
                    mTempPaint = null;
                    mTempMosaicPath = null;
                    mTempMosaicPaint = null;
                    mTempGraphBean = null;
                }

                if (mIsClickOnGraph && mCurrentGraphBean != null) {
                    mCurrentGraphBean.startPoint.x = mCurrentGraphBean.startX;
                    mCurrentGraphBean.startPoint.y = mCurrentGraphBean.startY;
                    mCurrentGraphBean.endPoint.x = mCurrentGraphBean.endX;
                    mCurrentGraphBean.endPoint.y = mCurrentGraphBean.endY;
                    mGraphMode = MODE.DRAG;
                } else {
                    mGraphMode = MODE.NONE;
                    mCurrentGraphBean = null;
                }

                if (mCallBack != null) {
                    mCallBack.onDrawComplete();
                }
                postInvalidate();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 按下时，初始化绘图参数
     */
    private void touchDownNormalPath() {
        if (mMode == MODE.GRAPH_MODE) {
            setModePaint(mMode);
            // 创建一个对象
            mTempGraphBean = new DrawGraphBean(mStartX, mStartY, mStartX, mStartY, mCurrentGraphType, mTempPaint);
        } else if (mMode == MODE.DOODLE_MODE) {
            // 设置对应mode的画笔
            setModePaint(mMode);
            mTempPath = new Path();
            mTempPath.moveTo(mStartX, mStartY);
        } else if (mMode == MODE.MOSAIC_MODE) {
            setModePaint(mMode);
            mTempMosaicPath = new Path();
            mTempMosaicPath.moveTo(mStartX, mStartY);
        }
    }

    /**
     * 按下时，初始化正在操作的图形
     */
    private void touchDownInitGraphOperate() {
        // 此时，在操作某个图形
        mCurrentGraphBean.rectFList.add(new RectF(mCurrentGraphBean.startPoint.x, mCurrentGraphBean.startPoint.y,
                mCurrentGraphBean.endPoint.x, mCurrentGraphBean.endPoint.y));
        mCurrentGraphBean.clickPoint.set(mMoveX, mMoveY);
        // 判断是否点击到了起点或者终点
        RectF startDotRect = new RectF(mCurrentGraphBean.startX - mDotRadius, mCurrentGraphBean.startY - mDotRadius,
                mCurrentGraphBean.startX + mDotRadius, mCurrentGraphBean.startY + mDotRadius);
        RectF endDotRect = new RectF(mCurrentGraphBean.endX - mDotRadius, mCurrentGraphBean.endY - mDotRadius,
                mCurrentGraphBean.endX + mDotRadius, mCurrentGraphBean.endY + mDotRadius);
        if (startDotRect.contains(mMoveX, mMoveY)) {
            Log.d(TAG, "点击到起始点了");
            mGraphMode = MODE.DRAG_START;
        } else if (endDotRect.contains(mMoveX, mMoveY)) {
            Log.d(TAG, "点击到终点了");
            mGraphMode = MODE.DRAG_END;
        } else {
            mGraphMode = MODE.DRAG;
        }
    }

    /**
     * 移动时，绘制路径或者图形
     */
    private void touchMoveNormalDraw() {
        // 使用队列中最后一条path进行操作
        if (mMode == MODE.DOODLE_MODE) {
            mTempPath.lineTo(mMoveX, mMoveY);
        } else if (mMode == MODE.MOSAIC_MODE) {
            mTempMosaicPath.lineTo(mMoveX, mMoveY);
        } else if (mMode == MODE.GRAPH_MODE) {
            // 只操作暂时的图形对象
            if (mTempGraphBean != null) {
                // 只有移动了足够距离，才算合格的图形
                if (mDelaX > mValidRadius || mDelaY > mValidRadius) {
                    mTempGraphBean.isPass = true;
                    if (mTempGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                        // 由于笔直的线的特殊性，需要特殊处理
                        float[] point = getDirectLineEndPoint(mTempGraphBean.startX, mTempGraphBean.startY, mMoveX, mMoveY);
                        mTempGraphBean.endX = point[0];
                        mTempGraphBean.endY = point[1];
                        mTempGraphBean.endPoint.x = point[0];
                        mTempGraphBean.endPoint.y = point[1];
                        // 此时的rectList应该只有一条数据
                        if (mTempGraphBean.rectFList.size() == 1) {
                            // 它对应的rect要么是水平的，要么是垂直的
                            RectF rectF = mTempGraphBean.rectFList.get(0);
                            if (mTempGraphBean.startY == mTempGraphBean.endY) {
                                // 水平的直线
                                rectF.left = mTempGraphBean.startX;
                                rectF.top = mTempGraphBean.startY - mDotRadius;
                                rectF.right = mTempGraphBean.endX;
                                rectF.bottom = mTempGraphBean.startY + mDotRadius;
                            } else {
                                // 垂直的直线
                                rectF.left = mTempGraphBean.startX - mDotRadius;
                                rectF.top = mTempGraphBean.startY;
                                rectF.right = mTempGraphBean.startX + mDotRadius;
                                rectF.bottom = mTempGraphBean.endY;
                            }
                        }
                    } else {
                        mTempGraphBean.endX = mMoveX;
                        mTempGraphBean.endY = mMoveY;
                        mTempGraphBean.endPoint.x = mMoveX;
                        mTempGraphBean.endPoint.y = mMoveY;
                        // 此时的rectList应该只有一条数据
                        if (mTempGraphBean.rectFList.size() == 1) {
                            mTempGraphBean.rectFList.get(0).right = mMoveX;
                            mTempGraphBean.rectFList.get(0).bottom = mMoveY;
                        }
                    }
                }
            }
        }
    }

    /**
     * 移动图形，包括缩放等
     */
    private void touchMoveGraphOperate() {
        if (mCurrentGraphBean != null && mGraphPath.size() > 0) {
            float dx = mMoveX - mCurrentGraphBean.clickPoint.x;
            float dy = mMoveY - mCurrentGraphBean.clickPoint.y;
            // 如果是拖拽模式
            changeGraphRect(dx, dy);
        }
    }

    /**
     * 拖拽缩放图形的操作
     *
     * @param offsetX x偏移量
     * @param offsetY y偏移量
     */
    private void changeGraphRect(float offsetX, float offsetY) {
        if (mCurrentGraphBean != null && mGraphPath.size() > 0) {
            int rectSize = mCurrentGraphBean.rectFList.size();
            if (rectSize > 0) {
                RectF tempRectF = mCurrentGraphBean.rectFList.get((rectSize - 1));
                if (mGraphMode == MODE.DRAG) {
                    mCurrentGraphBean.startX = mCurrentGraphBean.startPoint.x + offsetX;
                    mCurrentGraphBean.startY = mCurrentGraphBean.startPoint.y + offsetY;
                    mCurrentGraphBean.endX = mCurrentGraphBean.endPoint.x + offsetX;
                    mCurrentGraphBean.endY = mCurrentGraphBean.endPoint.y + offsetY;
                } else if (mGraphMode == MODE.DRAG_START) {
                    // 如果是拖动起始点
                    // 只需要变化起始点的坐标即可
                    if (mCurrentGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                        // 如果是笔直线，只在该对应方向进行平移
                        if (mCurrentGraphBean.startX == mCurrentGraphBean.endX) {
                            // 垂直的直线,x不变，只变化y
                            mCurrentGraphBean.startY = mCurrentGraphBean.startPoint.y + offsetY;
                        } else {
                            // 水平的直线，y不变
                            mCurrentGraphBean.startX = mCurrentGraphBean.startPoint.x + offsetX;
                        }
                    } else {
                        mCurrentGraphBean.startX = mCurrentGraphBean.startPoint.x + offsetX;
                        mCurrentGraphBean.startY = mCurrentGraphBean.startPoint.y + offsetY;
                    }
                    Log.d(TAG, "拖动起始点");
                } else if (mGraphMode == MODE.DRAG_END) {
                    // 如果是拖动终点
                    // 只需要变化终点的坐标即可
                    if (mCurrentGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                        // 如果是笔直线，只在该对应方向进行平移
                        if (mCurrentGraphBean.startX == mCurrentGraphBean.endX) {
                            // 垂直的直线,x不变，只变化y
                            mCurrentGraphBean.endY = mCurrentGraphBean.endPoint.y + offsetY;
                        } else {
                            // 水平的直线，y不变
                            mCurrentGraphBean.endX = mCurrentGraphBean.endPoint.x + offsetX;
                        }
                    } else {
                        mCurrentGraphBean.endX = mCurrentGraphBean.endPoint.x + offsetX;
                        mCurrentGraphBean.endY = mCurrentGraphBean.endPoint.y + offsetY;
                    }
                    Log.d(TAG, "拖动终点");
                }
                // 更新围绕的rect
                if (mCurrentGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                    if (mCurrentGraphBean.startX == mCurrentGraphBean.endX) {
                        // 垂直的直线
                        tempRectF.left = mCurrentGraphBean.startX - mDotRadius;
                        tempRectF.top = mCurrentGraphBean.startY;
                        tempRectF.right = mCurrentGraphBean.startX + mDotRadius;
                        tempRectF.bottom = mCurrentGraphBean.endY;
                    } else {
                        // 水平的直线
                        tempRectF.left = mCurrentGraphBean.startX;
                        tempRectF.top = mCurrentGraphBean.startY - mDotRadius;
                        tempRectF.right = mCurrentGraphBean.endX;
                        tempRectF.bottom = mCurrentGraphBean.startY + mDotRadius;
                    }
                } else {
                    tempRectF.left = mCurrentGraphBean.startX;
                    tempRectF.top = mCurrentGraphBean.startY;
                    tempRectF.right = mCurrentGraphBean.endX;
                    tempRectF.bottom = mCurrentGraphBean.endY;
                }
                Log.d(TAG, "拖动图形rect");
            }
        }
    }

    /**
     * 判断是否点击到图形，点击到的话，将点击的图形显示在最上层
     * 改成拖动模式，并设置是否点击图形为true
     */
    private void judgeGraphClick() {
        // 倒过来循环图形列表
        int clickIndex = -1;
        // 点击时，默认没选中
        mIsClickOnGraph = false;
        mGraphMode = MODE.NONE;
        // 重要！：当mCurrentGraphBean不为空时，在Down时，会new 一个记录操作的rect，典型的场景就是
        // 两个图形，第一个选中，此时点击第二个，就会多new 一个rect，因此在mCurrentGraphBean不是null，则需要移除这个多余的操作记录
        if (mCurrentGraphBean != null) {
            if (mCurrentGraphBean.rectFList.size() > 1) {
                mCurrentGraphBean.rectFList.remove(mCurrentGraphBean.rectFList.size() - 1);
            }
        }
        for (int i = mGraphPath.size() - 1; i > -1; i--) {
            DrawGraphBean graphBean = mGraphPath.get(i);
            if (graphBean != null) {
                RectF rectF = null;
                // 通过rect的contains判断，rect需要左上右下从小到大才能正确判断
                if (graphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                    // 笔直的线的x或者y会有之一相等
                    if (graphBean.rectFList.size() > 0) {
                        RectF lastedRect = graphBean.rectFList.get(graphBean.rectFList.size() - 1);
                        rectF = new RectF(Math.min(lastedRect.left, lastedRect.right),
                                Math.min(lastedRect.top, lastedRect.bottom),
                                Math.max(lastedRect.left, lastedRect.right),
                                Math.max(lastedRect.top, lastedRect.bottom));
                    }
                } else {
                    rectF = new RectF(Math.min(graphBean.startX, graphBean.endX),
                            Math.min(graphBean.startY, graphBean.endY),
                            Math.max(graphBean.startX, graphBean.endX),
                            Math.max(graphBean.startY, graphBean.endY));
                }
                // 通过rect来判断
                // 看看点击的点是否在这个框框里(这个框框的判定很奇怪，需要坐标从小到大）
                if (rectF != null && rectF.contains(mMoveX, mMoveY)) {
                    // 点击到文字了，说明接下来会进行图形操作，给mCurrentGraphBean赋新值
                    mCurrentGraphBean = graphBean;
                    Log.d(TAG, "点击到文字啦！" + rectF);
                    mIsClickOnGraph = true;
                    mGraphMode = MODE.DRAG;
                    clickIndex = i;
                    // 直接跳出当前循环
                    break;
                }
            } else {
                mCurrentGraphBean = null;
                mIsClickOnGraph = false;
                mGraphMode = MODE.NONE;
            }
        }
        if (mGraphPath.size() <= 0) {
            mCurrentGraphBean = null;
            mIsClickOnGraph = false;
            mGraphMode = MODE.NONE;
        }

        // 把在操作的图形添加到栈底
        if (mIsClickOnGraph && mCurrentGraphBean != null && clickIndex > -1 && clickIndex < mGraphPath.size()) {
            mGraphPath.remove(clickIndex);
            mGraphPath.add(mCurrentGraphBean);
        } else {
            mCurrentGraphBean = null;
            mIsClickOnGraph = false;
            mGraphMode = MODE.NONE;
        }
    }

    /**
     * 获取围绕直线的path
     */
    private Path getLineRectPath(DrawGraphBean graphBean) {
        if (graphBean == null) {
            return null;
        }
        Path path = null;
        if (graphBean.type == GRAPH_TYPE.LINE) {
            path = new Path();
            // 由于坐标参照系标准不同，需要进行转化
            // 获取与x的夹角
            float radius = getRotation(graphBean.startX, graphBean.startY, graphBean.endX, graphBean.endY);
            if (radius <= 0) {
                radius += 180;
            }
            radius -= 90;
            // 转换成canvas的圆坐标系
            radius += 180;
            float[] point1 = CustomerViewUtils.getCoordinatePoint(graphBean.startX, graphBean.startY, mDotRadius, radius);
            float[] point4 = CustomerViewUtils.getCoordinatePoint(graphBean.startX, graphBean.startY, mDotRadius, radius + 180);
            float[] point2 = CustomerViewUtils.getCoordinatePoint(graphBean.endX, graphBean.endY, mDotRadius, radius);
            float[] point3 = CustomerViewUtils.getCoordinatePoint(graphBean.endX, graphBean.endY, mDotRadius, radius + 180);
            path.moveTo(point1[0], point1[1]);
            path.lineTo(point2[0], point2[1]);
            path.lineTo(point3[0], point3[1]);
            path.lineTo(point4[0], point4[1]);
            path.lineTo(point1[0], point1[1]);
            path.close();
        }
        return path;
    }

    /**
     * 撤销操作
     *
     * @return 撤销后剩余可以撤销的步骤
     */
    public int revertPath() {
        // 撤销只针对当前模式的撤销，不是所有步骤的撤销
        int size = 0;
        if (!mIsClickOnGraph && mMode == MODE.DOODLE_MODE) {
            size = mDoodlePath.size();
            if (size > 0) {
                mDoodlePath.remove(size - 1);
            }
        } else if (!mIsClickOnGraph && mMode == MODE.MOSAIC_MODE) {
            size = mMosaicPath.size();
            if (size > 0) {
                mMosaicPath.remove(size - 1);
            }
        } else if (mIsClickOnGraph && mCurrentGraphBean != null) {
            // 如果当前选中了某个图形,但是此处的rectList不能删光，需要保留一条最初的数据
            size = mCurrentGraphBean.rectFList.size();
            if (size > 1) {
                mCurrentGraphBean.rectFList.remove(size - 1);
                int currentSize = size - 1;
                // 移除之后，需要给起点和终点重新赋值
                if (currentSize > 0) {
                    RectF rectF = mCurrentGraphBean.rectFList.get(currentSize - 1);
                    if (mCurrentGraphBean.type == GRAPH_TYPE.DIRECT_LINE) {
                        // 判断水平还是垂直
                        if (mCurrentGraphBean.startX == mCurrentGraphBean.endX) {
                            // 垂直的，
                            mCurrentGraphBean.startY = rectF.top;
                            mCurrentGraphBean.endY = rectF.bottom;
                            mCurrentGraphBean.startPoint.y = rectF.top;
                            mCurrentGraphBean.endPoint.y = rectF.bottom;
                            mCurrentGraphBean.startX = (rectF.left + rectF.right) / 2;
                            mCurrentGraphBean.endX = (rectF.left + rectF.right) / 2;
                            mCurrentGraphBean.startPoint.x = (rectF.left + rectF.right) / 2;
                            mCurrentGraphBean.endPoint.x = (rectF.left + rectF.right) / 2;
                        } else {
                            // 水平的，y相关的都不需要变化
                            mCurrentGraphBean.startX = rectF.left;
                            mCurrentGraphBean.endX = rectF.right;
                            mCurrentGraphBean.startPoint.x = rectF.left;
                            mCurrentGraphBean.endPoint.x = rectF.right;
                            mCurrentGraphBean.startY = (rectF.top + rectF.bottom) / 2;
                            mCurrentGraphBean.endY = (rectF.top + rectF.bottom) / 2;
                            mCurrentGraphBean.startPoint.y = (rectF.top + rectF.bottom) / 2;
                            mCurrentGraphBean.endPoint.y = (rectF.top + rectF.bottom) / 2;
                        }
                    } else {
                        mCurrentGraphBean.startX = rectF.left;
                        mCurrentGraphBean.startY = rectF.top;
                        mCurrentGraphBean.endX = rectF.right;
                        mCurrentGraphBean.endY = rectF.bottom;
                        mCurrentGraphBean.startPoint.x = rectF.left;
                        mCurrentGraphBean.startPoint.y = rectF.top;
                        mCurrentGraphBean.endPoint.x = rectF.right;
                        mCurrentGraphBean.endPoint.y = rectF.bottom;
                    }
                }
            }
            size -= 1;
        }
        postInvalidate();
        return size;
    }

    /**
     * 删除选中的某个图形
     */
    public void deleteCurrentGraph() {
        if (mIsClickOnGraph && mCurrentGraphBean != null && mGraphPath.size() > 0) {
            mGraphPath.remove(mCurrentGraphBean);
            mCurrentGraphBean = null;
            mIsClickOnGraph = false;
            mGraphMode = MODE.NONE;
        }
        postInvalidate();
    }

    /**
     * 清楚正在操作的图形的焦点
     */
    public void clearGraphFocus() {
        mCurrentGraphBean = null;
        mIsClickOnGraph = false;
        mGraphMode = MODE.NONE;
        postInvalidate();
    }

    /**
     * 计算两点对应的角度
     *
     * @return float
     */
    public float getRotation(float startX, float startY, float endX, float endY) {
        float deltaX = startX - endX;
        float deltaY = startY - endY;
        // 计算坐标点相对于圆点所对应的弧度
        double radians = Math.atan2(deltaY, deltaX);
        // 把弧度转换成角度
        return (float) Math.toDegrees(radians);
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
     * 设置所要画图形的种类
     *
     * @param graphType graphType
     */
    public void setGraphType(GRAPH_TYPE graphType) {
        // 设置模式前，先把焦点给clear一下
        clearGraphFocus();
        this.mCurrentGraphType = graphType;
    }

    /**
     * 画笔直笔直的直线
     */
    private float[] getDirectLineEndPoint(float sx, float sy, float ex, float ey) {
        float degrees = getRotation(sx, sy, ex, ey);
        float[] point = new float[2];
        // 根据角度画直线
        if ((-45 <= degrees && degrees <= 45) || degrees >= 135 || degrees <= -135) {
            // 往x轴的方向绘制，即y值不变
            point[0] = ex;
            point[1] = sy;
        } else {
            // 往y轴的方向绘制，即x值不变
            point[0] = sx;
            point[1] = ey;
        }
        return point;
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

    public void setMode(MODE mode) {
        // 设置模式前，先把焦点给clear一下
        clearGraphFocus();
        this.mMode = mode;
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

    /**
     * 设置是否可编辑
     *
     * @param editable 能否编辑
     */
    public void setEditable(boolean editable) {
        this.mIsEditable = editable;
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
        NONE, GRAPH_MODE, DOODLE_MODE, MOSAIC_MODE, DRAG, ZOOM, DRAG_START, DRAG_END
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

    public enum GRAPH_TYPE {
        RECT, OVAL, ARROW, LINE, DIRECT_LINE
    }

    public interface DoodleCallback {
        void onDrawStart();

        void onDrawing();

        void onDrawComplete();
    }

    /**
     * 记录图形绘制的实例类
     */
    class DrawGraphBean {
        // 这四个点是实时变化的，用来绘制图形的四个点
        public float startX, startY, endX, endY;
        public GRAPH_TYPE type;
        public Paint paint;
        public PointF clickPoint = new PointF();
        // 两个点的变量，用于平移缩放的操作，只有在UP后，才会同步四个点的值
        PointF startPoint = new PointF();
        PointF endPoint = new PointF();
        // 是否是符合要求的图形
        boolean isPass = false;
        // 用于撤销移动缩放的操作
        List<RectF> rectFList = new ArrayList<>();

        DrawGraphBean(float startX, float startY, float endx, float endY, GRAPH_TYPE type, Paint paint) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endx;
            this.endY = endY;
            this.type = type;
            this.paint = paint;
            this.startPoint.x = startX;
            this.startPoint.y = startY;
            this.endPoint.x = endx;
            this.endPoint.y = endY;
            rectFList.add(new RectF(startX, startY, endx, endY));
        }
    }
}
