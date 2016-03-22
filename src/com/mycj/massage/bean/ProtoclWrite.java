package com.mycj.massage.bean;

import com.laputa.blue.util.XLog;
import com.mycj.massage.util.DataUtil;



public class ProtoclWrite {
	private static ProtoclWrite mProtoclWrite;
	
	public final static int TIME_10 = 10;
	public final static int TIME_15 = 15;
	private ProtoclWrite(){
		
	}
	public static  ProtoclWrite instance(){
		if (mProtoclWrite==null) {
			mProtoclWrite = new ProtoclWrite();
		}
		return mProtoclWrite;
	}
	
	/**
	 * @param onOff 00：停止；01：开始；55关机；
	 * @param model 0-4；
	 * @param power 1-11
	 * @param minute	10或者15
	 * @return byte[] 
	 */
	public byte[] protoclForStartAndEnd(int onOff,int model,int power,int minute){
		
//		if (onOff !=0 || onOff !=1 ||onOff !=55) {
//			XLog.e(ProtoclWrite.class,"命令不对");
//			return null;
//		}
//		
//		if (minute!=TIME_10 || minute !=TIME_15) {
//			XLog.e(ProtoclWrite.class,"时间不对");
//			return null;
//		}
//		
//		if (model < 0 || model >4 ) {
//			XLog.e(ProtoclWrite.class,"模式不对");
//			return null;
//		}
//		
//		if (power<1 || power>99) {
//			XLog.e(ProtoclWrite.class,"力度不对");
//			return null;
//		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("50");
		sb.append(DataUtil.toHexString(onOff));
		sb.append(DataUtil.toHexString(model));
		sb.append(DataUtil.toHexString(power));
		sb.append(DataUtil.toHexString(minute));
		XLog.e(ProtoclWrite.class,"协议："+sb.toString());
		
		return DataUtil.getBytesByString(sb.toString());
	}

	/**
	 * 力度切换
	 * @param power 0x01 ~ 0x11
	 * @return
	 */
	public byte[] protoclForChangePower(int power){
		if (power<1) {
			return null;
		}
		if (power>0x11) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("30");
		sb.append(DataUtil.toHexString(power));
		return DataUtil.getBytesByString(sb.toString());
	}

	/**
	 * 说明：手机每次回到功能界面，会向按摩器发送一次此协议用于请求当前按摩器的状态
	 * @return
	 */
	public byte[] protoclForState(){
		StringBuffer sb = new StringBuffer();
		sb.append("AA");
		return DataUtil.getBytesByString(sb.toString());
	}
	
}
