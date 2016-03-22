package com.langchao.leo.esplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.bean.FMChannelBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChannelAdapter extends BaseAdapter {

	private Context mContext = null;
	
	private LayoutInflater mInflater = null;
	
	private List<FMChannelBean> mData = new ArrayList<FMChannelBean>();

	//
	private DisplayImageOptions options = null;
	
	public ChannelAdapter(Context context, List<FMChannelBean> data) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mData = data;
		
		options = new DisplayImageOptions.Builder()
						.bitmapConfig(Config.RGB_565)//设置压缩方式
						.cacheInMemory(true)// 打开内存缓存
						.cacheOnDisk(true)// 打开磁盘缓存
						.showImageForEmptyUri(R.drawable.ic_launcher)//设置URI为空时显示的图片
						.showImageOnFail(R.drawable.ic_shuffle)//设置下载失败时显示的图片
						.showImageOnLoading(R.drawable.song_cover)// 设置加载时显示的图片
						.build();
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
			convertView = mInflater.inflate(R.layout.item_radio_channel, parent, false);
			
			holder = new ViewHolder();
			holder.mChannelCoverIv = (ImageView) convertView.findViewById(R.id.iv_radio_channel_cover);
			holder.mChannelNameTv = (TextView) convertView.findViewById(R.id.tv_radio_channel_name);
			
			convertView.setTag(holder);
			
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// 绑定数据
		final FMChannelBean channel = (FMChannelBean) getItem(position);
		holder.mChannelNameTv.setText(channel.getChannel_name());
		
		ImageLoader.getInstance().displayImage(
				channel.getCoverUrl(), 
				holder.mChannelCoverIv, 
				options);
		
		return convertView;
	}
	
	private class ViewHolder {
		ImageView mChannelCoverIv = null;
		TextView mChannelNameTv = null;
	}

}
