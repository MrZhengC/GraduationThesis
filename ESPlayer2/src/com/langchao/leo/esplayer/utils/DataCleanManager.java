package com.langchao.leo.esplayer.utils;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 数据清理管理器
 * @author 碧空
 *
 */
public class DataCleanManager {

	/**
	 * 获取应用缓存大小
	 * @param context
	 * @return
	 */
	@SuppressLint("SdCardPath")
	public static long getAppCacheSize(Context context){
		
		// 1、应用内部缓存（缓存目录，文件目录，SharedPreference目录）；
		// 2、应用外部缓存（缓存目录，文件目录，SharedPreference目录），如果存在的话；
		// 3、ImageLoader缓存目录；
		// 4、Volley缓存目录（一般情况下在应用内部缓存中）
		long result = 0;
		
		// 应用内缓存文件夹
		result = FileUtils.size(context.getCacheDir());
		// 应用内文件文件夹
		result += FileUtils.size(context.getFilesDir());
		// SharedPreference文件夹
		result += FileUtils.size(
				new File("/data/data/" 
						+ context.getPackageName() 
						+ "/shared_prefs"));
		
		// 外部应用缓存文件夹和文件文件夹
		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)) {
			result += FileUtils.size(context.getExternalCacheDir());
			result += FileUtils.size(context.getExternalFilesDir(null));
		}
		
		// ImageLoader缓存文件夹
		result += FileUtils.size(
				ImageLoader.getInstance()
					.getDiskCache().getDirectory());
		
		return result;
	} 
	
	/**
	 * 清除应用缓存
	 * @param context
	 */
	@SuppressLint("SdCardPath")
	public static void cleanAppCache(Context context){

		FileUtils.delete(context.getCacheDir());
		FileUtils.delete(context.getFilesDir());
		FileUtils.delete(new File("/data/data/" 
				+ context.getPackageName() 
				+ "/shared_prefs"));
		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)) {
			FileUtils.delete(context.getExternalCacheDir());
			FileUtils.delete(context.getExternalFilesDir(null));
		}
		
		FileUtils.delete(
				ImageLoader.getInstance()
					.getDiskCache().getDirectory());
		
	}
	
}
