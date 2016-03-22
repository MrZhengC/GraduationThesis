package com.langchao.leo.esplayer.utils;

/**
 * 
 * @author 碧空
 *
 */
public class TimerUtil {

	private static String DefaultHourFormat = "%1$02d:%2$02d %3$02d";
	
	private static String DefaultMinuteFormat = "%1$02d:%2$02d";
	// 默认60分钟
	private static final int SECOND_SIXTY_MINUTES = 60*60;
	// 默认12小时
	private static final int SECOND_TWELVE_HOURS = 24*3600;
	
	/**
	 * 
	 * @param sec 秒数
	 * @return
	 */
	public static String formatTimeMinute(int sec) throws UnsupportedOperationException{
		if (sec < 0 || sec > SECOND_SIXTY_MINUTES){
			return String.format(DefaultMinuteFormat, 0, 0);
		}
		
		int minute, second;
		
		String formattedTime = "";
		
		minute = (sec / 60) % 60;
		second = sec % 60;
		formattedTime = String.format(DefaultMinuteFormat, minute, second);
		
		return formattedTime;
	}
	
	/**
	 * 
	 * @param sec 秒数
	 * @return
	 */
	public static String formatTimeHour(int sec) throws UnsupportedOperationException{
		if (sec < 0 || sec > SECOND_TWELVE_HOURS){
			return String.format(DefaultHourFormat, 0, 0, 0);
		}
		
		int hour, minute, second;
		
		String formattedTime = "";
		
		hour = sec / 3600;
		minute = (sec / 60) % 60;
		second = sec % 60;
		formattedTime = String.format(DefaultHourFormat, hour, minute, second);
		
		return formattedTime;
	}
	
}
