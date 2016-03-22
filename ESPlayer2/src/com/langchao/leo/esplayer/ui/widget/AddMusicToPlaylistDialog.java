package com.langchao.leo.esplayer.ui.widget;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.bean.Playlist;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.db.PlaylistTableHelper;
import com.langchao.leo.esplayer.ui.adapter.CommonMusicManageAdapter;

/**
 * 添加音乐到播放列表对话框
 * @author 碧空
 *
 */
public class AddMusicToPlaylistDialog {

	private Context mContext = null;
	
	private CommonMusicManageAdapter mMusicAdapter = null;
	
	private AlertDialog mDialog = null;
	
	private PlaylistTableHelper mPlaylistTableHelper = null;
	
	private List<Playlist> mPlaylists = null;
	
	private int mSelectedIndex = -1;
	
	public AddMusicToPlaylistDialog(Context context, CommonMusicManageAdapter adapter) {
		mContext = context;
		mMusicAdapter = adapter;
		mPlaylistTableHelper = new PlaylistTableHelper(mContext);
		
		initDialog();
		
	}

	/**
	 * 初始化对话框
	 */
	private void initDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("添加到播放列表");
		builder.setPositiveButton(mContext.getString(R.string.sure), 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						if (mSelectedIndex >= 0) {
							final List<RealSong> songs = mMusicAdapter.getSelectedItems();
							final Playlist playlist = mPlaylists.get(mSelectedIndex);
							
							long result = 
									mPlaylistTableHelper.insertMusicToPlaylistIfNotExist(playlist, songs);
							
							Toast.makeText(mContext, 
									"选择" + songs.size() + "首歌曲，添加成功" + result + "首", 
									Toast.LENGTH_SHORT).show();
						}
						
						mMusicAdapter.cancelAllItems();
						
					}
				});
		builder.setNegativeButton(mContext.getString(R.string.cancel), 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mMusicAdapter.cancelAllItems();
					}
				});
		
		mPlaylists = mPlaylistTableHelper.queryAllPlaylists();
		
		if (mPlaylists != null && mPlaylists.size() > 0) {
			int size = mPlaylists.size();
			
			String[] playlistNames = new String[size];
			
			for (int i = 0; i < size; i++) {
				playlistNames[i] = mPlaylists.get(i).getPlaylistName();
			}
			
			builder.setSingleChoiceItems(playlistNames, -1, 
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mSelectedIndex = which;
						}
					});
		} else {
			builder.setMessage("您还没有新建播放列表，赶紧去新建一个吧");
		}
		
		mDialog = builder.create();
		
	}
	
	public void show () {
		if (mDialog != null && !mDialog.isShowing()) {
			mDialog.show();
		}
	}
	
	public boolean isShowing() {
		if (mDialog!= null) {
			return mDialog.isShowing();
		}
		return false;
	}
	
	public void dismiss() {
		if (isShowing()) {
			mDialog.dismiss();
		}
	}
	
}
