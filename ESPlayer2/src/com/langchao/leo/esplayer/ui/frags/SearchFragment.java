package com.langchao.leo.esplayer.ui.frags;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.db.MusicTableHelper;
import com.langchao.leo.esplayer.interfaces.IActivityInteraction;
import com.langchao.leo.esplayer.ui.adapter.CommonMusicAdapter;
import com.langchao.leo.esplayer.utils.ESLog;
import com.umeng.analytics.MobclickAgent;

/**
 * 搜索页面，现暂支持本地搜索
 * @author 碧空
 *
 */
public class SearchFragment extends Fragment 
	implements OnClickListener, OnItemClickListener{

	private TextView btnBack = null;
	private EditText mSearchEdt = null;
	private TextView mSearchBtn = null;

	private ListView mSearchResultLv = null;
	
	private CommonMusicAdapter mMusicAdapter = null;
	
	private MusicTableHelper mMusicTableHelper = null;
	
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
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.fragment_search, container, false);
		
		initUI(mRootView);
		
		mMusicTableHelper = new MusicTableHelper(getActivity());
		
		return mRootView;
	}
	
	public void initUI(View mRootView) {
		btnBack = (TextView) mRootView.findViewById(R.id.btn_nav_back);
		mSearchEdt = (EditText) mRootView.findViewById(R.id.edt_nav_search);
		mSearchBtn = (TextView) mRootView.findViewById(R.id.btn_nav_search);

		mSearchResultLv = (ListView) mRootView.findViewById(R.id.lv_search_music_content);
		mMusicAdapter = new CommonMusicAdapter(getActivity(), null);
		mSearchResultLv.setAdapter(mMusicAdapter);
		mSearchResultLv.setOnItemClickListener(this);
		
		btnBack.setText("");
		btnBack.setOnClickListener(this);
		mSearchBtn.setOnClickListener(this);
		
		mSearchEdt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				ESLog.i("on text changed");
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				ESLog.i("before text changed");
			}
			@Override
			public void afterTextChanged(Editable s) {
				ESLog.i("after text changed");
			}
		});
		
	}

	/**
	 * 搜索歌曲
	 */
	private void soSearch(String key){
		//mMusicTableHelper
	}
	
	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.btn_nav_back){
			// 从activity中移除自己
			((IActivityInteraction)getActivity()).popBackStack(SearchFragment.class.getName());
		} else if (id == R.id.btn_nav_search) {
			// 开始搜索
			
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}

}
