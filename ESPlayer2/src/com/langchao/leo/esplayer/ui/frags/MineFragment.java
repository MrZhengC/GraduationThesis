package com.langchao.leo.esplayer.ui.frags;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.langchao.leo.esplayer.HomeActivity;
import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.app.AppConfig;
import com.langchao.leo.esplayer.bean.Playlist;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.db.MusicTableHelper;
import com.langchao.leo.esplayer.db.PlaylistTableHelper;
import com.langchao.leo.esplayer.interfaces.IAsyncLoadListener;
import com.langchao.leo.esplayer.ui.activities.PlaylistManageActivity;
import com.langchao.leo.esplayer.ui.adapter.PlaylistAdapter;
import com.langchao.leo.esplayer.ui.widget.NewPlaylistDialog;
import com.langchao.leo.esplayer.utils.ESLog;
import com.umeng.analytics.MobclickAgent;

/**
 * 我的Fragment
 * @author 碧空
 *
 */
public class MineFragment extends Fragment implements OnClickListener{

	public final static int REQUEST_CODE_MANAGE_PLAYLIST = 0;
	
	private TextView mHistoryNumTv = null;
	
	private RelativeLayout mLocalMusicBtn = null;
	private TextView mLocalMusicNumTv = null;
	
	private RelativeLayout mDownloadBtn = null;
	private TextView mDownloadRemarkTv = null;
	
	private RelativeLayout mFavoriteBtn = null;
	private TextView mFavoriteNumTv = null;
	
	private RelativeLayout mRecentlyPlayBtn = null;
	private TextView mRecentlyPlayNumTv = null;
	
	private TextView mNumOfPlaylistTv = null;
	private TextView mPlaylistManageTv = null;
	private TextView mPlaylistAddTv = null;
	private ListView mPlaylistLv = null;
	
	private PlaylistAdapter mPlaylistAdapter = null;
	
	private MusicTableHelper mMusicTableHelper = null;
	
	private PlaylistTableHelper mPlaylistTableHelper = null;
	
	@Override
	public void onResume() {
		refreshData();
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
		ESLog.d("onCreateView。。。");
		// 友盟统计页面开始
		MobclickAgent.onPageStart(RadioFragment.class.getSimpleName());
		
		View mRootView = inflater.inflate(R.layout.fragment_mine, container, false);
		
		initUI(mRootView);
		
		initData();
		
		return mRootView;
	}
	
	/**
	 * 初始化界面
	 * @param mRootView
	 */
	private void initUI(View mRootView) {
		
		mHistoryNumTv = (TextView) mRootView.findViewById(R.id.tv_mine_history);
		
		mLocalMusicBtn = (RelativeLayout) mRootView.findViewById(R.id.btn_local_music);
		mLocalMusicNumTv = (TextView) mRootView.findViewById(R.id.tv_local_music_num);
		mLocalMusicBtn.setOnClickListener(this);
		
		mDownloadBtn = (RelativeLayout) mRootView.findViewById(R.id.btn_download);
		mDownloadRemarkTv = (TextView) mRootView.findViewById(R.id.tv_download_remark);
		mDownloadBtn.setOnClickListener(this);
		
		mFavoriteBtn = (RelativeLayout) mRootView.findViewById(R.id.btn_favorite);
		mFavoriteNumTv = (TextView) mRootView.findViewById(R.id.tv_favorite_num);
		mFavoriteBtn.setOnClickListener(this);
		
		mRecentlyPlayBtn = (RelativeLayout) mRootView.findViewById(R.id.btn_recently_play);
		mRecentlyPlayNumTv = (TextView) mRootView.findViewById(R.id.tv_recently_play_num);
		mRecentlyPlayBtn.setOnClickListener(this);
		
		mNumOfPlaylistTv = (TextView) mRootView.findViewById(R.id.tv_num_of_playlist);
		mPlaylistManageTv = (TextView) mRootView.findViewById(R.id.tv_playlist_manager);
		mPlaylistAddTv = (TextView) mRootView.findViewById(R.id.tv_playlist_add);
		
		mPlaylistManageTv.setOnClickListener(this);
		mPlaylistAddTv.setOnClickListener(this);
		
		mPlaylistLv = (ListView) mRootView.findViewById(R.id.lv_playlist);
		mPlaylistAdapter = new PlaylistAdapter(getActivity());
		mPlaylistLv.setAdapter(mPlaylistAdapter);
		mPlaylistLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 进入播放列表
				Bundle args = new Bundle();
				args.putSerializable("playlist", mPlaylistAdapter.getItem(position));
				enterPlaylistFragment(args);
			}
		});

		setHistoryMusicNum(0);
		setLocalMusicNum(0);
		setDownloadNum(0);
		setFavoriteMusicNum(0);
		setRecentlyPlayMusicNum(0);
		setPlaylistNum(0);
		
	}

	/**
	 * 初始化数据
	 */
	private void initData(){
		mMusicTableHelper = new MusicTableHelper(getActivity());
		mPlaylistTableHelper = new PlaylistTableHelper(getActivity());
	}
	
	/**
	 * 刷新
	 */
	public void refreshData(){
		// 播放统计的个数
		final int numOfStatistics = AppConfig.getInstance(getActivity())
				.getPlayerHistoryStatistics();
		setHistoryMusicNum(numOfStatistics);
		
		// 本地音乐个数
		final int numOfLocalMusic = mMusicTableHelper.getAllLocalMusicCount();
		setLocalMusicNum(numOfLocalMusic);
		
		// 收藏音乐个数
		final int numOfFavorite = mMusicTableHelper.getAllFavoriteMusicCount();
		setFavoriteMusicNum(numOfFavorite);
		
		// 暂时下载数据设置为0
		setDownloadNum(0);
		
		// 历史纪录的个数
		final int numOfHistory = mMusicTableHelper.getAllHistoryMusicCount();
		setRecentlyPlayMusicNum(numOfHistory);
		
//		// 播放列表数量
//		final int numOfPlaylist = mPlaylistTableHelper.getPlaylistCount();
//		setPlaylistNum(numOfPlaylist);
		
		refreshPlaylist();
		
	}
	
	/**
	 * 刷新播放列表数据
	 */
	public void refreshPlaylist(){
		if (mPlaylistTableHelper != null){
			List<Playlist> playlists = mPlaylistTableHelper.queryAllPlaylists();
			if (mPlaylistAdapter != null) {
				mPlaylistAdapter.clear();
			}
			if (playlists != null) {
				
				ESLog.d("playlists : " + playlists);
				
				if (mPlaylistAdapter != null) {
					mPlaylistAdapter.addItems(playlists);
				}
				setPlaylistNum(playlists.size());
			}else{
				setPlaylistNum(0);
			}
			
		}
	}
	
	@Override
	public void onClick(View v) {
		final int vid = v.getId();
		
		switch (vid) {
		case R.id.btn_local_music:
		{
			Bundle args = new Bundle();
			args.putInt(Constants.FIELD_PAGE_ID,
					Constants.PAGE_ID_LOCAL_MUSIC);
			args.putString(Constants.FIELD_PAGE_TITLE, "本地音乐");
			
			enterMusicFragment(args);
			
		}
		break;
		case R.id.btn_download:
		{
			
		}
		break;
		case R.id.btn_favorite:
		{
			Bundle args = new Bundle();
			args.putInt(Constants.FIELD_PAGE_ID,
					Constants.PAGE_ID_FAVORTIE);
			args.putString(Constants.FIELD_PAGE_TITLE, "我的最爱");
			
			enterMusicFragment(args);
		}
		break;
		case R.id.btn_recently_play:
		{
			Bundle args = new Bundle();
			args.putInt(Constants.FIELD_PAGE_ID,
					Constants.PAGE_ID_RECENTLY_PLAY);
			args.putString(Constants.FIELD_PAGE_TITLE, "最近播放");
			
			enterMusicFragment(args);
		}
		break;
		case R.id.tv_playlist_add:
		{
			//
			NewPlaylistDialog.showDialog(getActivity(), new IAsyncLoadListener<Void>() {
				@Override
				public void onSuccess(Void t) {
					// 刷新播放列表
					refreshPlaylist();
				}
				@Override
				public void onFailure(String msg) {}
			});
			
		}
		break;
		case R.id.tv_playlist_manager:
		{
			Intent intent = new Intent(getActivity(), PlaylistManageActivity.class);
			startActivityForResult(intent, REQUEST_CODE_MANAGE_PLAYLIST);
		}
		break;
		default:
			break;
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_MANAGE_PLAYLIST){
			refreshPlaylist();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 进入音乐列表页面
	 * @param data
	 */
	protected void enterMusicFragment(Bundle data) {
		
		final Activity activity = getActivity();
		if (activity instanceof HomeActivity){
			HomeActivity homeActivity = (HomeActivity) activity;

			CommonMusicListFragment fragment = new CommonMusicListFragment();
			
			fragment.setArguments(data);
			
			homeActivity.addFragment(
					R.id.panel_fragment_content, 
					fragment, 
					CommonMusicListFragment.class.getName());
		}
	}
	
	/**
	 * 进入播放列表页面
	 * @param data
	 */
	protected void enterPlaylistFragment(Bundle data) {
		
		final Activity activity = getActivity();
		if (activity instanceof HomeActivity){
			HomeActivity homeActivity = (HomeActivity) activity;
			
			PlaylistFragment fragment = new PlaylistFragment();
			fragment.setArguments(data);
			
			homeActivity.addFragment(
					R.id.panel_fragment_content, 
					fragment, 
					PlaylistFragment.class.getName());
		}
	}
	
	/**
	 * 设置历史纪录音乐的个数
	 * @param num
	 */
	private void setHistoryMusicNum(int num){
    	if (mHistoryNumTv != null){
    		mHistoryNumTv.setText(String.format(getString(R.string.music_history_num), num));
    	}
    }
    
	/**
	 * 设置本地音乐的个数
	 * @param num
	 */
    private void setLocalMusicNum(int num){
    	if (mLocalMusicNumTv != null){
    		mLocalMusicNumTv.setText(String.format(getString(R.string.num_of_music), num));
    	}
    }
   
    /**
	 * 设置收藏音乐的个数
	 * @param num
	 */
    private void setFavoriteMusicNum(int num){
    	if (mFavoriteNumTv != null){
    		mFavoriteNumTv.setText(String.format(getString(R.string.num_of_music), num));
    	}
    }
   
    /**
	 * 设置最近播放音乐的个数
	 * @param num
	 */
    private void setRecentlyPlayMusicNum(int num){
    	if (mRecentlyPlayNumTv != null) {
    		mRecentlyPlayNumTv.setText(String.format(getString(R.string.num_of_music), num));
    	}
    }
  
    /**
	 * 设置播放列表的个数
	 * @param num
	 */
    private void setPlaylistNum(int num){
    	if(mNumOfPlaylistTv!=null){
    		mNumOfPlaylistTv.setText(String.format(getString(R.string.num_of_playlist), num));
    	}
    }
    
    /**
	 * 设置正在下载歌曲的个数
	 * @param num
	 */
    private void setDownloadNum(int num){
    	if (mDownloadRemarkTv != null){
    		if (num > 0) {
    			mDownloadRemarkTv.setText(String.format(getString(R.string.music_download_num), num));
    		} else {
    			mDownloadRemarkTv.setText(getString(R.string.no_download_task));
    		}
    	}
    }
	
}
