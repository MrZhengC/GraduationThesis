package com.langchao.leo.esplayer.ui.frags;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.bean.Playlist;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.db.PlaylistTableHelper;
import com.langchao.leo.esplayer.interfaces.IActivityInteraction;
import com.langchao.leo.esplayer.interfaces.OnOperationActionListener;
import com.langchao.leo.esplayer.ui.activities.AddMusicAcitivity;
import com.langchao.leo.esplayer.ui.activities.CommonMusicManageAcitivity;
import com.langchao.leo.esplayer.ui.adapter.CommonMusicAdapter;
import com.langchao.leo.esplayer.utils.CommonUtils;
import com.langchao.leo.esplayer.utils.DensityUtil;
import com.langchao.leo.esplayer.utils.ESLog;
import com.langchao.leo.esplayer.utils.FileUtils;
import com.langchao.leo.esplayer.utils.PhotoUtils;
import com.langchao.leo.esplayer.utils.ScreenUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 * 播放列表Fragment
 * @author 碧空
 *
 */
public class PlaylistFragment extends Fragment implements 
		OnClickListener, 
		OnItemClickListener,
		OnMenuItemClickListener, 
		OnOperationActionListener{

	private static final int REQUEST_CODE_ADD_MUSIC = 0x123;
	private static final int REQUEST_CODE_MUSIC_MANAGE = 0x124;

	private final int REQUEST_CODE_TAKE_PICTURE = 1001;// 拍照
	private final int REQUEST_CODE_PICK_PICTURE = 1002;// 相册选择
	private final int REQUEST_CODE_PHOTO_RESULT = 1003;// 结果
	
	private View mBackBtn = null;
	private View mMoreBtn = null;
	private TextView mPageTitleTv = null;
	
	private ListView mMusicContentLv = null;
	
	/**
	 * 播放所有按钮
	 */
	private View mPlayAllBtn = null;
	
	private TextView mMusicNumTv = null;
	
	private TextView mMusicManageTv = null;
	
	private ImageView mPlaylistCoverIv = null;
	
	private CommonMusicAdapter mMusicAdapter = null;
	
	private Playlist mCurrentPlaylist = null;
	
	private PlaylistTableHelper mPlaylistTableHelper = null;
	
	private PopupWindow mBottomPopupMenu;
	private View mActivityRootView;
	
	private View mTakePictureBtn;
	private View mPhotoAlbumBtn;
	private View mMenuCancelBtn;

	// 拍照图片名称
	private String photoName;
	
	// 是否是第一次播放该列表
	private boolean isFirstPlay = true;
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		Bundle args = getArguments();
		mCurrentPlaylist = (Playlist) args.getSerializable("playlist");
		super.onCreate(savedInstanceState);
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.fragment_playlist, container, false);
		
		initUI(mRootView);
		
		initData();
		
		return mRootView;
	}
	
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
	
	/**
	 * 初始化UI界面
	 * @param mRootView
	 */
	private void initUI(View mRootView) {
		
		initNavBar(mRootView);
		
		mMusicContentLv = (ListView) mRootView.findViewById(R.id.lv_music_content);
		
		mPlayAllBtn = mRootView.findViewById(R.id.panel_music_list_play_all);
		mPlayAllBtn.setOnClickListener(this);
		
		mMusicNumTv = (TextView) mRootView.findViewById(R.id.tv_music_list_num);
		
		mMusicManageTv = (TextView) mRootView.findViewById(R.id.tv_music_list_manage);
		mMusicManageTv.setOnClickListener(this);
		
		mPlaylistCoverIv = (ImageView) mRootView.findViewById(R.id.iv_playlist_cover);
		
		mMusicAdapter = new CommonMusicAdapter(getActivity(), this);
		mMusicContentLv.setAdapter(mMusicAdapter);
		mMusicContentLv.setOnItemClickListener(this);
		
		setMusicNum(mCurrentPlaylist.getCount());
		
		initPopupWindow();
		
		File cover = new File(mCurrentPlaylist.getCoverUrl()+"");
		ImageLoader.getInstance().displayImage(Uri.fromFile(cover).toString(), mPlaylistCoverIv);
		
	}

	/**
	 * 初始化PopupWindow
	 */
	@SuppressLint("InflateParams")
	private void initPopupWindow() {
		
		mActivityRootView = getActivity().findViewById(R.id.draglayout);
		
		View contentView = 
				LayoutInflater.from(getActivity()).inflate(R.layout.layout_crop_photo_menu, null);
		
		mBottomPopupMenu = new PopupWindow(contentView, 
				LayoutParams.MATCH_PARENT, 
				LayoutParams.WRAP_CONTENT, 
				true);
		mBottomPopupMenu.setBackgroundDrawable(new ColorDrawable());
		mBottomPopupMenu.setAnimationStyle(R.style.popwindow_anim_style);
		
		mTakePictureBtn = contentView.findViewById(R.id.btn_take_picture);
		mPhotoAlbumBtn = contentView.findViewById(R.id.btn_photo_album);
		mMenuCancelBtn = contentView.findViewById(R.id.btn_cancel);
		
		mTakePictureBtn.setOnClickListener(this);
		mPhotoAlbumBtn.setOnClickListener(this);
		mMenuCancelBtn.setOnClickListener(this);
		
	}

	private void initNavBar(View mRootView) {
		mBackBtn = mRootView.findViewById(R.id.btn_nav_back);
		mBackBtn.setOnClickListener(this);
		mPageTitleTv = (TextView) mRootView.findViewById(R.id.tv_nav_title);
		
		mMoreBtn = mRootView.findViewById(R.id.btn_nav_more);
		mMoreBtn.setVisibility(View.VISIBLE);
		mMoreBtn.setOnClickListener(this);

		mPageTitleTv.setText("" + mCurrentPlaylist.getPlaylistName());
	}

	/**
	 * 设置歌曲个数
	 * @param num
	 */
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
		mPlaylistTableHelper = new PlaylistTableHelper(getActivity());
		
		refreshData();
	}

	/**
	 * 刷新数据
	 */
	private void refreshData() {
		
		List<RealSong> songs = mPlaylistTableHelper.obtainAllSongs(mCurrentPlaylist);
		
		if (songs != null && songs.size() > 0) {
			// 如果数据不为空，那么重新设置播放所有和管理按钮可点击
			mPlayAllBtn.setClickable(true);
			mMusicManageTv.setClickable(true);
			// 清空原有数据，重新设置数据
			mMusicAdapter.clear();
			mMusicAdapter.addItems(songs);
			//设置歌曲数量
			setMusicNum(songs.size());
			
			// 将结果设置给当前播放列表
			mCurrentPlaylist.setCount(songs.size());
			mCurrentPlaylist.setSongs(songs);
			
		} else {
			// 如果数据为空，那么设置播放所有和管理按钮不可点击
			mPlayAllBtn.setClickable(false);
			mMusicManageTv.setClickable(false);
			//设置歌曲数量
			setMusicNum(0);

			// 将结果设置给当前播放列表
			mCurrentPlaylist.setCount(0);
			mCurrentPlaylist.setSongs(null);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		if (isFirstPlay) {
			isFirstPlay = false;
			
			Bundle data = new Bundle();
			data.putInt(Constants.KEY_PLAYER_POSITION, position);
			data.putSerializable(Constants.KEY_PLAYER_PLAYLIST, mCurrentPlaylist);
			CommonUtils.startPlayerService(
					getActivity(), 
					Constants.ACTION_PLAYER_PLAY_ALL_PLAYLIST, 
					data);
		}else{
			Bundle data = new Bundle();
			data.putInt(Constants.KEY_PLAYER_POSITION, position);
			CommonUtils.startPlayerService(
					getActivity(), 
					Constants.ACTION_PLAYER_PLAY_FORM_POSITION, 
					data);
		}
		
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
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
			mMorePopupMenu.inflate(R.menu.playlist_more_menu);
			mMorePopupMenu.show();
			
		}
		break;
		case R.id.panel_music_list_play_all:
		{
			// 播放整个列表
			Toast.makeText(getActivity(), "播放所有音乐", Toast.LENGTH_SHORT).show();
			
			if (isFirstPlay) {
				isFirstPlay = false;
				
				Bundle data = new Bundle();
				data.putInt(Constants.KEY_PLAYER_POSITION, 0);
				data.putSerializable(Constants.KEY_PLAYER_PLAYLIST, mCurrentPlaylist);
				CommonUtils.startPlayerService(
						getActivity(), 
						Constants.ACTION_PLAYER_PLAY_ALL_PLAYLIST, 
						data);
			}
			
		}
		break;
		case R.id.tv_music_list_manage:
		{
			// 进入管理页面
			Intent intent = new Intent(getActivity(), CommonMusicManageAcitivity.class);
			intent.putExtra(Constants.FIELD_PAGE_ID, Constants.PAGE_ID_PLAYLIST);
			intent.putExtra("playlist", mCurrentPlaylist);
			startActivityForResult(intent, REQUEST_CODE_MUSIC_MANAGE);
		}
		break;
		case R.id.btn_cancel:
			mBottomPopupMenu.dismiss();
			break;
		case R.id.btn_photo_album:
		{
			//使用Intent调用系统相册或者文件管理器 获取内容
			mBottomPopupMenu.dismiss();
			
			//构造输出文件路径
			photoName = PhotoUtils.createJPEGTempFileName();
			FileUtils.mkdirs(Constants.APP_COVER_IMAGE_FOLDER);
			File picture = new File(Constants.APP_COVER_IMAGE_FOLDER, photoName);
			
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
                    "image/*");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture));
			
			startActivityForResult(intent, REQUEST_CODE_PICK_PICTURE);
		}
		break;
		case R.id.btn_take_picture:
		{
			mBottomPopupMenu.dismiss();
			// 拍照

			//构造输出文件路径
			photoName = PhotoUtils.createJPEGTempFileName();
			FileUtils.mkdirs(Constants.APP_COVER_IMAGE_FOLDER);
			File picture = new File(Constants.APP_COVER_IMAGE_FOLDER, photoName);
			
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture));
			
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
		}
		break;
		default:
			break;
		}
		
	}

	@Override
	public void handleAction(int action) {
		if (action == OnOperationActionListener.ACTION_OPERATION_DELETE) {
			setMusicNum(mMusicAdapter.getCount());
		}
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int itemId = item.getItemId();
		
		switch (itemId) {
		case R.id.item_add_music:
		{
			// 添加音乐,进入添加音乐页面
			Intent intent = new Intent(getActivity(), AddMusicAcitivity.class);
			intent.putExtra("playlist", mCurrentPlaylist);
			startActivityForResult(intent, REQUEST_CODE_ADD_MUSIC);
			
		}
		break;
		case R.id.item_change_cover:
		{
			// 显示PopupWindown更换封面
			mBottomPopupMenu.showAtLocation(mActivityRootView, 
					Gravity.BOTTOM, 0, 0);
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
				+ ", resultCode : " + resultCode 
				+ ", intent : " + data);
		
		if (requestCode == REQUEST_CODE_ADD_MUSIC 
				|| requestCode == REQUEST_CODE_MUSIC_MANAGE) {
			refreshData();
		} 
		
		else if (requestCode == REQUEST_CODE_PICK_PICTURE) {
			if (resultCode == Activity.RESULT_OK) {

				// 构造裁剪输出路径
				photoName = PhotoUtils.createJPEGTempFileName();
				FileUtils.mkdirs(Constants.APP_COVER_IMAGE_FOLDER);
				File picture = new File(Constants.APP_COVER_IMAGE_FOLDER, photoName);
				
				if (Build.VERSION.SDK_INT < 19) {
					if (data != null) {
						// 获取源图片路径
						Uri uri = data.getData();
						startPhotoZoom(uri, Uri.fromFile(picture));
					}
				} else {
					ESLog.e("pick picture : " + data.getDataString());
					// 获取源图片路径
					Uri uri = data.getData();
					String thePath = PhotoUtils.getPath(getActivity(), uri);
					Uri resUri = Uri.fromFile(new File(thePath));
					startPhotoZoom(resUri, Uri.fromFile(picture));
				}
			}
		}else if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
			ESLog.e("Take a pictrue, result code : " + resultCode);
			if (resultCode == Activity.RESULT_OK) {
				File picture = new File(Constants.APP_COVER_IMAGE_FOLDER, photoName);
				
				if (picture.exists()) {
					Uri uri = Uri.fromFile(picture);
					startPhotoZoom(uri, uri);
				}
			}
			
		} else if (requestCode == REQUEST_CODE_PHOTO_RESULT) {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					File picture = new File(Constants.APP_COVER_IMAGE_FOLDER, photoName);
					// 展示在当前页面
					ImageLoader.getInstance()
						.displayImage(Uri.fromFile(picture).toString(), mPlaylistCoverIv);
					// 设置到当前播放列表对象中
					mCurrentPlaylist.setCoverUrl(picture.getAbsolutePath());
					// 插入数据库
					mPlaylistTableHelper.changePlaylistCover(mCurrentPlaylist);
				}
			}
		}
	}

	/** 
     * 裁剪图片方法实现 
     * @param uri 
     */ 
    public void startPhotoZoom(Uri uri, Uri output) { 
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页 
         * yourself_sdk_path/docs/reference/android/content/Intent.html 
         */ 
        Intent intent = new Intent("com.android.camera.action.CROP"); 
        intent.setDataAndType(uri, "image/*"); 
        
        int width = ScreenUtils.getScreenWidth(getActivity());
		int height = 0;
		if (mPlaylistCoverIv != null) {
			height = mPlaylistCoverIv.getHeight();
		} else {
			height = DensityUtil.dp2px(getActivity(), 240);
		}
		
		//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例 
		intent.putExtra("aspectX", width);
		intent.putExtra("aspectY", height);
		// outputX outputY 是裁剪图片宽高 
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", height);
		// 设置图片输出路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
    	intent.putExtra("outputFormat", "JPEG");// 图片格式
		intent.putExtra("noFaceDetection", "true");
        intent.putExtra("return-data", "true"); 
        
        startActivityForResult(intent, REQUEST_CODE_PHOTO_RESULT);
    }

}
