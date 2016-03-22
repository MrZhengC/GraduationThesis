package com.langchao.leo.esplayer.ui.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.app.ESApplication;
import com.langchao.leo.esplayer.bean.Playlist;
import com.langchao.leo.esplayer.db.PlaylistTableHelper;
import com.langchao.leo.esplayer.interfaces.OnItemSelectChangeListener;
import com.langchao.leo.esplayer.ui.adapter.PlaylistManageAdapter;
import com.umeng.analytics.MobclickAgent;

public class PlaylistManageActivity extends Activity implements 
	OnClickListener, OnItemSelectChangeListener, OnItemClickListener{

	private TextView mBackBtn = null;
	private TextView mTitletTv = null;
	private TextView mMoreOptionBtn = null;
	
	private View mDeleteBtn;

	private ListView mPlaylistContentLv = null;
	
	private PlaylistManageAdapter mPlaylistManageAdapter = null;
	
	private PlaylistTableHelper mPlaylistTableHelper = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playlist_manage);
		ESApplication.getInstance().addActivity(this);
		
		initUI();
		
		loadData();
		
	}

	public void initUI() {
		initHeader();
		
		mDeleteBtn = findViewById(R.id.tv_playlist_manage_delete);
		mDeleteBtn.setOnClickListener(this);
		
		mPlaylistContentLv = (ListView) findViewById(R.id.lv_playlist_content);
		mPlaylistManageAdapter = new PlaylistManageAdapter(this, this);
		mPlaylistContentLv.setAdapter(mPlaylistManageAdapter);
		mPlaylistContentLv.setOnItemClickListener(this);
	}

	protected void initHeader(){
		mBackBtn = (TextView) findViewById(R.id.btn_nav_back);
		mTitletTv = (TextView) findViewById(R.id.tv_nav_title);
		mMoreOptionBtn = (TextView) findViewById(R.id.btn_nav_more);
		
		mTitletTv.setText("");
		mTitletTv.setVisibility(View.GONE);
		
		mMoreOptionBtn.setText("全选");
		mMoreOptionBtn.setCompoundDrawables(null, null, null, null);
		mMoreOptionBtn.setVisibility(View.VISIBLE);
		
		mBackBtn.setText("选择播放列表");
		mBackBtn.setOnClickListener(this);
		mMoreOptionBtn.setOnClickListener(this);
	}
	
	public void loadData() {
		mPlaylistTableHelper = new PlaylistTableHelper(this);
		List<Playlist> playlists =
				mPlaylistTableHelper.queryAllPlaylists();
		mPlaylistManageAdapter.addItems(playlists);
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
	public void onBackPressed() {
		super.onBackPressed();
		setResult(0);
	}
	
	@Override
	public void onClick(View v) {
		final int vid = v.getId();
		switch (vid) {
		case R.id.btn_nav_back:
		{
			onBackPressed();
		}
		break;
		case R.id.btn_nav_more:
		{
			// 如果当前全选取消按钮，显示的是全选，则执行全选方法，全部选中
			if (mMoreOptionBtn.getText().equals("全选")){
				// 设置所有条目选中
				mPlaylistManageAdapter.selectAllItems();
				// 将按钮文字设置为取消
				mMoreOptionBtn.setText("取消");
				
			} else {
				// 设置所有条目取消选中
				mPlaylistManageAdapter.cancelAllItems();
				// 将按钮文字设置为全选
				mMoreOptionBtn.setText("全选");
				
			}
		}	
		break;
		case R.id.tv_playlist_manage_delete:
		{
			new AlertDialog.Builder(this)
			.setTitle("删除提示")
			.setMessage("您确定要删除这些播放列表吗？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 获取被选中的歌曲列表
					final List<Playlist> playlists = 
							mPlaylistManageAdapter.getSelectedItems();
					
					// 删除数据库
					mPlaylistTableHelper.delete(playlists);
					
					// 遍历删除
					for (Playlist playlist : playlists) {
						// 移除adapter中的数据
						mPlaylistManageAdapter.removeItem(playlist);
					}
					mPlaylistManageAdapter.cancelAllItems();
				}
			})
			.setNegativeButton("取消", null)
			.create().show();
		}
		break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mPlaylistManageAdapter.selectItem(position);
	}
	
	/**
	 * 选中的播放列表个数
	 * @param count
	 */
	private void setSelectedPlaylistNum(int count) {
		if (mBackBtn != null) {
			if (count == 0) {
				mBackBtn.setText("选择播放列表");
			} else {
				mBackBtn.setText("已选择"+count+"个播放列表");
			}
		}
	}

	@Override
	public void onItemSelectChanage(int count) {
		if (count == mPlaylistManageAdapter.getCount()){
			mMoreOptionBtn.setText("取消");
		} else { 
			mMoreOptionBtn.setText("全选");
		}
		
		setSelectedPlaylistNum(count);
		
		// 设置操作按钮的可点击性
		if (count > 0) {
			mDeleteBtn.setEnabled(true);
		}else{
			mDeleteBtn.setEnabled(false);
		}
	}

}
