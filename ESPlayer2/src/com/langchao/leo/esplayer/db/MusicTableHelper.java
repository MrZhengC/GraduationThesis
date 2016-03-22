package com.langchao.leo.esplayer.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.db.ESSQLiteOpenHelper.TableSongInfo;
import com.langchao.leo.esplayer.utils.CommonUtils;


/**
 * 
 * @author 碧空
 *
 */
public class MusicTableHelper {

	private ESSQLiteOpenHelper mSQLiteOpenHelper = null; 
	
	public MusicTableHelper(Context mContext) {
		mSQLiteOpenHelper = new ESSQLiteOpenHelper(mContext);
	}

	/**
	 * 获取所有本地音乐
	 */
	public List<RealSong> queryAllLocalMusic(){
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();
		Cursor cursor = readableDB.rawQuery("select * from t_song_info where _source = 0", null);
		
		List<RealSong> mLocalMusic = null;
		if (cursor.moveToFirst()){
			mLocalMusic = new ArrayList<RealSong>();
			while(!cursor.isAfterLast()){
				final RealSong song = CommonUtils.createRealSong(cursor);
				if (song != null){
					mLocalMusic.add(song);
				}
				cursor.moveToNext();
			}
		}

		cursor.close();
		readableDB.close();
		
		return mLocalMusic;
	}
	
	/**
	 * 获取所有本地音乐的个数
	 * @return
	 */
	public int getAllLocalMusicCount(){
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();
		Cursor cursor = readableDB.rawQuery("select count(*) from t_song_info where _source = 0", null);
		
		int result = 0;
		if (cursor.moveToFirst()){
			result = cursor.getInt(0);
		}
		cursor.close();
		readableDB.close();
		
		return result;
	}
	
	/**
	 * 获取所有网络音乐
	 */
	public List<RealSong> queryAllWebMusic(){
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();
		Cursor cursor = readableDB.rawQuery("select * from t_song_info where _source = 1", null);
		
		List<RealSong> mLocalMusic = null;
		if (cursor.moveToFirst()){
			mLocalMusic = new ArrayList<RealSong>();
			while(!cursor.isAfterLast()){
				final RealSong song = CommonUtils.createRealSong(cursor);
				if (song != null){
					mLocalMusic.add(song);
				}
				cursor.moveToNext();
			}
		}
		
		cursor.close();
		readableDB.close();
		
		return mLocalMusic;
	}
	
	/**
	 * 获取历史纪录，也即所有lastPlayTime > 0 的内容，但是按照lastPlayTime排序
	 */
	public List<RealSong> queryAllHistory(){
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();
		
		Cursor cursor = readableDB.query(
				ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, 
				null, 
				TableSongInfo.LASTPLAY_TIME + " > 0 ", 
				null, null, null, TableSongInfo.LASTPLAY_TIME + " DESC");
		
		List<RealSong> mHistoryMusic = null;
		if (cursor.moveToFirst()){
			mHistoryMusic = new ArrayList<RealSong>();
			while(!cursor.isAfterLast()){
				final RealSong song = CommonUtils.createRealSong(cursor);
				if (song != null){
					mHistoryMusic.add(song);
				}
				cursor.moveToNext();
			}
		}
		
		cursor.close();
		readableDB.close();
		
		return mHistoryMusic;
	}
	
	/**
	 * 获取历史纪录个数
	 * @return
	 */
	public int getAllHistoryMusicCount(){
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();

		// 认为播放过的歌曲一定会存在lastplaytime且要大于0
		String sql = "select count(*) from t_song_info where " 
						+ TableSongInfo.LASTPLAY_TIME + " > 0";
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
	 * 获取收藏
	 */
	public List<RealSong> queryAllFavorite(){
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();
		
		String selection = TableSongInfo.FAVORITE_TIME + " > 0 ";
		Cursor cursor = readableDB.query(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, 
				null, selection, null, null, null, TableSongInfo.FAVORITE_TIME + " DESC");
		
		List<RealSong> mFavoriteMusic = null;
		if (cursor.moveToFirst()){
			mFavoriteMusic = new ArrayList<RealSong>();
			while(!cursor.isAfterLast()){
				final RealSong song = CommonUtils.createRealSong(cursor);
				if (song != null){
					mFavoriteMusic.add(song);
				}
				cursor.moveToNext();
			}
		}
		
		cursor.close();
		readableDB.close();
		
		return mFavoriteMusic;
	}
	
	/**
	 * 获取收藏歌曲个数
	 * @return
	 */
	public int getAllFavoriteMusicCount(){
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();

		// 认为播放过的歌曲一定会存在lastplaytime且要大于0
		String sql = "select count(*) from t_song_info where " 
						+ TableSongInfo.FAVORITE_TIME + " > 0 ";
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
	 * 更改最后播放时间
	 * @param song
	 * @param lastPlayTime
	 * @return
	 */
	public synchronized boolean changeLastPlayTime(RealSong song,long lastPlayTime){
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();

		// 更新SQL语句
		String sql = "UPDATE " + ESSQLiteOpenHelper.DB_TABLE_SONG_INFO 
				+ " SET " + TableSongInfo.LASTPLAY_TIME + " = ? ";
		
		sql += CommonUtils.createUniqueWhereClause(song);
		
		Cursor cursor = writableDB.rawQuery(sql, new String[]{""+lastPlayTime});

		int result = cursor.getCount();
		
		cursor.close();
		writableDB.close();
		
		return result > 0;
	}
	
	/**
	 * 更改收藏时间
	 * @param song
	 * @param favoriteTime
	 * @return
	 */
	public synchronized boolean changeFavoriteTime(RealSong song,long favoriteTime){
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		// 更新SQL语句 
		String sql = "UPDATE " + ESSQLiteOpenHelper.DB_TABLE_SONG_INFO 
				+ " SET " + TableSongInfo.FAVORITE_TIME + " = ? ";
		
		// 生成where子句
		sql += CommonUtils.createUniqueWhereClause(song);
		
		Cursor cursor = writableDB.rawQuery(sql, new String[]{""+favoriteTime});
		
		int result = cursor.getCount();
		
		cursor.close();
		writableDB.close();
		
		return result > 0;
	}

	/**
	 * 是否收藏了该歌曲
	 * @return
	 */
	public boolean isFavorite(RealSong song){
		
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();

		String sql = "SELECT " + TableSongInfo.FAVORITE_TIME
				+ " FROM " + ESSQLiteOpenHelper.DB_TABLE_SONG_INFO;
		
		// 生成where子句
		sql += CommonUtils.createUniqueWhereClause(song);
		
		Cursor cursor = readableDB.rawQuery(sql, null);
		
		// 此处一定要是long型否则出错
		long result = 0;
		if (cursor.moveToFirst()) {
			result = cursor.getLong(0);
		}
		
		cursor.close();
		readableDB.close();
		
		return result>0;
	}
	
	/**
	 * 是否存在
	 * @param song
	 * @return
	 */
	public boolean isExist(RealSong song){
		if (song!=null){
			SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();
			// selection
			String selection = CommonUtils.createUniqueSelection(song);
			Cursor cursor = readableDB.query(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, 
					null, selection, null, null, null, null);
			int result = cursor.getCount();
			cursor.close();
			readableDB.close();
			return result>0;
		}
		return false;
	}
	
	/**
	 * 插入一条数据
	 * @param song
	 * @return
	 */
	public synchronized boolean insert(RealSong song){
		if (song == null) {
			return false;
		}
		
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		// selection 首先判断是否存在
		String selection = CommonUtils.createUniqueSelection(song);
		Cursor cursor = writableDB.query(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, 
				null, selection, null, null, null, null);
		
		long result = 0;
		if (cursor.getCount() == 0){
			ContentValues values = CommonUtils.createContentValues(song);
			result = writableDB.insert(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, null, values);
		}else{
			result = -1;
		}

		cursor.close();
		writableDB.close();
		
		return result>0;
	}

	/**
	 * 插入一批数据
	 * @param songs
	 * @return 成功的条目数
	 */
	public synchronized long insertAll(List<RealSong> songs){
		if (songs==null || songs.size() == 0){
			return 0;
		}
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		long result = 0;
		// 开启事务
		writableDB.beginTransaction();
		for (RealSong song : songs) {
			
			// selection 首先判断是否存在
			String selection = CommonUtils.createUniqueSelection(song);
			Cursor cursor = writableDB.query(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, 
					null, selection, null, null, null, null);
			int n = cursor.getCount();
			cursor.close();
			if (n > 0){
				continue;
			}
			
			final ContentValues values = CommonUtils.createContentValues(song);
			result += writableDB.insert(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, null, values);
		}
		// 设置事务成功
		writableDB.setTransactionSuccessful();
		// 关闭事务
		writableDB.endTransaction();
		writableDB.close();
		return result;
	}

	/**
	 * 更新一条记录，或者如果不存在插入一条数据
	 * @param songs
	 * @return
	 */
	public synchronized long updateOrInsertIfNotExist(List<RealSong> songs){
		if (songs==null || songs.size() == 0){
			return 0;
		}
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		long result = 0;
		// 开启事务
		writableDB.beginTransaction();
		for (RealSong song : songs) {
			// selection 首先判断是否存在
			String selection = CommonUtils.createUniqueSelection(song);
			Cursor cursor = writableDB.query(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, 
					null, selection, null, null, null, null);
			int n = cursor.getCount();
			cursor.close();
			
			final ContentValues values = CommonUtils.createContentValues(song);
			if (n > 0){
				// 如果存在则更新该条记录
				result += writableDB.update(
						ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, 
						values, 
						selection, null);
			}else {
				// 如果不存在则插入一条新纪录
				result += writableDB.insert(
						ESSQLiteOpenHelper.DB_TABLE_SONG_INFO,
						null, values);
			}
		}
		// 设置事务成功
		writableDB.setTransactionSuccessful();
		// 关闭事务
		writableDB.endTransaction();
		writableDB.close();
		return result;
	}
	
	/**
	 * 删除一条记录，并且会删除与这些本地音乐相关联的播放列表中的数据
	 * @param song
	 * @return
	 */
	public synchronized boolean delete(RealSong song){
		if (song == null) {
			return false;
		}
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		String whereStr = CommonUtils.createUniqueSelection(song);
		int result = writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, whereStr, null);
		// 同时删除与该本地文件相关联的播放列表中的记录
		writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST_SONG, whereStr, null);
		
		writableDB.close();
		return result>0;
	}
	
	/**
	 * 删除一批记录，并且会删除与这些本地音乐相关联的播放列表中的数据
	 * @param song
	 * @return
	 */
	public synchronized int delete(List<RealSong> songs){
		if (songs == null || songs.size()==0) {
			return 0;
		}
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		// 开启事务
		writableDB.beginTransaction();
		
		int result = 0;
		for (RealSong song : songs) {
			String whereStr = CommonUtils.createUniqueSelection(song);
			result += writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, whereStr, null);
			// 同时删除与该本地文件相关联的播放列表中的记录
			writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST_SONG, whereStr, null);
			
		}
		// 设置事务执行成功
		writableDB.setTransactionSuccessful();
		// 关闭事务
		writableDB.endTransaction();
		
		writableDB.close();
		return result;
	}
	
	/**
	 * 删除所有本地音乐，同时清空所有播放列表
	 * @return
	 */
	public synchronized boolean deleteAllLocalMusic(){
		int result = 0;
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		result = writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, 
				TableSongInfo.SOURCE + " = 0 ", null);
		// 同时清空所有播放列表
		writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_PLAYLIST_SONG, null, null);
		
		writableDB.close();
		return result > 0;
	}
	
	/**
	 * 删除一条历史纪录
	 * @param song
	 * @return
	 */
	public synchronized boolean deleteHistory(RealSong song){
		int result = 0;
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(TableSongInfo.LASTPLAY_TIME, 0);
		
		String whereStr = CommonUtils.createUniqueSelection(song);
		
		result = writableDB.update(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, values, whereStr, null);
		
		writableDB.close();
		return result > 0;
	}
	/**
	 * 取消一批数据收藏
	 * @param song
	 * @return
	 */
	public synchronized boolean deleteFavorite(List<RealSong> songs){
		if (songs == null || songs.size()==0) {
			return false;
		}
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(TableSongInfo.FAVORITE_TIME, 0);
		
		writableDB.beginTransaction();
		
		int result = 0;
		for (RealSong song : songs) {
			String whereStr = CommonUtils.createUniqueSelection(song);
			result += writableDB.update(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, values, whereStr, null);
		}
		
		writableDB.setTransactionSuccessful();
		writableDB.endTransaction();
		
		writableDB.close();
		return result > 0;
	}

	/**
	 * 删除一批历史纪录
	 * @param song
	 * @return
	 */
	public synchronized boolean deleteHistory(List<RealSong> songs){
		if (songs == null || songs.size()==0) {
			return false;
		}
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(TableSongInfo.LASTPLAY_TIME, 0);
		
		writableDB.beginTransaction();
		
		int result = 0;
		for (RealSong song : songs) {
			String whereStr = CommonUtils.createUniqueSelection(song);
			result += writableDB.update(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, values, whereStr, null);
		}
		
		writableDB.setTransactionSuccessful();
		writableDB.endTransaction();
		
		writableDB.close();
		return result > 0;
	}
	
	/**
	 * 清除所有历史纪录
	 * @return
	 */
	public synchronized boolean clearAllHistroy(){
		int result = 0;
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(TableSongInfo.LASTPLAY_TIME, 0);
		
		result = writableDB.update(ESSQLiteOpenHelper.DB_TABLE_SONG_INFO, values, null, null);
		
		writableDB.close();
		return result > 0;
	}
	
}
