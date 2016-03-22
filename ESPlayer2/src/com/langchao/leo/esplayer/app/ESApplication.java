package com.langchao.leo.esplayer.app;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.langchao.leo.esplayer.core.PlayerService;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.analytics.MobclickAgent;

/**
 * 自定义Application
 * 1、注册管理所有Activity;
 * 2、注册管理所有Service;
 * 3、其他的一些配置;
 * 
 * @author 碧空
 *
 */
public class ESApplication extends Application {
	
	// Activity栈
	private static Stack<Activity> activityStack;
	
	private static ESApplication singleton;

	// Returns the application instance
	public static ESApplication getInstance() {
		return singleton;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//将当前实例赋值给类变量
		singleton = this;
		
		// 初始化ImageLoader
		initImageloader();
		
		// 当要统计详细的Fragment页面的时候，需要关闭Activity的默认统计方式；
		// 设置友盟统计,关闭Activity的默认统计方式；
		MobclickAgent.openActivityDurationTrack(false);
		
		// 设置全局未捕获异常处理器
		GlobalCrashHandler handler = GlobalCrashHandler.getInstance();
		handler.init(this);
		
	}
	
	/**
	 * 配置ImageLoader
	 */
	private void initImageloader(){
		
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
		// 设置线程池大小
		config.threadPoolSize(3);
		// 设置线程优先级
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		// 设置对于同一个URL在内存中仅保存一份数据
		config.denyCacheImageMultipleSizesInMemory();
		try {
			config.diskCache(new LruDiskCache(
					new File(""+AppConfig.getInstance(this).getAppImageCacheFolder()),// 设置磁盘图片缓存地址 
					new Md5FileNameGenerator(), // 名称生成器
					50 * 1024 * 1024));//磁盘缓存大小
		} catch (IOException e) {
			e.printStackTrace();
		}
		//config.writeDebugLogs();
		
		// 将config设置给ImageLoader
		ImageLoader.getInstance().init(config.build());
		
	}

	/**
	 * add Activity 添加Activity到栈
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * get current Activity 获取当前Activity（栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit() {
		try {
			// 销毁所有activity
			finishAllActivity();
			
			// 停止所有服务
			stopAllService();
		} catch (Exception e) {
			// do noting.
		}
	}
	
	/**
	 * 关闭所有服务,现暂只关闭音乐播放服务
	 */
	private void stopAllService() {
		Intent service = new Intent(this, PlayerService.class);
		stopService(service);
	}
	
}
