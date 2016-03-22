package com.langchao.leo.esplayer.http;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;

import android.content.Context;

/**
 * Volley助手:
 * 1、管理全局的RequestQueue;
 * 2、使用OkHttp作为Volley的底层网络访问实现；
 * 3、其他的全局Volley配置
 * @author 碧空
 *
 */
public class VolleyHelper {

	private Context mAppContext;
	
	private static VolleyHelper mSingleInstance = null;
	
	// 全局的请求队列
	private RequestQueue mAppRequestQueue = null;
	// 图片加载
	private ImageLoader mVolleyImageLoader = null;
	// 图片缓存实现
	private LruBitmapCache mLruBitmapCache = null;
	
	
	private VolleyHelper(Context context){
		this.mAppContext = context.getApplicationContext();
		
		mAppRequestQueue = getAppRequestQueue();
		
		mVolleyImageLoader = getVolleyImageLoader();
		
	}

	public static VolleyHelper getInstance(Context context){
		if (mSingleInstance == null) {
			synchronized (VolleyHelper.class) {
				if (mSingleInstance == null) {
					mSingleInstance = new VolleyHelper(context);
				}
			}
		}
		return mSingleInstance;
	}
	
	/**
	 * 获取应用级请求队列
	 * @return
	 */
	public RequestQueue getAppRequestQueue() {
		if (mAppRequestQueue == null) {
			mAppRequestQueue = Volley.newRequestQueue(mAppContext, 
					new OkHttpStack(new OkHttpClient()));
		}
		return mAppRequestQueue;
	}
	
	/**
	 * 添加一个请求任务
	 * @param request
	 * @param tag
	 */
	public void addRequestTask(Request<?> request, Object tag){
		// 为请求添加Tag
		request.setTag(tag);
		// 添加到请求队列中
		mAppRequestQueue.add(request);
	}
	
	/**
	 * 取消由Tag标识的所有请求任务
	 * @param tag
	 */
	public void cancelRequestByTag(Object tag){
		mAppRequestQueue.cancelAll(tag);
	}
	
	/**
	 * 获取Volley库中的ImageLoader对象
	 * @return
	 */
	public ImageLoader getVolleyImageLoader() {
		if (mVolleyImageLoader == null) {
			mVolleyImageLoader = new ImageLoader(getAppRequestQueue(), getLruImageCache());
		}
		return mVolleyImageLoader;
	}

	/**
	 * 获取LruImageCache
	 * @return
	 */
	private ImageCache getLruImageCache() {
		 if(mLruBitmapCache == null){
			 mLruBitmapCache = new LruBitmapCache(mAppContext);
		 }
		 return mLruBitmapCache;
	}
	
}
