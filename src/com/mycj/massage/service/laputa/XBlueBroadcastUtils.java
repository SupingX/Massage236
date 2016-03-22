package com.mycj.massage.service.laputa;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.mycj.massage.bean.MassageInfo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class XBlueBroadcastUtils {
	public static final String ACTION_DO_NOT_SUPPORT_BLE = "ACTION_DO_NOT_SUPPORT_BLE";
	public static final String ACTION_BLUETOOTH_ADAPTER_DISABLE = "ACTION_BLUETOOTH_ADAPTER_DISABLE";
	public static final String ACTION_DEVICE_FOUND = "ACTION_DEVICE_FOUND";
	public static final String ACTION_SERVICE_DISCOVERED = "ACTION_SERVICE_DISCOVERED";
	public static final String ACTION_CONNECTED = "ACTION_CONNECTED";
	public static final String ACTION_DISCONNECTED = "ACTION_DISCONNECTED";
	public static final String ACTION_FU_ZAI_CHANGED = "ACTION_FU_ZAI_CHANGED";
	public static final String ACTION_POWER_CHANGED = "ACTION_POWER_CHANGED";
	public static final String ACTION_MASSAGE_CHANGED = "ACTION_MASSAGE_CHANGED";
	public static final String ACTION_TIME_CHANGED = "ACTION_TIME_CHANGED";
	
	public static final String EXTRA_DEVICES = "EXTRA_DEVICES";
	public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
	public static final String EXTRA_ADDRESS = "EXTRA_ADDRESS";
	public static final String EXTRA_FU_ZAI = "EXTRA_FU_ZAI";
	public static final String EXTRA_POWER = "EXTRA_POWER";
	public static final String EXTRA_MASSAGE = "EXTRA_MASSAGE";
	public static final String EXTRA_TIME = "EXTRA_TIME";
	
	private static XBlueBroadcastUtils mBroadcastUtils;
	private XBlueBroadcastUtils(){
		
	}
	public static XBlueBroadcastUtils instance(){
		if (mBroadcastUtils ==null) {
			mBroadcastUtils = new XBlueBroadcastUtils();
		}
		return mBroadcastUtils;
	}
	
	public IntentFilter getIntentFilter(){
		IntentFilter f = new IntentFilter();
		f.addAction(ACTION_DEVICE_FOUND);
		f.addAction(ACTION_SERVICE_DISCOVERED);
		f.addAction(ACTION_CONNECTED);
		f.addAction(ACTION_DISCONNECTED);
		f.addAction(ACTION_FU_ZAI_CHANGED);
		f.addAction(ACTION_POWER_CHANGED);
		f.addAction(ACTION_MASSAGE_CHANGED);
		f.addAction(ACTION_TIME_CHANGED);
		return f;
	}
	
	public void sendBroadcastDoNotSupportBle(Context context){
		Intent intent = new Intent(ACTION_DO_NOT_SUPPORT_BLE);
		context.sendBroadcast(intent);
	}
	
	public void sendBroadcastBluetoothAdapterDisable(Context context){
		Intent intent = new Intent(ACTION_BLUETOOTH_ADAPTER_DISABLE);
		context.sendBroadcast(intent);
	}
	
	public void sendBroadcastDeviceFound(Context context,ArrayList<BluetoothDevice> devices){
		Intent intent = new Intent(ACTION_DEVICE_FOUND);
		intent.putExtra(EXTRA_DEVICES, devices);
		context.sendBroadcast(intent);
	}
	
	public void sendBroadcastServiceDiscovered(Context context,BluetoothDevice device){
		Intent intent = new Intent(ACTION_SERVICE_DISCOVERED);
		intent.putExtra(EXTRA_DEVICE, device);
		context.sendBroadcast(intent);
	}
	
	public void sendBroadcastConnected(Context context,BluetoothDevice device){
		Intent intent = new Intent(ACTION_CONNECTED);
		intent.putExtra(EXTRA_DEVICE, device);
		context.sendBroadcast(intent);
	}
	public void sendBroadcastDisConnected(Context context,BluetoothDevice device){
		Intent intent = new Intent(ACTION_DISCONNECTED);
		intent.putExtra(EXTRA_DEVICE, device);
		context.sendBroadcast(intent);
	}
	public void sendBroadcastFuzaiChanged(Context context,BluetoothDevice device,int fuzai){
		Intent intent = new Intent(ACTION_FU_ZAI_CHANGED);
		intent.putExtra(EXTRA_FU_ZAI, fuzai);
		intent.putExtra(EXTRA_DEVICE, device);
		context.sendBroadcast(intent);
	}
	
	public void sendBroadcastFuzaiChanged(Context context,String address,int fuzai){
		Intent intent = new Intent(ACTION_FU_ZAI_CHANGED);
		intent.putExtra(EXTRA_FU_ZAI, fuzai);
		intent.putExtra(EXTRA_ADDRESS, address);
		context.sendBroadcast(intent);
	}
	
	
	public void sendBroadcastPowerChanged(Context context,String address,int power){
		Intent intent = new Intent(ACTION_POWER_CHANGED);
		intent.putExtra(EXTRA_POWER, power);
		intent.putExtra(EXTRA_ADDRESS, address);
		context.sendBroadcast(intent);
	}
	
	public void sendBroadcastMassageChanged(Context context,String address,MassageInfo info){
		Intent intent = new Intent(ACTION_MASSAGE_CHANGED);
		intent.putExtra(EXTRA_MASSAGE, info);
		intent.putExtra(EXTRA_ADDRESS, address);
		context.sendBroadcast(intent);
	}
	
	public void sendBroadcastTimeChanged(Context context,String address,int  time){
		Intent intent = new Intent(ACTION_TIME_CHANGED);
		intent.putExtra(EXTRA_TIME, time);
		intent.putExtra(EXTRA_ADDRESS, address);
		context.sendBroadcast(intent);
	}
	
}
