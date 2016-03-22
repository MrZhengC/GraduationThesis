package com.langchao.leo.esplayer.interfaces;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Activity交互接口
 * @author 碧空
 *
 */
public interface IActivityInteraction {
	/**
	 * 添加Fragment
	 * @param containerViewId
	 * @param frag
	 * @param tag
	 */
	void addFragment(@IdRes int containerViewId, Fragment frag, @Nullable String tag);
	
	/**
	 * 替换Fragment
	 * @param containerView
	 * @param frag
	 * @param tag
	 */
	void replaceFragment(@IdRes int containerViewId, Fragment frag, @Nullable String tag);
	
	/**
	 * 移除Fragment
	 * @param frag
	 */
	void removeFragment(Fragment frag);
	
	/**
	 * 将回退栈栈顶事务出栈
	 */
	void popBackStack();

	/**
	 * 将回退栈中名称为‘name’的事务，及以上所有的事务都出栈
	 * @param name
	 */
	void popBackStack(@Nullable String name);
	
}
