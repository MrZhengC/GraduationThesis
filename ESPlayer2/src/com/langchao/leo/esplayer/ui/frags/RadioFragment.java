package com.langchao.leo.esplayer.ui.frags;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.bean.FMChannelBean;
import com.langchao.leo.esplayer.bean.FMChannelCategoryBean;
import com.langchao.leo.esplayer.core.FMChannelParser;
import com.langchao.leo.esplayer.interfaces.IAsyncLoadListener;
import com.langchao.leo.esplayer.ui.adapter.CarouselFigurePagerAdapter;
import com.langchao.leo.esplayer.ui.adapter.ChannelCategoryAdapter;
import com.langchao.leo.esplayer.ui.widget.autoscrollviewpager.AutoScrollViewPager;
import com.umeng.analytics.MobclickAgent;

/**
 * 电台Fragment
 * @author 碧空
 *
 */
public class RadioFragment extends Fragment {
	
	// 默认id
	private static final int DEFAULT_DOT_ID =  0x12<<4;
	
	private ListView mContentLv = null;
	
	private View mContentHeader = null;
	
	private ChannelCategoryAdapter mCategoryAdapter = null;
	
	// 轮播图
	private AutoScrollViewPager mHotContentVp = null;
	// 指示器
	private RadioGroup mVPIndicatorPanel = null;
	
	// 热门推荐频道数据，用于轮播图
	private List<FMChannelBean> mHotChannelList = null;
	
	// 轮播图adapter
	private CarouselFigurePagerAdapter mVPagerAdapter = null;
	
	// 轮播图View
	private List<View> mPagerViews = new ArrayList<View>();
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.fragment_radio, container, false);
		
		initUI(mRootView);
		
		initData();
		
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
		
		mContentLv = (ListView) mRootView.findViewById(R.id.lv_radio_content);
		
		mContentHeader = LayoutInflater
				.from(getContext())
				.inflate(R.layout.layout_radio_top, mContentLv, false);
		initHeaderView(mContentHeader);
		mContentLv.addHeaderView(mContentHeader);
		
		mCategoryAdapter = new ChannelCategoryAdapter(getContext());
		
		mContentLv.setAdapter(mCategoryAdapter);
		
	}
	
	/**
	 * 初始化顶部视图
	 * @param mContentHeader2
	 */
	private void initHeaderView(View mContentHeader) {
		
		mHotContentVp = (AutoScrollViewPager) mContentHeader.findViewById(R.id.avp_radio);
		mHotContentVp.setCycle(true);
		mHotContentVp.setInterval(2000);
		mHotContentVp.startAutoScroll();
		mHotContentVp.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_TO_PARENT);
		
		mVPagerAdapter = new CarouselFigurePagerAdapter(mPagerViews);
		
		mHotContentVp.setAdapter(mVPagerAdapter);
		mHotContentVp.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				// 设置选中
				mVPIndicatorPanel.check(DEFAULT_DOT_ID + arg0);
			}
			
			@Override public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override public void onPageScrollStateChanged(int arg0) {}
		});
		
		mVPIndicatorPanel = (RadioGroup) mContentHeader.findViewById(R.id.panel_vp_indicator);

	}
	
	/**
	 * 生成轮播图
	 * @return
	 */
	protected View createPagerView(){
		return LayoutInflater.from(getContext()).inflate(
				R.layout.layout_hot_channel, 
					mHotContentVp, false);
	}
	
	/**
	 * 生成点指示器
	 * @return
	 */
	protected RadioButton createVPIndicatorDot(){
		RadioButton rbtn = (RadioButton) LayoutInflater.from(getContext())
				.inflate(R.layout.dot_radiobutton, 
						mVPIndicatorPanel, false);
		rbtn.setId(DEFAULT_DOT_ID + mVPIndicatorPanel.getChildCount());
		return rbtn;
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		new FMChannelParser(getContext(), new IAsyncLoadListener<List<FMChannelCategoryBean>>() {
			@Override
			public void onSuccess(List<FMChannelCategoryBean> t) {
				if (t != null) {
					// 设置频道分类ListView数据
					mCategoryAdapter.addItems(t);
				
					// 获取顶部轮播图数据
					for (FMChannelCategoryBean category : t) {
						final List<FMChannelBean> channels = category.getChannelList();
						int index = new Random().nextInt(channels.size());
						if (mHotChannelList == null) {
							mHotChannelList = new ArrayList<FMChannelBean>();
						}
						mHotChannelList.add(channels.get(index));
					}
					
					// 设置轮播图数据
					for (FMChannelBean channel : mHotChannelList) {
						if(mPagerViews==null){
							mPagerViews = new ArrayList<View>();
						}
						final View pagerView = createPagerView();
						pagerView.setTag(channel);
						mPagerViews.add(pagerView);
						
						mVPIndicatorPanel.addView(createVPIndicatorDot());
					}
					mVPagerAdapter.setData(mPagerViews);
					mVPagerAdapter.notifyDataSetChanged();
					
					mHotContentVp.setCurrentItem(0);
					mVPIndicatorPanel.check(DEFAULT_DOT_ID);
				}
			}
			@Override
			public void onFailure(String msg) {
			}
		}).execute();
	}
	
}
