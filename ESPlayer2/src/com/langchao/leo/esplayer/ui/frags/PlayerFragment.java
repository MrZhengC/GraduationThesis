package com.langchao.leo.esplayer.ui.frags;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.app.AppConfig;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.core.PlayerService;
import com.langchao.leo.esplayer.core.PlayerService.PlayerBinder;
import com.langchao.leo.esplayer.core.WebSongDataEngine;
import com.langchao.leo.esplayer.db.MusicTableHelper;
import com.langchao.leo.esplayer.interfaces.IActivityInteraction;
import com.langchao.leo.esplayer.interfaces.IAsyncLoadListener;
import com.langchao.leo.esplayer.interfaces.IOnBackPressed;
import com.langchao.leo.esplayer.ui.widget.lrc.LrcRow;
import com.langchao.leo.esplayer.ui.widget.lrc.LrcView;
import com.langchao.leo.esplayer.utils.CommonUtils;
import com.langchao.leo.esplayer.utils.ESLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 * 播放页面Fragment
 * @author 碧空
 *
 */
public class PlayerFragment extends Fragment implements IOnBackPressed , OnClickListener{

	private ImageView mBackIv = null;
	private ImageView mShareIv = null;
	private ImageView mFavoriteIv = null;
	
	private ImageView mMusicCoverIv = null;
	private LrcView mLrcView = null;
	
	private TextView mMusicNameTv = null;
	private TextView mMusicArtistTv = null;
	
	private ImageView mPlayOrderIv = null;
	private ImageView mSkipPreviousIv = null;
	private ImageView mPlayOrPauseIv = null;
	private ImageView mSkipNextIv = null;
	private ImageView mDownloadIv = null;

	private TextView mCurProgressTv = null;
	private TextView mDuraionTv = null;
	private SeekBar mProgressSb = null;
	
	// 数据库操作
	private MusicTableHelper mMusicTableHelper = null;
	
	// 列表播放顺序
	private final int[] mPlayOrderImageIds = new int[]{
			R.drawable.ic_repeat,	//列表循环
			R.drawable.ic_shuffle,	//随机播放
			R.drawable.ic_repeat_one//单曲循环	
	}; 
	
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
			refreshUI();
		}
	};
	
	// 本地广播管理器
	private LocalBroadcastManager mPlayerBroadcastManager = null;
	// 播放器广播接收器
	private PlayerBroadcastReceiver mPlayerBroadcastReceiver = null;
	
	private class PlayerBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取intent的action
			final String action = intent.getAction();
			if (Constants.ACTION_PLAYER_ONSTART.equals(action)) {
				// 开始回调 - 广播
				refreshUI();
			} else if (Constants.ACTION_PLAYER_ONPAUSE.equals(action)) {
				// 暂停回调 - 广播
				mPlayOrPauseIv.setImageResource(R.drawable.ic_play_arrow2);
			} else if (Constants.ACTION_PLAYER_ONRESUME.equals(action)) {
				// 重新开始回调 - 广播
				mPlayOrPauseIv.setImageResource(R.drawable.ic_pause);
			} else if (Constants.ACTION_PLAYER_ONSTOP.equals(action)) {
				// 停止回调 - 广播
				mPlayOrPauseIv.setImageResource(R.drawable.ic_play_arrow2);
				if (mPlayerService != null) {
					// 设置总时长
					mProgressSb.setMax(0);
					mDuraionTv.setText(CommonUtils.formatTimeMinute(0));
				}
			} else if (Constants.ACTION_PLAYER_PLAY_PROGRESS.equals(action)) {
				// 播放进度回调 - 广播
				if (mPlayerService != null) {
					// 设置当前播放进度
					mProgressSb.setProgress(mPlayerService.getProgress());
					mCurProgressTv.setText(
							CommonUtils.formatTimeMinute(
									mPlayerService.getProgress()));
				}
			} else if (Constants.ACTION_PLAYER_BUFFER_PROGRESS.equals(action)) {
				// 缓冲进度回调 - 广播
				if (mPlayerService != null) {
					Bundle data = intent.getBundleExtra(Constants.KEY_PLAYER_DATA);
					if (data != null) {
						int percent = data.getInt(Constants.KEY_PLAYER_PROGRESS);
						// 设置当前播放进度
						int secProgress = mPlayerService.getDuration() * percent / 100;
						mProgressSb.setSecondaryProgress(secProgress);
					}
				}
			}
		}
	}
	
	@Override
	public void onAttach(Context context) {
		ESLog.e("onAttach..");
		// 绑定播放服务
		Intent intent = new Intent(context, PlayerService.class);
		context.bindService(intent, mPlayerConnection, Context.BIND_AUTO_CREATE);
		
		// 获取广播管理器
		mPlayerBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

		// 定义广播接收器过滤器
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_PLAYER_ONSTART);
		filter.addAction(Constants.ACTION_PLAYER_ONPAUSE);
		filter.addAction(Constants.ACTION_PLAYER_ONRESUME);
		filter.addAction(Constants.ACTION_PLAYER_ONSTOP);
		filter.addAction(Constants.ACTION_PLAYER_PLAY_PROGRESS);
		filter.addAction(Constants.ACTION_PLAYER_BUFFER_PROGRESS);
		// 构造广播接收器
		mPlayerBroadcastReceiver = new PlayerBroadcastReceiver();
		// 注册广播
		mPlayerBroadcastManager.registerReceiver(mPlayerBroadcastReceiver, filter);
		
		super.onAttach(context);
	}

	@Override
	public void onDetach() {
		ESLog.e("onDetach..");
		// 解除广播接收器
		if (mPlayerBroadcastManager != null && mPlayerBroadcastReceiver != null) {
			mPlayerBroadcastManager.unregisterReceiver(mPlayerBroadcastReceiver);
		}
				
		if (mPlayerConnection != null) {
			// 当然在本Fragment中肯定为非空
			getActivity().unbindService(mPlayerConnection);
		}
		
		super.onDetach();
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
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ESLog.e("onCreateView...");
		// 初始化数据库操作类
		mMusicTableHelper = new MusicTableHelper(getActivity());

		View mRootView = inflater.inflate(R.layout.fragment_player, container, false);
		initUI(mRootView);
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		ESLog.e("onDestroyView...");
		super.onDestroyView();
	}
	
	/**
	 * 初始化界面
	 * @param mRootView
	 */
	private void initUI(View mRootView) {
		mBackIv = (ImageView) mRootView.findViewById(R.id.iv_player_back);
		mShareIv = (ImageView) mRootView.findViewById(R.id.iv_player_share);
		mFavoriteIv = (ImageView) mRootView.findViewById(R.id.iv_player_favorite);
		
		mBackIv.setOnClickListener(this);
		mFavoriteIv.setOnClickListener(this);
		mShareIv.setOnClickListener(this);

		mMusicCoverIv = (ImageView) mRootView.findViewById(R.id.player_music_cover);
		mLrcView = (LrcView) mRootView.findViewById(R.id.player_lrcview);
		
		mMusicNameTv = (TextView) mRootView.findViewById(R.id.tv_player_music_name);
		mMusicArtistTv = (TextView) mRootView.findViewById(R.id.tv_player_music_artist);
		
		mPlayOrderIv = (ImageView) mRootView.findViewById(R.id.iv_player_order);
		mSkipPreviousIv = (ImageView) mRootView.findViewById(R.id.iv_player_previous);
		mPlayOrPauseIv = (ImageView) mRootView.findViewById(R.id.iv_player_play_or_pause);
		mSkipNextIv = (ImageView) mRootView.findViewById(R.id.iv_player_next);
		mDownloadIv = (ImageView) mRootView.findViewById(R.id.iv_player_download);
		
		mPlayOrderIv.setOnClickListener(this);
		mSkipPreviousIv.setOnClickListener(this);
		mPlayOrPauseIv.setOnClickListener(this);
		mSkipNextIv.setOnClickListener(this);
		mDownloadIv.setOnClickListener(this);
		
		mCurProgressTv = (TextView) mRootView.findViewById(R.id.tv_player_current_progress);
		mDuraionTv = (TextView) mRootView.findViewById(R.id.tv_player_duration);
		mProgressSb = (SeekBar) mRootView.findViewById(R.id.sb_player_progress);
		
		mProgressSb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mLrcView.seekTo(progress, true, fromUser);
				mCurProgressTv.setText(CommonUtils.formatTimeMinute(progress));
				if (fromUser) {
					if (mPlayerService != null) {
						mPlayerService.seekTo(progress);
					}
				}
			}
		});
		
	}

	/**
	 * 刷新界面操作
	 */
	private void refreshUI(){

		// 列表播放次序按钮
		int order = AppConfig.getInstance(getActivity()).getPlayOrder();
		try {
			ESLog.i("获取播放顺序， order : " + order);
			mPlayOrderIv.setImageResource(mPlayOrderImageIds[order]);
		} catch (Exception e) {
		}
		
		// 播放服务为空，直接返回
		if (mPlayerService == null) {
			return;
		}
		
		// 获得正在播放的音乐
		final RealSong song = mPlayerService.getPlayingSong();
		if (song == null) {
			return;
		}
		
		// 加载封面
		ImageLoader.getInstance().displayImage(CommonUtils.getUri(song), mMusicCoverIv,
				CommonUtils.getDefaultMusicCoverOptions());
		
		// 加载歌词
		WebSongDataEngine.getInstance(getActivity()).getWebSongLrc(song, 
				new IAsyncLoadListener<List<LrcRow>>() {
			@Override
			public void onSuccess(List<LrcRow> t) {
				// 将歌词设置给LrcView
				mLrcView.setLrcRows(t);
				// 跳转到正确的位置
				mLrcView.seekTo(mPlayerService.getProgress(), false, false);
			}
			@Override
			public void onFailure(String msg) {
			}
		});
		
		// 播放按钮
		if (mPlayerService.isPlaying()){
			mPlayOrPauseIv.setImageResource(R.drawable.ic_pause);
		}else{
			mPlayOrPauseIv.setImageResource(R.drawable.ic_play_arrow2);
		}
		
		// 歌曲名称
		mMusicNameTv.setText("" + song.getSongName());
		// 歌手名称
		if (TextUtils.isEmpty(song.getArtist())){
			mMusicArtistTv.setText("<未知艺术家>");
		} else {
			mMusicArtistTv.setText("" + song.getArtist());
		}
		
		// 设置总时长
		mProgressSb.setMax(mPlayerService.getDuration());
		mDuraionTv.setText(
				CommonUtils.formatTimeMinute(
						mPlayerService.getDuration()));
		// 设置当前播放进度
		mProgressSb.setProgress(mPlayerService.getProgress());
		mCurProgressTv.setText(
				CommonUtils.formatTimeMinute(
						mPlayerService.getProgress()));

		// 是否收藏过
		if(mMusicTableHelper.isFavorite(song)){
			mFavoriteIv.setImageResource(R.drawable.ic_favorite);
		} else {
			mFavoriteIv.setImageResource(R.drawable.favorite_outline);
		}
		
	}
	
	@Override
	public boolean onBack() {
		return false;
	}

	@Override
	public void onClick(View v) {
		final int vid = v.getId();

		switch (vid) {
		case R.id.iv_player_back:
		{	
			// 获取本Fragment附加到的Activity对象
			final Activity activity = getActivity();
			// 判断该activity是否是IActivityInteraction的一个实例
			if (activity instanceof IActivityInteraction) {
				// 将该Activity作为IActivityInteraction这个接口来使用
				final IActivityInteraction interaction = (IActivityInteraction)activity;
				// 调用出栈方法
				interaction.popBackStack(this.getClass().getName());
			}
		}
		break;
		case R.id.iv_player_share:
		{
			//
		}
		break;
		case R.id.iv_player_favorite:
		{
			// 添加到收藏
			if (mPlayerService != null) {
				RealSong song = mPlayerService.getPlayingSong();
				// 判断是否收藏过，如果收藏过则取消收藏，否则添加收藏
				if (mMusicTableHelper.isFavorite(song)) {
					mMusicTableHelper.changeFavoriteTime(song, 0);
					mFavoriteIv.setImageResource(R.drawable.favorite_outline);
				}else{
					mMusicTableHelper.changeFavoriteTime(song, System.currentTimeMillis());
					mFavoriteIv.setImageResource(R.drawable.ic_favorite);
				}
			}
		}
		break;
		case R.id.iv_player_order:
		{
			// 首先获取列表播放次序
			int order = AppConfig.getInstance(getActivity()).getPlayOrder();
			// 计算新的次序
			order += 1;
			order %= 3;
			// 更换播放次序图标 
			mPlayOrderIv.setImageResource(mPlayOrderImageIds[order]);
			// 重新设置回配置中去
			AppConfig.getInstance(getActivity()).setPlayOrder(order);
		}
		break;
		case R.id.iv_player_previous:
		{
			if(mPlayerService != null){
				mPlayerService.previous();
			}
		}
		break;
		case R.id.iv_player_play_or_pause:
		{
			if (mPlayerService != null) {
				if (mPlayerService.isPause()) {
					mPlayerService.resume();
					// 更改图标的图片
					mPlayOrPauseIv.setImageResource(R.drawable.ic_pause);
				}else{
					mPlayerService.pause();
					// 更改图标的图片
					mPlayOrPauseIv.setImageResource(R.drawable.ic_play_arrow2);
				}
			}
		}
		break;
		case R.id.iv_player_next:
		{
			if(mPlayerService != null){
				mPlayerService.next();
			}
		}
		break;
		case R.id.iv_player_download:
		{
			
		}
		break;
		default:
			break;
		}
	}
	
}
