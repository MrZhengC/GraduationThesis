package com.langchao.leo.esplayer.ui.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.langchao.leo.esplayer.R;

public class TimerDialog extends AlertDialog {

	private boolean mHasStarted;
	
	private SeekBar mSeekBar;
	
	private TextView mProgressNumberMax;
	private TextView mProgressNumberMin;
	//
	private TextView mCurrentProgress;
	
	private String mProgressNumberFormat = "%1d分";;
	private static String mDialogTitle = "定时关闭";
	
    private int mMax;
    private int mProgressVal;

	private Handler mViewUpdateHandler = null;
	
	private OnSeekBarChangerListenerImpl seekBarChangerListener = new OnSeekBarChangerListenerImpl();
	
	protected TimerDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	protected TimerDialog(Context context, int themeResId) {
		super(context, themeResId);
	}

	protected TimerDialog(Context context) {
		super(context);
	}

    public static TimerDialog getInstance(Context context) {
        return getInstance(context, false, null);
    }

    public static TimerDialog getInstance(Context context, boolean cancelable) {
        return getInstance(context, cancelable, null);
    }

    public static TimerDialog getInstance(Context context, boolean cancelable, OnCancelListener cancelListener) {
        TimerDialog dialog = new TimerDialog(context);
        dialog.setTitle(mDialogTitle);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        return dialog;
    }
    
	@SuppressLint({ "InflateParams", "HandlerLeak" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		
        /* Use a separate handler to update the text views as they
         * must be updated on the same thread that created them.
         */
        mViewUpdateHandler = new Handler() {
        	@Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
               
                /* Update the number and percent */
                int progress = mSeekBar.getProgress();
                int max = mSeekBar.getMax();
                if (mProgressNumberFormat != null) {
                    String format = mProgressNumberFormat;
                    mProgressNumberMin.setText(String.format(format, 0));
                    mProgressNumberMax.setText(String.format(format, max));
                    mCurrentProgress.setText(String.format(format, progress));
                } else {
                	mProgressNumberMin.setText("");
                    mProgressNumberMax.setText("");
                    mCurrentProgress.setText("");
                }
            }
        };
        
        View view = inflater.inflate(R.layout.layout_timer_selector, null);
        mSeekBar = (SeekBar) view.findViewById(R.id.selector);
        mSeekBar.setOnSeekBarChangeListener(seekBarChangerListener);
        mProgressNumberMin = (TextView) view.findViewById(R.id.selector_min);
        mProgressNumberMax = (TextView) view.findViewById(R.id.selector_max);
        mCurrentProgress = (TextView) view.findViewById(R.id.selector_note);
        setView(view);

        if (mMax > 0) {
            setMax(mMax);
        }
       
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }

        onProgressChanged();
        
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		mHasStarted = true;
		super.onStart();
	}

	@Override
	protected void onStop() {
		mHasStarted = false;
		super.onStop();
	}

	public TimerDialog showDialog(){
		super.show();
		return this;
	}
	
	public void setProgress(int value) {
        if (mHasStarted) {
            mSeekBar.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    public int getProgress() {
        if (mSeekBar != null) {
            return mSeekBar.getProgress();
        }
        return mProgressVal;
    }

    public int getMax() {
        if (mSeekBar != null) {
            return mSeekBar.getMax();
        }
        return mMax;
    }

    public TimerDialog setMax(int max) {
        if (mSeekBar != null) {
            mSeekBar.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
        return this;
    }
	
    public TimerDialog setPositiveButton(@StringRes int textId, final OnClickListener listener) {
        setButton(DialogInterface.BUTTON_POSITIVE, getContext().getText(textId), listener);
        return this;
    }

    public TimerDialog setPositiveButton(CharSequence text, final OnClickListener listener) {
    	setButton(DialogInterface.BUTTON_POSITIVE, text, listener);
        return this;
    }

    public TimerDialog setNegativeButton(@StringRes int textId, final OnClickListener listener) {
    	setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getText(textId), listener);
        return this;
    }

    public TimerDialog setNegativeButton(CharSequence text, final OnClickListener listener) {
    	setButton(DialogInterface.BUTTON_NEGATIVE, text, listener);
        return this;
    }
    
	private void onProgressChanged() {
        if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
            mViewUpdateHandler.sendEmptyMessage(0);
        }
    }
	
	class OnSeekBarChangerListenerImpl implements OnSeekBarChangeListener{
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
	            mViewUpdateHandler.sendEmptyMessage(0);
	        }
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mCurrentProgress.setVisibility(View.VISIBLE);
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	}
	
}
