package com.langchao.leo.esplayer.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.langchao.leo.esplayer.bean.FMChannelBean;
import com.langchao.leo.esplayer.bean.FMChannelCategoryBean;
import com.langchao.leo.esplayer.interfaces.IAsyncLoadListener;

/**
 * 本地频道列表数据解析器
 * @author 碧空
 *
 */
public class FMChannelParser extends AsyncTask<Void, Void, List<FMChannelCategoryBean>> {

	private Context mContext = null;
	private IAsyncLoadListener<List<FMChannelCategoryBean>> mLoadListener = null;
	
	public FMChannelParser(Context context, 
			@Nullable IAsyncLoadListener<List<FMChannelCategoryBean>> listener) {
		
		this.mContext = context;
		
		this.mLoadListener = listener;
		
	}
	
	@Override
	protected List<FMChannelCategoryBean> doInBackground(Void... params) {
		
		InputStream is = null;
		try {
			is = mContext.getAssets().open("CHANNEL");
		} catch (IOException e) {
			e.printStackTrace();
			
			if (mLoadListener != null){
				mLoadListener.onFailure(e.toString());
			}
			
		}
		
		BufferedReader jsonReader = new BufferedReader(
				new InputStreamReader(is));
		
		// 通过Gson解析本地Json
		Gson gson = new Gson();
		ArrayList<FMChannelBean> channels = gson.fromJson(jsonReader, 
				new TypeToken<ArrayList<FMChannelBean>>(){}.getType());
		
		/////////////// 使用完一定要关闭！！！！！！！！！！！！！！
		try {
			is.close();
			jsonReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 构造目标对象
		List<FMChannelCategoryBean> categories = new ArrayList<FMChannelCategoryBean>();
		
		HashMap<String, List<FMChannelBean>> map = new HashMap<String, List<FMChannelBean>>();
		
		for (FMChannelBean channel : channels) {
			
			// 获取频道的分类名称
			String cate = channel.getCate();
			
			// 按照分类名称从map中获取列表
			List<FMChannelBean> fmcbs = map.get(cate);
			
			// 判断map中是否会有channel list 
			if (fmcbs == null) {
				fmcbs = new ArrayList<FMChannelBean>();
				map.put(cate, fmcbs);
			}
			
			fmcbs.add(channel);
			
		}
		
		Set<Entry<String, List<FMChannelBean>>> set = map.entrySet();
		Iterator<Entry<String, List<FMChannelBean>>> iterator = set.iterator();
		
		while (iterator.hasNext()) {
			Entry<String, List<FMChannelBean>> entry = iterator.next();
			
			String cate = entry.getKey();
			List<FMChannelBean> cs = entry.getValue();
			
			FMChannelCategoryBean category = new FMChannelCategoryBean();
			category.setCateName(cate);
			category.setChannelList(cs);
			
			categories.add(category);
			
		}
		
		return categories;
	}

	@Override
	protected void onCancelled() {
		if (mLoadListener != null){
			mLoadListener.onFailure("Task cancel.");
		}
		super.onCancelled();
	}

	/**
	 * 运行在主线程的 ！！！
	 */
	@Override
	protected void onPostExecute(List<FMChannelCategoryBean> result) {
		if (mLoadListener != null) {
			mLoadListener.onSuccess(result);
		}
		super.onPostExecute(result);
	}
	
}
