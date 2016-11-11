package com.xie.marcwidget.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.xie.marcwidget.entity.ChartEntity;
import com.xie.marcwidget.utils.CalculateUtil;
import com.xie.marcwidget.utils.DensityUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * des:线形图
 * author: marc
 * date:  2016/10/28 22:21
 * email：aliali_ha@yeah.net
 */
public class LineChart extends View {
    private Context mContext;
    /**
     * 背景的颜色
     */
    private static final int BG_COLOR = Color.parseColor("#EEEEEE");
    /**
     * 视图的宽和高
     */
    private int mTotalWidth, mTotalHeight;
    /**
     * x轴 y轴 起始坐标
     */
    private float xStartIndex, yStartIndex;
    /**
     * 图表绘制区域的顶部和底部  图表绘制区域的最大高度
     */
    private float paintTop, paintBottom, maxHeight;
    /**
     * 左边和上边的边距
     */
    private int leftMargin, topMargin;
    /**
     * 画笔 背景，轴 ，线 ，text ,点
     */
    private Paint bgPaint, axisPaint, linePaint, textPaint, pointPaint;

    /**
     * 点击的显示圆圈的画笔
     */
    private Paint bigCirclePaint;
    private Paint smallCirclePaint;
    /**
     * 原点的半径
     */
    private static final float RADIUS = 8;
    /**
     * 上下左右的白色部分
     */
    private Rect leftWhiteRect, rightWhiteRect, topWhiteRect, bottomWhiteRect;
    private List<ChartEntity> mData;//数据集合
    /**
     * 右边的最大和最小值
     */
    private int maxRight, minRight;
    /**
     * item中的最大值
     */
    private float maxValueInItems;
    /**
     * 最大分度值
     */
    private float maxDivisionValue;
    /**
     * 线的路径
     */
    Path linePath;
    /**
     * 向右边滑动的距离
     */
    private float leftMoving;
    //左边Y轴的单位
    private String leftAxisUnit = "单位";
    /**
     * 两个点之间的距离
     */
    private int space;
    /**
     * 保存点的x坐标
     */
    private List<Integer> linePoints = new ArrayList<>();

    /**
     * 保存点的Y坐标
     */
    private List<Integer> linePointy = new ArrayList<>();
    /**
     * 最后一次的x坐标
     */
    private float lastPointX;
    /**
     * 当前移动的距离
     */
    private float movingThisTime = 0.0f;

    /* 用户点击到了无效位置 */
    public static final int INVALID_POSITION = -1;
    private OnItemLineClickListener mOnItemLineClickListener;
    private GestureDetector mGestureListener;
    /**
     * 是否绘制点击效果
     */
    private boolean isDrawBorder;
    /**
     * 点击的地方
     */
    private int mClickPosition;
    protected MarkerView mMarkerView;
    /**
     * 是否话markerview，默认绘制
     */
    protected boolean mDrawMarkerViews = true;

    /**
     * 是否是平滑的曲线
     */
    private boolean isSmooth;

    public void setOnItemLineClickListener(OnItemLineClickListener onRangeBarClickListener) {
        this.mOnItemLineClickListener = onRangeBarClickListener;
    }

    public interface OnItemLineClickListener {
        void onClick(int position);
    }


    public LineChart(Context context) {
        super(context);
        init(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        mGestureListener = new GestureDetector(context, new RangeBarOnGestureListener());
        mContext = context;
        leftMargin = DensityUtil.dip2px(context, 16);
        topMargin = DensityUtil.dip2px(context, 30);

        bgPaint = new Paint();
        bgPaint.setColor(Color.WHITE);

        axisPaint = new Paint();
        axisPaint.setStrokeWidth(DensityUtil.dip2px(context, 1));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(DensityUtil.dip2px(getContext(), 10));

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(DensityUtil.dip2px(context, 1));
        linePaint.setColor(Color.BLUE);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);

        linePath = new Path();

        bigCirclePaint = new Paint();//点击显示大圆的图
        bigCirclePaint.setAntiAlias(true);
        bigCirclePaint.setStyle(Paint.Style.FILL);
        smallCirclePaint = new Paint();
        smallCirclePaint.setAntiAlias(true);
        smallCirclePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mTotalWidth = w - getPaddingLeft() - getPaddingRight();
        mTotalHeight = h - getPaddingTop() - getPaddingBottom();
        setNeedHeight();
        leftWhiteRect = new Rect(0, 0, 0, mTotalHeight);
        rightWhiteRect = new Rect(mTotalWidth - leftMargin * 2, 0, mTotalWidth, mTotalHeight);
        topWhiteRect = new Rect(0, 0, mTotalWidth, topMargin / 2);
        bottomWhiteRect = new Rect(0, (int) yStartIndex, mTotalWidth, mTotalHeight);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 设置矩形的顶部 底部 右边Y轴的3部分每部分的高度
     */
    private void setNeedHeight() {
        paintTop = topMargin * 2;
        paintBottom = mTotalHeight - topMargin / 2;
        maxHeight = paintBottom - paintTop;
        yStartIndex = mTotalHeight - topMargin / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        linePoints.clear();
        canvas.drawColor(BG_COLOR);
        if (mData == null) return;
        //得到每个bar的宽度
        getItemsWidth();
        //重置线
        linePath.reset();
        linePath.incReserve(mData.size());
        checkTheLeftMoving();
        canvas.drawRect(bottomWhiteRect, bgPaint);
        canvas.drawRect(topWhiteRect, bgPaint);
        //画中间的白线
        drawWhiteLine(canvas);
        //画线形图
        drawLines(canvas);
        //画线型图

        //画左边和右边的遮罩层
        leftWhiteRect.right = (int) xStartIndex;
        canvas.drawRect(leftWhiteRect, bgPaint);
        canvas.drawRect(rightWhiteRect, bgPaint);
        //画线上的点
        drawCircles(canvas);
        //画左边的Y轴
        canvas.drawLine(xStartIndex, yStartIndex, xStartIndex, topMargin / 2, axisPaint);
        //左边Y轴的单位
        canvas.drawText(leftAxisUnit, xStartIndex, topMargin / 2 - 14, textPaint);
        //画右边的Y轴
        canvas.drawLine(mTotalWidth - leftMargin * 2, yStartIndex, mTotalWidth - leftMargin * 2, topMargin / 2, axisPaint);
        //画左边的Y轴text
        drawLeftYAxis(canvas);
        //画X轴 下面的和上面
        canvas.drawLine(xStartIndex, yStartIndex, mTotalWidth - leftMargin * 2, yStartIndex, axisPaint);
        canvas.drawLine(xStartIndex, topMargin / 2, mTotalWidth - leftMargin * 2, topMargin / 2, axisPaint);
        //画X轴的text
        drawXAxisText(canvas);
    }

    private void drawXAxisText(Canvas canvas) {
        float distance = 0;
        for (int i = 0; i < mData.size(); i++) {
            distance = space * i - leftMoving;
            String text = mData.get(i).getxLabel();
            //当在可见的范围内才绘制
            if ((xStartIndex + distance) >= xStartIndex && (xStartIndex + distance) < (mTotalWidth - leftMargin * 2)) {
                canvas.drawText(text, xStartIndex + distance - textPaint.measureText(text) / 2, paintBottom + DensityUtil.dip2px(getContext(), 10), textPaint);
            }
        }
    }

    private void drawWhiteLine(Canvas canvas) {
        axisPaint.setColor(Color.WHITE);
        float eachHeight = ((paintBottom - topMargin / 2) / 5f);
        for (int i = 1; i <= 5; i++) {
            float startY = paintBottom - eachHeight * i;
            if (startY < topMargin / 2) {
                break;
            }
            canvas.drawLine(xStartIndex, startY, mTotalWidth - leftMargin * 2, startY, axisPaint);
        }
        axisPaint.setColor(Color.BLACK);
    }

    /**
     * 画线形图
     */
    private void drawLines(Canvas canvas) {
        float distance = 0;
        float prePointX = 0;//上一个点的x坐标
        float prePointY = 0;//上一个点的y坐标
        List<Point> listPoint = new ArrayList<>();
        //先计算各个点的坐标
        for (int i = 0; i < mData.size(); i++) {
            distance = space * i - leftMoving;
            linePoints.add((int) (xStartIndex + distance));
            float lineHeight = mData.get(i).getyValue() * maxHeight / maxDivisionValue;//y点的坐标
            if (i == 0) {
                //首先移动到第一个点
                linePath.moveTo(xStartIndex + distance, paintBottom - lineHeight);
            } else {
                Point point = new Point((int) (xStartIndex + distance), (int) (paintBottom - lineHeight));
                listPoint.add(point);
                if (isSmooth) {
                    linePath.quadTo(linePoints.get(i - 1), linePointy.get(i - 1)
                            , linePoints.get(i), linePointy.get(i));
                } else {
                    linePath.lineTo(xStartIndex + distance, paintBottom - lineHeight);
                }
            }
        }
        canvas.drawPath(linePath, linePaint);
    }

    /**
     * 画线上的点
     */
    private void drawCircles(Canvas canvas) {
        for (int i = 0; i < mData.size(); i++) {
            pointPaint.setColor(Color.parseColor("#6FCCEE"));
            //把点的坐标y轴放进集合中
            linePointy.add((int) (paintBottom - mData.get(i).getyValue() * maxHeight / maxDivisionValue));
            //只有在可见的范围内才绘制
            if (linePoints.get(i) >= xStartIndex && linePoints.get(i) < (mTotalWidth - leftMargin * 2)) {
                canvas.drawCircle(linePoints.get(i), paintBottom - mData.get(i).getyValue() * maxHeight / maxDivisionValue, RADIUS, pointPaint);
            }
        }
        if (isDrawBorder) {
            //确定点击的边框
            bigCirclePaint.setColor(Color.parseColor("#0E6DB0"));
            smallCirclePaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawCircle(linePoints.get(mClickPosition), linePointy.get(mClickPosition), RADIUS + 3, bigCirclePaint);
            canvas.drawCircle(linePoints.get(mClickPosition), linePointy.get(mClickPosition), RADIUS, smallCirclePaint);
            drawMarkerViews(canvas, mClickPosition);
        }
    }

    /**
     * 绘制markerview
     *
     * @param canvas
     * @param mClickPosition
     */
    private void drawMarkerViews(Canvas canvas, int mClickPosition) {
        if (mMarkerView == null || !mDrawMarkerViews) {
            return;
        }
        //这里把所有的markerview都绘制上去
        for (int i = 0; i < mData.size(); i++) {
            if (i == mClickPosition) {
                ChartEntity entry = mData.get(i);
                mMarkerView.refreshContent(entry);
                mMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                mMarkerView.layout(0, 0, mMarkerView.getMeasuredWidth(),
                        mMarkerView.getMeasuredHeight());
                //画markerview
                //需要解决 显示的时候才绘制
                mMarkerView.draw(canvas, linePoints.get(i), linePointy.get(i) - 10);
            }
        }
    }

    /**
     * 画Y轴上的text (1)当最大值大于1 的时候 将其分成5份 计算每个部分的高度  分成几份可以自己定
     * （2）当最大值大于0小于1的时候  也是将最大值分成5份
     * （3）当为0的时候使用默认的值
     *
     * @param canvas
     */
    private void drawLeftYAxis(Canvas canvas) {
        float eachHeight = ((paintBottom - topMargin / 2) / 5f);
        if (maxValueInItems > 1) {
            for (int i = 1; i <= 5; i++) {
                float startY = paintBottom - eachHeight * i;
                if (startY < topMargin / 2) {
                    break;
                }
//                canvas.drawLine(xStartIndex, startY, mTotalWidth - leftMargin*2, startY, axisPaint);
                BigDecimal maxValue = new BigDecimal(maxDivisionValue);
                BigDecimal fen = new BigDecimal(0.2 * i);
                long textValue = maxValue.multiply(fen).longValue();
                String text = String.valueOf(textValue);
                canvas.drawText(text, xStartIndex - textPaint.measureText(text) - 5, startY + textPaint.measureText("0"), textPaint);
            }
        } else if (maxValueInItems > 0 && maxValueInItems <= 1) {
            for (int i = 1; i <= 5; i++) {
                float startY = paintBottom - eachHeight * i;
                if (startY < topMargin / 2) {
                    break;
                }
//                canvas.drawLine(xStartIndex, startY, mTotalWidth - leftMargin*2, startY, axisPaint);
                float textValue = CalculateUtil.numMathMul(maxDivisionValue, (float) (0.2 * i));
                String text = String.valueOf(textValue);
                canvas.drawText(text, xStartIndex - textPaint.measureText(text) - 5, startY + textPaint.measureText("0"), textPaint);
            }
        } else {
            for (int i = 1; i <= 5; i++) {
                float startY = paintBottom - eachHeight * i;
                //                canvas.drawLine(xStartIndex, startY, mTotalWidth - leftMargin*2, startY, axisPaint);
                String text = String.valueOf(10 * i);
                canvas.drawText(text, xStartIndex - textPaint.measureText(text) - 5, startY + textPaint.measureText("0"), textPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPointX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float movex = event.getRawX();
                movingThisTime = lastPointX - movex;
                leftMoving = leftMoving + movingThisTime;
                lastPointX = movex;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                new Thread(new SmoothScrollThread(movingThisTime)).start();
                break;
            default:
                return super.onTouchEvent(event);
        }
        if (mGestureListener != null) {
            mGestureListener.onTouchEvent(event);
        }
        return true;
    }

    /**
     * 检查向左滑动的距离 确保没有画出屏幕
     */
    private void checkTheLeftMoving() {
        if (leftMoving < 0) {
            leftMoving = 0;
        }

        if (leftMoving > (maxRight - minRight)) {
            leftMoving = maxRight - minRight;
        }
    }

    /**
     * 设定两个点之间的间距 和向右边滑动的时候右边的最大距离
     */
    private void getItemsWidth() {
        space = DensityUtil.dip2px(getContext(), 30);
        maxRight = (int) (xStartIndex + space * mData.size());
        minRight = mTotalWidth - leftMargin * 2;
    }

    public void setData(List<ChartEntity> list) {
        this.mData = list;
        //计算最大值
        maxValueInItems = list.get(0).getyValue();
        for (ChartEntity entity : list) {
            if (entity.getyValue() > maxValueInItems) {
                maxValueInItems = entity.getyValue();
            }
        }
        getRange(maxValueInItems);
        invalidate();
    }

    /**
     * 得到柱状图的最大和最小的分度值
     *
     * @param maxValueInItems
     */
    private void getRange(float maxValueInItems) {
        int scale = CalculateUtil.getScale(maxValueInItems);//获取这个最大数 数总共有几位
        float unScaleValue = (float) (maxValueInItems / Math.pow(10, scale));//最大值除以位数之后剩下的值  比如1200/1000 后剩下1.2

        maxDivisionValue = (float) (CalculateUtil.getRangeTop(unScaleValue) * Math.pow(10, scale));//获取Y轴的最大的分度值
        xStartIndex = CalculateUtil.getDivisionTextMaxWidth(maxDivisionValue, mContext) + 20;
    }

    /**
     * 左右滑动的时候 当手指抬起的时候  使滑动慢慢停止 不会立刻停止
     */
    private class SmoothScrollThread implements Runnable {
        float lastMoving;
        boolean scrolling = true;

        private SmoothScrollThread(float lastMoving) {
            this.lastMoving = lastMoving;
            scrolling = true;
        }

        @Override
        public void run() {
            while (scrolling) {
                long start = System.currentTimeMillis();
                lastMoving = (int) (0.9f * lastMoving);
                leftMoving += lastMoving;

                checkTheLeftMoving();
                postInvalidate();

                if (Math.abs(lastMoving) < 5) {
                    scrolling = false;
                }

                long end = System.currentTimeMillis();
                if (end - start < 20) {
                    try {
                        Thread.sleep(20 - (end - start));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setIsSmooth(boolean isSmooth) {
        this.isSmooth = isSmooth;
        this.invalidate();
    }

    public boolean getIsSmooth() {
        return this.isSmooth;
    }

    /**
     * 手势监听器
     *
     * @author A Shuai
     */
    private class RangeBarOnGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int position = identifyWhichItemClick(e.getX(), e.getY());
            if (position != INVALID_POSITION && mOnItemLineClickListener != null) {
                mOnItemLineClickListener.onClick(position);
                setClicked(position);
                invalidate();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

    }

    /**
     * 根据点击的手势位置识别是第几个点被点击
     *
     * @param x 手势点击的x坐标
     * @param y 手势点击的y 坐标
     * @return -1时表示点击的是无效位置
     */
    private int identifyWhichItemClick(float x, float y) {
        float xPoint = 0;//点的x坐标
        float yPoint = 0;//点的y坐标
        if (mData != null) {
            for (int i = 0; i < mData.size(); i++) {
                xPoint = linePoints.get(i);//圆心x坐标
                yPoint = xPoint + 10;//圆心y坐标
                //如果点击的x、y坐标在第i个点的坐标范围内，就返回当前position
//                if (x < xPoint - 10 ) {
//                    //如果点击的x坐标的位置不在当前点的大致范围内。跳出
//                    break;
//                }
//                if (y < yPoint - 10 ) {
//                    break;
//                }
//                //如果点击的x和y的范围在以xPoint，yPoint为圆心10半径范围内区域。就返回当前position
//                if (x >= xPoint - 10 && x <= xPoint + 10 && y >= yPoint - 10 && y <= yPoint + 10) {
//                    return i;
//                }
//                if (x < (xPoint - 10)) {
//                    break;
//                }
                if ((xPoint - 10) <= x && x <= yPoint) {
                    return i;
                }
            }
        }
        return INVALID_POSITION;
    }

    /**
     * 设置选中的位置
     *
     * @param position
     */
    public void setClicked(int position) {
        isDrawBorder = true;
        mClickPosition = position;
    }

    /**
     * 设置是否话markerview
     *
     * @return
     */
    public void setIsDrawMarkerView(boolean isDrawMarker) {
        this.mDrawMarkerViews = isDrawMarker;
    }

    /**
     * 画markerview
     *
     * @param v
     */
    public void setMarkerView(MarkerView v) {
        mMarkerView = v;
    }

    /**
     * 启动和关闭硬件加速   在绘制View的时候支持硬件加速,充分利用GPU的特性,使得绘制更加平滑,但是会多消耗一些内存。
     *
     * @param enabled
     */
    public void setHardwareAccelerationEnabled(boolean enabled) {

        if (android.os.Build.VERSION.SDK_INT >= 11) {

            if (enabled)
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            else
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            Log.e("error",
                    "Cannot enable/disable hardware acceleration for devices below API level 11.");
        }
    }
}
