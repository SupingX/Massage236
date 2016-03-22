package com.mycj.massage.bean;

import com.mycj.massage.R;


public enum Ems {
	VIB(R.string.ems_1,R.drawable.ic_ems1,0),
	VIB_EMS(R.string.ems_2,R.drawable.ic_ems2,1),
	EMS1(R.string.ems_3,R.drawable.ic_ems3,2),
	EMS2(R.string.ems_4,R.drawable.ic_ems4,3),
	EMS3(R.string.ems_5,R.drawable.ic_ems5,4);
	private int text;
	private int img;
	private int model;
	private Ems(int text,int img,int model){
		this.text = text;
		this.img = img;
		this.model = model;
	}
	public int getText(){
		return this.text;
	}
	
	public int getImg(){
		return this.img;
	}
	
	public int getModel(){
		return this.model;
	}
}
