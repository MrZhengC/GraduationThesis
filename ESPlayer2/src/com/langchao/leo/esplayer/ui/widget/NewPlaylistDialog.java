package com.langchao.leo.esplayer.ui.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.bean.Playlist;
import com.langchao.leo.esplayer.db.PlaylistTableHelper;
import com.langchao.leo.esplayer.interfaces.IAsyncLoadListener;

/**
 * 新建播放列表对话框
 * @author 碧空
 */
public class NewPlaylistDialog extends AlertDialog {

	private static String mNewPlaylistName = "";
	
	private static boolean isShouldClose = false;
	
	protected NewPlaylistDialog(Context context) {
		super(context);
	}
	
	public static NewPlaylistDialog showDialog(final Context context, 
			@Nullable final IAsyncLoadListener<Void> listener){
		NewPlaylistDialog dialog = new NewPlaylistDialog(context);
		
		// 设置标题
		dialog.setTitle(context.getString(R.string.new_playlist));
		dialog.setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mNewPlaylistName = null;
				isShouldClose = true;
				dialog.dismiss();
			}
		});
		dialog.setButton(BUTTON_POSITIVE, context.getString(R.string.sure), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 处理新建播放列表的逻辑 
				if (TextUtils.isEmpty(mNewPlaylistName) 
						|| TextUtils.isEmpty(mNewPlaylistName.trim())) {
					Toast.makeText(context, "播放列表不能为空", Toast.LENGTH_SHORT).show();
				} else {
					// 数据库操作
					PlaylistTableHelper playlistTableHelper = new PlaylistTableHelper(context);

					// 判断该播放列表名称是否存在
					if (playlistTableHelper.isPlaylistExist(mNewPlaylistName)) {
						Toast.makeText(context, "该名称已经存在", Toast.LENGTH_SHORT).show();
					} else {
						// 新建播放列表对象
						Playlist playlilst = new Playlist();
						playlilst.setPlaylistName(mNewPlaylistName);
						
						if (playlistTableHelper.insert(playlilst)){
							
							// 成功回调
							if (listener != null) {
								listener.onSuccess(null);
							}
							
							isShouldClose = true;
							dialog.dismiss();
						} else {
							Toast.makeText(context, "新建失败", Toast.LENGTH_SHORT).show();
						}
					}
				}
				mNewPlaylistName = null;
			}
		});
		dialog.show();
		return dialog;
	}
	
	@Override
	public void dismiss() {
		if (isShouldClose) {
			isShouldClose = false;
			super.dismiss();
		}
	}

	@Override
	@SuppressLint("InflateParams")
	protected void onCreate(Bundle savedInstanceState) {
		// 加载自定义布局
		View customView = LayoutInflater.from(getContext())
				.inflate(R.layout.layout_new_playlist_edittext, null);
		
		EditText editText = (EditText) customView.findViewById(R.id.edt_new_playlist_name);
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				mNewPlaylistName = s.toString();
			}
		});
		// 把自定义View设置到dialog中
		setView(customView);
		super.onCreate(savedInstanceState);
	}
	
}
