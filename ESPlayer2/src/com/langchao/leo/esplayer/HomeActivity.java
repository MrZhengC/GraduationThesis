package com.langchao.leo.esplayer;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.langchao.leo.esplayer.app.ESApplication;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.core.PlayerService;
import com.langchao.leo.esplayer.core.PlayerService.PlayerBinder;
import com.langchao.leo.esplayer.db.MusicTableHelper;
import com.langchao.leo.esplayer.interfaces.IActivityInteraction;
import com.langchao.leo.esplayer.ui.frags.LeftMenuFragment;
import com.langchao.leo.esplayer.ui.frags.MineFragment;
import com.langchao.leo.esplayer.ui.frags.PlayerFragment;
import com.langchao.leo.esplayer.ui.frags.RadioFragment;
import com.langchao.leo.esplayer.ui.frags.RecommendFragment;
import com.langchao.leo.esplayer.ui.frags.SearchFragment;
import com.langchao.leo.esplayer.ui.widget.draglayout.DragLayout;
import com.langchao.leo.esplayer.ui.widget.draglayout.MainContentLayout;
import com.langchao.leo.esplayer.utils.CommonUtils;
import com.langchao.leo.esplayer.utils.ESLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 *  应用程序主界面
 */
public class HomeActivity extends FragmentActivity implements 
		OnClickListener, IActivityInteraction{

	private DragLayout mDragLayout = null;
	
	private MainContentLayout mMainContentLayout = null;
	
	//private RadioGroup mTabLayoutRg = null;
	private RadioButton mMineRBtn = null;
	private RadioButton mRadioRBtn = null;
//	private RadioButton mRecommendRBtn = null;
	
	private ImageView mHomeMenuIv = null;
	private ImageView mHomeSearchIv = null;
	
	private View mPlayerControllerView = null;
	private ImageView mMusicCoverIv = null;
	private TextView mMusicNameTv = null;
	private TextView mMusicArtistTv = null;
	private CheckBox mPlayOrPauseIv = null;
	private CheckBox mFavoriteIv = null;
	
	private ViewPager mContentVp = null;
	
	private List<Fragment> mFragements = null;
	
	// 音乐播放服务对象（即实际的音乐播放控制器）
	private PlayerService mPlayerService = null;
	
	private ServiceConnection mPlayerConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			ESLog.i("播放服务断开连接成功..");
			mPlayerService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			ESLog.i("播放服务连接成功..");
			// 获取播放服务对象
			mPlayerService = ((PlayerBinder)service).getService();
			// 之后即可操作
			refreshPlayerController();
		}
	};

	// 音乐数据表操作助手
	private MusicTableHelper mMusicTableHelper = null;
	// 本地广播管理器
	private LocalBroadcastManager mPlayerBroadcastManager = null;
	// 播放器
	private PlayerBroadcastReceiver mPlayerBroadcastReceiver = null;
	
	private class PlayerBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (Constants.ACTION_PLAYER_ONSTART.equals(action)) {
				// 开始回调 - 广播
				refreshPlayerController();
			} else if (Constants.ACTION_PLAYER_ONPAUSE.equals(action)) {
				// 暂停回调 - 广播
				mPlayOrPauseIv.setChecked(false);
			} else if (Constants.ACTION_PLAYER_ONRESUME.equals(action)) {
				// 重新开始回调 - 广播
				mPlayOrPauseIv.setChecked(true);
			} else if (Constants.ACTION_PLAYER_ONSTOP.equals(action)) {
				// 停止回调 - 广播
				mPlayOrPauseIv.setChecked(false);
			} else if (Constants.ACTION_PLAYER_BUFFER_PROGRESS.equals(action)) {
				// 缓冲进度回调 - 广播
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ESApplication.getInstance().addActivity(this);
		
		// 数据库操作助手
		mMusicTableHelper = new MusicTableHelper(this);
		
		// 绑定播放服务
		Intent intent = new Intent(this, PlayerService.class);
		bindService(intent, mPlayerConnection, Context.BIND_AUTO_CREATE);
		
		mPlayerBroadcastManager = LocalBroadcastManager.getInstance(this);
		// 定义广播接收器过滤器
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_PLAYER_ONSTART);
		filter.addAction(Constants.ACTION_PLAYER_ONPAUSE);
		filter.addAction(Constants.ACTION_PLAYER_ONRESUME);
		filter.addAction(Constants.ACTION_PLAYER_ONSTOP);
		filter.addAction(Constants.ACTION_PLAYER_BUFFER_PROGRESS);
		// 构造广播接收器
		mPlayerBroadcastReceiver = new PlayerBroadcastReceiver();
		// 注册广播
		mPlayerBroadcastManager.registerReceiver(mPlayerBroadcastReceiver, filter);
				
		initUI();
		
	}
	
	/**
	 * 初始化界面
	 */
	private void initUI() {
		mDragLayout = (DragLayout) findViewById(R.id.draglayout);
		mMainContentLayout = (MainContentLayout) findViewById(R.id.panel_main_content);
		mMainContentLayout.setDragLayout(mDragLayout);
		
		initLeftMenu();
		
		initMainContent();
		
		initPlayerController();
	}

	/**
	 * 初始化播放器控制条
	 */
	private void initPlayerController() {
		
		mPlayerControllerView = findViewById(R.id.panel_home_bottom);
		mMusicCoverIv = (ImageView) findViewById(R.id.iv_music_cover);
		mMusicNameTv = (TextView) findViewById(R.id.tv_music_name);
		mMusicArtistTv = (TextView) findViewById(R.id.tv_music_author);
		mPlayOrPauseIv = (CheckBox) findViewById(R.id.iv_play);
		mFavoriteIv = (CheckBox) findViewById(R.id.iv_favorite);
		
		mPlayerControllerView.setOnClickListener(this);
		mPlayOrPauseIv.setOnClickListener(this);
		mFavoriteIv.setOnClickListener(this);
		
		refreshPlayerController();
		
	}

	/**
	 * 刷新播放器控制条
	 */
	private void refreshPlayerController(){
		if (mPlayerService == null) {
			return;
		}

		final RealSong song = mPlayerService.getPlayingSong();
		if (song == null) {
			return;
		}
		
		// 封面
		ImageLoader.getInstance().displayImage(
				CommonUtils.getUri(song), 
				mMusicCoverIv, 
				CommonUtils.getDefaultMusicCoverOptions());

		// 歌曲信息
		mMusicNameTv.setText(""+song.getSongName());
		String artist = song.getArtist();
		if (TextUtils.isEmpty(artist)) {
			mMusicArtistTv.setText("<未知艺术家>");
		}else{
			mMusicArtistTv.setText(song.getArtist());
		}
		
		// 收藏
		boolean isFavorite = mMusicTableHelper.isFavorite(song);
		if (isFavorite) {
			mFavoriteIv.setBackgroundResource(R.drawable.favorite);
		}else{
			mFavoriteIv.setBackgroundResource(R.drawable.favorite_outline);
		}
		
		// 播放按钮
		boolean isPlaying = mPlayerService.isPlaying();
		if (isPlaying) {
			mPlayOrPauseIv.setBackgroundResource(R.drawable.ic_pause);
		}else{
			mPlayOrPauseIv.setBackgroundResource(R.drawable.ic_play_arrow);
		}
	}
	
	/**
	 * 初始化左侧边栏
	 */
	private void initLeftMenu(){
		
		// 获取FragmentManager,同样是support-v4包下的
		FragmentManager mFragmentManager = getSupportFragmentManager();
		//　开启fragment事务,同样是support-v4包下的
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		// 把fragment添加到布局文件中
		transaction.replace(R.id.panel_left_menu, new LeftMenuFragment());
		// 提交事务
		transaction.commit();
		
	}
	
	/**
	 * 初始化主布局
	 */
	@SuppressWarnings("deprecation")
	private void initMainContent(){
		
		mHomeMenuIv = (ImageView) findViewById(R.id.iv_home_menu);
		mHomeSearchIv = (ImageView) findViewById(R.id.iv_home_search);
		
		mHomeMenuIv.setOnClickListener(this);
		mHomeSearchIv.setOnClickListener(this);
		
		//mTabLayoutRg = (RadioGroup) findViewById(R.id.rg_tab_layout);
		mMineRBtn = (RadioButton) findViewById(R.id.rbtn_mine);
		mRadioRBtn = (RadioButton) findViewById(R.id.rbtn_radio);
//		mRecommendRBtn = (RadioButton) findViewById(R.id.rbtn_recommend);
		
		mContentVp = (ViewPager) findViewById(R.id.vp_content);
		
		// fragment集合
		mFragements = new ArrayList<Fragment>();
		mFragements.add(new MineFragment());
		mFragements.add(new RadioFragment());
//		mFragements.add(new RecommendFragment());
		
		//为ViewPager设置FragmentAdapter
		mContentVp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public int getCount() {
				return mFragements.size();
			}
			
			@Override
			public Fragment getItem(int arg0) {
				return mFragements.get(arg0);
			}
			
			/* 一下三个方法时复写PagerAdapter时必须要复写的方法，例如：使用ViewPager实现引导页
			/**
			 * 销毁掉一个fragment
			 *
			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				super.destroyItem(container, position, object);
			}

			/**
			 * 实例化一个fragment
			 *
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				return super.instantiateItem(container, position);
			}

			/**
			 * 是否来自object
			 *
			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}
			*/
		});
		
		mContentVp.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				//当页面被选中时，切换tablayout的选中状态
				if (arg0 == 0) {
					onClick(mMineRBtn);
				}else if(arg0 == 1){
					onClick(mRadioRBtn);
				}
//				}else if (arg0 == 2){
//					onClick(mRecommendRBtn);
//				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
//		// 使用该方法时会出现 onCheckedChanged 方法被循环调用，而达不到效果
//		mTabLayoutRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				
//				group.check(0);
//				
//			}
//		});
		
		mMineRBtn.setOnClickListener(this);
		mRadioRBtn.setOnClickListener(this);
//		mRecommendRBtn.setOnClickListener(this);
		
		// 我们默认选中中间的页面最先显示
		onClick(mRadioRBtn);
		mContentVp.setCurrentItem(1);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onDestroy() {
		// 解除广播接收器
		if (mPlayerBroadcastManager != null && mPlayerBroadcastReceiver != null) {
			mPlayerBroadcastManager.unregisterReceiver(mPlayerBroadcastReceiver);
		}
		// 解绑定服务
		if (mPlayerConnection != null) {
			unbindService(mPlayerConnection);
		}
		super.onDestroy();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onClick(View v) {
		final int vid = v.getId();
		
		if (vid == R.id.iv_home_menu) {
			// 打开抽屉
			mDragLayout.open();
			
		} else if (vid == R.id.iv_home_search) {
			// 搜索页面（Fragment）			
			replaceFragment(R.id.panel_fragment_content, 
					new SearchFragment(), 
					SearchFragment.class.getName());
		}
		
		if (vid == R.id.rbtn_mine) {
			// 设置被选中radiobutton它的字体颜色为绿色，其他未被选中的字体为黑色
			mMineRBtn.setTextColor(getResources().getColor(R.color.tab_checked_green6fCf6e));
			mRadioRBtn.setTextColor(getResources().getColor(android.R.color.black));
//			mRecommendRBtn.setTextColor(getResources().getColor(android.R.color.black));

			//设置viewPager的当前页面为我的页面
			mContentVp.setCurrentItem(0);
			
		} else if (vid == R.id.rbtn_radio) {
			// 设置被选中radiobutton它的字体颜色为绿色，其他未被选中的字体为黑色
			mMineRBtn.setTextColor(getResources().getColor(android.R.color.black));
			mRadioRBtn.setTextColor(getResources().getColor(R.color.tab_checked_green6fCf6e));
//			mRecommendRBtn.setTextColor(getResources().getColor(android.R.color.black));
			
			//设置viewPager的当前页面为我的页面
			mContentVp.setCurrentItem(1);
			
		}
//		} else if (vid == R.id.rbtn_recommend) {
//			// 设置被选中radiobutton它的字体颜色为绿色，其他未被选中的字体为黑色
//			mMineRBtn.setTextColor(getResources().getColor(android.R.color.black));
//			mRadioRBtn.setTextColor(getResources().getColor(android.R.color.black));
//			mRecommendRBtn.setTextColor(getResources().getColor(R.color.tab_checked_green6fCf6e));
//			
//			//设置viewPager的当前页面为我的页面
//			mContentVp.setCurrentItem(2);
//		}
			
			else if (vid == R.id.panel_home_bottom) {
			// 进入播放页面
			
			// 获取Fragment事务
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			
			// 四个参数：第一个是Fragment添加时的动画，第二个是移除时的动画
			// 第三个时，添加到后退栈时的动画，第四个是，从后退栈移除时的动画
			transaction.setCustomAnimations(R.anim.slide_in_up, 
											R.anim.slide_out_bottom, 
											R.anim.slide_in_up, 
											R.anim.slide_out_bottom);
			// 把Fragment添加到布局中
			transaction.add(R.id.panel_main_content, new PlayerFragment());
			// 添加到后退栈
			transaction.addToBackStack(PlayerFragment.class.getName());
			// 提交事务
			transaction.commit();
			
		} else if (vid == R.id.iv_play) {
			if (mPlayerService == null) {
				return;
			}
			
			if (!mPlayerService.isFirstInitCompleted()) {
				CommonUtils.startPlayerService(this, 
						Constants.ACTION_PLAYER_PLAY_ONLY_START, 
						null);
				return;
			}
			
			if (mPlayOrPauseIv.isChecked()) {
				mPlayOrPauseIv.setBackgroundResource(R.drawable.ic_pause);
				mPlayerService.resume();
			}else{
				mPlayOrPauseIv.setBackgroundResource(R.drawable.ic_play_arrow);
				mPlayerService.pause();
			}
		} else if (vid == R.id.iv_favorite) {
			
			if (mFavoriteIv.isChecked()) {
				mFavoriteIv.setBackgroundResource(R.drawable.favorite_outline);
			}else{
				mFavoriteIv.setBackgroundResource(R.drawable.favorite);
			}
			
		}
		
	}
	
	@Override
	public void onBackPressed() {
		//处理Fragment的onBack事件
		
		// 将当前栈顶的Fragment出栈
		popBackStack();
		
		if (getSupportFragmentManager().getBackStackEntryCount() == 0){
			//处理Activity的onBackPressed事件
			
			//this.finish();// 暂时使用该方法退出
			super.onBackPressed();
			return;
		}
	}

	@Override
	public void addFragment(@IdRes int containerViewId, Fragment frag,
			@Nullable String tag) {
		// 获取Fragment事务
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		
		// 四个参数：第一个是Fragment添加时的动画，第二个是移除时的动画
		// 第三个时，添加到后退栈时的动画，第四个是，从后退栈移除时的动画
		transaction.setCustomAnimations(R.anim.slide_in_right, 
										R.anim.slide_out_right, 
										R.anim.slide_in_right, 
										R.anim.slide_out_right);
		// 把Fragment添加到布局中
		transaction.add(containerViewId, frag);
		// 添加到后退栈
		transaction.addToBackStack(frag.getClass().getName());
		// 提交事务
		transaction.commit();
	}

	@Override
	public void replaceFragment(@IdRes int containerViewId, Fragment frag,
			@Nullable String tag) {
		// 获取Fragment事务
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		
		// 四个参数：第一个是Fragment添加时的动画，第二个是移除时的动画
		// 第三个时，添加到后退栈时的动画，第四个是，从后退栈移除时的动画
		transaction.setCustomAnimations(R.anim.slide_in_right, 
										R.anim.slide_out_right, 
										R.anim.slide_in_right, 
										R.anim.slide_out_right);
		// 把Fragment添加到布局中
		transaction.replace(containerViewId, frag);
		// 添加到后退栈
		transaction.addToBackStack(frag.getClass().getName());
		// 提交事务
		transaction.commit();
	}

	@Override
	public void removeFragment(Fragment frag) {
		// 获取Fragment事务
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		// 四个参数：第一个是Fragment添加时的动画，第二个是移除时的动画
		// 第三个时，添加到后退栈时的动画，第四个是，从后退栈移除时的动画
		transaction.setCustomAnimations(R.anim.slide_in_right, 
				R.anim.slide_out_right, 
				R.anim.slide_in_right, 
				R.anim.slide_out_right);
		// 将Fragment从管理器中移除
		transaction.remove(frag);
		// 提交事务
		transaction.commit();
	}

	@Override
	public void popBackStack() {
		// 获取Fragment管理器
		FragmentManager manager = getSupportFragmentManager();
		manager.popBackStack();
	}

	@Override
	public void popBackStack(@Nullable String name) {
		// 将所有‘name’事务出栈
		getSupportFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
}
