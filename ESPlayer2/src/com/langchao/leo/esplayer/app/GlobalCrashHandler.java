package com.langchao.leo.esplayer.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.langchao.leo.esplayer.contstants.Constants;

/**
 * 在Application中统一捕获异常，保存到文件中下次再打开时上传
 */
public class GlobalCrashHandler implements UncaughtExceptionHandler {
	/**
	 * 是否开启日志输出,在Debug状态下开启, 在Release状态下关闭以提示程序性能
	 * */
	public static final boolean DEBUG = true;
	private static final String LOG_TAG = "XDCrashHandler";
	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/** CrashHandler实例 */
	private static GlobalCrashHandler INSTANCE;

	/** 程序的Context对象 */
	private Application mContext;
	
	 /**用来存储设备信息和异常信息*/  
    private Map<String, String> infos = new HashMap<String, String>();  
  
    /**用于格式化日期,作为日志文件名的一部分*/  
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()); 
	
	/** 保证只有一个CrashHandler实例 */
	private GlobalCrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static GlobalCrashHandler getInstance() {
		if (INSTANCE == null) {
			synchronized (GlobalCrashHandler.class) {
				if (INSTANCE == null) {
					INSTANCE = new GlobalCrashHandler();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init(Application ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

        if(!handleException(ex) && mDefaultHandler != null){   
            //如果用户没有处理则让系统默认的异常处理器来处理    
            mDefaultHandler.uncaughtException(thread, ex);                
        }else{
            //Intent intent = new Intent(mContext.getApplicationContext(), SplashActivity.class);  
            //PendingIntent restartIntent = PendingIntent.getActivity(
            //	mContext.getApplicationContext(), 0, intent,    
            //       Intent.FLAG_ACTIVITY_NEW_TASK);                                                 
            //AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);    
            //mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用   

            //杀死该应用进程  
            android.os.Process.killProcess(android.os.Process.myPid());
            //退出程序                                          
            System.exit(1);
        }    
		
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @return true代表处理该异常，不再向上抛异常，
	 *         false代表不处理该异常(可以将该log信息存储起来)然后交给上层(这里就到了系统的异常处理)去处理，
	 *         简单来说就是true不会弹出那个错误提示框，false就会弹出
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
//		Log.e("crash Handler", "error，请重新启动.");
//		Log.e("crasher", ex.toString());
//		//使用Toast来显示异常信息
//		new Thread() {
//			@Override
//			public void run() {
//				Looper.prepare();
//				Toast.makeText(mContext.getApplicationContext(), 
//						"程序异常即将重新启动", 
//						Toast.LENGTH_LONG).show();
//				Looper.loop();
//			}
//		}.start();
		
		 //收集设备参数信息   
        collectDeviceInfo(mContext);  
        //保存日志文件   
        saveCrashInfo2File(ex);  
		
		return true;
	}

	// TODO 使用HTTP Post 发送错误报告到服务器 这里不再赘述
	// private void postReport(File file) {
	// 在上传的时候还可以将该app的version，该手机的机型等信息一并发送的服务器，
	// Android的兼容性众所周知，所以可能错误不是每个手机都会报错，还是有针对性的去debug比较好
	// }
	
	/** 
     * 收集设备参数信息 
     * @param ctx 
     */  
    public void collectDeviceInfo(Context ctx) {  
        try {  
            PackageManager pm = ctx.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);  
            if (pi != null) {  
                String versionName = pi.versionName == null ? "null" : pi.versionName;  
                String versionCode = pi.versionCode + "";  
                infos.put("versionName", versionName);  
                infos.put("versionCode", versionCode);  
            }  
        } catch (NameNotFoundException e) {  
            Log.e(LOG_TAG, "an error occured when collect package info", e);  
        }  
        Field[] fields = Build.class.getDeclaredFields();  
        for (Field field : fields) {  
            try {  
                field.setAccessible(true);  
                infos.put(field.getName(), field.get(null).toString());  
                Log.d(LOG_TAG, field.getName() + " : " + field.get(null));  
            } catch (Exception e) {  
                Log.e(LOG_TAG, "an error occured when collect crash info", e);  
            }  
        }  
    }  
  
    /** 
     * 保存错误信息到文件中 
     *  
     * @param ex 
     * @return  返回文件名称,便于将文件传送到服务器 
     */  
    @SuppressLint("SdCardPath")
	private String saveCrashInfo2File(Throwable ex) {  
          
        StringBuffer sb = new StringBuffer();  
        for (Map.Entry<String, String> entry : infos.entrySet()) {  
            String key = entry.getKey();  
            String value = entry.getValue();  
            sb.append(key + "=" + value + "\n");  
        }  
          
        Writer writer = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(writer);  
        ex.printStackTrace(printWriter);  
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
        printWriter.close();  
        String result = writer.toString();  
        sb.append(result);  
        try {  
            long timestamp = System.currentTimeMillis();  
            String time = formatter.format(new Date());  
            String fileName = "crash-" + time + "-" + timestamp + ".log";  
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
                String path = Environment.getExternalStorageDirectory().getAbsoluteFile() 
						+ Constants.APP_ROOT_CACHE_FOLDER;  
                File dir = new File(path);  
                if (!dir.exists()) {  
                    dir.mkdirs();  
                }  
                FileOutputStream fos = new FileOutputStream(path + fileName);  
                fos.write(sb.toString().getBytes());  
                fos.close();  
            }  
            return fileName;  
        } catch (Exception e) {  
            Log.e(LOG_TAG, "an error occured while writing file...", e);  
        }  
        return null;  
    } 
	
}