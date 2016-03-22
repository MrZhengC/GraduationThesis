package com.langchao.leo.esplayer.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.langchao.leo.esplayer.bean.Playlist;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.db.ESSQLiteOpenHelper.TablePlaylist;
import com.langchao.leo.esplayer.db.ESSQLiteOpenHelper.TablePlaylistSong;
import com.langchao.leo.esplayer.utils.CommonUtils;


/**
 * 播放列表数据库表操作助手
 * @author 碧空
 */
public class PlaylistTableHelper {

	private ESSQLiteOpenHelper mSQLiteOpenHelper = null; 
	
	public PlaylistTableHelper(Context mContext) {
		mSQLiteOpenHelper = new ESSQLiteOpenHelper(mContext);
	}

	/**
	 * 根据cursor生成歌曲列表对象
	 * @param cursor
	 * @return
	 */
	protected Playlist createPlaylist(Cursor cursor){
		Playlist playlist = null;
		if (cursor != null){
			playlist = new Playlist();
			try {
				playlist.setPlaylistId(cursor.getInt(cursor.getColumnIndex(TablePlaylist.PLAYLIST_ID)));
				playlist.setPlaylistName(cursor.getString(cursor.getColumnIndex(TablePlaylist.PLAYLIST_NAME)));
				playlist.setCoverUrl(cursor.getString(cursor.getColumnIndex(TablePlaylist.COVER_URL)));
			} catch (Exception e) {
			}
		}
		return playlist;
	}
	
	/**
	 * 获取所有播放列表,此方法不包含歌曲信息
	 * @return 
	 */
	public List<Playlist> queryAllPlaylists(){
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();
		
		// 开启事务
		readableDB.beginTransaction();
		
		// 查询所有播放列表信息
		String sql = "select * from " + ESSQLiteOpenHelper.DB_TABLE_PLAYLIST;
		// 查询某一个播放列表的歌曲数目
		String sqlCount = "select count(*) from " 
				+ ESSQLiteOpenHelper.DB_TABLE_PLAYLIST_SONG 
				+ " where " + TablePlaylist.PLAYLIST_ID + " = ?";
		Cursor cursor = readableDB.rawQuery(sql, null);
		
		List<Playlist> mPlaylists = null;
		if (cursor.moveToFirst()){
			mPlaylists = new ArrayList<Playlist>();
			while(!cursor.isAfterLast()){
				final Playlist playlist = createPlaylist(cursor);
				
				if (playlist != null){
					Cursor cs = readableDB.rawQuery(sqlCount, 
							new String[]{""+playlist.getPlaylistId()});
					if (cs.getCount()>0 && cs.moveToFirst()) {
						playlist.setCount(cs.getInt(0));
					}
					cs.close();
					mPlaylists.add(playlist);
				}
				cursor.moveToNext();
			}
		}

		readableDB.setTransactionSuccessful();
		readableDB.endTransaction();
		
		cursor.close();
		readableDB.close();
		
		return mPlaylists;
	}
	
	/**
	 * 获取所有播放列表的个数
	 * @return
	 */
	public int getPlaylistCount(){
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();
		String sql = "select count(*) from "+ ESSQLiteOpenHelper.DB_TABLE_PLAYLIST;
		Cursor cursor = readableDB.rawQuery(sql, null);
		
		int result = 0;
		if (cursor.moveToFirst()){
			result = cursor.getInt(0);
		}
		cursor.close();
		readableDB.close();
		
		return result;
	}
	
	/**
	 * 是否存在名称为name的播放列表
	 * @param name
	 * @return
	 */
	public boolean isPlaylistExist(String name){
		if (TextUtils.isEmpty(name)){
			return false;
		}
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();
		
		Cursor cursor = readableDB.query(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST, 
				null, 
				TablePlaylist.PLAYLIST_NAME + " = '" + CommonUtils.sqliteEscape(name) + "'", 
				null, null, null, null);
		int result = cursor.getCount();
		cursor.close();
		readableDB.close();
		return result>0;
	}
	
	/**
	 * 由 Playlist 对象生成 ContentValues
	 * @param playlist
	 * @return
	 */
	protected ContentValues createContentValues(Playlist playlist) {
		ContentValues values = null;
		
		if (playlist != null) {
			values = new ContentValues();
			//values.put(TablePlaylist.PLAYLIST_ID, playlist.getPlaylistId());
			values.put(TablePlaylist.PLAYLIST_NAME, playlist.getPlaylistName());
			values.put(TablePlaylist.COVER_URL, playlist.getCoverUrl());
		}
		
		return values;
	}
	
	/**
	 * 插入一条数据
	 * @param playlist
	 * @return
	 */
	public synchronized boolean insert(Playlist playlist){
		if (playlist == null) {
			return false;
		}
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		// 首先判断该名称是否存在
		
		Cursor cursor = writableDB.query(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST, 
				null, 
				TablePlaylist.PLAYLIST_NAME + " = '" +
						CommonUtils.sqliteEscape(playlist.getPlaylistName()) + "'", 
				null, null, null, null);
		
		long result = 0;
		if (cursor.getCount() <= 0) {
			ContentValues values = createContentValues(playlist);
			result = writableDB.insert(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST, null, values);
		}
		
		cursor.close();
		writableDB.close();
		return result>0;
	}

	/**
	 * 删除一条记录
	 * @param playlist
	 * @return
	 */
	public synchronized boolean delete(Playlist playlist){
		if (playlist == null) {
			return false;
		}
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		String whereStr = "";
		int result = 0;
		if (playlist.getPlaylistId()>0){
			whereStr += TablePlaylist.PLAYLIST_ID + " = " + playlist.getPlaylistId();
		} else {
			whereStr += TablePlaylist.PLAYLIST_NAME + " = '" + CommonUtils.sqliteEscape(playlist.getPlaylistName())+"'";
		}
		
		// 删除播放列表表
		result = writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST, whereStr, null);
		// 播放列表中的歌曲同样需要删除
		writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST_SONG, whereStr, null);
		
		writableDB.close();
		return result>0;
	}
	
	/**
	 * 删除一批记录
	 * @param playlists
	 * @return
	 */
	public synchronized int delete(List<Playlist> playlists){
		if (playlists == null || playlists.size() == 0) {
			return 0;
		}
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		int result = 0;
		for (Playlist playlist : playlists){
			String whereStr = "";
			if (playlist.getPlaylistId()>0){
				whereStr += TablePlaylist.PLAYLIST_ID + " = " + playlist.getPlaylistId();
			} else {
				whereStr += TablePlaylist.PLAYLIST_NAME + " = '" + 
						CommonUtils.sqliteEscape(playlist.getPlaylistName())+"'";
			}
			// 删除播放列表表
			result += writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST, whereStr, null);
			// 播放列表中的歌曲同样需要删除
			writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST_SONG, whereStr, null);
		}
		
		writableDB.close();
		return result;
	}
	
	
	
	/**
	 * 删除所有播放列表表中的内容且不删除数据库表
	 * @return
	 */
	public synchronized boolean deleteAllPlaylist(){
		int result = 0;
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST, null, null);
		writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST_SONG, null, null);
		
		writableDB.close();
		return result > 0;
	}
	
	
	/**
	 * 获取播放列表下的所有歌曲
	 * @param playlist
	 * @return
	 */
	public List<RealSong> obtainAllSongs(Playlist playlist){
		if (playlist == null) {
			return null;
		}
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();
		
		String selection = TablePlaylist.PLAYLIST_ID + " = " + playlist.getPlaylistId();
		
		Cursor cursor = readableDB.query(ESSQLiteOpenHelper.DB_VIEW_PLAYLIST_SONG, null, 
				selection, null, null, null, null);
		
		List<RealSong> mSongs = null;
		
		if (cursor.moveToFirst()){
			mSongs = new ArrayList<RealSong>();
			while(!cursor.isAfterLast()){
				RealSong song = CommonUtils.createRealSong(cursor);
				mSongs.add(song);
				cursor.moveToNext();
			}
		}
		
		cursor.close();
		readableDB.close();
		return mSongs;
	}
	
	/**
	 * 向播放列表中添加一首歌曲
	 * @param playlist
	 * @param song
	 * @return
	 */
	public synchronized boolean insertSongToPlaylist(Playlist playlist, RealSong song){
		if (playlist == null || song == null){
			return false;
		}
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(TablePlaylist.PLAYLIST_ID, playlist.getPlaylistId());
		values.put(TablePlaylistSong.SONG_ID, song.getSongId());
		long result = writableDB.insert(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST_SONG, null, values);
		
		writableDB.close();
		return result>0;
	}
	
	/**
	 * 向播放列表中添加一批歌曲
	 * @param playlist
	 * @param songs
	 * @return
	 */
	public synchronized long insertSongsToPlaylist(Playlist playlist, List<RealSong> songs){
		if (playlist == null || songs == null || songs.size()==0){
			return 0;
		}
		
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		writableDB.beginTransaction();
		
		ContentValues values = new ContentValues();
		values.put(TablePlaylist.PLAYLIST_ID, playlist.getPlaylistId());
		long result = 0;
		for (RealSong song : songs) {
			values.put(TablePlaylistSong.SONG_ID, song.getSongId());
			result += writableDB.insert(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST_SONG, null, values);
		}
		writableDB.setTransactionSuccessful();
		writableDB.endTransaction();
		
		writableDB.close();
		return result;
	}
	
	/**
	 * 从给定播放列表中删除歌曲
	 * @param playlist
	 * @param songs
	 * @return
	 */
	public synchronized long deleteMusicFromPlaylist(Playlist playlist, List<RealSong> songs){
		if (playlist == null || songs == null || songs.size() <= 0) {
			return 0;
		}
		
		SQLiteDatabase writeableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		String whereClause = "( " + TablePlaylistSong.PLAYLIST_ID 
								+ " = ? ) AND ( " 
								+ TablePlaylistSong.SONG_ID + " = ? )";
		
		// 开启事务
		writeableDB.beginTransaction();
		
		long result = 0;
		
		for (RealSong song : songs) {
			// 执行删除
			result += writeableDB.delete(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST_SONG,
											whereClause, 
											new String[]{
												"" + playlist.getPlaylistId(),
												"" + song.getSongId()
											});
		}
		
		// 设置事务执行成功
		writeableDB.setTransactionSuccessful();
		// 结束事务
		writeableDB.endTransaction();
		// 关闭数据库
		writeableDB.close();
		
		return result;
	}
	
	
	/**
	 * 插入一批歌曲到播放列表，如果不存在的话
	 * @param playlist
	 * @param songs
	 * @return
	 */
	public long insertMusicToPlaylistIfNotExist(Playlist playlist, List<RealSong> songs){
		if (playlist == null || songs == null || songs.size() <= 0) {
			return 0;
		}
		
		long result = 0;
		
		SQLiteDatabase writeableDB = mSQLiteOpenHelper.getWritableDatabase();
		writeableDB.beginTransaction();

		String selection = "( " + TablePlaylistSong.PLAYLIST_ID 
							+ " = ? ) AND ( " 
							+ TablePlaylistSong.SONG_ID + " = ? )";
		
		ContentValues values = new ContentValues();
		values.put(TablePlaylistSong.PLAYLIST_ID, playlist.getPlaylistId());
		
		for (RealSong song : songs) {
			
			// 首先要判断该歌曲是否存在，不存在再执行插入操作
			Cursor cursor = writeableDB.query(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST_SONG, 
								null, 
								selection, 
								new String[]{
									""+playlist.getPlaylistId(), 
									""+song.getSongId()
								}, null, null, null);
			
			if (cursor.getCount() == 0) {
				// 执行插入操作
				values.put(TablePlaylistSong.SONG_ID, song.getSongId());
				
				// 如果插入成功，结果加一
				if (writeableDB.insert(
						ESSQLiteOpenHelper.DB_TABLE_PLAYLIST_SONG, 
						null, 
						values) > 0){
					result += 1;
				}
				
			}
			
			cursor.close();
		}
		
		
		writeableDB.setTransactionSuccessful();
		writeableDB.endTransaction();
		writeableDB.close();
		
		return result;
	} 
	
	/**
	 * 更换封面
	 * @param playlist
	 * @return
	 */
	public boolean changePlaylistCover(Playlist playlist){
		if (playlist == null) {
			return false;
		}
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(TablePlaylist.COVER_URL, playlist.getCoverUrl());
		
		String whereClause = TablePlaylist.PLAYLIST_ID + " = " + playlist.getPlaylistId();
		int result = writableDB.update(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST, values, whereClause, null);
		
		writableDB.close();
		return result>0;
	}
	
}
