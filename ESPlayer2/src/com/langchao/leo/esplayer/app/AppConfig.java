package com.langchao.leo.esplayer.app;

import java.io.File;

import android.content.Context;

import com.langchao.leo.esplayer.bean.Playlist;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.utils.MD5;
import com.langchao.leo.esplayer.utils.SharedPrefUtils;

/**
 * 应用级配置
 * @author 碧空
 *
 */
public class AppConfig {

	private static AppConfig mInstance = null;
	
	private Context mContext = null;
	
	/**
	 * 列表播放控制次序-列表循环
	 */
	public final static int PLAY_ORDER_LIST_LOOP = 0;
	/**
	 * 列表播放控制次序-随机播放
	 */
	public final static int PLAY_ORDER_SHUFFLE = 1;
	/**
	 * 列表播放控制次序-单曲循环
	 */
	public final static int PLAY_ORDER_SINGLE_CYCLE = 2;
	
	
	private AppConfig(Context context){
		this.mContext = context.getApplicationContext();
	}
	
	public static AppConfig getInstance(Context context){
		if (mInstance == null) {
			mInstance = new AppConfig(context);
		}
		return mInstance;
	}
	
	/**
	 * 获取应用图片缓存地址
	 * @return
	 */
	public String getAppImageCacheFolder(){
		String imageCache = (String) SharedPrefUtils.get(
				mContext, 
				"image_cache",
				Constants.APP_IMAGE_CACHE_FOLDER);
		
		return imageCache;
	}
	
	/**
	 * 设置图片缓存地址
	 * @param imageCacheFolder
	 */
	public void setAppImageCacheFolder(String imageCacheFolder){
		SharedPrefUtils.put(mContext, "image_cache", imageCacheFolder);
	}
	
	/**
	 * 获取列表播放控制次序
	 * @return
	 */
	public int getPlayOrder(){
		Integer order = (Integer) SharedPrefUtils.get(
				mContext, "play_order", PLAY_ORDER_LIST_LOOP);
		return order.intValue();
	}
	
	/**
	 * 设置列表播放控制次序
	 * @param order
	 */
	public void setPlayOrder(int order){
		// 是在我们支持的三种播放控制次序内时直接设置；否则，使用默认的列表循环
		if (order >= 0 && order <= 2) {
			SharedPrefUtils.put(mContext, "play_order", order);
		} else{
			SharedPrefUtils.put(mContext, "play_order", PLAY_ORDER_LIST_LOOP);
		}
	}
	
	/**
	 * 获取系统配置 -- 是否只在wifi网络下联网播放歌曲
	 * @return
	 */
	public boolean isOnlyWifi(){
		return ((Boolean)SharedPrefUtils.get(mContext, "only_wifi", false)).booleanValue();
	}
	
	/**
	 * 设置是否只在wifi网络下联网播放歌曲
	 * @param isOnlyWifi
	 */
	public void setOnlyWifi(boolean isOnlyWifi){
		SharedPrefUtils.put(mContext, "only_wifi", isOnlyWifi);
	}
	
	/**
	 * 获取累计播放次数
	 * @return
	 */
	public int getPlayerHistoryStatistics(){
		return ((Integer) SharedPrefUtils.get(mContext, 
				Constants.KEY_HISTORY_STATISTICS, 0)).intValue();
	}
	
	/**
	 * 设置累计播放次数
	 * @param num
	 */
	public void setPlayerHistoryStatistics(int num){
		SharedPrefUtils.put(mContext, 
				Constants.KEY_HISTORY_STATISTICS, 
				num);
	}
	
	/**
	 * 获取保存的歌曲索引
	 * @return
	 */
	public int getSavedMusicIndex(){
		return ((Integer) SharedPrefUtils.get(mContext, "saved_music_index", 0)).intValue();
	}
	
	/**
	 * 保存歌曲索引
	 * @param index
	 */
	public void setSavedMusicIndex(int index) {
		SharedPrefUtils.put(mContext, "saved_music_index", index);
	}
	
	/**
	 * 获取保存的播放列表
	 * @return
	 */
	public Playlist getSavedPlaylist(){
		File srcObjFile = new File(mContext.getFilesDir(), MD5.md5("saved_playlist_name"));
		return Playlist.readFromFile(srcObjFile);
	}
	
	/**
	 * 保存播放列表到磁盘（序列化）
	 * @param playlist
	 */
	public void savePlaylist(Playlist playlist){
		File srcObjFile = new File(mContext.getFilesDir(), MD5.md5("saved_playlist_name"));
		Playlist.writeToFile(playlist, srcObjFile);
	}
	
}
