package com.langchao.leo.esplayer.core;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.langchao.leo.esplayer.HomeActivity;
import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.app.AppConfig;
import com.langchao.leo.esplayer.app.ESApplication;
import com.langchao.leo.esplayer.bean.Playlist;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.db.MusicTableHelper;
import com.langchao.leo.esplayer.interfaces.IAsyncLoadListener;
import com.langchao.leo.esplayer.utils.CommonUtils;
import com.langchao.leo.esplayer.utils.ESLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 播放器服务：<br>
 * 
 * @author 碧空
 *
 */
public class PlayerService extends Service implements 
						OnBufferingUpdateListener,
						OnCompletionListener,
						OnErrorListener, 
						OnInfoListener, 
						OnPreparedListener, 
						OnSeekCompleteListener{

	// 下一首音乐
	private static final String ACTION_NOTIFICATION_NEXT_MUSIC = 
			"com.esplayer.intent.action.NOTIFICATION_NEXT_MUSIC";
	// 暂停
	private static final String ACTION_NOTIFICATION_PAUSE_MUSIC = 
			"com.esplayer.intent.action.NOTIFICATION_PAUSE_MUSIC";
	// 退出
	private static final String ACTION_NOTIFICATION_EXIT = 
			"com.esplayer.intent.action.NOTIFICATION_EXIT";
	
	// PlayerNotification 标识ID
	private final int ID_PLAYER_NOTIFICATION = 110;
	// 通知栏整体点击请求码
	private final int REQUESTCODE_CLICK_PLAYER_NOTIFICATION = 111;
	
	public class PlayerBinder extends Binder{
		/**
		 * 返回当前服务对象
		 * @return
		 */
		public PlayerService getService(){
			return PlayerService.this;
		}
	}
	
	/**
	 * 通知栏点击广播接收器
	 * @author 碧空
	 *
	 */
	private class NotificationClickBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_NOTIFICATION_PAUSE_MUSIC.equals(action)) {
				// 暂停
				if (isPause()) {
					resume();
				}else{
					pause();
				}
				refreshNotificationMuiscInfo();
			} else if (ACTION_NOTIFICATION_NEXT_MUSIC.equals(action)) {
				// 下一首歌曲
				ESLog.i("下一首音乐，来自通知栏.");
				next();
			} else if (ACTION_NOTIFICATION_EXIT.equals(action)) {
				// 退出应用
				ESApplication.getInstance().AppExit();
			}
			
		}
	} 
	
	/**
	 * 电话状态监听
	 * @author 碧空
	 *
	 */
	private class ESTelephoneCallStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			
			if (state == TelephonyManager.CALL_STATE_IDLE) {// 空置
				resume();
			} else if (state == TelephonyManager.CALL_STATE_OFFHOOK
					|| state == TelephonyManager.CALL_STATE_RINGING) {//摘机、接通和响铃
				// 暂停
				pause();
			} 
			super.onCallStateChanged(state, incomingNumber);
		}
	}
	
	//
	private PlayerBinder mCorePlayerBinder = null;
	
	// 播放器
	private MediaPlayer mCorePlayer = null;
	// 当前播放列表
	private Playlist mCurrentPlaylist = null;
	// 当前正在播放的音乐
	private RealSong mCurrentSong = null;
	// 正在播放音乐的索引
	private int mCurrentSongIndex = 0;
	// 播放过的音乐索引
	private Stack<Integer> mSongIndexStack = new Stack<Integer>();

	// 通知栏管理器
	private NotificationManager mNotificationManager;
	// 通知栏对象
	private Notification mPlayerNotification = null;
	// 通知栏中的ContentView
	private RemoteViews notificationView = null;
	
	// 用于通知栏歌曲封面的图片加载监听
	private ImageLoadingListener mImageLoadingListener = null;

	private NotificationClickBroadcastReceiver mNotificationClickBroadcastReceiver = null; 
	
	// 随机播放索引生成器
	private Random mShuffleIndexGenerator = null;
	//　本地广播管理器
	private LocalBroadcastManager mPlayerBroadcastManager = null;
	// 音乐数据库操作助手
	private MusicTableHelper mMusicTableHelper = null;
	
	// 播放进度定时器
	private Timer mPlayerSendProgressTimer = null;
	
	/**
	 * 发送进度定时任务
	 * @author 碧空
	 */
	private class PlayerSendProgressTimerTask extends TimerTask{
		@Override
		public void run() {
			// 发送进度广播
			sendPlayerBroadcast(Constants.ACTION_PLAYER_PLAY_PROGRESS, null);
		}
	}
	
	@Override
	public void onCreate() {
		// TODO 初始化MediaPlayer，注册广播，初始化通知栏
		
		// 构造广播管理器
		mPlayerBroadcastManager = LocalBroadcastManager.getInstance(this);
		// 数据库操作助手
		mMusicTableHelper = new MusicTableHelper(this);
		
		// 注册电话状态监听
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		telephonyManager.listen(new ESTelephoneCallStateListener(), 
				PhoneStateListener.LISTEN_CALL_STATE);
		
		// 初始化播放器
		mCorePlayer = new MediaPlayer();
		//mCorePlayer.setLooping(true);
		mCorePlayer.setOnBufferingUpdateListener(this);	// 缓冲进度监听 
		mCorePlayer.setOnCompletionListener(this);		// 播放完成监听
		mCorePlayer.setOnErrorListener(this);			// 错误监听
		mCorePlayer.setOnInfoListener(this);			// 播放信息监听
		mCorePlayer.setOnPreparedListener(this);		// 准备完成监听，
		mCorePlayer.setOnSeekCompleteListener(this);	// 跳转完成监听
		//mCorePlayer.reset();
		
		// 通过系统服务获取NotificationManager
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 设置通知栏
		setupNotification();
		
		// 注册通知栏广播
		mNotificationClickBroadcastReceiver = new NotificationClickBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_NOTIFICATION_PAUSE_MUSIC);
		filter.addAction(ACTION_NOTIFICATION_NEXT_MUSIC);
		filter.addAction(ACTION_NOTIFICATION_EXIT);
		registerReceiver(mNotificationClickBroadcastReceiver, filter);
		
		// 获取保存的播放列表和歌曲索引
		mCurrentSongIndex = AppConfig.getInstance(this).getSavedMusicIndex();
		mCurrentPlaylist = AppConfig.getInstance(this).getSavedPlaylist();
		if (mCurrentPlaylist != null 
				&& mCurrentSongIndex >= 0 
				&& mCurrentSongIndex < mCurrentPlaylist.getCount()) {
			mCurrentSong = mCurrentPlaylist.getSongs().get(mCurrentSongIndex);
		}
		
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		if (mCorePlayerBinder == null) {
			mCorePlayerBinder = new PlayerBinder();
		}
		return mCorePlayerBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO 接收命令，进行不同的播放控制
		if (intent != null) {
			// 处理开始播放逻辑
			String action = intent.getAction();
			
			if (Constants.ACTION_PLAYER_PLAY_ONLY_START.equals(action)) {
				start();
				return super.onStartCommand(intent, flags, startId);
			}
			
			// 获取传递过来的数据
			Bundle data = intent.getBundleExtra(Constants.KEY_PLAYER_DATA);
			if (data == null) {
				return super.onStartCommand(intent, flags, startId);
			}
			if (Constants.ACTION_PLAYER_PLAY_ALL_PLAYLIST.equals(action)) {// 播放整个播放列表
				
				// 清空歌曲播放历史
				mSongIndexStack.clear();
				
				// 获取播放列表
				mCurrentPlaylist = (Playlist) data.getSerializable(Constants.KEY_PLAYER_PLAYLIST);
				mCurrentSongIndex = data.getInt(Constants.KEY_PLAYER_POSITION, 0);
				if (mCurrentPlaylist != null) {
					// 获取播放列表内歌曲列表
					List<RealSong> songs = mCurrentPlaylist.getSongs();
					if (songs != null) {
						mCurrentSong = songs.get(mCurrentSongIndex);
						start();
					}
				}
				
			} else if (Constants.ACTION_PLAYER_PLAY_FORM_POSITION.equals(action)) {
				
				mCurrentSongIndex = data.getInt(Constants.KEY_PLAYER_POSITION, 0);
				
				if (mCurrentPlaylist != null && 
						( mCurrentSongIndex >= 0 && 
						mCurrentSongIndex < mCurrentPlaylist.getCount())) {
					// 获取播放列表内歌曲列表
					List<RealSong> songs = mCurrentPlaylist.getSongs();
					if (songs != null) {
						mCurrentSong = songs.get(mCurrentSongIndex);
						start();
					}
				}
				
			}
			
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 初始化自定义通知栏
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void setupNotification(){
		
		// 获取Notification对象
		if (Build.VERSION.SDK_INT < 11){
			mPlayerNotification = new Notification();
			mPlayerNotification.icon = R.drawable.song_cover;
			
		} /*else if (Build.VERSION.SDK_INT < 16 && Build.VERSION.SDK_INT >= 11){
			mPlayerNotification = new Notification
					.Builder(getApplicationContext())
						.setSmallIcon(R.drawable.song_cover)
						.build();
		} */else {
			mPlayerNotification = new Notification
					.Builder(getApplicationContext())
						.setSmallIcon(R.drawable.song_cover)
						.build();
		}

		// 设置不可清除
		mPlayerNotification.flags |= Notification.FLAG_NO_CLEAR;

		// 初始化通知栏ContentView
		setupNotificationContentView();
		
		// 设置通知栏ContentView
		mPlayerNotification.contentView = notificationView;
		
		// 设置通知栏contentIntent，即点击整体条目时要处理的Intent
		// 此处的Intent要与PendingIntent一致
		Intent mIntent = new Intent(this, HomeActivity.class);
		PendingIntent mNotificationPendingIntent = 
				PendingIntent.getActivity(getApplicationContext(), 
						REQUESTCODE_CLICK_PLAYER_NOTIFICATION, 
						mIntent, 
						PendingIntent.FLAG_UPDATE_CURRENT);
		
		mPlayerNotification.contentIntent = mNotificationPendingIntent;
		
	}
	
	/**
	 * 初始化通知栏ContentView
	 */
	private void setupNotificationContentView() {
		notificationView = new RemoteViews(getPackageName(), R.layout.layout_player_notification);
		
		// 点击下一首
        Intent nextButtonIntent = new Intent(ACTION_NOTIFICATION_NEXT_MUSIC);  
        PendingIntent pendNextButtonIntent = 
        		PendingIntent.getBroadcast(this, 
        				0, 
        				nextButtonIntent, 
        				PendingIntent.FLAG_UPDATE_CURRENT);  
        notificationView.setOnClickPendingIntent(
        		R.id.notification_next_song_button, 
        		pendNextButtonIntent);  
        
        // 点击播放暂停
        Intent playButtonIntent = new Intent(ACTION_NOTIFICATION_PAUSE_MUSIC);  
        PendingIntent pendPlayButtonIntent = 
        		PendingIntent.getBroadcast(this, 
        				1, 
        				playButtonIntent, 
        				PendingIntent.FLAG_UPDATE_CURRENT);  
        notificationView.setOnClickPendingIntent(
        		R.id.notification_play_button, 
        		pendPlayButtonIntent);  
        
        // 点击退出
        Intent exitButton = new Intent(ACTION_NOTIFICATION_EXIT);  
        PendingIntent pendingExitButtonIntent = 
        		PendingIntent.getBroadcast(this, 
        				2,
        				exitButton, 
        				PendingIntent.FLAG_UPDATE_CURRENT);  
        notificationView.setOnClickPendingIntent(
        		R.id.notification_exit_button, 
        		pendingExitButtonIntent);  
		
	}

	/**
	 * 设置通知栏歌曲信息
	 */
	private void refreshNotificationMuiscInfo(){
		
		// 播放和暂停按钮的图标更换
		if(isPause){  
			notificationView.setImageViewResource(
					R.id.notification_play_button,
					R.drawable.ic_play_arrow);
		} else {  
			notificationView.setImageViewResource(
					R.id.notification_play_button,
					R.drawable.ic_pause);  
		}  
		
		if (mCurrentSong != null) {
			final RealSong song = mCurrentSong;
			
			notificationView.setTextViewText(R.id.notification_music_title, song.getSongName());
			if (TextUtils.isEmpty(song.getArtist())) {
				notificationView.setTextViewText(R.id.notification_music_artist, "<未知艺术家>");
			}else{
				notificationView.setTextViewText(R.id.notification_music_artist, song.getArtist());
			}
			
			if (mImageLoadingListener == null) {
				mImageLoadingListener = new ImageLoadingListener() {
					@Override public void onLoadingStarted(String arg0, View arg1) {}
					
					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.song_cover);
						notificationView.setImageViewBitmap(R.id.notification_artist_image, bitmap);
						mNotificationManager.notify(ID_PLAYER_NOTIFICATION, mPlayerNotification);
					}
					
					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
						ESLog.i("onLoadingComplete, bitmap : " + bitmap);
						if (bitmap != null) {
							notificationView.setImageViewBitmap(R.id.notification_artist_image, bitmap);
							mNotificationManager.notify(ID_PLAYER_NOTIFICATION, mPlayerNotification);
						} else {
							onLoadingFailed("bitmap==null", arg1, null);
						}
					}
					
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.song_cover);
						notificationView.setImageViewBitmap(R.id.notification_artist_image, bitmap);
						mNotificationManager.notify(ID_PLAYER_NOTIFICATION, mPlayerNotification);
					}
				};
			}
			
			ImageLoader.getInstance().loadImage(CommonUtils.getUri(mCurrentSong), 
					CommonUtils.getDefaultMusicCoverOptions(),
					mImageLoadingListener);
		}
		
		// 显示通知栏
		mNotificationManager.notify(ID_PLAYER_NOTIFICATION, mPlayerNotification);
	}
	
	/**
	 * 发送播放器广播：<br>
	 * 1、onStart<br>
	 * 2、onPause<br>
	 * 3、onResume<br>
	 * 4、onStop<br>
	 * 5、playProgress<br>
	 * 6、bufferProgress<br>
	 * 
	 * @param action
	 * @param data
	 */
	public void sendPlayerBroadcast(String action, Bundle data){
		
		if (TextUtils.isEmpty(action)) {return;}
		
		Intent intent = new Intent();
		intent.setAction(action);
		if (data != null) {
			intent.putExtra(Constants.KEY_PLAYER_DATA, data);
		}
		mPlayerBroadcastManager.sendBroadcast(intent);
	}
	
	@Override
	public void onDestroy() {
		// 保存最后播放的歌曲信息，释放资源
		ESLog.i("即将销毁服务..");
		
		// 停止播放
		if (mCorePlayer != null) {
			mCorePlayer.stop();
			mCorePlayer.release();
			mCorePlayer = null;
		}
		
		// 取消注册通知栏广播
		if (mNotificationClickBroadcastReceiver != null) {
			unregisterReceiver(mNotificationClickBroadcastReceiver);
		}

		// 清除通知栏
		if (mNotificationManager != null) {
			mNotificationManager.cancel(ID_PLAYER_NOTIFICATION);
			mPlayerNotification = null;
			mNotificationManager = null;
		}
		
		// 保存歌曲信息
		// 1、保存当前播放歌曲索引；-- SharedPreference
		AppConfig.getInstance(this).setSavedMusicIndex(mCurrentSongIndex);
		// 2、保存当前播放列表； -- 序列化
		AppConfig.getInstance(this).savePlaylist(mCurrentPlaylist);
		
		super.onDestroy();
	}
	
	@Override
	public void onPrepared(MediaPlayer mp) {
		ESLog.i("播放器准备完成，可以播放");
		
		if (!isFirstInitCompleted) {
			isFirstInitCompleted = true;
		}
		
		mCorePlayer.start();
		
		// 先插入一条数据，如果存在do nothing，然后将当前歌曲写入或更改历史纪录
		mMusicTableHelper.insert(mCurrentSong);
		mMusicTableHelper.changeLastPlayTime(mCurrentSong, System.currentTimeMillis());
		// 计算累计播放次数,重新设置回SharedPreference
		int num = AppConfig.getInstance(this).getPlayerHistoryStatistics();
		AppConfig.getInstance(this).setPlayerHistoryStatistics(++num);
		// 发送开始播放广播
		sendPlayerBroadcast(Constants.ACTION_PLAYER_ONSTART, null);
		// 刷新显示通知栏
		refreshNotificationMuiscInfo();
		// 启动定时器 
		if (mPlayerSendProgressTimer != null) {
			mPlayerSendProgressTimer.cancel();
			mPlayerSendProgressTimer = null;
		}
		mPlayerSendProgressTimer = new Timer();
		mPlayerSendProgressTimer.schedule(new PlayerSendProgressTimerTask(), 0, 1000);
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		ESLog.i("onInfo : what(" + what + "), extra (" + extra +")");
		return true;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		ESLog.i("onError : what(" + what + "), extra (" + extra +")");
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		ESLog.i("播放完成，进行播放次序控制流程");
		
		// 首先获取列表播放次序
		int order = AppConfig.getInstance(this).getPlayOrder();
		switch (order) {
		case AppConfig.PLAY_ORDER_LIST_LOOP:
		case AppConfig.PLAY_ORDER_SHUFFLE:
			mCorePlayer.setLooping(false);
			next();
			break;
			/*
		case AppConfig.PLAY_ORDER_SHUFFLE:
			mCorePlayer.setLooping(false);
			
			break;*/
		case AppConfig.PLAY_ORDER_SINGLE_CYCLE:
			if(!mCorePlayer.isLooping()) {
				mCorePlayer.setLooping(true);
				mCorePlayer.start();
			}
			break;
		default:
			mCorePlayer.setLooping(false);
			break;
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {

		if (!isBufferCompleted) {
			ESLog.i("缓冲网络数据，当前进度：" + percent + "%");
			Bundle data = new Bundle();
			data.putInt(Constants.KEY_PLAYER_PROGRESS, percent);
			sendPlayerBroadcast(Constants.ACTION_PLAYER_BUFFER_PROGRESS, data);
			if (percent == 100) {
				isBufferCompleted = true;
			}
		}
		
	}
	
	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub
		ESLog.i("跳转完成..");
	}

	///////////////////////////////////////////  控制器部分   ///////////////////////////////////////
	
	// 是否处于暂停状态
	private boolean isPause = false;
	// 是否缓冲完成
	private boolean isBufferCompleted = false;
	// 是否是正序播放
	private boolean isPositive = true;
	// 是否第一次初始化完成
	private boolean isFirstInitCompleted = false;
	
	/**
	 * 是否正在播放
	 * @return
	 */
	public boolean isPlaying(){
		if (mCorePlayer != null) {
			return mCorePlayer.isPlaying();
		}
		return false;
	}
	
	/**
	 * 是否处于暂停状态
	 * @return
	 */
	public boolean isPause(){
		return isPause;
	}
	
	/**
	 * 是否第一次初始化完成
	 * @return
	 */
	public boolean isFirstInitCompleted(){
		return isFirstInitCompleted;
	}
	
	/**
	 * 开始播放
	 */
	protected void start(){
		// 无当前歌曲，直接返回，不执行播放
		if (mCurrentSong == null) {
			return;
		}
		
		ESLog.i("开始播放，当前索引为：" + mCurrentSongIndex);
		// 保存播放索引,无论是否可以播放成功
		if (isPositive) {
			if (!mSongIndexStack.empty()) {
				if (mSongIndexStack.contains(mCurrentSongIndex)) {
					if (mSongIndexStack.lastElement().intValue() != mCurrentSongIndex) {
						mSongIndexStack.remove(Integer.valueOf(mCurrentSongIndex));
						mSongIndexStack.push(mCurrentSongIndex);
					}
				} else {
					mSongIndexStack.push(mCurrentSongIndex);
				}
			}else{
				mSongIndexStack.push(mCurrentSongIndex);
			}
		}
		
		try {
			//确保开始初始化之前，MediaPlayer处于空闲状态
			mCorePlayer.reset();
			
			// 给播放器设置音乐来源 setDataSource()
			if (mCurrentSong.getSource() == 1) {// 来自网络
				
				// 访问网络获取歌曲信息
				WebSongDataEngine.getInstance(this)
					.getWebSong(""+mCurrentSong.getSongId(), 
							new IAsyncLoadListener<RealSong>() {
								@Override
								public void onSuccess(RealSong t) {
									// 获取网络歌曲
									mCurrentSong = t;
									// 将获取到的新的歌曲信息对象设置回播放列表中
									List<RealSong> songs = mCurrentPlaylist.getSongs();
									if (songs != null) {
										songs.set(mCurrentSongIndex, mCurrentSong);
									}
									try {
										// 开始缓冲网络歌曲
										isBufferCompleted = false;
										mCorePlayer.setDataSource(mCurrentSong.getSongUrl());
										mCorePlayer.prepareAsync();
									} catch (IllegalArgumentException e) {
										e.printStackTrace();
										//next();
									} catch (SecurityException e) {
										e.printStackTrace();
										//next();
									} catch (IllegalStateException e) {
										e.printStackTrace();
										//next();
									} catch (IOException e) {
										e.printStackTrace();
										//next();
									}
								}
								@Override
								public void onFailure(String msg) {
									// 下一首
									next();
								}
							});
				
			} else { // 来自本地
				mCorePlayer.setDataSource(mCurrentSong.getLocalPath());
				mCorePlayer.prepare();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// 修复播放本地文件已经发送改变但是，音乐扫描的结果中并没有做处理的问题；
			// next();
		} catch (SecurityException e) {
			e.printStackTrace();
			// next();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			// next();
		} catch (IOException e) {
			e.printStackTrace();
			// next();
		}
	}
	
	/**
	 * 暂停播放
	 */
	public void pause(){
		if (!isPause) {
			mCorePlayer.pause();
			isPause = true;
			sendPlayerBroadcast(Constants.ACTION_PLAYER_ONPAUSE, null);
		}
	}
	
	/**
	 * 重新播放
	 */
	public void resume(){
		if (isPause) {
			mCorePlayer.start();
			isPause = false;
			sendPlayerBroadcast(Constants.ACTION_PLAYER_ONRESUME, null);
		}
	}
	
	/**
	 * 停止播放
	 */
	public void stop(){
		mCorePlayer.stop();
		sendPlayerBroadcast(Constants.ACTION_PLAYER_ONSTOP, null);
	}
	
	/**
	 * 获取下一首音乐的索引
	 * @return
	 */
	private int getNextMusicIndex() {

		if (mCurrentPlaylist == null){
			return -1;
		}
		
		int size = 0;
		try {
			size = mCurrentPlaylist.getSongs().size();
		} catch (Exception e) {
		}
		if (size == 0) {
			return -1;
		}
		
		int result = -1;
		
		// 首先获取列表播放次序
		int order = AppConfig.getInstance(this).getPlayOrder();
		switch (order) {
		case AppConfig.PLAY_ORDER_LIST_LOOP:
			result = mCurrentSongIndex + 1;
			result %= size;
			break;
		case AppConfig.PLAY_ORDER_SHUFFLE:
			if (mShuffleIndexGenerator == null) {
				mShuffleIndexGenerator  = new Random();
			}
			result = mShuffleIndexGenerator.nextInt(size);
			break;
		case AppConfig.PLAY_ORDER_SINGLE_CYCLE:
			result = mCurrentSongIndex;
			break;
		default:
			break;
		}
		return result;
	}
	
	/**
	 * 播放下一首
	 */
	public void next(){
		// 设置正序播放标志位为true
		isPositive = true;
		// 获取下一首音乐的索引
		int index = getNextMusicIndex();
		
		if (index >= 0) {
			// 16-1-8添加
			if (index == mCurrentSongIndex) {
				return;
			}
			mCurrentSongIndex = index;
			List<RealSong> songs = mCurrentPlaylist.getSongs();
			if (songs != null) {
				mCurrentSong = songs.get(mCurrentSongIndex);
				// 启动播放
				start();
			}
		}
	}
	
	/**
	 * 播放上一首
	 */
	public void previous(){
		// 将正序标志设置为false
		isPositive = false;
		
		if (!mSongIndexStack.empty()) {
			// 获取上一首音乐的索引
			int index = mSongIndexStack.pop().intValue();
			if (index >= 0) {
				// 后来添加，修复第一次上一首音乐，还是播放当前音乐的问题
				if (index == mCurrentSongIndex) {
					try {
						index = mSongIndexStack.pop().intValue();
					} catch (Exception e) {
						return;
					}
				}
				mCurrentSongIndex = index;
				List<RealSong> songs = mCurrentPlaylist.getSongs();
				if (songs != null) {
					mCurrentSong = songs.get(mCurrentSongIndex);
					// 启动播放
					start();
				}
			}
		}
	}
	
	/**
	 * 获取正在播放的音乐
	 * @return
	 */
	public RealSong getPlayingSong(){
		return mCurrentSong;
	}
	
	/**
	 * 获取当前播放进度
	 * @return
	 */
	public int getProgress(){
		if (mCorePlayer != null) {
			return mCorePlayer.getCurrentPosition();
		}
		return 0;
	}
	
	/**
	 * 跳转到某个位置
	 * @param progress
	 */
	public void seekTo(int progress){
		if (mCorePlayer != null) {
			mCorePlayer.seekTo(progress);
		}
	}
	
	/**
	 * 获取总时长
	 * @return
	 */
	public int getDuration(){
		if (mCorePlayer != null) {
			return mCorePlayer.getDuration();
		}
		return 0;
	}
	
}
