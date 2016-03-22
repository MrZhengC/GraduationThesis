package com.langchao.leo.esplayer.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.db.MusicTableHelper;
import com.langchao.leo.esplayer.interfaces.OnOperationActionListener;
import com.langchao.leo.esplayer.ui.widget.DeleteMusicDialog;
import com.langchao.leo.esplayer.utils.CommonUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 一般歌曲列表Adapter
 * @author 碧空
 *
 */
public class CommonMusicAdapter extends ESBaseAdapter<RealSong> {

	private DisplayImageOptions options = null;
	
	private OnOperationActionListener mListener = null;
	
	private MusicTableHelper mMusicTableHelper = null;
	
	public CommonMusicAdapter(Context context, OnOperationActionListener listener) {
		super(context);
		
		mListener = listener;
		mMusicTableHelper = new MusicTableHelper(context);
		
		options = new DisplayImageOptions.Builder()
						.cacheInMemory(true)
						.cacheOnDisk(true)
						.bitmapConfig(Config.RGB_565)
						.showImageForEmptyUri(R.drawable.song_cover)
						.showImageOnFail(R.drawable.song_cover)
						.showImageOnLoading(R.drawable.song_cover)
						.build();
	}

	@Override
	public View createView(LayoutInflater inflater, final int position, View convertView,
			ViewGroup parent) {
		
		final ViewHolder holder;
		if (convertView == null){
			convertView = inflater.inflate(R.layout.item_common_music, parent, false);
			
			holder = new ViewHolder();
			holder.mCoverIv = (ImageView) convertView.findViewById(R.id.iv_music_cover);
			holder.mNameTv = (TextView) convertView.findViewById(R.id.tv_music_name);
			holder.mArtistTv = (TextView) convertView.findViewById(R.id.tv_music_artist);
			holder.mMoreOptionIv = (ImageView) convertView.findViewById(R.id.iv_music_more_option);
			
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 绑定数据
		final RealSong song = getItem(position);
		
		ImageLoader.getInstance().displayImage(
				CommonUtils.getUri(song), 
				holder.mCoverIv, 
				options);
		
		String artist = song.getArtist().trim();
		if (TextUtils.isEmpty(artist)){
			artist = "未知艺术家";
		}
		
		holder.mNameTv.setText(song.getSongName());
		holder.mArtistTv.setText(""+artist);
		holder.mMoreOptionIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 更多操作
				PopupMenu popMenu = new PopupMenu(getContext(), holder.mMoreOptionIv);
				popMenu.inflate(R.menu.common_music_more_menu2);
				popMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						int itemId = item.getItemId();
						if (itemId == R.id.item_music_delete) {
							// 删除
							DeleteMusicDialog.showDialog(getContext(), song, 
									new OnOperationActionListener() {
								@Override
								public void handleAction(int action) {
									
									removeItem(position);
									
									if (mListener != null) {
										mListener.handleAction(action);
									}
								}
							});
							
						} else if (itemId == R.id.item_music_like) {
							// 收藏
							if (mMusicTableHelper.changeFavoriteTime(
									song, 
									System.currentTimeMillis())){
								// 开启收藏动画
								
							}
							
						}
						return false;
					}
				});
				popMenu.show();
			}
		});
		
		return convertView;
	}

	private class ViewHolder {
		ImageView mCoverIv = null;
		TextView mNameTv = null;
		TextView mArtistTv = null;
		ImageView mMoreOptionIv = null;
	}
	
}
