package com.langchao.leo.esplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.app.AppConfig;
import com.langchao.leo.esplayer.bean.FMChannelBean;
import com.langchao.leo.esplayer.bean.FMChannelCategoryBean;
import com.langchao.leo.esplayer.bean.FMChannelEntity;
import com.langchao.leo.esplayer.bean.FMChannelEntity.Song;
import com.langchao.leo.esplayer.bean.Playlist;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.contstants.Constants;
import com.langchao.leo.esplayer.core.WebSongDataEngine;
import com.langchao.leo.esplayer.interfaces.IAsyncLoadListener;
import com.langchao.leo.esplayer.ui.widget.NestedGridView;
import com.langchao.leo.esplayer.utils.CommonUtils;
import com.langchao.leo.esplayer.utils.ESLog;
import com.langchao.leo.esplayer.utils.NetworkUtils;

/**
 * 电台频道分类adapter
 * @author 碧空
 *
 */
public class ChannelCategoryAdapter extends BaseAdapter {

	private Context mContext = null;
	
	private LayoutInflater mLayoutInflater = null;
	
	private List<FMChannelCategoryBean> mData = new ArrayList<FMChannelCategoryBean>();
	
	public ChannelCategoryAdapter(Context context) {
		this.mContext = context;
		this.mLayoutInflater = LayoutInflater.from(mContext);
	}
	
	public void addItem(FMChannelCategoryBean category){
		mData.add(category);
		notifyDataSetChanged();
	}
	
	public void addItems(List<FMChannelCategoryBean> categories){
		mData.addAll(categories);
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
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_radio_category, parent, false);
			
			holder = new ViewHolder();
			
			holder.mCategoryNameTv = (TextView) convertView.findViewById(R.id.tv_radio_category_name);
			holder.mChannelListNgv = (NestedGridView) convertView.findViewById(R.id.ngv_channel_list);
			
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// 设置数据
		final FMChannelCategoryBean category = (FMChannelCategoryBean) getItem(position);
		
		holder.mCategoryNameTv.setText(category.getCateName());
		
		holder.mChannelListNgv.setAdapter(new ChannelAdapter(mContext, 
												category.getChannelList()));
		holder.mChannelListNgv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// 处理点击事件

				// 首先判断当前网络状况是否可用（only_wifi是否设置？当前是否为wifi联网？） 
				if (!NetworkUtils.isConnectInternet(mContext)) {
					Toast.makeText(mContext, "未连接互联网，请检查连接后重试..", Toast.LENGTH_SHORT).show();
					return;
				} else {
					// 获取only_wifi是否被设置
					boolean isOnlyWifi = AppConfig.getInstance(mContext).isOnlyWifi();
					// only_wifi==true 且 没有连接wifi网络的时候，返回，不联网获取歌曲
					if (isOnlyWifi && !NetworkUtils.isConnectWifi(mContext)) {
						Toast.makeText(mContext, "当前没有使用wifi联网，可能会产生流量费用..", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				
				// 获取当前本地频道信息，然后根据id获取该频道的歌曲列表
				// 成功则构造Playlist播放
				// 否则给提示
				
				final FMChannelBean channel = category.getChannelList().get(position);
				
				ESLog.i("Tag", "onClick, category " + category.getCateName() 
						+ ", channel : " + channel.getChannel_name());
				
				WebSongDataEngine.getInstance(mContext)
					.getWebChannelById(
							channel.getChannel_id(), new IAsyncLoadListener<FMChannelEntity>() {
								@Override
								public void onSuccess(FMChannelEntity t) {
									// 组装Playlist,构造临时播放列表
									Playlist playlist = new Playlist();
									playlist.setPlaylistId(-1);
									playlist.setPlaylistName(""+t.getChannel_name());
									
									// 添加歌曲
									List<RealSong> realSongs = new ArrayList<RealSong>();
									List<Song> webSongs = t.getList();
									if (webSongs != null){
										for (Song ws : webSongs) {
											RealSong song =  new RealSong();
											try {
												song.setSongId(Long.valueOf(ws.getId()));
											} catch (Exception e) {
												song.setSongId(-1);
											}
											song.setSource(1);///!!!!!!
											realSongs.add(song);
										}
									}
									playlist.setCount(realSongs.size());
									playlist.setSongs(realSongs);
									
									// 设置数据 
									Bundle data = new Bundle();
									data.putInt(Constants.KEY_PLAYER_POSITION, position);
									data.putSerializable(Constants.KEY_PLAYER_PLAYLIST, playlist);
									
									CommonUtils.startPlayerService(mContext, 
											Constants.ACTION_PLAYER_PLAY_ALL_PLAYLIST,
											data);
								}
								
								@Override
								public void onFailure(String msg) {
								}
							});
			}
		});
		
		return convertView;
	}

	private class ViewHolder {
		TextView mCategoryNameTv = null;
		NestedGridView mChannelListNgv = null;
	}
	
	
	
}
