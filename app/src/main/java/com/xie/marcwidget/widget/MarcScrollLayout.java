package com.xie.marcwidget.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * des:仿极客360首页
 * author: marc
 * date:  2016/11/17 20:24
 * email：aliali_ha@yeah.net
 */

public class MarcScrollLayout extends LinearLayout {
    private static final String TAG = "MarcScrollLayout";
    private ViewDragHelper mDragHelper;
    private ViewGroup mTopContent;//上半部分布局
    private ViewGroup mBottomContent;//下半部分布局
    private int mHeight;//屏幕高度
    private int mWidth;//屏幕宽度
    private int mRange;//可以被滑动拖拽的范围

    public MarcScrollLayout(Context context) {
        this(context, null);
    }

    public MarcScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarcScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        this.setOrientation(VERTICAL);
        mWidth = getScreenWidth(context);
        mHeight = getScreenHeight(context);
        //屏幕高度的一半
        mRange = (int) (mHeight * 0.2);
        Log.d(TAG, "MarcScrollLayout: mRange" + mRange);
        mDragHelper = ViewDragHelper.create(this, mCallBack);
    }

    //建立拖拽监听
    ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {

        /**
         * 尝试去拖拽的View
         * @param child 当前被拖拽的view
         * @param pointerId 区分多点触控
         * @return 返回谁就让谁能被拖拽
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //这里只能拖拽下面的viewgroup
            Log.d(TAG, "tryCaptureView: " + child);
            return child == mBottomContent ? true : false;
        }

        /**
         * 当childview被捕获时候调用
         * @param capturedChild
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            Log.d(TAG, "onViewCaptured: " + capturedChild);
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         * 竖向被拖拽的范围
         * @param child
         * @return 返回可以被拖拽的范围 ,这是个值，这里没用
         */
        @Override
        public int getViewVerticalDragRange(View child) {
            return mRange;
        }

        /**
         * 根据建议值修正将要移动的位置--纵向的
         * @param child 当前拖拽的view
         * @param top 新的位置的top值。新的top=child.getTop【老的top值】+dy【变化量】
         * @param dy 跟刚刚位置的差值，变化量
         * @return 哪个值，就移动到哪个位置,这里纵向的位置。返回0就是不移动
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //新的值=老的值+移动的值
//            top = child.getTop() - dy;
            if (child == mBottomContent) {
                top = fixTop(top);
            }
            Log.d(TAG, "clampViewPositionVertical: oldTop: " + child.getTop() + " dy: " + dy + " top: " + top);
            return top;
        }

        /**
         * 当view位置改变的时候要做的事情，更新状态，伴随动画，重绘界面。
         * @param changedView 改变的view
         * @param left 新的左边值
         * @param top 新的上面的值
         * @param dx 水平方向变化量
         * @param dy 数值方向的变化量
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            int newTop = top;
            newTop = mBottomContent.getTop() + dy;
            newTop = fixTop(newTop);
//            /更新状态，执行动画
            dispatchDragEvent(newTop);
            //兼容低版本。每次修改值后重绘
            invalidate();
        }

        /**
         * 当view被释放的时候，处理的事情(执行动画巴拉巴拉巴的)
         * @param releasedChild  被释放的子view（松手就是释放）
         * @param xvel 水平方向的速度（轻轻的停止，和快速滑动停止） 向右为正
         * @param yvel 竖向方向的速度 向下为正
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
        }
    };



    /**
     * 向下滑动
     *
     * @param top 向下滑动和向上滑动的2个最值确定
     * @return
     */
    private int fixTop(int top) {
//        top 小于0向上滑动,大于0 向下滑动
//        mRange是向上滑动最终停止的地方  向上滑动的top值是mRange-100;
        if (top < (mRange - 100)) {
            return mRange;
        }
        if (top > (mHeight - 500)) {
            return mHeight - 500;
        }
        return top;
    }

    /**
     * 动画
     * @param newTop
     */
    private void dispatchDragEvent(int newTop) {
        //开始从0开始。结束时mRange
        float percent = mRange/newTop*1.0f;
        ViewHelper.setScaleX(mTopContent,evaluate(percent, 0.5f, 1.0f));
        ViewHelper.setScaleY(mTopContent,evaluate(percent, 0.5f, 1.0f));
    }

    /**
     * 估值器
     *
     * @param fraction   过度的范围
     * @param startValue 开始值
     * @param endValue   结束值
     * @return
     */
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //交给自己去处理
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            mDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回true，是为了能够持续的接收到事件
        return true;
    }

    /**
     * 加载完成xml文件后就调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() < 2) {
            throw new IllegalStateException("布局至少应该有2个孩子");
        }
        if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)) {
            //2个布局都应该是是viewgroup，否则报错
            throw new IllegalStateException("子view必须是viewgroup");
        }
        mTopContent = (ViewGroup) getChildAt(0);
        mBottomContent = (ViewGroup) getChildAt(1);
    }

    /**
     * 当尺寸跟上次有变化的时候就调用
     * 在onMeasure后执行。为了得到可拖拽滑动的最大尺寸
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //当尺寸变化有变化的时候调用
        //屏幕高度(因为这里是用的布局是根局部了)

//        mHeight = getScreenWidth();
        //屏幕宽度，原理同上,也可以使用mMainContent.getHeight()和getWidth()
//        mWidth = getMeasuredWidth();
        //可滑动的范围
//        mRange = (int) (mHeight * 0.6);
    }

    /**
     * 有滑动效果必须有这个方法
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            //  如果返回true, 动画还需要继续执行、模板代码，死记住
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    public int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

}
