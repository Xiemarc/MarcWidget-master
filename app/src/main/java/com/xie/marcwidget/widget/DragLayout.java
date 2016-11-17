package com.xie.marcwidget.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;


/**
 * 描述：自定义draglayout、
 * 作者：Marc on 2016/11/11 09:50
 * 邮箱：aliali_ha@yeah.net
 */
public class DragLayout extends FrameLayout {
    private static final String TAG = "DragLayout";
    private ViewDragHelper mDragHelper;
    private ViewGroup mLeftContent;
    private ViewGroup mMainContent;
    private int mHeight;//屏幕的高度
    private int mWidth;//屏幕的宽度
    private int mRange;//可滑动拖拽的范围
    private onDragStatusChangeListener mListener;
    /**
     * 初始状态
     */
    private Status mStatus = Status.Close;

    /**
     * 滑动的三种状态 关闭，打开，拖拽
     */
    public static enum Status {
        Close, Open, Draging
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status mStatus) {
        this.mStatus = mStatus;
    }

    public interface onDragStatusChangeListener {
        void onClose();

        void onOpen();

        void onDraging(float percent);
    }

    public void setDragStatusListener(onDragStatusChangeListener mListener) {
        this.mListener = mListener;
    }

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //a.初始化,通過静态方法创建
        mDragHelper = ViewDragHelper.create(this, mCallBack);
    }

    //c.重写事件
    ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {
        //1.是否根据返回结果决定是否拖拽

        /**
         * @param child  当前被拖拽的view
         * @param pointerId 区分多点触摸的手指id
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //当尝试去抓view的时候调用
            //返回true 让左侧面板和主面板都能实现拖拽
            //这里让谁滑动就返回谁
            return true;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            //当childview被捕获的时候调用
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         * 获取横向拖拽范围 ,这里可以设定拖动范围
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            //返回拖拽的范围，不对拖拽进行真正的限制,仅仅决定了动画执行速度
            return mRange;
        }

        /**
         * 根据建议值修正将要移动到的位置--横向的
         * @param child 当前拖拽的view
         * @param left 新的位置的（fix值）建议值  新的left=child.getLeft()[老的left]+dx[变化量]
         * @param dx 跟刚刚位置的差值，变化量
         * @return return哪个值，就移动到哪个位置,这里横向的位置 .返回0就是不移动
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mMainContent) {
                //只能拖动主面板
                left = fixLeft(left);
            }
            return left;
        }
//
//        /**
//         * 根据建议值修正将要移动的位置--纵向的
//         * @param child 当前拖拽的view
//         * @param top 新的位置的top值。新的top=child.getTop【老的top值】+dy【变化量】
//         * @param dy 跟刚刚位置的差值，变化量
//         * @return 哪个值，就移动到哪个位置,这里纵向的位置。返回0就是不移动
//         */
//        @Override
//        public int clampViewPositionVertical(View child, int top, int dy) {
//            Log.d(TAG, String.format("clampViewPositionVertical的旧的top值：%s，变化量dx：%s,新的top:%S", child.getTop(), dy, top));
//            return top;
//        }

        /**
         * 当view位置改变的时候要做的事情，更新状态，伴随动画，重绘界面。
         * @param changedView 改变的view
         * @param left 新的左边值
         * @param top
         * @param dx 水平方向变化量
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            int newLeft = left;
            if (changedView == mLeftContent) {
                //如果当前拖拽的是左侧面板,把当前变化量传递给mMainContent
                newLeft = mMainContent.getLeft() + dx;
            }
            // 进行修正
            newLeft = fixLeft(newLeft);
            if (changedView == mLeftContent) {
                // 当左面板移动之后, 再强制放回去.,左侧面板一直在原位置
                mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
                mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
            }
            //更新状态，执行动画
            dispatchDragEvent(newLeft);
            //2.2的低版本的。没有调用重绘.所以每次修改值后，进行重绘
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
            //向右滑动left为正值。
            if (xvel == 0 && mMainContent.getLeft() > mRange / 2.0f) {
                //xvel位0，说明是慢速的滑动。如果滑动的距离大于mRange的一半。就打开左侧面板
                open();
            } else if (xvel > 0) {
                //xvel大于0，说明是往右快读的滑动
                open();
            } else {
                // 当速度小于0且滑动的距离小于Range的一半。就打开左侧面板
                // 当xvel速度是0 关闭
                close();
            }
        }
    };

    /**
     * 动画
     *
     * @param newLeft
     */
    private void dispatchDragEvent(int newLeft) {
        //开始从0开始。结束时mRange
        float percent = newLeft * 1.0f / mRange;
        if (null != mListener) {
            mListener.onDraging(percent);
        }
        //上一个状态
        Status preStatus = mStatus;
        //更新状态，执行回调
        mStatus = updateStatus(percent);
        if (mStatus != preStatus) {
            //当前状态跟上一个状态不同，说明状态发生变化
            if (mStatus == Status.Close) {
                //当前变为关闭状态,回调关闭方法
                if (mListener != null) {
                    mListener.onClose();
                }
            } else if (mStatus == Status.Open) {
                if (null != mListener) {
                    mListener.onOpen();
                }
            }
        }
        //percent 是当前相对于mRange的比例
        animViews(percent);
    }

    /**
     * 更新状态
     *
     * @param percent
     * @return
     */
    private Status updateStatus(float percent) {
        if (percent == 0) {
            return Status.Close;
        } else if (percent == 1.0f) {
            return Status.Open;
        } else {
            return Status.Draging;
        }
    }

    /**
     * 给view做动画
     *
     * @param percent
     */
    private void animViews(float percent) {
        //左面板
        //缩放动画
        ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
        ViewHelper.setScaleY(mLeftContent, 0.5f + 0.5f * percent);
        //平移动画
        ViewHelper.setTranslationX(mLeftContent, evaluate(percent, -mWidth / 2.0f, 0));
        //透明度动画 0.5-1.0f
        ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.5f, 1.0f));
        //主内容面版
        //缩放动画
        ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
        ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));
        //背景颜色:亮度变化（颜色变化）  这里面的2个值开始值跟结束值。可以自定义
        getBackground().setColorFilter((Integer) evaluateColor(percent, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }


    //b. =========自定义控件对事件的处理开始，=================//

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //交给自己去处理
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    //// TODO: 2016/11/11 这里可能会跟Barchart的左后滑动事件冲突
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
    //b. =========自定义控件对事件的处理结束=================//


    //c. =========拿到左侧面板和主面板开始=================//
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //onFinishInflate该方法在加载完xml文件后回调
        //这里需要有容错性检查（至少有2个view，子view必须是viewgroup）
        //这里异常举例 空指针异常,OOM异常。非法状态异常，非法参数异常，非法访问异常，类型转换异常
        if (getChildCount() < 2) {
            throw new IllegalStateException("布局至少有2个孩子");
        }
        if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalArgumentException("子view必须是viewgroup的子类");
        }
        mLeftContent = (ViewGroup) getChildAt(0);
        mMainContent = (ViewGroup) getChildAt(1);
    }
    //c. =========拿到左侧面板和主面板结束=================//

    /**
     * 当尺寸跟上次有变化的时候调用。在onmeasure后执行,为了得到可拖拽的最大尺寸
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
        mHeight = getMeasuredHeight();
        //屏幕宽度，原理同上,也可以使用mMainContent.getHeight()和getWidth()
        mWidth = getMeasuredWidth();
        //可滑动的范围
        mRange = (int) (mWidth * 0.6);
    }

    /**
     * 计算滑动相关
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        // 2. 持续平滑动画 (高频率调用)、模板代码，死记住
        if (mDragHelper.continueSettling(true)) {
            //  如果返回true, 动画还需要继续执行、模板代码，死记住
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    //========================计算相关======================//

    /**
     * 根据滑动的范围修正左边left值
     *
     * @param left
     * @return left的值
     */
    private Integer fixLeft(int left) {
        if (left < 0) {
            return 0;
        } else if (left > mRange) {
            return mRange;
        }
        return left;
    }


    /**
     * 关闭
     */
    public void close() {
        close(true);
    }

    /**
     * 关闭
     *
     * @param isSmooth true：平滑动画。false：不开启平滑动画
     */
    public void close(boolean isSmooth) {
        int finalLeft = 0;
        if (isSmooth) {
            //触发平滑动画
            if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
                // 返回true代表还没有移动到指定位置, 需要刷新界面.
                // 参数传this(child所在的ViewGroup)
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
        }
    }

    /**
     * 开启
     */
    public void open() {
        open(true);
    }

    /**
     * 平滑的开启动画
     *
     * @param isSmooth
     */
    public void open(boolean isSmooth) {
        int finalLeft = mRange;
        if (isSmooth) {
            // 1. 触发一个平滑动画
            if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
                // 返回true代表还没有移动到指定位置, 需要刷新界面.
                // 参数传this(child所在的ViewGroup)
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
        }
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


    /**
     * 颜色变化过度
     *
     * @param fraction   过度的范围
     * @param startValue 开始值
     * @param endValue   结束值
     * @return
     */
    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int) ((startA + (int) (fraction * (endA - startA))) << 24) |
                (int) ((startR + (int) (fraction * (endR - startR))) << 16) |
                (int) ((startG + (int) (fraction * (endG - startG))) << 8) |
                (int) ((startB + (int) (fraction * (endB - startB))));
    }
}
