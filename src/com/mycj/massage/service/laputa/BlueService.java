package com.mycj.massage.service.laputa;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.laputa.blue.core.AbstractSimpleLaputaBlue;
import com.laputa.blue.core.Configration;
import com.laputa.blue.core.OnBlueChangedListener;
import com.laputa.blue.core.SimpleLaputaBlue;
import com.laputa.blue.util.BondedDeviceUtil;
import com.laputa.blue.util.XLog;
import com.mycj.massage.bean.Ems;
import com.mycj.massage.bean.History;
import com.mycj.massage.bean.LitPalManager;
import com.mycj.massage.bean.MassageInfo;
import com.mycj.massage.bean.ProtoclNotify;
import com.mycj.massage.bean.ProtoclWrite;
import com.mycj.massage.util.DateUtil;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class BlueService extends Service {
	private static final String SDF = "yyyyMMdd hh:mm:ss";
//	private XBlueManager xplBlueManager;
	/**
	 * 是否同步？
	 */
	private boolean isTogeter = false;
	/**
	 *  当前模式
	 */
	private int currentModel = -1; 
	/**
	 * 按摩信息A
	 */
	private MassageInfo currentMassageInfoForA;
	/**
	 * 按摩信息B
	 */
	private MassageInfo currentMassageInfoForB;
	
	

	private Handler xHandler = new Handler() {
	};

	private void holdLock(){
		   PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
           wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
           wakeLock.acquire(); 
	}
	
	public AbstractSimpleLaputaBlue getSimpleLaputaBlue(){
		return this.simpleLaputaBlue;
	}
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				int state = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE);
				int preState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_PREVIOUS_STATE);
				String stateInfo = "之前的状态：" + preState;
				if (state == BluetoothAdapter.STATE_OFF) {
					stateInfo += "\n 关闭 :" + state;
					simpleLaputaBlue.closeAll();
				} else if (state == BluetoothAdapter.STATE_ON) {
					stateInfo += "\n 打开 :" + state;
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							simpleLaputaBlue.initAdapter();
						}
					}, 500);
				} else if (state == BluetoothAdapter.STATE_TURNING_OFF) {
					stateInfo += "\n 关闭中 :" + state;
				} else if (state == BluetoothAdapter.STATE_TURNING_ON) {
					stateInfo += "\n 打开中 :" + state;
				
				}
				XLog.e("ACTION_STATE_CHANGED  -->  adapter状态 " + stateInfo);

			} 
		}
	};
	@Override
	public void onCreate() {
		super.onCreate();
		holdLock();
		registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
		simpleLaputaBlue = new SimpleLaputaBlue(this, new Configration(), new OnBlueChangedListener() {
			
			@Override
			public void onServiceDiscovered(String address) {
				if (isTogeter) {
					if (address.equals(BondedDeviceUtil.get(1, getApplicationContext()))) {
						if (currentMassageInfoForA !=null && currentMassageInfoForA.getOnOff() == 1) {
							//
						} else {
//							stopMassageForA();
						}
					}
					if (address.equals(BondedDeviceUtil.get(2, getApplicationContext()))) {
						if (currentMassageInfoForA !=null  && currentMassageInfoForA.getOnOff() == 1) {
							//
						} else {
//							stopMassageForB();
						}
					}
					printMassageInfo();
				} else {
					if (address.equals(BondedDeviceUtil.get(1, getApplicationContext()))) {
						if (currentMassageInfoForA!=null && currentMassageInfoForA.getOnOff() == 1) {
							//
						} else {
//							stopMassageForA();
						}
					}
					if (address.equals(BondedDeviceUtil.get(1, getApplicationContext()))) {
						if (currentMassageInfoForB !=null && currentMassageInfoForB.getOnOff() == 1) {
							//
						} else {
//							stopMassageForB();
						}
					}
					printMassageInfo();
				}
			}
			
			@Override
			public void onCharacteristicChanged(String address, byte[] value) {
				ProtoclNotify notify = ProtoclNotify.instance();
				int type = notify.getDataTypeByData(value);
				if (type != -1) {
					switch (type) {
					case ProtoclNotify.TYPE_FU_ZAI:
						int fuzai = notify.notifyFuzai(value);
						if (fuzai != -1) {
							XBlueBroadcastUtils.instance().sendBroadcastFuzaiChanged(getApplicationContext(), address, fuzai);
						}
						break;
					case ProtoclNotify.TYPE_POWER:
						int power = notify.notifyPower(value);
						if (power != -1) {
							XBlueBroadcastUtils.instance().sendBroadcastPowerChanged(getApplicationContext(), address, power);
						}
						break;
					case ProtoclNotify.TYPE_MASSAGE:
						// 下位机每隔一秒更新按摩信息
						MassageInfo notifyMassageInfo = notify.notifyMassageInfo(value);
						if (notifyMassageInfo != null) {
							XBlueBroadcastUtils.instance().sendBroadcastMassageChanged(getApplicationContext(), address, notifyMassageInfo);
							if (address.equals(address.equals(BondedDeviceUtil.get(1, getApplicationContext())))) {
								currentMassageInfoForA = notifyMassageInfo;
							}else if (address.equals(BondedDeviceUtil.get(2, getApplicationContext()))) {
								currentMassageInfoForB = notifyMassageInfo;
							}
						}
						break;
					default:
						break;
					}
			}
		}});
		
	}

	public void setIsTogether(boolean isTogeter) {
		this.isTogeter = isTogeter;
	}

	public boolean getIsTogether() {
		return this.isTogeter;
	}

	public void setCurrentMassagerInfoForA(MassageInfo info) {
		this.currentMassageInfoForA = info;
	}

	public void setCurrentMassagerInfoForB(MassageInfo info) {
		this.currentMassageInfoForB = info;
	}

	public MassageInfo getCurrentMassageInfoForA() {
		return this.currentMassageInfoForA;
	}

	public MassageInfo getCurrentMassageInfoForB() {
		return this.currentMassageInfoForB;
	}

	public void startScan() {
		simpleLaputaBlue.scanDevice(true);
	}

	public void startOnlyScan() {
		simpleLaputaBlue.scanDevice(false);
	}

	/*public void writeAll(byte[] data) {
//		xplBlueManager.writeChacteristicAll(new byte[][] { data });
//		simpleLaputaBlue.write(address, values);
	}*/

	public void write(String address, byte[] data) {
//		xplBlueManager.writeChacteristic(address, data);
		simpleLaputaBlue.write(address, data);
	}

	public void writeA(byte[] data) {
		String address =BondedDeviceUtil.get(1, getApplicationContext());
//		xplBlueManager.writeChacteristic(address, data);
		simpleLaputaBlue.write(address, data);
	}

	public void writeB(byte[] data) {
		String address =BondedDeviceUtil.get(2, getApplicationContext());
//		xplBlueManager.writeChacteristic(address, data);
		simpleLaputaBlue.write(address, data);
	}

	public void stopScan() {
//		xplBlueManager.stopScan();
		simpleLaputaBlue.scanDevice(false);
	}



	public boolean isConnected(String address) {
//		return xplBlueManager.isConnected(address);
		return simpleLaputaBlue.isConnected(address);

	}
	public boolean isAllConnected() {
//		 return xplBlueManager.isAllConnected();
		
		
		return isAConnected() && isBConnected();
//		return true;
	}
	
	public String getAddressA (){
		String address =BondedDeviceUtil.get(1, getApplicationContext());
		return address;
	}
	
	public String getAddressB(){
		return BondedDeviceUtil.get(2, getApplicationContext());
	}
	public boolean isAConnected() {
		String address = getAddressA();
//		 return xplBlueManager.isConnected(address);
//		return true;
		boolean connected = simpleLaputaBlue.isConnected(address);
		XLog.e(BlueService.class,"isAConnected() " + connected);
		return connected;
	}

	public boolean isBConnected() {
		String address = getAddressB();
//		 return xplBlueManager.isConnected(address);
//		return true;
		boolean connected = simpleLaputaBlue.isConnected(address);
		XLog.e(BlueService.class,"isBConnected() " + connected);
		return connected;
	}

	public void connect(BluetoothDevice device) {
//		xplBlueManager.connect(device);
		simpleLaputaBlue.connect(device);
	}

	public void connect(String address) {
//		xplBlueManager.connect(address);
		simpleLaputaBlue.connect(address);
		;
	}

	public void close(String address) {
//		xplBlueManager.close(address);
		simpleLaputaBlue.close(address);
	}

	public void closeAll() {
//		xplBlueManager.stopScan();
//		xplBlueManager.closeAll();
		
		simpleLaputaBlue.closeAll();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return new XBlueBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	public class XBlueBinder extends Binder  {
		public BlueService getXBlueService() {
			return BlueService.this;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		closeAll();
		  if (wakeLock != null) {
              wakeLock.release();
              wakeLock = null;
          }
		isTogeter = false;
		unregisterReceiver(receiver);
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0xFA:
				if (currentMassageInfoForA != null) {
					int timePoint = currentMassageInfoForA.getTime();
					// Log.e("", "===========================================");
					// Log.e("", "=== 	当前剩余按摩时间： " + timePoint);
					// Log.e("", "===========================================");
					timePoint--;
					if (timePoint <= 0) {
						
						String date = DateUtil.dateToString(new Date(), SDF);
						History history = new History(date, currentMassageInfoForA.getPower(), currentMassageInfoForA.getModel());
						saveHistory(history);
						
						stopMassageForA();
						timePoint = 0;
						XBlueBroadcastUtils.instance().sendBroadcastTimeChanged(getApplicationContext(), getAddressA(), timePoint);
					} else {
						currentMassageInfoForA.setTime(timePoint);
						XBlueBroadcastUtils.instance().sendBroadcastTimeChanged(getApplicationContext(), getAddressA(), timePoint);
						mHandler.postDelayed(runTimerForA, 1 * 1000);
					}
				}
				break;
			case 0xFB:
				if (currentMassageInfoForB != null) {
					int timePoint = currentMassageInfoForB.getTime();
					// Log.e("", "===========================================");
					// Log.e("", "=== 	当前剩余按摩时间： " + timePoint);
					// Log.e("", "===========================================");
					timePoint--;
					Log.i("xpl", "timePoint : " + timePoint );
					String addressB = getAddressB();
					if (timePoint <= 0) {
						Log.i("xpl", "timePoint : 结束了！！！！"  );
						String date = DateUtil.dateToString(new Date(), SDF);
						History history = new History(date, currentMassageInfoForB.getPower(), currentMassageInfoForB.getModel());
						saveHistory(history);
						stopMassageForB();
						timePoint = 0;
						XBlueBroadcastUtils.instance().sendBroadcastTimeChanged(getApplicationContext(), addressB, timePoint);
					} else {
						currentMassageInfoForB.setTime(timePoint);
						XBlueBroadcastUtils.instance().sendBroadcastTimeChanged(getApplicationContext(), addressB, timePoint);
						mHandler.postDelayed(runTimerForB, 1 * 1000);
					}
				}
				break;
			case 0xFC:
				if (currentMassageInfoForA != null) {
					int timePoint = currentMassageInfoForA.getTime();
					// Log.e("", "===========================================");
					// Log.e("", "=== 	当前剩余按摩时间： " + timePoint);
					// Log.e("", "===========================================");
					timePoint--;
					if (timePoint <= 0) {
						String date = DateUtil.dateToString(new Date(), SDF);
						History history = new History(date, currentMassageInfoForA.getPower(), currentMassageInfoForA.getModel());
						saveHistory(history);
						stopMassageForAll();
						timePoint = 0;
						XBlueBroadcastUtils.instance().sendBroadcastTimeChanged(getApplicationContext(), "all", timePoint);
					} else {
						currentMassageInfoForA.setTime(timePoint);
						currentMassageInfoForB.setTime(timePoint);
						XBlueBroadcastUtils.instance().sendBroadcastTimeChanged(getApplicationContext(), "all", timePoint);
						mHandler.postDelayed(runTimerForAll, 1 * 1000);
					}
				}
				break;

			default:
				break;
			}
		}

	
	};

	
	public MassageInfo getStartMassage(int model){
		int onOff = 1;
		int power = 1;
		int time = ProtoclWrite.TIME_15 * 60;
		if (model == 0) {
			time = ProtoclWrite.TIME_10 * 60;
		}
		return new MassageInfo(onOff, model, power, time);
	}
	
	/**
	 * 从一个模式进入，点击开始按摩键：
	 * 		时间--固定10/15
	 * 		力度不能调节--所以为1
	 * @param model
	 */
	public void startMassageForA(int model) {
		currentModel = model;
		currentMassageInfoForA = getStartMassage(model);
		int onOff = currentMassageInfoForA.getOnOff();
		int power = currentMassageInfoForA.getPower();
		int time = ProtoclWrite.TIME_15 * 60;
		if (model == 0) {
			time = ProtoclWrite.TIME_10 * 60;
		}
		byte[] protoclForStartAndEnd = ProtoclWrite.instance()
				.protoclForStartAndEnd(onOff, model, power, time);
		writeA(protoclForStartAndEnd);
		
		
		// 开启计时
		startTimerForA();
	}

	public void stopMassageForA() {
		byte[] protoclForStartAndEnd = ProtoclWrite.instance().protoclForStartAndEnd(0, 0, 1, ProtoclWrite.TIME_15 * 60);
		writeA(protoclForStartAndEnd);
		currentMassageInfoForA = null;
		if (currentMassageInfoForB == null) {
			currentModel = -1;
		}
		
		// 关闭计时
		stopTimerForA();
	}

	public void startMassageForB(int model) {
		currentModel = model;
		currentMassageInfoForB = getStartMassage(model);
		int onOff = currentMassageInfoForB.getOnOff();
		int power = currentMassageInfoForB.getPower();
		int time = ProtoclWrite.TIME_15 * 60;
		if (model == 0) {
			time = ProtoclWrite.TIME_10 * 60;
		}
		byte[] protoclForStartAndEnd = ProtoclWrite.instance()
				.protoclForStartAndEnd(onOff, model, power, time);
		writeB(protoclForStartAndEnd);
		startTimerForB();
	}

	public void stopMassageForB() {
		byte[] protoclForStartAndEnd = ProtoclWrite.instance().protoclForStartAndEnd(0, 0, 1, ProtoclWrite.TIME_15 * 60);
		writeA(protoclForStartAndEnd);
		currentMassageInfoForB = null;
		if (currentMassageInfoForA == null) {
			currentModel = -1;
		}
		stopTimerForB();
	}

	public void startMassageForAll(int model) {
		startMassageForA(model);
		startMassageForB(model);
		startTimerForAll();
	}

	public void stopMassageForAll() {
		stopMassageForA();
		stopMassageForB();
		stopTimerForAll();
	}

	private Runnable runTimerForA = new Runnable() {
		public void run() {
			Message message = new Message();
			message.what = 0xFA;
			mHandler.sendMessage(message);
		}
	};
	private Runnable runTimerForB = new Runnable() {
		public void run() {
			Message message = new Message();
			message.what = 0xFB;
			mHandler.sendMessage(message);
		}
	};

	private Runnable runTimerForAll = new Runnable() {
		public void run() {
			Message message = new Message();
			message.what = 0xFC;
			mHandler.sendMessage(message);
		}
	};
	private WakeLock wakeLock;
	private AbstractSimpleLaputaBlue simpleLaputaBlue;

	public void startTimerForA() {
		stopTimerForA();
		mHandler.postDelayed(runTimerForA, 1 * 1000);

	}

	public void stopTimerForA() {
		mHandler.removeCallbacks(runTimerForA);
		// mHandler.removeCallbacks(runTimerForAll);
		mHandler.removeMessages(0xFA);
		// mHandler.removeMessages(0xFC);
	}

	public void startTimerForB() {
		stopTimerForB();
		mHandler.postDelayed(runTimerForB, 1 * 1000);

	}

	public void stopTimerForB() {
		mHandler.removeCallbacks(runTimerForB);
		// mHandler.removeCallbacks(runTimerForAll);
		// mHandler.removeMessages(0xFC);
		mHandler.removeMessages(0xFB);
	}

	public void startTimerForAll() {
		stopTimerForAll();
		mHandler.postDelayed(runTimerForAll, 1 * 1000);

	}
	public int getCurrentModel() {
		return currentModel;
	}

	public void setCurrentModel(int currentModel) {
		this.currentModel = currentModel;
	}
	public void stopTimerForAll() {
		mHandler.removeCallbacks(runTimerForA);
		mHandler.removeCallbacks(runTimerForB);
		mHandler.removeCallbacks(runTimerForAll);
		mHandler.removeMessages(0xFA);
		mHandler.removeMessages(0xFB);
		mHandler.removeMessages(0xFC);
	}

	public void printMassageInfo() {
		Log.e("", "____________massageInfoA : " + currentMassageInfoForA);
		Log.e("", "____________massageInfoB : " + currentMassageInfoForB);
	}

	public void printIsTogether() {
		Log.e("", "___________ isTogether : " + this.isTogeter);
	}

	public Ems getModel() {
		Ems model = null;
		if (currentMassageInfoForA != null) {
			switch (currentMassageInfoForA.getModel()) {
			case 0:
				model = Ems.VIB;
				break;
			case 1:
				model = Ems.VIB_EMS;
				break;
			case 2:
				model = Ems.EMS1;
				break;
			case 3:
				model = Ems.EMS2;
				break;
			case 4:
				model = Ems.EMS3;
				break;

			default:
				break;
			}
		} 
		return model;
	}
	private void saveHistory(History history) {
//		new SaveHistoryAsyncTask().execute(history);
		history.save();
	};
	
	
	private class SaveHistoryAsyncTask extends AsyncTask<History, Void, Void>{
		

	@Override
	protected Void doInBackground(History... params) {
		History history = params[0];
		List<History> histiryListByTime = LitPalManager.instance().getHistiryListByTime(history.getDate());
		if (histiryListByTime==null) {
			history.save();
		}else{
			ContentValues va = new ContentValues();
			va.put("power", history.getPower());
//			DataSupport.where("date like ?",history.getDate()+"%")
			DataSupport.updateAll(History.class, va,"date like ?",(history.getDate()+"%"));
		}
		return null;
		}
	}
}
