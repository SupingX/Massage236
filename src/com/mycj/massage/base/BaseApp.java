package com.mycj.massage.base;

import com.mycj.massage.service.laputa.BlueService;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class BaseApp extends Application {

	public BlueService getXBlueService() {
		return this.xBlueService;
	}

	private BlueService xBlueService;
	private ServiceConnection xBlueConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			xBlueService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			BlueService.XBlueBinder xplBinder = (BlueService.XBlueBinder) service;
			xBlueService = (BlueService) xplBinder.getXBlueService();
			Log.e("", "==xplBluetoothService :" + xBlueService);
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Intent xplIntent = new Intent(this, BlueService.class);
		bindService(xplIntent, xBlueConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onTerminate() {
		xBlueService.closeAll();
		unbindService(xBlueConnection);
		super.onTerminate();
	}
	/*
	 * private SimpleBlueService simpleBlueService; public SimpleBlueService
	 * getSimpleBlueService(){ return this.simpleBlueService; } private
	 * ServiceConnection conn = new ServiceConnection() {
	 * 
	 * @Override public void onServiceDisconnected(ComponentName name) {
	 * 
	 * }
	 * 
	 * @Override public void onServiceConnected(ComponentName name, IBinder
	 * service) { SimpleBlueService.MyBinder binder =
	 * (SimpleBlueService.MyBinder )service; simpleBlueService =
	 * binder.getSimpleBlueService(); XLog.e("BaseApp", "application获取服务 ："+
	 * simpleBlueService); } };
	 * 
	 * 
	 * @Override public void onCreate() { super.onCreate(); Intent intent = new
	 * Intent(this,SimpleBlueService.class); bindService(intent, conn,
	 * Context.BIND_AUTO_CREATE);
	 * 
	 * 
	 * }
	 * 
	 * 
	 * @Override public void onTerminate() { super.onTerminate();
	 * if(simpleBlueService != null){
	 * 
	 * } unbindService(conn); }
	 */

}
