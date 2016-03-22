package com.langchao.leo.esplayer.contstants;

import android.os.Environment;

/**
 * 应用内有用的常量
 * @author 碧空
 *
 */
public class Constants {

	/**
	 * SDcard路径
	 */
	public final static String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	/**
	 * app根文件夹
	 */
	public final static String APP_ROOT_FOLDER = SDCARD_PATH + "/ESPlayer/";
	
	/**
	 * 应用缓存文件夹
	 */
	public final static String APP_ROOT_CACHE_FOLDER = APP_ROOT_FOLDER + "caches/";
	
	/**
	 * 崩溃
	 */
	public final static String APP_CRASH_LOG_FOLDER = APP_ROOT_FOLDER + "crashes/";
	
	/**
	 * 应用图片缓存文件夹
	 */
	public final static String APP_IMAGE_CACHE_FOLDER = APP_ROOT_CACHE_FOLDER + "images/";
	
	/**
	 * 歌曲封面
	 */
	public final static String APP_COVER_IMAGE_FOLDER = APP_ROOT_FOLDER + "covers/";
	
	
	
	/**
	 * 第一次进入应用
	 */
	public final static String KEY_FIRST_START = "is_first_start";
	
	/**
	 * 播放歌曲数量统计
	 */
	public final static String KEY_HISTORY_STATISTICS = "history_statistics";
	
	
	
	/**
	 * 页面标识ID字段
	 */
	public final static String FIELD_PAGE_ID = "page_id";
	
	/**
	 * 页面标题字段
	 */
	public final static String FIELD_PAGE_TITLE = "page_title";
	
	/**
	 * 本地音乐页面ID
	 */
	public final static int PAGE_ID_LOCAL_MUSIC = 0;
	
	/**
	 * 收藏页面ID
	 */
	public final static int PAGE_ID_FAVORTIE = 1;
	
	/**
	 * 最近播放页面ID
	 */
	public final static int PAGE_ID_RECENTLY_PLAY = 2;
	
	/**
	 * 播放列表页面标识ID
	 */
	public static final int PAGE_ID_PLAYLIST = 3;

	
	/**************
	 * 播放器相关
	 */
	
	// 传递给播放服务的数据的key值 -- 数据包
	public static final String KEY_PLAYER_DATA = "esplayer.data";
	// 传递给播放服务的数据的key值 -- 当前音乐在播放列表中的位置
	public static final String KEY_PLAYER_POSITION = "esplayer.position";
	// 传递给播放服务的数据的key值 -- 当前播放列表
	public static final String KEY_PLAYER_PLAYLIST = "esplayer.playlist";
	// 传递给播放服务的数据的key值 -- 进度
	public static final String KEY_PLAYER_PROGRESS = "esplayer.progress";
	
	// 从某个位置开始播放
	public static final String ACTION_PLAYER_PLAY_FORM_POSITION = "action.esplayer.play.fromPosition";
	// 播放整个播放列表
	public static final String ACTION_PLAYER_PLAY_ALL_PLAYLIST = "action.esplayer.play.allPlaylist";
	// 直接播放
	public static final String ACTION_PLAYER_PLAY_ONLY_START = "action.esplayer.play.onlyStart";
	
	// 播放器开始播放Action
	public static final String ACTION_PLAYER_ONSTART = "action.esplayer.onStart";
	// 播放器暂停播放Action
	public static final String ACTION_PLAYER_ONPAUSE = "action.esplayer.onPause";
	// 播放器重新开始播放Action
	public static final String ACTION_PLAYER_ONRESUME = "action.esplayer.onResume";
	// 播放器停止播放Action
	public static final String ACTION_PLAYER_ONSTOP = "action.esplayer.onStop";
	// 播放器播放进度Action
	public static final String ACTION_PLAYER_PLAY_PROGRESS = "action.esplayer.play.progress";
	// 播放器网络歌曲缓冲进度Action
	public static final String ACTION_PLAYER_BUFFER_PROGRESS = "action.esplayer.buffer.progress";
	
	
}
