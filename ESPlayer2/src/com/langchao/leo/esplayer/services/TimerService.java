package com.langchao.leo.esplayer.services;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * 定时service
 * @author 碧空
 *
 */
public class TimerService extends Service {

	public final static String KEY_DURATION = "duration";
	
	private IBinder timerBinder = new TimerBinder();
	
	public class TimerBinder extends Binder{
		public TimerService getService(){
			return TimerService.this;
		}
	} 
	
	private OnPulseListener mOnPulseListener = null;
	
	public interface OnPulseListener{
		void onPulse(long total, long progress);
	}
	
	public OnPulseListener getOnPulseListener() {
		return mOnPulseListener;
	}

	public void setOnPulseListener(OnPulseListener mOnPulseListener) {
		this.mOnPulseListener = mOnPulseListener;
	}

	private Timer timer = null;
	
	private TimerTask timerTask = null; 
	
	private long mTaskDuration;
	
	@Override
	public IBinder onBind(Intent intent) {
		return timerBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			mTaskDuration = intent.getLongExtra(KEY_DURATION, 0);
			
			if (timer != null) {
				timer.cancel();
			}
			
			if (mTaskDuration > 0) {
				timer = new Timer();
				
				timerTask = new RealTimerTask(mTaskDuration);
				timer.schedule(timerTask, 0, 1000);
			}
		}
		
		return START_STICKY;
	}
	
	class RealTimerTask extends TimerTask{
		
		private long total;
		private long progress;
		
		public RealTimerTask(long total){
			this.total = total;
		}
		
		public long getMax(){
			return total;
		}
		
		public long getProgress(){
			return progress;
		}
		
		@Override
		public void run() {
			if (total <= ++progress) {
				timer.cancel();
				timer = null;
				timerTask = null;
				return;
			}
			
			if (mTaskDuration > 0 && mOnPulseListener != null){
				mOnPulseListener.onPulse(total, progress);
			}
		}
	}
	
}
