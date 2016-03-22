package com.langchao.leo.esplayer.bean;

import java.io.Serializable;

/**
 * 下载任务实体
 * @author 碧空
 *
 */
public class DownloadEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 等待下载
     */
    public static final int STAT_WAIT = 0;
    /**
     * 正在下载
     */
    public static final int STAT_DOWNLOADING = 1;
    /**
     * 取消下载
     */
    public static final int STAT_CANCEL = 2;
    /**
     * 下载完成
     */
    public static final int STAT_COMPLETED = 3;
    /**
     * 下载失败
     */
    public static final int STAT_FAILURE = 4;
	
	/**
	 * 下载任务ID
	 */
	private long id = 0;
	/**
	 * 歌曲名称
	 */
	private String songName = "";
	/**
	 * 歌曲URL
	 */
	private String songUrl = "";
	/**
	 * 目的文件夹
	 */
	private String desDir = "";
	/**
	 * 进度
	 */
	private long progress = 0;
	/**
	 * 文件总大小
	 */
	private long total = 0;
	/**
	 * 状态 ：0 等待; 1正在下载; 2取消; 3完成; 4失败
	 */
	private int state = 0;
	/**
	 * 开始下载的时间
	 */
	private long completedTime = 0;
	/**
	 * 下载回调
	 */
	private ESDownloadCallback downloadCallback = null;
	
	public interface ESDownloadCallback{
		public void onStart(); 
		public void onDownloading(long totalSize, long currentSize, long speed); 
		public void onCompleted(); 
		public void onFailure(String strMsg);
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSongName() {
		return songName;
	}
	public void setSongName(String songName) {
		this.songName = songName;
	}
	public String getSongUrl() {
		return songUrl;
	}
	public void setSongUrl(String songUrl) {
		this.songUrl = songUrl;
	}
	public String getDesDir() {
		return desDir;
	}
	public void setDesDir(String desDir) {
		this.desDir = desDir;
	}
	public long getProgress() {
		return progress;
	}
	public void setProgress(long progress) {
		this.progress = progress;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public long getCompletedTime() {
		return completedTime;
	}
	public void setCompletedTime(long joinTime) {
		this.completedTime = joinTime;
	}
	public ESDownloadCallback getDownloadCallback() {
		return downloadCallback;
	}
	public void setDownloadCallback(ESDownloadCallback downloadCallback) {
		this.downloadCallback = downloadCallback;
	}
	@Override
	public String toString() {
		return "DownloadTaskInfo [id=" + id + ", songName=" + songName
				+ ", songUrl=" + songUrl + ", desDir=" + desDir + ", progress="
				+ progress + ", total=" + total + ", state=" + state
				+ ", joinTime=" + completedTime + "]";
	}

	
	
}
