package com.mycj.massage.util;

public class TimeUtil {
	public static String getStringTime(int second){
		StringBuffer sb = new StringBuffer();
		if (second<=60) {
			sb.append("00:")
				.append(getDouble(second));
		}else{
			int minute = second/60;
			sb.append(getDouble(minute))
				.append(":")
				.append(getDouble(second-minute*60));
		}
		return sb.toString();
	}
	
	public static int getIntTime(String time){
		String[] split = time.split(":");
		int minute = Integer.valueOf(split[0]);
		int second = Integer.valueOf(split[1]);
		return minute * 60 + second;
	}
	
	
	private static String getDouble(int value){
		if (value<10) {
			return "0"+value;
		}else{
			return String.valueOf(value);
		}
	}
}
