package com.langchao.leo.esplayer.ui.activities;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.db.MusicTableHelper;
import com.langchao.leo.esplayer.db.PlaylistTableHelper;
import com.langchao.leo.esplayer.interfaces.OnItemSelectChangeListener;
import com.langchao.leo.esplayer.ui.adapter.CommonMusicManageAdapter;
import com.langchao.leo.esplayer.utils.ESLog;
import com.umeng.analytics.MobclickAgent;

/**
 * 想播放列表中添加歌曲的页面
 * @author 碧空
 *
 */
public class AddMusicActivity2 extends Activity implements 
	OnClickListener, OnItemClickListener, OnItemSelectChangeListener{

	private TextView mBackBtn = null;
	private TextView mPageTitleTv = null;
	private TextView mMoreOptionBtn = null;
	
	private ListView mMusicContentLv = null;
	
	private TextView mAddToPlaylistBtn = null;
	private TextView mDeleteBtn = null;
	
	private CommonMusicManageAdapter mMusicManageAdapter = null;
	
	private Playlist mCurrentPlaylist = null;
	
	private PlaylistTableHelper mPlaylistTableHelper = null;
	
	private MusicTableHelper mMusicTableHelper = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_music_manage);
		ESApplication.getInstance().addActivity(this);
		
		mCurrentPlaylist = (Playlist) getIntent().getSerializableExtra("playlist");
		
		initUI();
		
		initData();
		
	}

	/**
	 * 初始化UI界面
	 */
	private void initUI() {
		initNavBar();
		
		mAddToPlaylistBtn = (TextView) findViewById(R.id.tv_music_manage_add_to_playlist);
		mDeleteBtn = (TextView) findViewById(R.id.tv_music_manage_delete);
		
		mAddToPlaylistBtn.setOnClickListener(this);
		mDeleteBtn.setOnClickListener(this);
		mDeleteBtn.setVisibility(View.GONE);

		// 初始时下部操作按钮不可点击
		mAddToPlaylistBtn.setEnabled(false);
		mDeleteBtn.setEnabled(false);
		
		mMusicContentLv = (ListView) findViewById(R.id.lv_music_content);
		mMusicContentLv.setOnItemClickListener(this);
		mMusicManageAdapter = new CommonMusicManageAdapter(this, this);
		mMusicContentLv.setAdapter(mMusicManageAdapter);
		
	}
	
	/**
	 * 初始化导航条
	 */
	private void initNavBar() {
		
		mBackBtn = (TextView) findViewById(R.id.btn_nav_back);
		mPageTitleTv = (TextView) findViewById(R.id.tv_nav_title);
		mMoreOptionBtn = (TextView) findViewById(R.id.btn_nav_more);
		
		mBackBtn.setText("选择歌曲");
		
		mPageTitleTv.setText("");
		
		mMoreOptionBtn.setText("全选");
		mMoreOptionBtn.setVisibility(View.VISIBLE);
		mMoreOptionBtn.setCompoundDrawables(null, null, null, null);
		
		mBackBtn.setOnClickListener(this);
		mMoreOptionBtn.setOnClickListener(this);
		
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		if (mCurrentPlaylist != null) {
			mPlaylistTableHelper = new PlaylistTableHelper(this);
			
			mMusicTableHelper = new MusicTableHelper(this);
			
			List<RealSong> playlistSongs = mPlaylistTableHelper.obtainAllSongs(mCurrentPlaylist);
			List<RealSong> allSongs = mMusicTableHelper.queryAllLocalMusic();
			
			if (playlistSongs != null && allSongs != null) {
				for (RealSong song : playlistSongs) {
					if (allSongs.contains(song)) {
						allSongs.remove(song);
					}
				}
			}
			
			if (allSongs != null) {
				mMusicManageAdapter.addItems(allSongs);
			}
		}
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ESLog.e("onItemClick : " + position);
		// 更改选中状态
		mMusicManageAdapter.selectItem(position);
	}

	@SuppressLint("InflateParams")
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
			// 全选/取消
			
			// 如果当前全选取消按钮，显示的是全选，则执行全选方法，全部选中
			if (mMoreOptionBtn.getText().equals("全选")){
				// 设置所有条目选中
				mMusicManageAdapter.selectAllItems();
				// 将按钮文字设置为取消
				mMoreOptionBtn.setText("取消");
				
			} else {
				// 设置所有条目取消选中
				mMusicManageAdapter.cancelAllItems();
				// 将按钮文字设置为全选
				mMoreOptionBtn.setText("全选");
			}
		}
		break;
		case R.id.tv_music_manage_add_to_playlist:
		{
			// 添加到播放列表
			List<RealSong> songs = mMusicManageAdapter.getSelectedItems();
			mPlaylistTableHelper.insertSongsToPlaylist(mCurrentPlaylist, songs);
			// 添加成功即返回
			onBackPressed();
			
		}
		break;
		case R.id.tv_music_manage_delete:{
			// 删除
		}
		break;

		default:
			break;
		}
	}
	
	/**
	 * 设置选中歌曲个数
	 * @param num
	 */
	private void setSelectedMusicNum(int num){
		if (mBackBtn != null) {
			if (num == 0) {
				mBackBtn.setText("选择歌曲");
			} else {
				mBackBtn.setText("已选择"+num+"首歌曲");
			}
		}
	}

	@Override
	public void onItemSelectChanage(int count) {
		
		if (count == mMusicManageAdapter.getCount()){
			mMoreOptionBtn.setText("取消");
		} else { 
			mMoreOptionBtn.setText("全选");
		}
		
		setSelectedMusicNum(count);
		
		// 设置操作按钮的可点击性
		if (count > 0) {
			mAddToPlaylistBtn.setEnabled(true);
			mDeleteBtn.setEnabled(true);
		}else{
			mAddToPlaylistBtn.setEnabled(false);
			mDeleteBtn.setEnabled(false);
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(0);
	}
	
}
