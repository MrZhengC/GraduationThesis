package com.langchao.leo.esplayer.ui.frags;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.app.AppConfig;
import com.langchao.leo.esplayer.ui.activities.FeedbackActivity;
import com.langchao.leo.esplayer.ui.activities.MusicAlarmActivity;
import com.langchao.leo.esplayer.ui.activities.MusicScannerActivity;
import com.langchao.leo.esplayer.ui.activities.SettingsActivity;
import com.langchao.leo.esplayer.ui.adapter.LeftMenuOptionAdatper;
import com.langchao.leo.esplayer.ui.widget.ExitAppDialog;
import com.langchao.leo.esplayer.ui.widget.TimerDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 左侧边栏,同样是support-v4包下的
 * @author 碧空
 */
public class LeftMenuFragment extends Fragment implements OnClickListener{

	private ListView mOptionLv = null;
	private CheckBox mOnlyWifiCb = null;
	private TextView mSettingsTv = null;
	private TextView mExitTv =null;
	
	private LeftMenuOptionAdatper mOptionAdatper = null;
	
	private TimerDialog mTimerDialog;
	
	// 定时操作的总时间长度
	private long timerTaskDuration = 0;
	
	private CustomCountDownTimer mCountDownTimer = null;
	
	@Override
	public void onResume() {
		super.onResume();
		// 友盟统计页面开始
		MobclickAgent.onPageStart(RadioFragment.class.getSimpleName());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// 友盟统计页面介绍
		MobclickAgent.onPageEnd(RadioFragment.class.getSimpleName());
	}
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// 友盟统计页面开始
		MobclickAgent.onPageStart(RadioFragment.class.getSimpleName());
		
		View mRootView = inflater.inflate(R.layout.fragment_left_menu, container, false);
		
		initUI(mRootView);
		
		return mRootView;
	}
	
	/**
	 * 初始化界面，findviewbyID和事件的绑定
	 * @param rootView
	 */
	private void initUI(View rootView){
		
		mOptionLv = (ListView) rootView.findViewById(R.id.lv_options);
		mOptionAdatper = new LeftMenuOptionAdatper(getActivity());
		mOptionLv.setAdapter(mOptionAdatper);
		
		mOnlyWifiCb = (CheckBox) rootView.findViewById(R.id.btn_only_wifi);
		mSettingsTv = (TextView) rootView.findViewById(R.id.tv_settings);
		mExitTv = (TextView) rootView.findViewById(R.id.tv_exit);
		
		mSettingsTv.setOnClickListener(this);
		mExitTv.setOnClickListener(this);
		
		// 从App配置中获取only_wifi的值
		mOnlyWifiCb.setChecked(AppConfig.getInstance(getActivity()).isOnlyWifi());
		mOnlyWifiCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// 更改配置
				AppConfig.getInstance(getActivity()).setOnlyWifi(isChecked);
			}
		});
		
		mOptionLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					// 处理定时关闭
					mTimerDialog = TimerDialog.getInstance(getActivity(), true)
					.setNegativeButton(getString(R.string.cancel), null)
					.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 获取总秒数
							// 取消上次的定时
							if (mCountDownTimer != null) {
								mCountDownTimer.cancel();
							}
							// 获取总时间
							timerTaskDuration = mTimerDialog.getProgress() * 60 * 1000;
							// 如果定时时间大于0，则重新设置定时器
							if (timerTaskDuration > 0) {
								mCountDownTimer = new CustomCountDownTimer(timerTaskDuration, 1000);
								mCountDownTimer.start();
							}
							
							// 更新时间
							mOptionAdatper.updateTimer(timerTaskDuration, mOptionLv.getFirstVisiblePosition());
							
						}
					}).showDialog();
					return;
				}
				
				Intent intent = new Intent();
				if (position == 1) { 
					intent.setClass(getActivity(), MusicAlarmActivity.class);
				} else if (position == 2) {
					intent.setClass(getActivity(), MusicScannerActivity.class);
				} else if (position == 3) {
					intent.setClass(getActivity(), FeedbackActivity.class);
				}
				getActivity().startActivity(intent);
			}
		});
		
	}

	@Override
	public void onClick(View v) {
		final int vid = v.getId();
		switch (vid) {
		case R.id.tv_settings:
		{
			// 处理setting点击事件
			Intent intent = new Intent(getActivity(), SettingsActivity.class);
			getActivity().startActivity(intent);
		}
		break;
		case R.id.tv_exit:
		{
			// 处理退出按钮的点击事件
			//ESApplication.getInstance().AppExit();
			ExitAppDialog.showDialog(getActivity());
			
		}
		break;	
		default:
			break;
		}
		
	}
	
	/**
	 * 定时关闭
	 * @author 碧空
	 */
	private class CustomCountDownTimer extends CountDownTimer {

		public CustomCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// 更新时间
			mOptionAdatper.updateTimer(millisUntilFinished, mOptionLv.getFirstVisiblePosition());
		}

		@Override
		public void onFinish() {
			mOptionAdatper.updateTimer(0, mOptionLv.getFirstVisiblePosition());
			ExitAppDialog.showDialog(getActivity(), true);
			
		}
	}
	
}
