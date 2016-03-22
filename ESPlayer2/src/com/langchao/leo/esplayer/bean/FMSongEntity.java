package com.langchao.leo.esplayer.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 百度FM歌曲实体
 * @author 碧空
 * {"errorCode":22000,
 * "data":
 * {
 * "xcode":"cbec4ce875af141b2bad0af50eb43478",
 * "songList":[
 * {"queryId":"237474",
 * "songId":237474,
 * "songName":"\u6211\u4eec\u7684\u604b\u7231\u662f\u5bf9\u751f\u547d\u7684\u4e25\u91cd\u6d6a\u8d39",
 * "artistId":"1557",
 * "artistName":"\u8bb8\u5d69",
 * "albumId":68929,
 * "albumName":"\u5bfb\u96fe\u542f\u793a",
 * "songPicSmall":"http:\/\/musicdata.baidu.com\/data2\/pic\/88389971\/88389971.jpg",
 * "songPicBig":"http:\/\/musicdata.baidu.com\/data2\/pic\/88389963\/88389963.jpg",
 * "songPicRadio":"http:\/\/musicdata.baidu.com\/data2\/pic\/88389953\/88389953.jpg",
 * "lrcLink":"\/data2\/lrc\/13908402\/13908402.lrc",
 * "version":"",
 * "copyType":1,
 * "time":212,
 * "linkCode":22000,
 * "songLink":"http:\/\/yinyueshiting.baidu.com\/data2\/music\/137480912\/23747464800320.mp3?xcode=cbec4ce875af141b5990d376263109e0",
 * "showLink":"http:\/\/yinyueshiting.baidu.com\/data2\/music\/137480912\/23747464800320.mp3?xcode=cbec4ce875af141b5990d376263109e0",
 * "format":"mp3",
 * "rate":320,
 * "size":8510979,
 * "relateStatus":"0",
 * "resourceType":"0",
 * "source":"web"}]}}
 *
 */
public class FMSongEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int errorCode;
	private SongInfo data;
	
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public SongInfo getData() {
		return data;
	}

	public void setData(SongInfo data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "FMSongEntity [errorCode=" + errorCode + ", data=" + data + "]";
	}

	public class SongInfo {
		private String xcode;
		private List<RealSongEntity> songList;
		public String getXcode() {
			return xcode;
		}
		public void setXcode(String xcode) {
			this.xcode = xcode;
		}
		public List<RealSongEntity> getSongList() {
			return songList;
		}
		public void setSongList(List<RealSongEntity> songList) {
			this.songList = songList;
		}
		@Override
		public String toString() {
			return "SongInfo [xcode=" + xcode + ", songList=" + songList + "]";
		}
		
	}
	
	public class RealSongEntity{
		private String queryId;
		private String songId;
		private String songName;
		private String artistId;
		private String artistName;
		private int albumId;
		private String albumName;
		private String songPicSmall;
		private String songPicBig;
		private String songPicRadio;
		private String lrcLink;
		private String version;
		private int copyType;
		private int time;
		private long linkCode;
		private String songLink;
		private String showLink;
		private String format;
		private int rate;
		private long size;
		private String relateStatus;
		private String resourceType;
		private String source;
		
		
		
		public String getQueryId() {
			return queryId;
		}



		public void setQueryId(String queryId) {
			this.queryId = queryId;
		}



		public String getSongId() {
			return songId;
		}



		public void setSongId(String songId) {
			this.songId = songId;
		}



		public String getSongName() {
			return songName;
		}



		public void setSongName(String songName) {
			this.songName = songName;
		}



		public String getArtistId() {
			return artistId;
		}



		public void setArtistId(String artistId) {
			this.artistId = artistId;
		}



		public String getArtistName() {
			return artistName;
		}



		public void setArtistName(String artistName) {
			this.artistName = artistName;
		}



		public int getAlbumId() {
			return albumId;
		}



		public void setAlbumId(int albumId) {
			this.albumId = albumId;
		}



		public String getAlbumName() {
			return albumName;
		}



		public void setAlbumName(String albumName) {
			this.albumName = albumName;
		}



		public String getSongPicSmall() {
			return songPicSmall;
		}



		public void setSongPicSmall(String songPicSmall) {
			this.songPicSmall = songPicSmall;
		}



		public String getSongPicBig() {
			return songPicBig;
		}



		public void setSongPicBig(String songPicBig) {
			this.songPicBig = songPicBig;
		}



		public String getSongPicRadio() {
			return songPicRadio;
		}



		public void setSongPicRadio(String songPicRadio) {
			this.songPicRadio = songPicRadio;
		}



		public String getLrcLink() {
			return lrcLink;
		}



		public void setLrcLink(String lrcLink) {
			this.lrcLink = lrcLink;
		}



		public String getVersion() {
			return version;
		}



		public void setVersion(String version) {
			this.version = version;
		}



		public int getCopyType() {
			return copyType;
		}



		public void setCopyType(int copyType) {
			this.copyType = copyType;
		}



		public int getTime() {
			return time;
		}



		public void setTime(int time) {
			this.time = time;
		}



		public long getLinkCode() {
			return linkCode;
		}



		public void setLinkCode(long linkCode) {
			this.linkCode = linkCode;
		}



		public String getSongLink() {
			return songLink;
		}



		public void setSongLink(String songLink) {
			this.songLink = songLink;
		}



		public String getShowLink() {
			return showLink;
		}



		public void setShowLink(String showLink) {
			this.showLink = showLink;
		}



		public String getFormat() {
			return format;
		}



		public void setFormat(String format) {
			this.format = format;
		}



		public int getRate() {
			return rate;
		}



		public void setRate(int rate) {
			this.rate = rate;
		}



		public long getSize() {
			return size;
		}



		public void setSize(long size) {
			this.size = size;
		}



		public String getRelateStatus() {
			return relateStatus;
		}



		public void setRelateStatus(String relateStatus) {
			this.relateStatus = relateStatus;
		}



		public String getResourceType() {
			return resourceType;
		}



		public void setResourceType(String resourceType) {
			this.resourceType = resourceType;
		}



		public String getSource() {
			return source;
		}



		public void setSource(String source) {
			this.source = source;
		}



		@Override
		public String toString() {
			return "RealSongEntity [queryId=" + queryId + ", songId=" + songId
					+ ", songName=" + songName + ", artistId=" + artistId
					+ ", artistName=" + artistName + ", albumId=" + albumId
					+ ", albumName=" + albumName + ", songPicSmall="
					+ songPicSmall + ", songPicBig=" + songPicBig
					+ ", songPicRadio=" + songPicRadio + ", lrcLink=" + lrcLink
					+ ", version=" + version + ", copyType=" + copyType
					+ ", time=" + time + ", linkCode=" + linkCode
					+ ", songLink=" + songLink + ", showLink=" + showLink
					+ ", format=" + format + ", rate=" + rate + ", size="
					+ size + ", relateStatus=" + relateStatus
					+ ", resourceType=" + resourceType + ", source=" + source
					+ "]";
		}
		
	}
}
