### 首先是侧滑面板。

先看效果图

![](http://i.imgur.com/Z60TE85.gif)

这里使用的是ViewDragHelper类。注释非常清楚。可以逐行的看。

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
