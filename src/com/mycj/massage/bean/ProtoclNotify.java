package com.mycj.massage.bean;



public class ProtoclNotify {
	private static ProtoclNotify mProtoclNotify;
	private ProtoclNotify(){
		
	}
	public static  ProtoclNotify instance(){
		if (mProtoclNotify==null) {
			mProtoclNotify = new ProtoclNotify();
		}
		return mProtoclNotify;
	}
	
	public int getDataTypeByData(byte[] data){
		if ( data==null ) {
			return -1;
		}
		return data[0];
	}
	
	public static final int TYPE_DIAN_LIANG = 0x10;
	public static final int TYPE_FU_ZAI = 0x20;
	public static final int TYPE_MASSAGE = 0x50;
	public static final int TYPE_POWER = 0x30;
	
	/**
	 * 返回按摩信息
	 * 0x01	0x00 ~ 0x01	0x00 ~ 0x06	0x01 ~ 0x0F	0x01 ~ 0x0F	0x00 ~ 0x3B
	 * @param data
	 */
	public MassageInfo notifyMassageInfo(byte[] data){
		if (data.length!=6) {
			return null ;
		}
		if (data[0] != TYPE_MASSAGE) {
			return null ;
		}
		int onOff = data[1];
		int model = data[2];
		int power = data[3];
		int setTime = data[4];
		int leftTime = data[5];
		MassageInfo info = new MassageInfo(onOff, power, model, setTime*60);
		return info;
	}
	
	/**
	 * 
	 * @param data
	 * @return 0-3
	 */
	public int notifyDianLiang(byte[] data){
		if (data.length!=2) {
			return -1 ;
		}
		if (data[0] != TYPE_DIAN_LIANG) {
			return -1 ;
		}
		return data[1];
	}
	
	/**
	 * 0 无负载
	 * 1 有负载
	 * @param data
	 * @return
	 */
	public int notifyFuzai(byte[] data){
		if (data.length!=2) {
			return -1 ;
		}
		if (data[0] != TYPE_FU_ZAI) {
			return -1 ;
		}
		return data[1];
	}
	
	/**
	 * 按摩结束时的力度
	 * @param data
	 * @return
	 */
	public int notifyPower(byte[] data){
		if (data.length!=2) {
			return -1 ;
		}
		if (data[0] != TYPE_POWER) {
			return -1 ;
		}
		return data[1];
	}
	
//	/**
//	 * 模式切换时
//	 * @param data
//	 * @return
//	 */
//	public int notifyModelChanged(byte[] data){
//		if (data.length!=2) {
//			return -1;
//		}
//		
//		if (data[0] != 0x02) {
//			return -1;
//		}
//		
//		return data[1] ;
//	}
//	/**
//	 * 力度切换时
//	 * @param data
//	 * @return
//	 */
//	public int notifyPowerChanged(byte[] data){
//		if (data.length!=2) {
//			return -1;
//		}
//		
//		if (data[0] != 0x03) {
//			return -1;
//		}
//		
//		return data[1] ;
//	}
//	
//	/**
//	 * 时间切换时
//	 * @param data
//	 * @return
//	 */
//	public int[] notifyTimeChanged(byte[] data){
//		if (data.length!=3) {
//			return null;
//		}
//		
//		if (data[0] != 0x04) {
//			return null;
//		}
//		
//		return new int[]{data[1],data[2]} ;
//	}
//	
//	/**
//	 * 心率切换时
//	 * @param data
//	 * @return
//	 */
//	public int notifyHeartRateChanged(byte[] data){
//		if (data.length!=2) {
//			return -1;
//		}
//		if (data[0] != 0x06) {
//			return -1;
//		}
//		return data[1] ;
//	}
	
}
