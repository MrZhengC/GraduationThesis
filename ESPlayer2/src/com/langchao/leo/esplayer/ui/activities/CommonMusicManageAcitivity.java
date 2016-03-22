package com.langchao.leo.esplayer.ui.activities;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.app.ESApplication;
import com.langchao.leo.esplayer.bean.Playlist;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.db.MusicTableHelper;
import com.langchao.leo.esplayer.db.PlaylistTableHelper;
import com.langchao.leo.esplayer.interfaces.OnItemSelectChangeListener;
import com.langchao.leo.esplayer.ui.adapter.CommonMusicManageAdapter;
import com.langchao.leo.esplayer.ui.widget.AddMusicToPlaylistDialog;
import com.langchao.leo.esplayer.utils.ESLog;
import com.umeng.analytics.MobclickAgent;

/**
 * 公共音乐管理界面
 * @author 碧空
 *
 */
public class CommonMusicManageAcitivity extends Activity implements 
	OnClickListener, OnItemClickListener, OnItemSelectChangeListener{

	private TextView mBackBtn = null;
	private TextView mPageTitleTv = null;
	private TextView mMoreOptionBtn = null;
	
	private ListView mMusicContentLv = null;
	
	private TextView mAddToPlaylistBtn = null;
	private TextView mDeleteBtn = null;
	
	private int mCurrentPageId = -1;
	
	private MusicTableHelper mMusicTableHelper = null;
	
	private CommonMusicManageAdapter mMusicManageAdapter = null;
	
	private PlaylistTableHelper mPlaylistTableHelper = null;
	private Playlist mCurrentPlaylist = null;
	
	private View mDialogCustomView = null;
	
	private CheckBox mDeleteLocalMusicTogetherCB = null;
	
	private AddMusicToPlaylistDialog mAddMusicDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_music_manage);
		ESApplication.getInstance().addActivity(this);
		
		Intent intent = getIntent();
		mCurrentPageId = intent.getIntExtra(Constants.FIELD_PAGE_ID, -1);
		try {
			mCurrentPlaylist = (Playlist) intent.getSerializableExtra("playlist");
		} catch (Exception e) {
		}
		
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

		// 初始时下部操作按钮不可点击
		mAddToPlaylistBtn.setEnabled(false);
		mDeleteBtn.setEnabled(false);
		
		switch (mCurrentPageId) {
		case Constants.PAGE_ID_LOCAL_MUSIC:
			mAddToPlaylistBtn.setVisibility(View.VISIBLE);
			break;
		case Constants.PAGE_ID_FAVORTIE:
		case Constants.PAGE_ID_RECENTLY_PLAY:
		case Constants.PAGE_ID_PLAYLIST:
			mAddToPlaylistBtn.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		
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
		
		mMusicTableHelper = new MusicTableHelper(this);
		
		List<RealSong> songs = null;
		
		switch (mCurrentPageId) {
		case Constants.PAGE_ID_LOCAL_MUSIC:
		{
			songs = mMusicTableHelper.queryAllLocalMusic();
		}
		break;
		case Constants.PAGE_ID_FAVORTIE:
		{
			songs = mMusicTableHelper.queryAllFavorite();
		}
		break;
		case Constants.PAGE_ID_RECENTLY_PLAY:
		{
			songs = mMusicTableHelper.queryAllHistory();
		}
		break;
		case Constants.PAGE_ID_PLAYLIST:
		{
			mPlaylistTableHelper = new PlaylistTableHelper(this);
			songs = mPlaylistTableHelper.obtainAllSongs(mCurrentPlaylist);
			
		}
		break;
		default:
			break;
		}
		
		if (songs != null) {
			mMusicManageAdapter.addItems(songs);
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
	public void onBackPressed() {
		super.onBackPressed();
		setResult(0);
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
			if (mAddMusicDialog == null) {
				mAddMusicDialog = new AddMusicToPlaylistDialog(this, mMusicManageAdapter);
			}
			mAddMusicDialog.show();
		}
		break;
		case R.id.tv_music_manage_delete:
		{
			// 删除
			//从资源文件中加载View
			mDialogCustomView = LayoutInflater.from(this)
					.inflate(R.layout.layout_delete_local_music, null);
			// 获取check box 控件
			mDeleteLocalMusicTogetherCB = 
					(CheckBox) mDialogCustomView.findViewById(R.id.cb_delete_local_music);
			
			new AlertDialog.Builder(this)
			.setTitle("删除提示！！！")
			.setMessage("您真要删除这些歌曲吗？")
			.setView(mDialogCustomView)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 获取被选中的歌曲列表
					final List<RealSong> songs = 
							mMusicManageAdapter.getSelectedItems();
					
					// 删除数据库中的记录
					switch (mCurrentPageId) {
					case Constants.PAGE_ID_LOCAL_MUSIC:
						//删除本地文件
						mMusicTableHelper.delete(songs);
						// 同时删除与本地音乐相关的播放列表内的数据
						
						break;
					case Constants.PAGE_ID_FAVORTIE:
						mMusicTableHelper.deleteFavorite(songs);
						break;
					case Constants.PAGE_ID_RECENTLY_PLAY:
						mMusicTableHelper.deleteHistory(songs);
						break;
					case Constants.PAGE_ID_PLAYLIST:
						// 从播放列表中删除歌曲
						mPlaylistTableHelper.deleteMusicFromPlaylist(mCurrentPlaylist, songs);
						break;
					default:
						break;
					}
					
					// 遍历删除
					for (RealSong song : songs) {
						// 确定处理操作
						if (mDeleteLocalMusicTogetherCB.isChecked()) {
							File songFile = new File(song.getLocalPath());
							if (songFile.exists()) {
								songFile.delete();
							}
						}
						// 移除adapter中的数据
						mMusicManageAdapter.removeItem(song);
					}
					
					mMusicManageAdapter.cancelAllItems();
					
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
	
}
