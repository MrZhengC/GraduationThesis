package com.langchao.leo.esplayer.ui.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.bean.Playlist;
import com.langchao.leo.esplayer.interfaces.OnItemSelectChangeListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 公共音乐管理Adapter
 * @author 碧空
 *
 */
public class PlaylistManageAdapter extends ESBaseAdapter<Playlist> {

	private DisplayImageOptions options = null;
	
	private List<Boolean> mSelectedItems = null;
	
	private OnItemSelectChangeListener mSelectChangeListener = null;
	
	/**
	 * 选中的条目个数
	 */
	private int mSelectedCount = 0;

	public PlaylistManageAdapter(Context context, OnItemSelectChangeListener listener) {
		super(context);
		
		mSelectedItems = new ArrayList<Boolean>();
		
		mSelectChangeListener = listener;
		
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
	public void addItem(Playlist item) {
		mSelectedItems.add(false);
		super.addItem(item);
	}

	@Override
	public void addItems(List<Playlist> items) {
		for (int i = 0; i < items.size(); i++) {
			mSelectedItems.add(false);
		}
		super.addItems(items);
	}
	
	@Override
	public void addItem(Playlist item, int position) {
		if (position >= 0 && position < getCount()) {
			mSelectedItems.add(position, false);
		}
		super.addItem(item, position);
	}
	@Override
	public void clear(){
		mSelectedItems.clear();
		super.clear();
	}
	
	@Override
	public void removeItem(Playlist item) {
		int index = mData.indexOf(item);
		mSelectedItems.remove(index);
		super.removeItem(item);
	}

	@Override
	public void removeItem(int position) {
		if (position>=0 && position < getCount() ) {
			mSelectedItems.remove(position);
		}
		super.removeItem(position);
	}
	
	/**
	 * 选择条目以更改选中状态
	 * @param position
	 */
	public void selectItem(int position){
		if (position >= 0 && position < getCount()) {
			mSelectedItems.set(position, !mSelectedItems.get(position));

			if (mSelectedItems.get(position)) {
				mSelectedCount += 1;
			} else {
				mSelectedCount -= 1;
			}
			
			// 回调
			if (mSelectChangeListener != null) {
				mSelectChangeListener.onItemSelectChanage(mSelectedCount);
			}
			
			notifyDataSetChanged();
		}
	}
	
	/**
	 * 全选
	 */
	public void selectAllItems() {
		for (int i = 0; i < mSelectedItems.size(); i++) {
			mSelectedItems.set(i, true);
		}
		
		// 选中的条目为整个数据的大小
		mSelectedCount = mData.size();
		
		// 回调
		if (mSelectChangeListener != null) {
			mSelectChangeListener.onItemSelectChanage(mSelectedCount);
		}
		
		notifyDataSetChanged();
	}

	/**
	 * 取消所有选中
	 */
	public void cancelAllItems(){
		for (int i = 0; i < mSelectedItems.size(); i++) {
			mSelectedItems.set(i, false);
		}
		
		mSelectedCount = 0;
		
		// 回调
		if (mSelectChangeListener != null) {
			mSelectChangeListener.onItemSelectChanage(mSelectedCount);
		}
		
		notifyDataSetChanged();
	}
	
	/**
	 * 获取被选中的数据
	 * @return
	 */
	public List<Playlist> getSelectedItems(){
		List<Playlist> playlists = new ArrayList<Playlist>();
		
		int size = getCount();
		for (int i = 0; i < size; i++) {
			// 判断该条目是否被选中
			if (mSelectedItems.get(i)) {
				// 添加数据
				playlists.add(getItem(i));
			}
		}
		
		return playlists;
	}
	
	@Override
	public View createView(LayoutInflater inflater, final int position,
			View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_common_music_manage, parent, false);
			
			holder = new ViewHolder();
			holder.mPlaylistCoverIv = (ImageView) convertView.findViewById(R.id.iv_music_cover);
			holder.mPlaylistNameTv = (TextView) convertView.findViewById(R.id.tv_music_name);
			holder.mMusicNumTv = (TextView) convertView.findViewById(R.id.tv_music_artist);
			holder.mSeletorCb = (CheckBox) convertView.findViewById(R.id.cb_music_selector);
			
			convertView.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Playlist playlist = getItem(position);
		// 设置图片
		String coverUrl = playlist.getCoverUrl();
		if (!TextUtils.isEmpty(coverUrl)) {
			ImageLoader.getInstance().displayImage(
					""+Uri.fromFile(new File(coverUrl)).toString(),
					holder.mPlaylistCoverIv, options);
		}else{
			ImageLoader.getInstance().displayImage("", holder.mPlaylistCoverIv, options);
		}
		
		holder.mPlaylistNameTv.setText(""+playlist.getPlaylistName());
		holder.mMusicNumTv.setText(String.format(getContext().getResources().getString(R.string.num_of_music), playlist.getCount()));
		
		// 设置选中状态
		holder.mSeletorCb.setChecked(mSelectedItems.get(position));
		holder.mSeletorCb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击选中
				selectItem(position);
			}
		});
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView mPlaylistCoverIv = null;
		TextView mPlaylistNameTv = null;
		TextView mMusicNumTv = null;
		CheckBox mSeletorCb = null;
	}
	
}
