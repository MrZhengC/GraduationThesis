package com.langchao.leo.esplayer.bean;

import java.io.File;
import java.io.Serializable;

import com.langchao.leo.esplayer.services.MusicParser;

/**
 * 歌曲实体
 * @author 碧空
 *
 */
public class RealSong implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long songId = -1;
	
	private String songName = "";
	
	private String songUrl = "";
	
	private String localPath = "";
	
	private String artist = "";
	
	private String album = "";
	
	private String bigSongPic = "";
	
	private String smallSongPic = "";
	
	private String lrcLink = "";
	
	private long size = 0;
	
	private long duration = 0;
	
	private long favoriteTime = 0;
	
	private long lastPlayTime = 0;
	
	private int source = 0;

	public RealSong(){
	}
	
	public RealSong(File file){
		setSongName(""+file.getName());
		setLocalPath(""+file.getAbsolutePath());
		setSize(file.length());
		MusicParser.getMp3MusicInfo(this);
	}
	
	public RealSong(String localPath) {
		this(new File(localPath));
	}
	
	public long getSongId() {
		return songId;
	}

	public void setSongId(long songId) {
		this.songId = songId;
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

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getBigSongPic() {
		return bigSongPic;
	}

	public void setBigSongPic(String bigSongPic) {
		this.bigSongPic = bigSongPic;
	}

	public String getSmallSongPic() {
		return smallSongPic;
	}

	public void setSmallSongPic(String smallSongPic) {
		this.smallSongPic = smallSongPic;
	}

	public String getLrcLink() {
		return lrcLink;
	}

	public void setLrcLink(String lrcLink) {
		this.lrcLink = lrcLink;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getFavoriteTime() {
		return favoriteTime;
	}

	public void setFavoriteTime(long favoriteTime) {
		this.favoriteTime = favoriteTime;
	}

	public long getLastPlayTime() {
		return lastPlayTime;
	}

	public void setLastPlayTime(long lastPlayTime) {
		this.lastPlayTime = lastPlayTime;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "RealSong [songId=" + songId + ", songName=" + songName
				+ ", songUrl=" + songUrl + ", localPath=" + localPath
				+ ", artist=" + artist + ", album=" + album + ", bigSongPic="
				+ bigSongPic + ", smallSongPic=" + smallSongPic + ", lrcLink="
				+ lrcLink + ", size=" + size + ", duration=" + duration
				+ ", favoriteTime=" + favoriteTime + ", lastPlayTime="
				+ lastPlayTime + ", source=" + source + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RealSong) {
			RealSong other = (RealSong) o;
			if (songId != -1 && other.getSongId() == songId) {
				return true;
			}
		}
		return super.equals(o);
	}
	
}
