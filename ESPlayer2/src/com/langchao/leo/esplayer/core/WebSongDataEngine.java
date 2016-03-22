package com.langchao.leo.esplayer.core;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.langchao.leo.esplayer.bean.FMChannelEntity;
import com.langchao.leo.esplayer.bean.FMSongEntity;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.contstants.NetConstants;
import com.langchao.leo.esplayer.http.CustomGsonRequest;
import com.langchao.leo.esplayer.http.LrcRequest;
import com.langchao.leo.esplayer.http.VolleyHelper;
import com.langchao.leo.esplayer.interfaces.IAsyncLoadListener;
import com.langchao.leo.esplayer.ui.widget.lrc.LrcRow;
import com.langchao.leo.esplayer.utils.CommonUtils;
import com.langchao.leo.esplayer.utils.ESLog;

/**
 * 网络歌曲获取引擎：<br>
 * 1、获取频道歌曲列表；<br>
 * 2、获取歌曲信息；<br>
 * 3、获取歌曲封面；<br>
 * 4、获取歌曲歌词；<br>
 * @author 碧空
 *
 */
public class WebSongDataEngine {
	
	private Context mAppContext = null;
	
	private VolleyHelper mVolleyHelper = null;

	private static WebSongDataEngine mEngineInstance = null;
	
	private WebSongDataEngine(Context context){
		mAppContext = context.getApplicationContext();
		mVolleyHelper = VolleyHelper.getInstance(mAppContext);
	}
	
	public static WebSongDataEngine getInstance(Context context){
		if (mEngineInstance == null) {
			synchronized (WebSongDataEngine.class) {
				if (mEngineInstance == null) {
					mEngineInstance = new WebSongDataEngine(context);
				}
			}
		}
		return mEngineInstance;
	}
	
	/**
	 * 获取网络频道数据
	 * @param channelId
	 * @param listener
	 */
	public void getWebChannelById(String channelId, final IAsyncLoadListener<FMChannelEntity> listener){
		if (TextUtils.isEmpty(channelId)){
			if (listener != null){
				listener.onFailure("ChannelId may not be null.");
			}
			return;
		}
		
		// 获取频道数据的URL
		final String url = NetConstants.HTTP_URL_GET_SONG_LIST_BY_CHANNEL + channelId;
		
		mVolleyHelper.addRequestTask(new CustomGsonRequest<FMChannelEntity>(
				url, 
				FMChannelEntity.class, 
				null,
				new Listener<FMChannelEntity>() {
					@Override
					public void onResponse(FMChannelEntity result) {
						// 获取成功，组装Playlist
						if (result != null) {
							if (listener != null){
								listener.onSuccess(result);
							}
						}else{
							if (listener != null){
								listener.onFailure("result == null.");
							}
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (listener != null) {
							listener.onFailure(error.getLocalizedMessage());
						}
					}
				}), url);
	}
	
	/**
	 * 获取歌曲歌词
	 * @param song
	 * @param listener
	 */
	public void getWebSongLrc(RealSong song, final IAsyncLoadListener<List<LrcRow>> listener){
		if (song == null) {
			return;
		}
		
		mVolleyHelper.addRequestTask(new LrcRequest(song.getLrcLink(), 
				new Listener<List<LrcRow>>() {
					@Override
					public void onResponse(List<LrcRow> result) {
						if (result != null) {
							if (listener != null) {
								listener.onSuccess(result);
							}
						} else {
							if (listener != null) {
								listener.onFailure("result == null.");
							}
						}
						
					}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (listener != null) {
					listener.onFailure(""+error.getLocalizedMessage());
				}
			}
		}), song.getLrcLink());
	}
	
	/**
	 * 根据歌曲ID获取歌曲详细信息
	 * @param songId
	 * @param listener
	 */
	public void getWebSong(String songId, final IAsyncLoadListener<RealSong> listener) {
		if (TextUtils.isEmpty(songId)) {
			if (listener != null) {
				listener.onFailure("songId为空(null或者\"\")");
			}
			return;
		}
		
		ESLog.i("添加任务，获取歌曲：" + songId);
		
		String url = NetConstants.BASE_DES_URL + songId;
		mVolleyHelper.addRequestTask(new CustomGsonRequest<FMSongEntity>(
				url, 
				FMSongEntity.class, 
				null, 
				new Response.Listener<FMSongEntity>() {
					@Override
					public void onResponse(FMSongEntity result) {
						
						if (result == null) {
							if (listener != null) {
								listener.onFailure("result == null.");
								return;
							}
						}
						
						RealSong song = CommonUtils.createRealSong(result);
						if (listener != null) {
							listener.onSuccess(song);
						}
					}
				}, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						if (listener != null) {
							listener.onFailure(arg0.getLocalizedMessage());
						}
					}
				}), url);
		
//		mVolleyHelper.addRequestTask( new CustomGsonRequest.RequestBuilder<FMSongEntity>()
//		.url(NetConstants.BASE_DES_URL + songId)
//		.clazz(FMSongEntity.class)
//		.successListener(new Listener<FMSongEntity>(){
//			@Override
//			public void onResponse(FMSongEntity arg0) {
//				ESLog.i("获取单首音乐 result : " + arg0);
//				if (arg0 == null) {
//					if (listener != null) {
//						listener.onFailure("返回值为null");
//					}
//					return;
//				}
//				// 新建工具方法 createRealSong
//				RealSong song = CommonUtils.createRealSong(arg0);
//				if (listener != null) {
//					listener.onSuccess(song);
//				}
//			}
//		})
//		.errorListener(new ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				ESLog.e("volley error:" + arg0.getLocalizedMessage());
//				if (listener != null) {
//					listener.onFailure(arg0.getLocalizedMessage());
//				}
//			}
//		})
//		.build(), "" + songId);
	}
	
}
