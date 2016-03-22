package com.langchao.leo.esplayer.contstants;

public class NetConstants {

	// 根据频道ID获取频道内歌曲列表的URL
	public static final String HTTP_URL_GET_SONG_LIST_BY_CHANNEL = "http://fm.baidu.com/dev/api/?tn=playlist&format=json&id=";
	// DETAILS URL 获取某一首音乐的详细信息
	public static final String BASE_DES_URL = "http://music.baidu.com/data/music/fmlink?type=mp3&rate=320&songIds=";
	// LRC URL 歌词的baseUrl
	public static final String BASE_LRC_URL = "http://fm.baidu.com";

	public static final int DEFAULT_CHANNEL_ID = 10101;
	public static final int DEFAULT_SONG_ID = 31233590;
	
	public static final String DEFAULT_LRC_URL = BASE_LRC_URL + "/data2/lrc/14872996/14872996.lrc";
	public static final String DEFAULT_SONG_URL = "http://file.qianqian.com/data2/music/123193572/123193572.mp3?xcode=58b500c0b9d0b54de977ca24e3730051&src=\"http%3A%2F%2Fpan.baidu.com%2Fshare%2Flink%3Fshareid%3D3209134637%26uk%3D3998432082\"";
	
}
