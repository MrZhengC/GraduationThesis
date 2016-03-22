package com.langchao.leo.esplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库创建助手
 * @author 碧空
 *
 */
public class ESSQLiteOpenHelper extends SQLiteOpenHelper {

	//数据库的名称
	private static final String DB_NAME = "esplayer";
	
	// 数据库版本为1
	private static final int DB_VERSION = 1;
	
	/**
	 * 数据库表名,歌曲信息表
	 */
	public static final String DB_TABLE_SONG_INFO = "t_song_info";
	
	/**
	 * 歌曲信息表结构
	 * @author 碧空
	 */
	public interface TableSongInfo {
		/**
		 * 歌曲ID
		 */
		public static final String SONG_ID = "_songId";
		
		/**
		 * 歌曲名称
		 */
		public static final String SONG_NAME = "_songName";
		
		/**
		 * 歌曲URL
		 */
		public static final String SONG_URL = "_songUrl";
		
		/**
		 * 本地歌曲名称
		 */
		public static final String LOCAL_PATH = "_localPath";
		/**
		 * 歌手名称
		 */
		public static final String ARTIST_NAME = "_artistName";
		/**
		 * 专辑名称
		 */
		public static final String ALBUM_NAME = "_albumName";
		/**
		 * 大歌曲封面
		 */
		public static final String SONG_PIC_BIG = "_songPicBig";
		/**
		 * 小歌曲封面
		 */
		public static final String SONG_PIC_SMALL = "_songPicSmall";
		/**
		 * 歌词链接地址
		 */
		public static final String LRC_LINK = "_lrcLink";
		/**
		 * 总时长
		 */
		public static final String DURATION = "_duration";
		/**
		 * 大小
		 */
		public static final String SIZE = "_size";
		/**
		 * 收藏时间 
		 */
		public static final String FAVORITE_TIME = "_favoriteTime";
		
		/**
		 * 上次播放时间
		 */
		public static final String LASTPLAY_TIME = "_lastPlayTime";
		/**
		 * 来源，0-本地；1-网络
		 */
		public static final String SOURCE = "_source";
		
	}
	
	// 创建歌曲信息表SQL语句
	private static final String SQL_CREATE_TABLE_SONG_INFO = 
					"CREATE TABLE '" + DB_TABLE_SONG_INFO + "' ( '"
						+ TableSongInfo.SONG_ID + "' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '"
						+ TableSongInfo.SONG_NAME + "' TEXT NOT NULL, '"
						+ TableSongInfo.SONG_URL + "' TEXT, '"
						+ TableSongInfo.LOCAL_PATH + "' TEXT, '"
						+ TableSongInfo.ARTIST_NAME + "' TEXT, '"
						+ TableSongInfo.ALBUM_NAME + "' TEXT, '"
						+ TableSongInfo.SONG_PIC_BIG + "' TEXT, '"
						+ TableSongInfo.SONG_PIC_SMALL + "' TEXT, '"
						+ TableSongInfo.LRC_LINK + "' TEXT, '"
						+ TableSongInfo.DURATION + "' INTEGER, '"
						+ TableSongInfo.SIZE + "' INTEGER, '"
						+ TableSongInfo.FAVORITE_TIME + "' INTEGER  NOT NULL, '"
						+ TableSongInfo.LASTPLAY_TIME + "' INTEGER  NOT NULL, '"
						+ TableSongInfo.SOURCE + "' INTEGER  NOT NULL);";
	
	/**
	 * 播放列表表名
	 */
	public static final String DB_TABLE_PLAYLIST = "t_playlist";
	
	public interface BaseTable{
		public static final String ID = "_id";
	}
	
	/**
	 * 播放列表表结构
	 * @author 碧空
	 *
	 */
	public interface TablePlaylist{
		public static final String PLAYLIST_ID = "_playlistId";
		public static final String PLAYLIST_NAME = "_playlistName";
		public static final String COVER_URL = "_coverUrl";
	}
	
	// 创建播放列表表SQL语句
	private final static String SQL_CREATE_TABLE_PLAYLIST = 
						"CREATE TABLE '" + DB_TABLE_PLAYLIST + "' ('"
							+ TablePlaylist.PLAYLIST_ID + "' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '"
							+ TablePlaylist.PLAYLIST_NAME + "' TEXT NOT NULL, '"
							+ TablePlaylist.COVER_URL + "' TEXT );";
	
	/**
	 * 播放列表歌曲表表名
	 */
	public final static String DB_TABLE_PLAYLIST_SONG = "t_playlist_song";
	
	/**
	 * 播放列表歌曲表表结构
	 * @author 碧空
	 *
	 */
	public interface TablePlaylistSong extends BaseTable{
		public static final String PLAYLIST_ID = TablePlaylist.PLAYLIST_ID;
		public static final String SONG_ID = TableSongInfo.SONG_ID;
	}
	
	// 创建播放列表歌曲表SQL语句
	private static final String SQL_CREATE_TABLE_PLAYLIST_SONG = 
							"CREATE TABLE '" + DB_TABLE_PLAYLIST_SONG + "' ('"
								+ TablePlaylistSong.ID + "' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '"
								+ TablePlaylistSong.PLAYLIST_ID + "' INTEGER NOT NULL, '"
								+ TablePlaylistSong.SONG_ID + "' INTEGER NOT NULL);";
	
	/**
	 * 播放列表歌曲视图名称
	 */
	public static final String DB_VIEW_PLAYLIST_SONG = "v_playlist_song";
	
	// 播放列表视图，查询各个播放列表内容
	private final static String SQL_CREATE_VIEW_PLAYLIST_SONG = 
							"CREATE VIEW '" + DB_VIEW_PLAYLIST_SONG + "' AS " +
							" SELECT " +
							DB_TABLE_PLAYLIST + "." + TablePlaylist.PLAYLIST_ID + ", " +
							DB_TABLE_PLAYLIST + "." + TablePlaylist.PLAYLIST_NAME + ", " +
							DB_TABLE_PLAYLIST + "." + TablePlaylist.COVER_URL + ", " +
							DB_TABLE_PLAYLIST_SONG + "." + TablePlaylistSong.SONG_ID + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.SONG_NAME + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.SONG_URL + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.LOCAL_PATH + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.ARTIST_NAME + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.ALBUM_NAME + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.SONG_PIC_BIG + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.SONG_PIC_SMALL + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.LRC_LINK + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.SIZE + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.DURATION + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.FAVORITE_TIME + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.LASTPLAY_TIME + ", " +
							DB_TABLE_SONG_INFO + "." + TableSongInfo.SOURCE +
							" FROM " + 
							DB_TABLE_PLAYLIST + ", " + 
							DB_TABLE_PLAYLIST_SONG + ", " + 
							DB_TABLE_SONG_INFO + 
							" WHERE ("+
							DB_TABLE_PLAYLIST +"."+ TablePlaylist.PLAYLIST_ID + " = " + 
							DB_TABLE_PLAYLIST_SONG +"."+ TablePlaylistSong.PLAYLIST_ID + 
							") AND (" + 
							DB_TABLE_PLAYLIST_SONG +"."+ TablePlaylistSong.SONG_ID + " = " + 
							DB_TABLE_SONG_INFO +"."+ TableSongInfo.SONG_ID +");";
	
	/**
	 * 下载表表名
	 */
	public static final String DB_TABLE_DOWNLOAD = "t_download";
	
	/**
	 * 下载表结构
	 * @author 碧空
	 *
	 */
	public interface TableDownload extends BaseTable{
		public static final String SONG_NAME = "_songName";
		public static final String SONG_URL = "_songUrl";
		public static final String DES_DIR = "_desDir";
		public static final String STATE = "_state";
		public static final String COMPLETED_TIME = "_completedTime";
	}
	
	// 创建下载表SQL语句
	private static final String SQL_CREATE_TABLE_DOWNLOAD = 
							"CREATE TABLE '"+ DB_TABLE_DOWNLOAD +"' ('"
								+ TableDownload.ID + "' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '"
								+ TableDownload.SONG_NAME + "' TEXT NOT NULL, '"
								+ TableDownload.SONG_URL + "' TEXT NOT NULL, '"
								+ TableDownload.DES_DIR + "', TEXT NOT NULL, '"
								+ TableDownload.STATE + "' INTEGER, '"
								+ TableDownload.COMPLETED_TIME + "' INTEGER);";
	
	
	public ESSQLiteOpenHelper(Context context){
		this(context, DB_NAME, null, DB_VERSION);
	}
	
	public ESSQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	/**
	 * 创建数据库时调用
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 1、歌曲表
		db.execSQL(SQL_CREATE_TABLE_SONG_INFO);
		
		// 2、播放列表表
		db.execSQL(SQL_CREATE_TABLE_PLAYLIST);
		
		// 3、播放列表歌曲表
		db.execSQL(SQL_CREATE_TABLE_PLAYLIST_SONG);
		
		// 4、播放列表歌曲视图
		db.execSQL(SQL_CREATE_VIEW_PLAYLIST_SONG);
		
		// 5、下载表
		db.execSQL(SQL_CREATE_TABLE_DOWNLOAD);
	}

	/**
	 * 更新数据库时使用
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
