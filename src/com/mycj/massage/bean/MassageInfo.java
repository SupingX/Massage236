package com.mycj.massage.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MassageInfo implements Parcelable{
	private int onOff; // 0:关；1:开
	private int model; // 0-4
	private int power; // 1-99
	private int time;  // 10或者15
	
	public int getOnOff() {
		return onOff;
	}
	public void setOnOff(int onOff) {
		this.onOff = onOff;
	}
	public int getModel() {
		return model;
	}
	public void setModel(int model) {
		this.model = model;
	}
	public int getPower() {
		return power;
	}
	public void setPower(int power) {
		this.power = power;
	}
	
	@Override
	public String toString() {
		return "MassageInfo [onOff=" + onOff + ", model=" + model + ", power=" + power + ", time=" + time + "]";
	}
	public MassageInfo() {
		super();
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.onOff);
		dest.writeInt(this.model);
		dest.writeInt(this.power);
		dest.writeInt(this.time);
	}
	
	public MassageInfo(int onOff, int model, int power, int time) {
		super();
		this.onOff = onOff;
		this.model = model;
		this.power = power;
		this.time = time;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}

	public static final Parcelable.Creator<MassageInfo> CREATOR = new Creator<MassageInfo>() {
		
		@Override
		public MassageInfo[] newArray(int size) {
			return null;
		}
		
		@Override
		public MassageInfo createFromParcel(Parcel source) {
			int onOff = source.readInt();
			int model = source.readInt();
			int power = source.readInt();
			int time = source.readInt();
			return new MassageInfo(onOff, model, power, time);
		}
	};
	
}
