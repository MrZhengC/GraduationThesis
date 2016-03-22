package com.langchao.leo.esplayer.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.db.MusicTableHelper;

/**
 * 音乐扫描服务<br>
 * 从磁盘中扫描出支持的音乐格式的音乐，并把他们添加到数据库的歌曲信息表中
 * @author 碧空
 *
 */
public class MusicScannerService extends Thread {

	/**
	 * 扫描进度
	 */
	public final static int FIELD_SCAN_MUSIC_PROGRESS = 0x123;
	
	/**
	 * 扫描完成
	 */
	public final static int FIELD_SCAN_MUSIC_SECCESS = 0x124;
	
	private Handler mUIHandler = null;
	private String mRootPath = "";
	
	private MusicTableHelper mMusicTableHelper = null;
	
	private List<RealSong> mSongs = new ArrayList<RealSong>();
	
	public MusicScannerService(Context context, Handler uiHandler, String rootPath) {
		this.mUIHandler = uiHandler; 
		this.mRootPath = rootPath;
		this.mMusicTableHelper = new MusicTableHelper(context);
	}
	
	@Override
	public void run() {
		
		scan(new File(mRootPath));
		
		if (mSongs.size() > 0){
			// 写入数据库
			mMusicTableHelper.updateOrInsertIfNotExist(mSongs);
		}
		
		// 扫描完成
		if (mUIHandler != null){
			Message msg = mUIHandler.obtainMessage(FIELD_SCAN_MUSIC_SECCESS);
			msg.arg1 = mSongs.size();
			msg.sendToTarget();
		}
		
		super.run();
	}
	
	private void scan(File folder){
		// 文件等于null或者不存在的时候直接返回
		if (folder == null || !folder.exists()) {
			return;
		}
		
		// 判断是否文件文件夹
		if (folder.isDirectory()) {
			// 如果为文件夹，则列出所有子文件
			File[] children = folder.listFiles();
			
			// 遍历文件夹
			if (children != null){
				for (File child : children){
					// 如果文件为 Android 或者 android 则跳过不扫描
					if (child.getName().equals("Android") 
							|| child.getName().equals("android")) {
						continue;
					}
					
					// 判断 是否存在 nomedia文件
					if (child.getName().equals(".nomedia")) {
						break;
					}
					scan(child);
				}
			}
		}
		else {
			// 如果为文件，判断是否为支持的音乐格式
			// 如果是支持的文件格式，则通知UI更新，并添加到缓存中
			if (isSupportedMusic(folder.getAbsolutePath())) {
				RealSong song = new RealSong(folder);
				mSongs.add(song);
				// 通知UI界面更新
				sendToUI(folder.getAbsolutePath());
			}
		}
	}
	
	private void sendToUI(String path){
		if (mUIHandler != null){
			Message msg = mUIHandler.obtainMessage(FIELD_SCAN_MUSIC_PROGRESS);
			msg.obj = path;
			msg.sendToTarget();
		}
	}
	
	/**
	 * 给定路径的文件是否是应用支持的音乐格式
	 * @param path
	 * @return
	 */
	public static boolean isSupportedMusic(String path){
		path = path.toLowerCase(Locale.getDefault());
		if (path.endsWith(".mp3")){
			return true;
		}
		return false;
	}
	
}
