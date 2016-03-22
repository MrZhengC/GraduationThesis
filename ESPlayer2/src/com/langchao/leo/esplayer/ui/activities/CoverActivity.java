package com.langchao.leo.esplayer.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.langchao.leo.esplayer.HomeActivity;
import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.app.ESApplication;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.services.MusicScannerService;
import com.langchao.leo.esplayer.utils.ESLog;
import com.langchao.leo.esplayer.utils.SharedPrefUtils;

public class CoverActivity extends FragmentActivity {

	private ImageView mCoverIv = null;
	
	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_cover);
		ESApplication.getInstance().addActivity(this);
		
		// 添加到Activity栈中
		ESApplication.getInstance().addActivity(this);
		
		// 第一次进入应用的时候进行扫描
		Boolean isFirstStart = (Boolean) SharedPrefUtils.get(getApplicationContext(), 
									Constants.KEY_FIRST_START, true);
		if (isFirstStart){
			ESLog.d("第一次打开应用，需要扫描音乐。");
			// 设置第一次打开应用完成
			SharedPrefUtils.put(getApplicationContext(), 
					Constants.KEY_FIRST_START, 
					false);
			
			// 开启音乐扫描服务扫描音乐
			new MusicScannerService(getApplicationContext(), 
					null, 
					Constants.SDCARD_PATH).start();
			
			// 启动引导页
			
			
		} 
//		else {
		// 测试代码
		
		/**
		// 获取助手类
		String url = NetConstants.BASE_DES_URL + NetConstants.DEFAULT_SONG_ID;
		VolleyHelper volley = VolleyHelper.getInstance(this);
		
		volley.addRequestTask(
				new StringRequest(Method.GET, 
						url, 
						new Listener<String>() {
							@Override
							public void onResponse(String arg0) {
								ESLog.d("test", "String result : " + arg0);
							}
						}, new ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
							}
						}), 
						url);
		
		volley.addRequestTask(
				new JsonObjectRequest(url, 
						new Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject arg0) {
								ESLog.d("JsonObject result: " + arg0);
							}
						}, new ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
							}
						}), 
						url);* /
		VolleyHelper volleyHelper = VolleyHelper.getInstance(this);
		String getChannelUrl = NetConstants.HTTP_URL_GET_SONG_LIST_BY_CHANNEL + NetConstants.DEFAULT_CHANNEL_ID;
		volleyHelper.addRequestTask(new CustomGsonRequest<FMChannelEntity>(getChannelUrl, 
				FMChannelEntity.class, null, 
				new Listener<FMChannelEntity>() {
					@Override
					public void onResponse(FMChannelEntity result) {
						ESLog.d("Result : " + result);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ESLog.e("Error : " + error.getLocalizedMessage());
					}
				}), getChannelUrl);
		//*/
		
			mCoverIv = (ImageView) findViewById(R.id.iv_cover);
			mCoverIv.postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(CoverActivity.this, HomeActivity.class);
					startActivity(intent);
					CoverActivity.this.finish();
				}
			}, 2000);
//		}
		
	}
	
	@Override
	public void onBackPressed() {
	}
	
	
	
}
