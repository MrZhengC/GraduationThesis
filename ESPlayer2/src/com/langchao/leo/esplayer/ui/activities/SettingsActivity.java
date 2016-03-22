package com.langchao.leo.esplayer.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.app.ESApplication;
import com.langchao.leo.esplayer.utils.CommonUtils;
import com.langchao.leo.esplayer.utils.DataCleanManager;
import com.langchao.leo.esplayer.utils.ESLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class SettingsActivity extends FragmentActivity implements OnClickListener{
	
	private TextView btnBack = null;
	private TextView mTitletTv = null;
	private TextView mMoreOptionTv = null;
	
	private View btnCleanCache = null;
	private TextView tvCacheSize = null;
	private View btnAboutUs = null;
	private View btnUserGuide = null;
	
	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_settings);
		ESApplication.getInstance().addActivity(this);
		initUI();
	}

	/**
	 * 初始化界面
	 */
	private void initUI() {
		
		initHeader();
		
		btnCleanCache = findViewById(R.id.btn_clean_cache);
		tvCacheSize = (TextView) findViewById(R.id.tv_app_cache_size);
		btnAboutUs = findViewById(R.id.btn_about_us);
		btnUserGuide = findViewById(R.id.btn_user_guide);
		
		btnCleanCache.setOnClickListener(this);
		btnAboutUs.setOnClickListener(this);
		btnUserGuide.setOnClickListener(this);
		
		tvCacheSize.setText("" + CommonUtils.getFormatSize(
				DataCleanManager.getAppCacheSize(this)));
		
	}
	
	private void initHeader(){
		btnBack = (TextView) findViewById(R.id.btn_nav_back);
		mTitletTv = (TextView) findViewById(R.id.tv_nav_title);
		mMoreOptionTv = (TextView) findViewById(R.id.btn_nav_more);
		btnBack.setText("");
		mTitletTv.setText("设置");
		mMoreOptionTv.setVisibility(View.GONE);
		btnBack.setOnClickListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	/**
	 * 点击事件
	 * @param view
	 */
	@Override
	public void onClick(View view){
		final int id = view.getId();
		if (id == R.id.btn_nav_back){
			finish();
		}else if (id == R.id.btn_clean_cache){
			// 清理缓存
			// 1、图片缓存；
			// 2、应用内私有缓存文件夹、文件夹；
			// 3、应用内SD卡私有缓存文件夹、文件夹；
			// 4、ImageLoader图片缓存；
			// 5、SharedPreference的内容；
			ESLog.e("ImageLoader image cache : " + ImageLoader.getInstance()
					.getDiskCache().getDirectory().getAbsolutePath());
			
			tvCacheSize.setText(CommonUtils.getFormatSize(0));
			
			DataCleanManager.cleanAppCache(this);
			
			ESLog.e("清理过缓存之后，应用缓存大小为：" 
			+ CommonUtils.getFormatSize(
					DataCleanManager.getAppCacheSize(this)));
			
		}else if (id == R.id.btn_about_us){
			
		}else if (id == R.id.btn_user_guide){
			
		}
	}
	
}
