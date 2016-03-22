package com.langchao.leo.esplayer.ui.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.bean.Playlist;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 播放列表Adapter
 * @author 碧空
 *
 */
public class PlaylistAdapter extends BaseAdapter {

	private Context mContext = null;
	private LayoutInflater mInflater = null;
	
	private List<Playlist> mData = new ArrayList<Playlist>();
	
	private DisplayImageOptions options = null;
	
	public PlaylistAdapter(Context context) {
		this(context, null);
	}
	
	public PlaylistAdapter(Context context, @Nullable List<Playlist> data) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (data != null) {
			this.mData.addAll(data);
		}
		
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.bitmapConfig(Config.RGB_565)
		.showImageForEmptyUri(R.drawable.song_cover)
		.showImageOnFail(R.drawable.song_cover)
		.showImageOnLoading(R.drawable.song_cover)
		.build();
		
	}
	
	public void addItem(Playlist playlist){
		mData.add(playlist);
		notifyDataSetChanged();
	}
	
	public void addItems(List<Playlist> playlists){
		mData.addAll(playlists);
		notifyDataSetChanged();
	}
	
	public void clear() {
		mData.clear();
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Playlist getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// 声明ViewHolder对象
		ViewHolder holder = null;
		if (convertView == null) {
			// 加载布局文件
			convertView = mInflater.inflate(R.layout.item_playlist, parent, false);
			
			// 初始化ViewHolder
			holder = new ViewHolder();
			
			holder.mCoverIv = (ImageView) convertView.findViewById(R.id.iv_playlist_cover);
			holder.mNameTv = (TextView) convertView.findViewById(R.id.tv_playlist_name);
			holder.mNumTv = (TextView) convertView.findViewById(R.id.tv_num_of_playlist);
			
			// 将ViewHolder设置到convertView的Tag
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// 绑定数据到布局
		// 获取数据
		final Playlist playlist = getItem(position);
		// 设置图片
		String coverUrl = playlist.getCoverUrl();
		if (!TextUtils.isEmpty(coverUrl)) {
			ImageLoader.getInstance().displayImage(
					""+Uri.fromFile(new File(coverUrl)).toString(),
					holder.mCoverIv, options);
		}else{
			ImageLoader.getInstance().displayImage("", holder.mCoverIv, options);
		}
		
		holder.mNameTv.setText(playlist.getPlaylistName());
		holder.mNumTv.setText(
				String.format(mContext.getString(R.string.num_of_music), 
				playlist.getCount()));
		
		return convertView;
	}

	private class ViewHolder {
		ImageView mCoverIv = null;
		TextView mNameTv = null;
		TextView mNumTv = null;
	}
	
}
