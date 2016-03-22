package com.langchao.leo.esplayer.ui.widget;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.bean.RealSong;
import com.langchao.leo.esplayer.db.MusicTableHelper;
import com.langchao.leo.esplayer.interfaces.OnOperationActionListener;
import com.langchao.leo.esplayer.utils.FileUtils;

/**
 * 删除文件对话框
 * @author 碧空
 *
 */
public class DeleteMusicDialog extends AlertDialog {

	private static boolean isCheckedDeleteLocalFile = false;
	
	protected DeleteMusicDialog(Context context) {
		super(context);
	}
	
	public static DeleteMusicDialog showDialog(
			final Context context, 
			final RealSong song, 
			final OnOperationActionListener listener){
		
		DeleteMusicDialog dialog = new DeleteMusicDialog(context);
		dialog.setTitle("删除歌曲");
		dialog.setMessage("您真的要删除  歌曲吗");
		dialog.setButton(BUTTON_NEGATIVE, 
				context.getString(R.string.cancel), 
				new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
		});
		dialog.setButton(BUTTON_POSITIVE, 
				context.getString(R.string.sure),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MusicTableHelper musicTableHelper = new MusicTableHelper(context);
						if (musicTableHelper.delete(song)){
							// 删除成功，回调
							if (isCheckedDeleteLocalFile) {
								// 删除本地
								FileUtils.delete(new File(""+song.getLocalPath()));
							}
							
							if (listener != null) {
								listener.handleAction(OnOperationActionListener.ACTION_OPERATION_DELETE);
							}
						}
					}
		});
		dialog.show();
		return dialog;
	}

	@Override
	@SuppressLint("InflateParams")
	protected void onCreate(Bundle savedInstanceState) {
		
		View customView = LayoutInflater.from(getContext())
				.inflate(R.layout.layout_delete_music_dialog, null);
		
		CheckBox selector = (CheckBox) customView.findViewById(R.id.cb_delete_local_file);
		selector.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isCheckedDeleteLocalFile = isChecked;
			}
		});
		
		setView(customView);
		
		super.onCreate(savedInstanceState);
	}
	
}
