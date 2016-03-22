package com.langchao.leo.esplayer.ui.frags;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.langchao.leo.esplayer.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 推荐Fragment
 * 此界面暂时没用功能，注销
 *
 */
public class RecommendFragment extends Fragment {

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.fragment_recommend, container, false);
		
		initUI(mRootView);
		
		return mRootView;
	}
	@Override
	public void onResume() {
		super.onResume();
		// 友盟统计页面开始
		MobclickAgent.onPageStart(RadioFragment.class.getSimpleName());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// 友盟统计页面介绍
		MobclickAgent.onPageEnd(RadioFragment.class.getSimpleName());
	}	
	/**
	 * 初始化界面
	 * @param mRootView
	 */
	private void initUI(View mRootView) {
		
	}
	
}
