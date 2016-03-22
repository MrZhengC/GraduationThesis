package com.langchao.leo.esplayer.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 可嵌套的GridView
 * @author 碧空
 *
 */
public class NestedGridView extends GridView {

	public NestedGridView(Context context) {
		super(context);
	}

	public NestedGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public NestedGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		
		super.onMeasure(widthMeasureSpec, heightSpec);
	}
	
}
