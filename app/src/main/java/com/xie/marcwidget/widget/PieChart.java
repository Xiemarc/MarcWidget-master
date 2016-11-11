package com.xie.marcwidget.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xie.marcwidget.entity.PieDataEntity;
import com.xie.marcwidget.utils.CalculateUtil;

import java.util.Arrays;
import java.util.List;

/**
 * des:饼图
 * author: marc
 * date:  2016/10/28 22:21
 * email：aliali_ha@yeah.net
 */
public class PieChart extends View {
    /**
     * 视图的宽和高
     */
    private int mTotalWidth, mTotalHeight;
    /**
     * 绘制区域的半径
     */
    private float mRadius;

    private Paint mPaint, mLinePaint, mTextPaint;

    private Path mPath;
    /**
     * 扇形的绘制区域
     */
    private RectF mRectF;
    /**
     * 点击之后的扇形的绘制区域
     */
    private RectF mRectFTouch;

    private List<PieDataEntity> mDataList;
    /**
     * 所有的数据加起来的总值
     */
    private float mTotalValue;
    /**
     * 起始角度的集合
     */
    private float[] angles;
    /**
     * 手点击的部分的position
     */
    private int position = -1;
    /**
     * 点击监听
     */
    private OnItemPieClickListener mOnItemPieClickListener;

    float mCurrentAngle = 0f;
    // 中心点
    float mCenterX;
    float mCenterY;// 最后触屏坐标
    private float mLastX = 0;
    private float mLastY = 0;
    float currentSweep = 0;

    public void setOnItemPieClickListener(OnItemPieClickListener onItemPieClickListener) {
        mOnItemPieClickListener = onItemPieClickListener;
    }

    public interface OnItemPieClickListener {
        void onClick(int position);
    }

    public PieChart(Context context) {
        super(context);
        init(context);
    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mRectF = new RectF();
        mRectFTouch = new RectF();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setColor(Color.BLACK);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(24);

        mPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w - getPaddingLeft() - getPaddingRight();
        mTotalHeight = h - getPaddingTop() - getPaddingBottom();

        mRadius = (float) (Math.min(mTotalWidth, mTotalHeight) / 2 * 0.7);

        mRectF.left = -mRadius;
        mRectF.top = -mRadius;
        mRectF.right = mRadius;
        mRectF.bottom = mRadius;

        mRectFTouch.left = -mRadius - 16;
        mRectFTouch.top = -mRadius - 16;
        mRectFTouch.right = mRadius + 16;
        mRectFTouch.bottom = mRadius + 16;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDataList == null)
            return;
        canvas.translate(mTotalWidth / 2, mTotalHeight / 2);
        //绘制饼图的每块区域
        drawPiePath(canvas);
    }

    /**
     * 绘制饼图的每块区域 和文本
     *
     * @param canvas
     */
    private void drawPiePath(Canvas canvas) {
        //起始地角度
//        float startAngle = 0;
        float startAngle = currentSweep;
        for (int i = 0; i < mDataList.size(); i++) {
            float sweepAngle = mDataList.get(i).getValue() / mTotalValue * 360 - 1;//每个扇形的角度
            mPath.moveTo(0, 0);
            if (position - 1 == i) {
                mPath.arcTo(mRectFTouch, startAngle, sweepAngle);
            } else {
                mPath.arcTo(mRectF, startAngle, sweepAngle);
            }
            mPaint.setColor(mDataList.get(i).getColor());
            canvas.drawPath(mPath, mPaint);
            canvas.drawArc(mRectF, startAngle, sweepAngle, true, mPaint);
            mPath.reset();
            Log.i("toRadians", (startAngle + sweepAngle / 2) + "****" + Math.toRadians(startAngle + sweepAngle / 2));
            float pxs = (float) (mRadius * Math.cos(Math.toRadians(startAngle + sweepAngle / 2)));
            float pys = (float) (mRadius * Math.sin(Math.toRadians(startAngle + sweepAngle / 2)));
            float pxt = (float) ((mRadius + 30) * Math.cos(Math.toRadians(startAngle + sweepAngle / 2)));
            float pyt = (float) ((mRadius + 30) * Math.sin(Math.toRadians(startAngle + sweepAngle / 2)));
            angles[i] = startAngle;
            startAngle += sweepAngle + 1;
            //绘制线和文本
            canvas.drawLine(pxs, pys, pxt, pyt, mLinePaint);
            float res = mDataList.get(i).getValue() / mTotalValue * 100;
            //提供精确的小数位四舍五入处理。
            double resToRound = CalculateUtil.round(res, 2);
            float v = startAngle % 360;
            if (startAngle % 360.0 >= 90.0 && startAngle % 360.0 <= 270.0) {
                canvas.drawText(resToRound + "%", pxt - mTextPaint.measureText(resToRound + "%"), pyt, mTextPaint);
            } else {
                canvas.drawText(resToRound + "%", pxt, pyt, mTextPaint);
            }
        }
    }

    public void setDataList(List<PieDataEntity> dataList) {
        this.mDataList = dataList;
        mTotalValue = 0;
        for (PieDataEntity pieData : mDataList) {
            mTotalValue += pieData.getValue();
        }
        angles = new float[mDataList.size()];
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取当前坐标
        float currentX = event.getX();
        float currentY = event.getY();
        // 获取view偏移量
        float left = 0;
        float top = 0;
        // 相对位置
        float relativeX = currentX - left;
        float relativeY = currentY - top;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = relativeX;
                mLastY = relativeY;
                float x = event.getX() - (mTotalWidth / 2);
                float y = event.getY() - (mTotalHeight / 2);
                float touchAngle = 0;
                if (x < 0 && y < 0) {  //2 象限
                    touchAngle += 180;
                } else if (y < 0 && x > 0) {  //1象限
                    touchAngle += 360;
                } else if (y > 0 && x < 0) {  //3象限
                    touchAngle += 180;
                }
                //Math.atan(y/x) 返回正数值表示相对于 x 轴的逆时针转角，返回负数值则表示顺时针转角。
                //返回值乘以 180/π，将弧度转换为角度。
                touchAngle += Math.toDegrees(Math.atan(y / x));
                if (touchAngle < 0) {
                    touchAngle = touchAngle + 360;
                }
                float touchRadius = (float) Math.sqrt(y * y + x * x);
                if (touchRadius < mRadius) {
                    position = -Arrays.binarySearch(angles, (touchAngle)) - 1;
                    invalidate();
                    if (mOnItemPieClickListener != null) {
                        mOnItemPieClickListener.onClick(position - 1);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float c = (float) Math.sqrt((Math.pow(Math.abs((mLastX - relativeX)), 2) + Math.pow(Math.abs((mLastY - relativeY)), 2)));
                float a = (float) Math.sqrt((Math.pow(Math.abs(mLastX - mCenterX), 2) + Math.pow(Math.abs((mLastY - mCenterY)), 2)));
                float b = (float) Math.sqrt((Math.pow(Math.abs(relativeX - mCenterX), 2) + Math.pow(Math.abs((relativeY - mCenterY)), 2)));
                float cosc = (float) (Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b);
                // 确定方向
                if (rotateDirection(relativeX, relativeY)) {
                    mCurrentAngle = (float) (mCurrentAngle + Math.acos(cosc) * 180 / Math.PI);
                } else {
                    mCurrentAngle = (float) (mCurrentAngle - Math.acos(cosc) * 180 / Math.PI);
                }
                // 重置
                mLastX = relativeX;
                mLastY = relativeY;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return super.onTouchEvent(event);
        }
        return true;
    }


    /**
     * 判断方向
     *
     * @param x
     * @param y
     * @return
     */
    private boolean rotateDirection(float x, float y) {
        float first_angle = getAngle((mLastX - mCenterX), (mLastY - mCenterY));
        float second_angle = getAngle((x - mCenterX), (y - mCenterY));
        if ((90 > first_angle) && (second_angle > 270)) {
            first_angle = first_angle + (float) 360;
        }
        if ((90 > second_angle) && (first_angle > 270)) {
            second_angle = second_angle + (float) 360;
        }

        return second_angle > first_angle;
    }

    private float getAngle(float x, float y) {
        if ((x == 0) && (y == 0)) {
            return 0;
        }
        float angle = (float) (Math.atan(y / x) * 180 / Math.PI);
        if (x == 0) {
            if (y > 0) {
                return 90;
            } else {
                return 270;
            }
        }
        if (x > 0) {
            if (y < 0) {
                return (float) 360 + angle;
            }
        }
        if (x < 0) {
            if (y > 0) {
                return (float) 180 + angle;
            } else if (y == 0) {
                return 180;
            } else if (y < 0) {
                return (float) 180 + angle;
            }
        }
        return angle;
    }

}
