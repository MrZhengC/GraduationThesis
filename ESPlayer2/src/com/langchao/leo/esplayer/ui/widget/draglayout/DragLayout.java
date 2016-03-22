package com.langchao.leo.esplayer.ui.widget.draglayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.view.ViewHelper;

/**
 * 通过ViewDragHelper实现的侧滑控件<br>
 * 页面中的顶级布局文件，
 * @author 碧空
 * 
 */
@SuppressLint("ClickableViewAccessibility")
public class DragLayout extends ViewGroup {

	// 单位dp
	private static final float MIN_DRAWER_MARGIN = 32;

	/**
     * Minimum velocity that will be detected as a fling
     * 可以被检测为fling的最小速度
     */
    private static final float MIN_FLING_VELOCITY = 400;
	
    /**
     * drawer离父容器右边的最小外边距（当左侧视图设置为match_parent时有效）
     */
	private int mMinDrawerMargin;
    
	// 左侧视图
	private View mLeftContentView;
	// 主视图
    private View mMainContentView;
    
    // 左侧布局宽度
    private int mLeftContentViewWidth;
    
    // 当前状态
    private Status mStatus = Status.Close;
    
    private GestureDetectorCompat mDetectorCompat;
    
    private ViewDragHelper mDragHelper;
    
    /**
     * drawer显示出来的占自身的百分比[0, 1]
     */
    private float mLeftMenuOnScrren = 0;
    
    /**
     * 布局Drag回调
     */
    private OnLayoutDragingListener mListener;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        final float density = getResources().getDisplayMetrics().density;
        final float minVel = MIN_FLING_VELOCITY * density;
        mMinDrawerMargin = (int) (MIN_DRAWER_MARGIN * density + 0.5f);
        
        //创建ViewDragHelper的静态工厂方法有两个，一个带float sensitivity参数，一个不带，这个参数的默认值是1.0f
        //第一个参数是父布局，也就是这里的DragLayout，给个this即可
        //第二个参数(若有),是灵敏度，默认值是1.0f
        //第三个参数是回调接口
        //ViewDragHelper.create(this,1.0f,mCallback);
        //对应参数：父布局、灵感度、回调
        mDragHelper = ViewDragHelper.create(this, 0.5f, mDragCallBack);
        
        mDetectorCompat = new GestureDetectorCompat(getContext(), mGestureListener);
        
        //设置edge_left track
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        //设置minVelocity
        mDragHelper.setMinVelocity(minVel);
        
    }
    
    /**
     * widthMeasureSpec 
     * 
     * 高2位为mode<br>
     * 		UNSPECIFIED(未定义、未指定):宽高都设置为0，或者没有设置时为该模式<br>
     * 		EXACTLY(恰好的)：设置有固定宽高或者match_parent为该模式，子视图总是充满剩余空间，故子视图的大小是确定的，也即父控件决定子视图大小，	<br>
     * 		AT_MOST(至多):wrap_content时为该模式，即子视图可以达到任意想要达到的宽高	<br>
     * 低30位为size
     * 
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        // 直接设置获得宽高，此方法被调用之后 mMeasuredWidth mMeasuredHeight就可使用了
        setMeasuredDimension(widthSize, heightSize);
        
        // 测量左侧视图
        final View leftMenuView = getChildAt(0);
        MarginLayoutParams lp = (MarginLayoutParams)
                leftMenuView.getLayoutParams();

        final int drawerWidthSpec = getChildMeasureSpec(widthMeasureSpec,
                mMinDrawerMargin + lp.leftMargin + lp.rightMargin,
                lp.width);
        final int drawerHeightSpec = getChildMeasureSpec(heightMeasureSpec,
                lp.topMargin + lp.bottomMargin,
                lp.height);
        leftMenuView.measure(drawerWidthSpec, drawerHeightSpec);

        // 测量主视图
        final View contentView =  getChildAt(1);
        lp = (MarginLayoutParams) contentView.getLayoutParams();
        final int contentWidthSpec = MeasureSpec.makeMeasureSpec(
                widthSize - lp.leftMargin - lp.rightMargin, MeasureSpec.EXACTLY);
        final int contentHeightSpec = MeasureSpec.makeMeasureSpec(
                heightSize - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY);
        contentView.measure(contentWidthSpec, contentHeightSpec);

        mLeftContentView = leftMenuView;
        mMainContentView = contentView;

    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    	final View contentView = mMainContentView;
    	final View menuView = mLeftContentView;
    	
    	MarginLayoutParams lp = (MarginLayoutParams) contentView.getLayoutParams();
    	contentView.layout(lp.leftMargin, lp.topMargin,
    			lp.leftMargin + contentView.getMeasuredWidth(),
    			lp.topMargin + contentView.getMeasuredHeight());

    	lp = (MarginLayoutParams) menuView.getLayoutParams();

    	final int menuWidth = menuView.getMeasuredWidth();
    	int childLeft = -menuWidth + (int) (menuWidth * mLeftMenuOnScrren);

    	menuView.layout(childLeft, lp.topMargin, childLeft + menuWidth,
    			lp.topMargin + menuView.getMeasuredHeight());
    }

    /**
     * 拦截触摸事件时调用，在ViewGroup里面定义，非View方法
     * 
     * 返回为false时事件会传递给子控件的onInterceptTouchEvent()；
     * 返回值为true时事件会传递给当前控件的onTouchEvent()
     * 
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	
    	final int action = MotionEventCompat.getActionMasked(ev);
    	// 当触摸事件被取消或者手指抬起时取消

    	if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
    		mDragHelper.cancel();
    		return false;
    	}

    	// 传入手势检测器（GestureDetector）
        boolean onTouchEvent = mDetectorCompat.onTouchEvent(ev);
        
        //将Touch事件传递给ViewDragHelper
        return mDragHelper.shouldInterceptTouchEvent(ev) & onTouchEvent;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            //将Touch事件传递给ViewDragHelper
            mDragHelper.processTouchEvent(event);
        } catch (Exception e) {
        }
        return true;
    }
    
    /**
     * 在此处理back事件
     */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			// 当此时Drawer处于打开状态时，按下back键后关闭Drawer
			if (mStatus == Status.Open){
				//ESLog.e("dispatchKeyEvent, drawlayout is opened");
				close(true);
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	
	@Override
    public void computeScroll() {
        // 高频率调用，决定是否有下一个变动等待执行
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 重新获取左侧视图的宽度
        mLeftContentViewWidth = mLeftContentView.getMeasuredWidth();
    }

    /**
     * 填充结束时获得两个子布局的引用
     */
    @Override
    protected void onFinishInflate() {
        int childCount = getChildCount();
        // 必要的检验
        if (childCount < 2) {
            throw new IllegalStateException(
                    "You need two childrens in your content");
        }

        if (!(getChildAt(0) instanceof ViewGroup)
                || !(getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalArgumentException(
                    "Your childrens must be an instance of ViewGroup");
        }

        mLeftContentView = getChildAt(0);
        mMainContentView = getChildAt(1);
    }
    
    
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new MarginLayoutParams(p);
    }
    
    /**
     * 关闭Drawer
     */
    public void close() {
        close(true);
    }

    /**
     * 打开Drawer
     */
    public void open() {
        open(true);
    }

    /**
     * 关闭Drawer
     * @param isSmooth 是否平滑关闭
     */
    public void close(boolean isSmooth) {
    	mLeftMenuOnScrren = 0;
        if (isSmooth) {
            // 执行动画，返回true代表有未完成的动画, 需要继续执行
            if (mDragHelper.smoothSlideViewTo(mLeftContentView, -mLeftContentViewWidth, 0)) {
                // 注意：参数传递根ViewGroup
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
        	invalidate();
        }
    }

    /**
     * 打开Drawer
     * @param isSmooth 是否平滑
     */
    public void open(boolean isSmooth) {
    	mLeftMenuOnScrren = 1;
        if (isSmooth) {
            if (mDragHelper.smoothSlideViewTo(mLeftContentView, 0, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
        	invalidate();
        }
    }

   
	private SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener() {
        
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
			
            if((Math.abs(distanceX) > Math.abs(distanceY)) && distanceX<0 && mStatus==Status.Close){
                return true;
            }else if((Math.abs(distanceX) > Math.abs(distanceY)) && distanceX >0 &&mStatus==Status.Open){
                return true;
            }else {
                return false;
            }
            
        }
    };
    
    /**
     * 当前所处的状态
     * @author 碧空
     */
    public static enum Status {
        Open, Close, Draging
    }

    public Status getStatus() {
        return mStatus;
    }

    protected void setStatus(Status mStatus) {
        this.mStatus = mStatus;
    }
    
    /**
     * 回调接口
     * @author 碧空
     *
     */
    public static interface OnLayoutDragingListener {
        void onOpen();

        void onClose();

        void onDraging(float percent);
    }

    public void setOnLayoutDragingListener(OnLayoutDragingListener l) {
        mListener = l;
    }
    
    /**
     * 伴随动画：使用NineOldAndroids做兼容
     * @param percent
     */
	private void animViews(float percent) {
        final View mContent = mMainContentView;
        final View mMenu = mLeftContentView;
        final float scale = 1 - percent;

        final float rightScale = 1.0f - 0.2f * (1 - scale);// 右侧从1.0-->0.8
        final float leftScale = 1 - 0.3f * scale;// 左侧从0.7-->1.0
        
        // 左侧边栏动画
        // 设置缩放中心轴
        ViewHelper.setPivotX(mMenu, mMenu.getMeasuredWidth());
        ViewHelper.setPivotY(mMenu, mMenu.getMeasuredHeight() / 2);
        // 设置缩放比例
        ViewHelper.setScaleX(mMenu, leftScale);
        ViewHelper.setScaleY(mMenu, leftScale);
        // 设置透明度 0.6 --> 1.0
        ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
        float dx = -mLeftContentViewWidth/4 + mLeftContentViewWidth/4 * percent;
        ViewHelper.setTranslationX(mLeftContentView, dx);

        // 右侧内容页面动画
        ViewHelper.setTranslationX(mContent, mMenu.getMeasuredWidth() * (1 - scale));
        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent, mContent.getMeasuredHeight() / 2);
        mContent.invalidate();
        ViewHelper.setScaleX(mContent, rightScale);
        ViewHelper.setScaleY(mContent, rightScale);
        
    }
    
    /**
     * 更新状态
     * @param percent
     * @return
     */
    private Status updateStatus(float percent) {
        if (percent == 0) {
            mStatus = Status.Close;
        }else if (percent == 1){
        	mStatus = Status.Open;
        } else {
            mStatus = Status.Draging;
        }
        return mStatus;
    }
    
    /**
     * 分发拖动事件
     * @param percent
     */
    protected void dispatchDragEvent(float percent) {
    	// 开启动画
    	animViews(percent);

        if (mListener != null) {
            mListener.onDraging(mLeftMenuOnScrren);
        }

        final Status lastStatus = mStatus;
        final Status newStatus = updateStatus(percent);
        if (newStatus != lastStatus) {
            if (mListener == null) {
                return;
            }
            if (lastStatus == Status.Draging) {
                if (mStatus == Status.Close) {
                    mListener.onClose();
                } else if (mStatus == Status.Open) {
                    mListener.onOpen();
                }
            }
        }
    }
    
    /**
     * 拖动左侧视图，根据拖动偏移移动主视图
     */
    private final ViewDragHelper.Callback mDragCallBack = new ViewDragHelper.Callback() {
    	
		@Override
		public int getOrderedChildIndex(int index) {
			return super.getOrderedChildIndex(index);
		}
    	
    	/**
         * 如何返回true则表示可以捕获该view，你可以根据传入的第一个view参数决定哪些可以捕获
         * @param child 当前触摸着的View,
         * @param pointerId 多点触控的触点ID，可以不去处理
         * @return true 表示该Child可以被拖动
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId){
        	// 设置只有左侧视图可以拖动
            return child == mLeftContentView;
        }
    	
        /**
         * 当captureview被捕获时回调
         */
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			super.onViewCaptured(capturedChild, activePointerId);
		}
        
		/**
		 * 当触摸到边界时回调
		 */
		@Override
		public void onEdgeTouched(int edgeFlags, int pointerId) {
			super.onEdgeTouched(edgeFlags, pointerId);
		}
		
		/**
		 * 如果你想在边缘滑动的时候根据滑动距离移动一个子view，
		 * 可以通过实现onEdgeDragStarted方法，并在onEdgeDragStarted方法中手动指定要移动的子View
		 */
        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
        	// 当处于关闭状态时，指定LeftContentView可以移动
        	if (mStatus == Status.Close){
        		mDragHelper.captureChildView(mLeftContentView, pointerId);
        	}
        }
        
        /**
         * true的时候会锁住当前的边界，false则unLock。
         */
		@Override
		public boolean onEdgeLock(int edgeFlags) {
			return super.onEdgeLock(edgeFlags);
		}
		
    	/**
    	 * clampViewPositionHorizontal,clampViewPositionVertical
    	 * 在该方法中对子视图child移动的边界进行控制，left , top 分别为即将移动到的位置
    	 * 
    	 */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            final int newLeft = Math.max(-child.getWidth(), Math.min(left, 0));
            return newLeft;
        }
        
		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			return super.clampViewPositionVertical(child, top, dy);
		}

		/*
		 * 如果你可以拖动的视图是可点击的，也即添加了clickable = true，要重写下面这两个方法：
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
		     return getMeasuredWidth()-child.getMeasuredWidth();
		}
		@Override
		public int getViewVerticalDragRange(View child) {
		     return getMeasuredHeight()-child.getMeasuredHeight();
		}//*/
		
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
    		final int childWidth = changedView.getWidth();
    		final float offset = (float) (childWidth + left) / childWidth;
    		mLeftMenuOnScrren = offset;
    		
    		//当View正在被拖动时，分发拖动事件
    		dispatchDragEvent(offset);
    		
    		//ESLog.e("changedView == left ? " + (changedView == mLeftContentView));
        	//ESLog.e("changedView == main ? " + (changedView == mMainContentView));
    		
        	// 我们只在检测到边缘拖动时设置了左侧布局可以被移动
        	// 所有我们设置当左侧视图偏移大于0时才显示
    		changedView.setVisibility(offset == 0 ? View.INVISIBLE : View.VISIBLE);
    		// 刷新界面
    		invalidate();
        }
        
        /**
         * 手指抬起时被调用
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
        	
        	//ESLog.e("releasedChild == left ? " + (releasedChild == mLeftContentView));
        	//ESLog.e("releasedChild == main ? " + (releasedChild == mMainContentView));
        	
        	final int childWidth = releasedChild.getWidth();
        	float offset = (childWidth + releasedChild.getLeft()) * 1.0f / childWidth;
        	mDragHelper.settleCapturedViewAt(xvel > 0 || xvel == 0 && offset > 0.5f ? 0 : -childWidth, releasedChild.getTop());
        	invalidate();
        }
        
        /**
         * 当DragState发生变化时被调用
         */
		@Override
		public void onViewDragStateChanged(int state) {
			super.onViewDragStateChanged(state);
		}
        
    };
    
}