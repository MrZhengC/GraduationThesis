package com.langchao.leo.esplayer.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.langchao.leo.esplayer.bean.DownloadEntity;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.db.ESSQLiteOpenHelper.TableDownload;


/**
 * 
 * @author 碧空
 *
 */
public class DownloadTableHelper {

	private ESSQLiteOpenHelper mSQLiteOpenHelper = null; 
	
	public DownloadTableHelper(Context mContext) {
		mSQLiteOpenHelper = new ESSQLiteOpenHelper(mContext);
	}
	
	/**
	 * 获得所有歌曲
	 * @return
	 */
	public List<RealSong> queryAll(){
		SQLiteDatabase readableDB = mSQLiteOpenHelper.getReadableDatabase();
		
		Cursor cursor  = readableDB.query(ESSQLiteOpenHelper.DB_TABLE_DOWNLOAD, 
				null, null, null, null, null, null);
		List<RealSong> songs = null;
		if (cursor.moveToFirst()){
			songs = new ArrayList<RealSong>();
			while(!cursor.isAfterLast()){
				
				RealSong song = new RealSong();
				song.setSongUrl(cursor.getString(cursor.getColumnIndex(TableDownload.SONG_URL)));
				song.setSongName(cursor.getString(cursor.getColumnIndex(TableDownload.SONG_NAME)));
				song.setLocalPath(cursor.getString(cursor.getColumnIndex(TableDownload.DES_DIR)));
				song.setSource(1);
				songs.add(song);
				
				cursor.moveToNext();
			}
		}
		cursor.close();
		readableDB.close();
		return songs;
	}
	
	/**
	 * 添加一条已下载歌曲
	 * @param task
	 * @return
	 */
	public boolean insert(DownloadEntity task){
		if (task == null){
			return false;
		}
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(TableDownload.SONG_URL, task.getSongUrl());
		values.put(TableDownload.SONG_NAME, task.getSongName());
		values.put(TableDownload.DES_DIR, task.getDesDir());
		values.put(TableDownload.COMPLETED_TIME, task.getCompletedTime());
		
		long result = writableDB.insert(ESSQLiteOpenHelper.DB_TABLE_DOWNLOAD, null, values);
		
		writableDB.close();
		return result > 0;
	}
	
	/**
	 * 删除一首已下载歌曲
	 * @param url
	 * @return
	 */
	public boolean delete(String url){
		if(!TextUtils.isEmpty(url)){
			SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
			
			int result = writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_DOWNLOAD, 
					TableDownload.SONG_URL + " = '?'", new String[]{ url });
			writableDB.close();
			return result > 0;
		}
		return false;
	}
	
	/**
	 * 清空已下载歌曲
	 * @return
	 */
	public boolean clear(){
		SQLiteDatabase writableDB = mSQLiteOpenHelper.getWritableDatabase();
		writableDB.delete(ESSQLiteOpenHelper.DB_TABLE_DOWNLOAD, null, null);
		writableDB.close();
		return true;
	}
	
}
