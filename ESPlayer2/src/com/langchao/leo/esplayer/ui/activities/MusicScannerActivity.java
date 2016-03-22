package com.langchao.leo.esplayer.ui.activities;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.app.ESApplication;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.services.MusicScannerService;
import com.umeng.analytics.MobclickAgent;

/**
 * 音乐扫描activity
 * @author 碧空
 *
 */
public class MusicScannerActivity extends FragmentActivity 
	implements OnClickListener{
	
	private View mBackBtn = null;
	private View mMoreBtn = null;
	private TextView mPageTitleTv = null;
	
	private TextView mScannerPathTv = null;
	
	private Button mOneKeyScanBtn = null;
	
	private UIHandler mUIHanlder = null;
	
	private boolean isScanCompleted = true;
	
	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_music_scanner);
		ESApplication.getInstance().addActivity(this);
		
		initUI();
		
	}

	private void initUI() {
		initNavBar();
		
		mScannerPathTv = (TextView) findViewById(R.id.tv_scanner_path);
		mOneKeyScanBtn = (Button) findViewById(R.id.btn_one_key_scan);
		mOneKeyScanBtn.setOnClickListener(this);
	}

	private void initNavBar() {
		mBackBtn = findViewById(R.id.btn_nav_back);
		mBackBtn.setOnClickListener(this);
		mPageTitleTv = (TextView) findViewById(R.id.tv_nav_title);
		mPageTitleTv.setText("音乐扫描");
		mMoreBtn = findViewById(R.id.btn_nav_more);
		mMoreBtn.setVisibility(View.GONE);
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
	
	@Override
	public void onClick(View v) {
		final int vid = v.getId();
		switch (vid) {
		case R.id.btn_nav_back:{
			onBackPressed();
		}
		break;
		case R.id.btn_one_key_scan:
		{
			if (isScanCompleted){
				isScanCompleted = false;
				if (mUIHanlder == null){
					mUIHanlder = new UIHandler(MusicScannerActivity.this);
				}
				new MusicScannerService(getApplicationContext(), 
						mUIHanlder, 
						Constants.SDCARD_PATH).start();
			}
		}
		break;

		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(0);
	}
	
	@SuppressLint("HandlerLeak")
	class UIHandler extends Handler{
		
		private WeakReference<MusicScannerActivity> activity = null;
		
		public UIHandler(MusicScannerActivity activity) {
			super(getMainLooper());
			this.activity = new WeakReference<MusicScannerActivity>(activity); 
		}

		@Override
		public void handleMessage(Message msg) {
			if (activity != null && activity.get() != null){
				// 处理消息
				if (msg.what == MusicScannerService.FIELD_SCAN_MUSIC_PROGRESS){
					
					// 设置扫描到的路径
					mScannerPathTv.setText(""+msg.obj);
					
				} else if (msg.what == MusicScannerService.FIELD_SCAN_MUSIC_SECCESS){
					// 设置扫描完成
					mScannerPathTv.setText("扫描完成，已扫描" + msg.arg1 + "首歌曲");
					
					isScanCompleted = true;
				}
			}
			super.handleMessage(msg);
		}
		
	}
	
}
