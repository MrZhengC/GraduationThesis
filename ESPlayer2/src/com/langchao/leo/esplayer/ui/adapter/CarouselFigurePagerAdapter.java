package com.langchao.leo.esplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.bean.FMChannelBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.graphics.Bitmap.Config;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CarouselFigurePagerAdapter extends PagerAdapter {

	// 轮播图View
	private List<View> mPagerViews = new ArrayList<View>();
	
	private DisplayImageOptions options = null;
	
	public CarouselFigurePagerAdapter(List<View> mPagerViews) {
		this.mPagerViews = mPagerViews;
		
		options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.bitmapConfig(Config.RGB_565)
					.showImageOnLoading(R.drawable.song_cover)
					.showImageOnFail(R.drawable.song_cover)
					.build();
		
	}
	
	public void setData(List<View> mPagerViews) {
		this.mPagerViews = mPagerViews;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mPagerViews == null ? 0 : mPagerViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		
		if (mPagerViews != null) {
			container.removeView(mPagerViews.get(position));
		}
		
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View itemView = null;
		if (mPagerViews != null) {
			itemView = mPagerViews.get(position);
			
			final FMChannelBean channel = (FMChannelBean) itemView.getTag();
			if (channel != null) {
				final TextView name = ((TextView)itemView.findViewById(R.id.tv_hot_channel_name));
				name.setText("" + channel.getChannel_name());
				final ImageView cover = ((ImageView)itemView.findViewById(R.id.iv_hot_channel_cover));
				ImageLoader.getInstance().displayImage(channel.getCoverUrl(), cover, options);
			}

			container.addView(itemView);
		}
		return itemView;
	}
	
}
