package com.langchao.leo.esplayer.ui.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;

import com.langchao.leo.esplayer.R;
import com.langchao.leo.esplayer.app.ESApplication;

public class ExitAppDialog extends AlertDialog {

	private static CustomCountDownTimer mCustomCountDownTimer = null;
	
	protected ExitAppDialog(Context context, boolean hasTimer) {
		super(context);
		if (hasTimer) {
			mCustomCountDownTimer = new CustomCountDownTimer(60*1000, 1000);
		}
	}

	public static ExitAppDialog showDialog(Context context) {
		return showDialog(context, false);
	} 
	
	public static ExitAppDialog showDialog(Context context, boolean hasTimer) {
		final ExitAppDialog mDialogInstance = new ExitAppDialog(context, hasTimer);
		mDialogInstance.setCancelable(true);
		mDialogInstance.setTitle(R.string.exit_app);
		if (hasTimer) {
			mDialogInstance.setMessage(String.format(context.getString(R.string.exit_app_note_timer), 60));
		}else {
			mDialogInstance.setMessage(context.getString(R.string.exit_app_note));
		}
		mDialogInstance.setButton(DialogInterface.BUTTON_NEGATIVE, 
				context.getString(R.string.cancel), 
				new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {}
		});
		mDialogInstance.setButton(
				DialogInterface.BUTTON_POSITIVE, 
				context.getString(R.string.sure), 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDialogInstance.dismiss();
						ESApplication.getInstance().AppExit();
					}
				});
		if (hasTimer && mCustomCountDownTimer != null) {
			mCustomCountDownTimer.start();
		}
		mDialogInstance.show();
		return mDialogInstance;
	} 
	
	/**
	 * 定时关闭
	 * @author 碧空
	 *
	 */
	private class CustomCountDownTimer extends CountDownTimer {

		public CustomCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		@SuppressLint("SimpleDateFormat")
		public void onTick(long millisUntilFinished) {
			int second = (int) (millisUntilFinished/1000);
			setMessage(String.format(getContext().getString(R.string.exit_app_note_timer), second));
		}

		@Override
		public void onFinish() {
			dismiss();
			mCustomCountDownTimer.cancel();
			mCustomCountDownTimer = null;
			ESApplication.getInstance().AppExit();
		}
	}
	
}
