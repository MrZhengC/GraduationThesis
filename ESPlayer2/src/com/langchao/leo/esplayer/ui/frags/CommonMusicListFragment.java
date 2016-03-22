package com.langchao.leo.esplayer.ui.frags;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.bean.Playlist;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.db.MusicTableHelper;
import com.langchao.leo.esplayer.interfaces.IActivityInteraction;
import com.langchao.leo.esplayer.interfaces.OnOperationActionListener;
import com.langchao.leo.esplayer.ui.activities.CommonMusicManageAcitivity;
import com.langchao.leo.esplayer.ui.activities.MusicScannerActivity;
import com.langchao.leo.esplayer.ui.adapter.CommonMusicAdapter;
import com.langchao.leo.esplayer.utils.CommonUtils;
import com.langchao.leo.esplayer.utils.ESLog;
import com.umeng.analytics.MobclickAgent;

/**
 * 一般的音乐列表Fragment<br>
 * 1、本地音乐
 * 2、我的收藏
 * 3、最近播放
 * @author 碧空
 *
 */
public class CommonMusicListFragment extends Fragment implements 
		OnClickListener, 
		OnItemClickListener,
		OnMenuItemClickListener, 
		OnOperationActionListener{
	
	/**
	 * 管理页面请求码
	 */
	public final static int REQUEST_CODE_MANAGE = 0;
	
	/**
	 * 一键扫描请求码
	 */
	public final static int REQUEST_CODE_ONE_KEY_SCAN = 1;
	
	private View mBackBtn = null;
	private View mMoreBtn = null;
	private TextView mPageTitleTv = null;
	
	private ListView mMusicContentLv = null;
	
	/**
	 * 音乐内容管理条
	 */
	private View mManageBarHeader = null;
	
	/**
	 * 播放所有按钮
	 */
	private View mPlayAllBtn = null;
	
	private TextView mMusicNumTv = null;
	
	private TextView mMusicManageTv = null;
	
	private CommonMusicAdapter mMusicAdapter = null;
	
	private MusicTableHelper mMusicTableHelper = null;
	
	private int mCurrentPageId = Constants.PAGE_ID_LOCAL_MUSIC;
	
	// 当前歌曲列表
	private List<RealSong> mCurrentSongs = null;
	// 是否是第一次播放该列表
	private boolean isFirstPlay = true;
	
	@Override
	public void onPause() {
		super.onPause();
		switch (mCurrentPageId) {
		case Constants.PAGE_ID_LOCAL_MUSIC:
		{
			MobclickAgent.onPageEnd("LocalFragment");
		}
		break;
		case Constants.PAGE_ID_FAVORTIE:
		{
			MobclickAgent.onPageEnd("FavoriteFragment");
		}
		break;
		case Constants.PAGE_ID_RECENTLY_PLAY:
		{
			MobclickAgent.onPageEnd("RecentlyPlayFragment");
		}
		break;
		default:
			break;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		switch (mCurrentPageId) {
		case Constants.PAGE_ID_LOCAL_MUSIC:
		{
			MobclickAgent.onPageStart("LocalFragment");
		}
		break;
		case Constants.PAGE_ID_FAVORTIE:
		{
			MobclickAgent.onPageStart("FavoriteFragment");
		}
		break;
		case Constants.PAGE_ID_RECENTLY_PLAY:
		{
			MobclickAgent.onPageStart("RecentlyPlayFragment");
		}
		break;
		default:
			break;
		}
	}
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.fragment_common_music_list, container, false);
		
		initUI(mRootView);
		
		initData();
		
		return mRootView;
	}

	/**
	 * 初始化UI界面
	 * @param mRootView
	 */
	private void initUI(View mRootView) {
		
		initNavBar(mRootView);
		
		mMusicContentLv = (ListView) mRootView.findViewById(R.id.lv_music_content);
		
		mManageBarHeader = LayoutInflater.from(getActivity())
				.inflate(R.layout.layout_music_list_top, mMusicContentLv, false);
		
		initHeader();
		
		mMusicContentLv.addHeaderView(mManageBarHeader);
		
		mMusicAdapter = new CommonMusicAdapter(getActivity(), this);
		mMusicContentLv.setAdapter(mMusicAdapter);
		mMusicContentLv.setOnItemClickListener(this);
		
		setMusicNum(0);
		
	}

	private void initNavBar(View mRootView) {
		mBackBtn = mRootView.findViewById(R.id.btn_nav_back);
		mBackBtn.setOnClickListener(this);
		mPageTitleTv = (TextView) mRootView.findViewById(R.id.tv_nav_title);
		
		mMoreBtn = mRootView.findViewById(R.id.btn_nav_more);
		mMoreBtn.setVisibility(View.VISIBLE);
		mMoreBtn.setOnClickListener(this);

		Bundle data = getArguments();
		mPageTitleTv.setText("" + data.getString(Constants.FIELD_PAGE_TITLE, ""));
	}
	
	/**
	 * 初始化ListView header
	 */
	private void initHeader() {
		mPlayAllBtn = mManageBarHeader.findViewById(R.id.panel_music_list_play_all);
		mPlayAllBtn.setOnClickListener(this);
		
		mMusicNumTv = (TextView) mManageBarHeader.findViewById(R.id.tv_music_list_num);
		
		mMusicManageTv = (TextView) mManageBarHeader.findViewById(R.id.tv_music_list_manage);
		mMusicManageTv.setOnClickListener(this);
	}
	
	protected void setMusicNum(int num) {
		if (mMusicNumTv != null) {
			mMusicNumTv.setText(String.format(
					getResources().getString(R.string.num_of_music), 
					num));
		}
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		mMusicTableHelper = new MusicTableHelper(getActivity());
		
		Bundle args = getArguments();
		if (args!=null){
			mCurrentPageId = args.getInt(Constants.FIELD_PAGE_ID);
			
			refreshData();
			
		}
	}

	/**
	 * 刷新数据
	 */
	private void refreshData() {
		
		// 重置歌曲列表
		mCurrentSongs = null;
		
		switch (mCurrentPageId) {
		case Constants.PAGE_ID_LOCAL_MUSIC:
		{
			mCurrentSongs = mMusicTableHelper.queryAllLocalMusic();
		}
		break;
		case Constants.PAGE_ID_FAVORTIE:
		{
			mCurrentSongs = mMusicTableHelper.queryAllFavorite();
		}
		break;
		case Constants.PAGE_ID_RECENTLY_PLAY:
		{
			mCurrentSongs = mMusicTableHelper.queryAllHistory();
		}
		break;
		default:
			break;
		}
		
		mMusicAdapter.clear();
		if (mCurrentSongs != null && mCurrentSongs.size() > 0) {
			// 如果数据不为空，那么重新设置播放所有和管理按钮可点击
			mPlayAllBtn.setClickable(true);
			mMusicManageTv.setClickable(true);
			// 清空原有数据，重新设置数据
			mMusicAdapter.addItems(mCurrentSongs);
			//设置歌曲数量
			setMusicNum(mCurrentSongs.size());
			
		} else {
			// 如果数据为空，那么设置播放所有和管理按钮不可点击
			mPlayAllBtn.setClickable(false);
			mMusicManageTv.setClickable(false);
			//设置歌曲数量
			setMusicNum(0);
		}
	}

	@Override
	public void handleAction(int action) {
		if (action == OnOperationActionListener.ACTION_OPERATION_DELETE) {
			setMusicNum(mMusicAdapter.getCount());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final int realPosition = position - mMusicContentLv.getHeaderViewsCount();
		
//		Intent startPlayerIntent = new Intent(getActivity(), PlayerService.class);
//		// 为intent设置action
//		startPlayerIntent.setAction(Constants.ACTION_PLAYER_PLAY_ALL_PLAYLIST);
//		// 为Intent设置数据
//		startPlayerIntent.putExtra(Constants.KEY_PLAYER_DATA, data);
//		getActivity().startService(startPlayerIntent);
		
		// 构造临时播放列表
		if (isFirstPlay){
			
			isFirstPlay = false;
			
			Playlist playlist = new Playlist();
			playlist.setPlaylistId(-1);
			playlist.setPlaylistName("本地");
			if (mCurrentSongs != null) { 
				playlist.setCount(mCurrentSongs.size());
				playlist.setSongs(mCurrentSongs);
			}
			
			// 设置数据 
			Bundle data = new Bundle();
			data.putInt(Constants.KEY_PLAYER_POSITION, realPosition);
			data.putSerializable(Constants.KEY_PLAYER_PLAYLIST, playlist);
			
			CommonUtils.startPlayerService(getActivity(), 
					Constants.ACTION_PLAYER_PLAY_ALL_PLAYLIST,
					data);
			
		} else {
			// 设置数据 
			Bundle data = new Bundle();
			data.putInt(Constants.KEY_PLAYER_POSITION, realPosition);
			
			CommonUtils.startPlayerService(getActivity(), 
					Constants.ACTION_PLAYER_PLAY_FORM_POSITION,
					data);
		}
		
	}	
	
	@Override
	public void onClick(View v) {
		final int vid = v.getId();
		
		switch (vid) {
		case R.id.btn_nav_back:
		{
			// 关闭当前页面
			// 获取本Fragment附加到的Activity对象
			final Activity activity = getActivity();
			
			// 判断该activity是否是IActivityInteraction的一个实例
			if (activity instanceof IActivityInteraction) {
				// 将该Activity作为IActivityInteraction这个接口来使用
				final IActivityInteraction interaction = (IActivityInteraction)activity;
				// 调用出栈方法
				interaction.popBackStack(this.getClass().getName());
			}
			
		}
		break;
		case R.id.btn_nav_more:
		{
			// 更多菜单
			PopupMenu mMorePopupMenu = new PopupMenu(getContext(), mMoreBtn);
			// 设置点击事件
			mMorePopupMenu.setOnMenuItemClickListener(this);
			// 加载menu资源文件到popmenu中
			if (mCurrentPageId == Constants.PAGE_ID_LOCAL_MUSIC) {
				
				mMorePopupMenu.inflate(R.menu.local_music_more_menu);
				
			} else if (mCurrentPageId == Constants.PAGE_ID_FAVORTIE) {
				
				mMorePopupMenu.inflate(R.menu.favorite_more_menu);
				
			} else if (mCurrentPageId == Constants.PAGE_ID_RECENTLY_PLAY) {
				
				mMorePopupMenu.inflate(R.menu.history_more_menu);
				
			}

			mMorePopupMenu.show();
			
		}
		break;
		case R.id.panel_music_list_play_all:
		{
			// 播放整个列表
			Toast.makeText(getActivity(), "播放所有音乐", Toast.LENGTH_SHORT).show();
			if (isFirstPlay){
				
				isFirstPlay = false;
				
				Playlist playlist = new Playlist();
				playlist.setPlaylistId(-1);
				playlist.setPlaylistName("本地");
				if (mCurrentSongs != null) { 
					playlist.setCount(mCurrentSongs.size());
					playlist.setSongs(mCurrentSongs);
				}
				
				// 设置数据 
				Bundle data = new Bundle();
				data.putInt(Constants.KEY_PLAYER_POSITION, 0);
				data.putSerializable(Constants.KEY_PLAYER_PLAYLIST, playlist);
				
				CommonUtils.startPlayerService(getActivity(), 
						Constants.ACTION_PLAYER_PLAY_ALL_PLAYLIST,
						data);
				
			}
		}
		break;
		case R.id.tv_music_list_manage:
		{
			// 进入管理页面
			Intent intent = new Intent(getActivity(), CommonMusicManageAcitivity.class);
			intent.putExtra(Constants.FIELD_PAGE_ID, mCurrentPageId);
			startActivityForResult(intent, REQUEST_CODE_MANAGE);
		}
		break;
		default:
			break;
		}
		
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int itemId = item.getItemId();
		
		switch (itemId) {
		case R.id.item_scan:
		{
			// 本地音乐-一键扫描
			Intent intent = new Intent(getActivity(), MusicScannerActivity.class);
			startActivityForResult(intent, REQUEST_CODE_ONE_KEY_SCAN);
		}
		break;
		case R.id.item_synchronization:
		{
			// 我得最爱-同步
			// TODO Nothing to do.
		}
		break;
		case R.id.item_clear:
		{
			// 最近播放-清空
			
		}
		break;

		default:
			break;
		}
		
		return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 刷新页面数据
		ESLog.e("onActivityResult, requestCode " + requestCode 
				+ ", resultCode : " + resultCode);
		
		refreshData();
		
//		if (requestCode == REQUEST_CODE_ONE_KEY_SCAN) {
//		
//		} else if (requestCode == REQUEST_CODE_MANAGE) {
//		
//		}
		
	}
	
}
