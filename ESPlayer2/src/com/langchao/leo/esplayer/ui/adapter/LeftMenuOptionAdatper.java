package com.langchao.leo.esplayer.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;

public class LeftMenuOptionAdatper extends BaseAdapter {

	private Context mContext = null;

	// 存放我得option数据
	private String[] mData = null;
	
	private TextView mCountDownTv = null;
	
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat mDefaultDateFormat = new SimpleDateFormat("mm:ss");
	
	public LeftMenuOptionAdatper(Context context) {
		this.mContext = context;
		mData = context.getResources().getStringArray(R.array.option_array);
	}
	
	@Override
	public int getCount() {
		return mData.length;
	}

	@Override
	public String getItem(int position) {
		return mData[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			// 获取新布局
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.item_left_menu_option, parent, false);
			
			// 新建ViewHolder
			viewHolder = new ViewHolder();
			
			viewHolder.mOptionName = (TextView) convertView.findViewById(R.id.tv_option_name);
			viewHolder.mOptionRemark = (TextView) convertView.findViewById(R.id.tv_option_remark);
			
			// 将ViewHolder给设置到convertView中去，
			convertView.setTag(viewHolder);
			
		} else {
			// 从convertView中获取到第一次加载时设置的ViewHolder
			viewHolder = (ViewHolder) convertView.getTag();
			
		}
		
		if (position == 0) {
			mCountDownTv = viewHolder.mOptionRemark;
		}
		
		// 给每一个条目设置数据
		final String optionName = getItem(position);
		viewHolder.mOptionName.setText(optionName);
		
		return convertView;
	}
	
	/**
	 * 更新倒计时显示时间
	 * @param millis
	 * @param firstIndex
	 */
	public void updateTimer(long millis, int firstIndex){
		if (mCountDownTv != null ){
			if(millis > 0 && firstIndex == 0) {
				mCountDownTv.setVisibility(View.VISIBLE);
				Date date = new Date(millis);
				String time = mDefaultDateFormat.format(date);
				mCountDownTv.setText("" + time);
			}else{
				mCountDownTv.setVisibility(View.GONE);
			}
		}
	}

	private class ViewHolder {
		TextView mOptionName;
		TextView mOptionRemark;
	}
	
}
