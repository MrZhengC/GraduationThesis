package com.langchao.leo.esplayer.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.app.ESApplication;
import com.langchao.leo.esplayer.interfaces.IAsyncLoadListener;
import com.langchao.leo.esplayer.utils.NetworkUtils;
import com.langchao.leo.esplayer.utils.SendMail;
import com.umeng.analytics.MobclickAgent;

/**
 * 意见反馈activity
 * @author 碧空
 *
 */
public class FeedbackActivity extends FragmentActivity implements OnClickListener{

	private TextView btnBack = null;
	private TextView mTitletTv = null;
	private TextView mMoreOptionTv = null;
	
	private EditText mMessageEt = null;
	private EditText mPhoneOrQQEt = null;
	private View btnSubmit = null;
	
	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_feedback);
		ESApplication.getInstance().addActivity(this);
		
		initUI(arg0);
		
	}

	public void initUI(Bundle savedInstanceState) {
		initHeader();
		
		mMessageEt = (EditText) findViewById(R.id.et_feedback_message);
		mPhoneOrQQEt = (EditText) findViewById(R.id.et_feedback_phone_or_qq);
		btnSubmit = findViewById(R.id.btn_submit);
		btnSubmit.setOnClickListener(this);
	}

	protected void initHeader(){
		btnBack = (TextView) findViewById(R.id.btn_nav_back);
		mTitletTv = (TextView) findViewById(R.id.tv_nav_title);
		mMoreOptionTv = (TextView) findViewById(R.id.btn_nav_more);
		btnBack.setText("");
		mTitletTv.setText("意见反馈");
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

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.btn_nav_back){
			finish();
		}else if (id == R.id.btn_submit){
			
			final String message = mMessageEt.getText().toString();
			final String phoneOrQQ = mPhoneOrQQEt.getText().toString();
			
			if (TextUtils.isEmpty(message)){
				Toast.makeText(getApplicationContext(), "请输入您的宝贵建议", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (!NetworkUtils.isConnectInternet(this)) {
				Toast.makeText(this, "网络不可用，请检查网络连接", Toast.LENGTH_SHORT).show();
				return;
			}
			
			SendMail.sendmail(message + '\n' + "Phone&QQ：" + phoneOrQQ, new IAsyncLoadListener<Void>() {
				
				@Override
				public void onSuccess(Void data) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "发送成功!!", Toast.LENGTH_SHORT).show();
							FeedbackActivity.this.finish();
						}
					});
				}
				
				@Override
				public void onFailure(String error) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "方式失败!!", Toast.LENGTH_SHORT).show();
						}
					});
				}
				
			});
			
		}
		
	}
	
}
