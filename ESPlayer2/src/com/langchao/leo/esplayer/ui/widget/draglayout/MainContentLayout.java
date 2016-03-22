package com.langchao.leo.esplayer.ui.widget.draglayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.langchao.leo.esplayer.ui.widget.draglayout.DragLayout.Status;

/**
 * 主视图，与DragLayout配合使用，解决ViewPager和侧滑冲突的问题
 * @author 碧空
 *
 */
@SuppressLint("ClickableViewAccessibility")
public class MainContentLayout extends RelativeLayout {

    private DragLayout mDragLayout;

    public MainContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainContentLayout(Context context) {
        super(context);
    }

    public void setDragLayout(DragLayout mDragLayout) {
        this.mDragLayout = mDragLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mDragLayout.getStatus() == Status.Close){
            return super.onInterceptTouchEvent(ev);
        }else {
            return true;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mDragLayout.getStatus() == Status.Close){
            return super.onTouchEvent(event);
        }else {
            if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP){
                mDragLayout.close();
            }
        }
        return true;
    }

}