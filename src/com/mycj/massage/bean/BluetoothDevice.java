package com.mycj.massage.bean;

public class BluetoothDevice {
	private String name ;
	private String address;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public BluetoothDevice() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BluetoothDevice(String name, String address) {
		super();
		this.name = name;
		this.address = address;
	}
	
}
