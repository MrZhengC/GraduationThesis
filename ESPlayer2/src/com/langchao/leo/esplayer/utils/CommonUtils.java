package com.langchao.leo.esplayer.utils;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.bean.FMSongEntity;
import com.langchao.leo.esplayer.bean.FMSongEntity.RealSongEntity;
import com.langchao.leo.esplayer.bean.FMSongEntity.SongInfo;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.core.PlayerService;
import com.langchao.leo.esplayer.db.ESSQLiteOpenHelper.TableSongInfo;
import com.langchao.leo.esplayer.interfaces.IActivityInteraction;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 一般工具类
 * @author 碧空
 *
 */
public class CommonUtils {

	/**
	 * 转义sql语句
	 * @param sql
	 * @return
	 */
	public static String sqliteEscape(String sql){
		if (TextUtils.isEmpty(sql)) {
			return sql;
		}
		return sql.replaceAll("'", "''"); 
	}

	/**
	 * 生成唯一查询语句
	 * @param song
	 * @return
	 */
	public static String createUniqueSelection(RealSong song){
		if (song == null) {
			return "";
		}
		String whereStr = "";
		if (song.getSongId() > 0){
			whereStr += TableSongInfo.SONG_ID + " = " + song.getSongId();
		} else {
			if (song.getSource() == 0){
				whereStr += TableSongInfo.LOCAL_PATH + " = '" + CommonUtils.sqliteEscape(song.getLocalPath()) + "'";
			} else {
				whereStr += TableSongInfo.SONG_URL + " = '" + CommonUtils.sqliteEscape(song.getSongUrl()) +"'";
			}
		}
		return whereStr;
	}
	/**
	 * 生成唯一where子句
	 * @param song
	 * @return
	 */
	public static String createUniqueWhereClause(RealSong song){
		if (song == null) {
			return "";
		}
		String whereStr = "";
		if (song.getSongId() > 0){
			whereStr += " WHERE " + TableSongInfo.SONG_ID + " = " + song.getSongId();
		} else {
			// 否则使用URL 或者 localPath
			if (song.getSource() == 0){
				whereStr += " WHERE " + TableSongInfo.LOCAL_PATH + " = '"+ sqliteEscape(song.getLocalPath()) +"'";
			} else {
				whereStr += " WHERE " + TableSongInfo.SONG_URL + " = '"+ sqliteEscape(song.getSongUrl()) +"'";
			}
		}
		return whereStr;
	}
	
	/**
	 * 生成歌曲对象
	 * @param cursor
	 * @return
	 */
	public static RealSong createRealSong(Cursor cursor){
		RealSong song = null;
		if (cursor != null){
			song = new RealSong();
			try {
				song.setSongId(cursor.getLong(cursor.getColumnIndex(TableSongInfo.SONG_ID)));
				song.setSongName(cursor.getString(cursor.getColumnIndex(TableSongInfo.SONG_NAME)));
				song.setSongUrl(cursor.getString(cursor.getColumnIndex(TableSongInfo.SONG_URL)));
				song.setLocalPath(cursor.getString(cursor.getColumnIndex(TableSongInfo.LOCAL_PATH)));
				song.setArtist(cursor.getString(cursor.getColumnIndex(TableSongInfo.ARTIST_NAME)));
				song.setAlbum(cursor.getString(cursor.getColumnIndex(TableSongInfo.ALBUM_NAME)));
				song.setBigSongPic(cursor.getString(cursor.getColumnIndex(TableSongInfo.SONG_PIC_BIG)));
				song.setSmallSongPic(cursor.getString(cursor.getColumnIndex(TableSongInfo.SONG_PIC_SMALL)));
				song.setLrcLink(cursor.getString(cursor.getColumnIndex(TableSongInfo.LRC_LINK)));
				song.setDuration(cursor.getLong(cursor.getColumnIndex(TableSongInfo.DURATION)));
				song.setSize(cursor.getLong(cursor.getColumnIndex(TableSongInfo.SIZE)));
				long favoriteTime = cursor.getLong(cursor.getColumnIndex(TableSongInfo.FAVORITE_TIME));
				song.setFavoriteTime(favoriteTime);
				song.setLastPlayTime(cursor.getLong(cursor.getColumnIndex(TableSongInfo.LASTPLAY_TIME)));
				song.setSource(cursor.getInt(cursor.getColumnIndex(TableSongInfo.SOURCE)));
			} catch (Exception e) {
			}
		}
		return song;
	}
	
	/**
	 * 由网络歌曲实体组装RealSong对象
	 * @param entity
	 * @return
	 */
	public static RealSong createRealSong(FMSongEntity entity) {
		if (entity == null) {
			return null;
		}
		
		SongInfo info = entity.getData();
		if (info == null) {
			return null;
		}
		
		List<RealSongEntity>  entities = info.getSongList();
		
		if (entities == null || entities.size() == 0) {
			return null;
		}
		
		RealSongEntity rse = entities.get(0);

		RealSong song = new RealSong();
		song.setSongId(Long.valueOf(rse.getSongId()).longValue());
		song.setSource(1);
		song.setSongName(rse.getSongName());
		song.setAlbum(rse.getAlbumName());
		song.setArtist(rse.getArtistName());
		song.setBigSongPic(rse.getSongPicBig());
		song.setDuration(rse.getTime()*1000);// 网络获取的时间是以秒为单位的
		song.setLrcLink(rse.getLrcLink());
		song.setSize(rse.getSize());
		song.setSmallSongPic(rse.getSongPicSmall());
		song.setSongUrl(rse.getSongLink());
		
		return song;
	}
	
	/**
	 * 
	 * @param song
	 * @return
	 */
	public static ContentValues createContentValues(RealSong song) {
		ContentValues values = null;
		
		if (song != null) {
			values = new ContentValues();
			values.put(TableSongInfo.SONG_NAME, song.getSongName());
			values.put(TableSongInfo.SONG_URL, song.getSongUrl());
			values.put(TableSongInfo.LOCAL_PATH, song.getLocalPath());
			values.put(TableSongInfo.ARTIST_NAME, song.getArtist());
			values.put(TableSongInfo.ALBUM_NAME, song.getAlbum());
			values.put(TableSongInfo.SONG_PIC_BIG, song.getBigSongPic());
			values.put(TableSongInfo.SONG_PIC_SMALL, song.getSmallSongPic());
			values.put(TableSongInfo.LRC_LINK, song.getLrcLink());
			values.put(TableSongInfo.DURATION, song.getDuration());
			values.put(TableSongInfo.SIZE, song.getSize());
			values.put(TableSongInfo.FAVORITE_TIME, song.getFavoriteTime());
			values.put(TableSongInfo.LASTPLAY_TIME, song.getLastPlayTime());
			values.put(TableSongInfo.SOURCE, song.getSource());
		}
		return values;
	}
	
	/**
	 * 关闭Fragment
	 * @param frag
	 */
	public static void finishFragment(Fragment frag){
		// 关闭当前页面
		// 获取本Fragment附加到的Activity对象
		final Activity activity = frag.getActivity();
		// 判断该activity是否是IActivityInteraction的一个实例
		if (activity instanceof IActivityInteraction) {
			// 将该Activity作为IActivityInteraction这个接口来使用
			final IActivityInteraction interaction = (IActivityInteraction)activity;
			// 调用出栈方法
			interaction.popBackStack(frag.getClass().getName());
		}
	}
	
	/**
	 * 获取Uri路径
	 * @param song
	 * @return
	 */
	public static String getUri(RealSong song){
		if (song != null) {
			if (song.getSource() == 0 ) {
				if (!TextUtils.isEmpty(song.getBigSongPic())) {
					File file = new File(""+song.getBigSongPic());
					return Uri.fromFile(file).toString(); 
				} else if (!TextUtils.isEmpty(song.getSmallSongPic())) {
					File file = new File(""+song.getSmallSongPic());
					return Uri.fromFile(file).toString(); 
				}
			} else {
				return song.getBigSongPic();
			}
		}
		return null;
	}
	
	/**
	 * 获取默认的歌曲封面image options
	 * @return
	 */
	public static DisplayImageOptions getDefaultMusicCoverOptions(){
		DisplayImageOptions mImageOptions = new DisplayImageOptions.Builder()
												.cacheInMemory(true)
												.cacheOnDisk(true)
												.bitmapConfig(Config.RGB_565)
												.showImageForEmptyUri(R.drawable.song_cover)
												.showImageOnFail(R.drawable.song_cover)
												.showImageOnLoading(R.drawable.song_cover)
												.build();
		return mImageOptions;
	}
	
	/**
	 * 默认的分钟格式化语句
	 */
	private static String DefaultMinuteFormat = "%1$02d:%2$02d";
	
	/**
	 * 获取格式化时间
	 * @param millisec 毫秒数
	 * @return
	 */
	public static String formatTimeMinute(int millisec) {
		
		if (millisec <= 0) {
			return String.format(DefaultMinuteFormat, 0, 0);
		}
		
		int minute, second;
		minute = ((millisec/1000) / 60) % 60;
		second = (millisec/1000) % 60;
		
		return String.format(DefaultMinuteFormat, minute, second);
	}
	
	/**
	 * 启动播放服务
	 * @param context
	 * @param action
	 * @param data
	 */
	public static void startPlayerService(Context context, String action, Bundle data){
		Intent service = new Intent(context, PlayerService.class);
		service.setAction(action);
		service.putExtra(Constants.KEY_PLAYER_DATA, data);
		context.startService(service);
	}

	/**
	 * 格式化单位
	 * @param size
	 * @return
	 */
	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return size + "B";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "GB";
		}
		
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
				+ "TB";
	}
}
