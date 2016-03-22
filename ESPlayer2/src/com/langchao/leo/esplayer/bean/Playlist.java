package com.langchao.leo.esplayer.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.List;

/**
 * 播放列表数据结构
 * @author 碧空
 *
 */
public class Playlist implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int playlistId = 0;
	
	/**
	 * 播放列表名称
	 */
	private String playlistName = null;
	
	/**
	 * 播放列表内歌曲的数量
	 */
	private int count = 0;
	
	/**
	 * 封面的URL
	 */
	private String coverUrl = null;

	/**
	 * 歌曲实体列表
	 */
	private List<RealSong> mSongs = null;
	
	public int getPlaylistId() {
		return playlistId;
	}

	public void setPlaylistId(int playlistId) {
		this.playlistId = playlistId;
	}
	
	public List<RealSong> getSongs() {
		return mSongs;
	}

	public void setSongs(List<RealSong> mSongs) {
		this.mSongs = mSongs;
	}

	public String getPlaylistName() {
		return playlistName;
	}

	public void setPlaylistName(String playlistName) {
		this.playlistName = playlistName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	@Override
	public String toString() {
		return "Playlist [playlistId=" + playlistId + ", playlistName="
				+ playlistName + ", count=" + count + ", coverUrl=" + coverUrl
				+ ", mSongs=" + mSongs + "]";
	}

	/**
	 * 序列化到文件
	 * @param playlist
	 * @param desFile
	 */
	public static void writeToFile(Playlist playlist, File desFile){
		try {
			ObjectOutputStream objStream = new ObjectOutputStream(new FileOutputStream(desFile));
			objStream.writeObject(playlist);
			objStream.flush();
			objStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从序列化文件中读取对象
	 * @param srcFile
	 */
	public static Playlist readFromFile(File srcFile){
		Playlist playlist = null;
		
		try {
			ObjectInputStream objStream = new ObjectInputStream(new FileInputStream(srcFile));
			playlist = (Playlist) objStream.readObject();
			objStream.close();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return playlist;
	}
	
}
