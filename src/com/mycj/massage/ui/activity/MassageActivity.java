package com.mycj.massage.ui.activity;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.laputa.blue.broadcast.LaputaBroadcast;
import com.laputa.blue.broadcast.LaputaBroadcastReceiver;
import com.laputa.blue.core.AbstractSimpleLaputaBlue;
import com.laputa.blue.util.BondedDeviceUtil;
import com.laputa.blue.util.XLog;
import com.laputa.dialog.AbstractLaputaDialog;
import com.laputa.dialog.LaputaAlertDialog;
import com.mycj.massage.R;
import com.mycj.massage.R.anim;
import com.mycj.massage.R.drawable;
import com.mycj.massage.R.id;
import com.mycj.massage.R.layout;
import com.mycj.massage.adapter.DeviceAdapter;
import com.mycj.massage.base.BaseActivity;
import com.mycj.massage.bean.Ems;
import com.mycj.massage.bean.History;
import com.mycj.massage.bean.MassageInfo;
import com.mycj.massage.bean.ProtoclWrite;
import com.mycj.massage.service.laputa.XBlueBroadcastReceiver;
import com.mycj.massage.service.laputa.XBlueBroadcastUtils;
import com.mycj.massage.util.DataUtil;
import com.mycj.massage.util.DateUtil;
import com.mycj.massage.util.TimeUtil;
import com.mycj.massage.view.AlertDialog;
import com.mycj.massage.view.AlphaImageView;
import com.mycj.massage.view.ColorCircleView;
import com.mycj.massage.view.ColorCircleView.OnTimePointChangeListener;
import com.mycj.massage.view.DeviceDialog;
import com.mycj.massage.view.DeviceDialog.OnButtonClickListener;
import com.mycj.massage.view.SanView;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MassageActivity extends BaseActivity implements OnClickListener {
	private TextView tvTitle;
	private ImageView imgStartB;
	private ImageView imgFuzaiA;
	private ImageView imgFuzaiB;
	private ImageView imgStartA;
	private ImageView imgTongshi;
	private ImageView imgBack;
	private TextView tvTimeA;
	private TextView tvTimeB;
	private TextView tvBleA;
	private List<BluetoothDevice> devices;
	private DeviceAdapter adapter;
	private TextView tvBleB;
	private ColorCircleView ccA;
	private ColorCircleView ccB;
	private SanView sanHistory;
	private DeviceDialog chooseBlueADialog;
	private DeviceDialog chooseBlueBDialog;
	private AlphaImageView imgBlueAState;
	private AlphaImageView imgBlueBState;
	private Ems ems;
	private AlertDialog removeDilog;
	private boolean isStartA = false;
	private boolean isStartB = false;;

	private Handler mHandler = new Handler();
	private LaputaBroadcastReceiver receiverLaputa = new LaputaBroadcastReceiver(){

		@Override
		protected void onDevicesFound(final ArrayList<BluetoothDevice> datas) {
			super.onDevicesFound(datas);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					devices.clear();
					if (datas != null) {
						devices.addAll(datas);
					}
					adapter.notifyDataSetChanged();
				}
			});
		}

		@Override
		protected void onStateChanged(final String address, final int state) {
			super.onStateChanged(address, state);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (state == BluetoothProfile.STATE_DISCONNECTED) {
						if (address.equals(BondedDeviceUtil.get(1, MassageActivity.this))) {
							updateBlueAState(false);
						} else if (address.equals(BondedDeviceUtil.get(2, MassageActivity.this))) {
							updateBlueBState(false);
						}
						
					} else if (state == AbstractSimpleLaputaBlue.STATE_SERVICE_DISCOVERED) {
						if (address.equals(BondedDeviceUtil.get(1, MassageActivity.this))) {
							updateBlueAState(true);
						} else if (address.equals(BondedDeviceUtil.get(2, MassageActivity.this))) {
							updateBlueBState(true);
						}
					}
				}
			});
		}
		
		
	};
	
	
	private XBlueBroadcastReceiver receiver = new XBlueBroadcastReceiver() {



		@Override
		public void doFuzaiChanged(final BluetoothDevice device, final int fuzai) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					String address = device.getAddress();
					if (address.equals(BondedDeviceUtil.get(1, getApplicationContext()))) {
						updateFuzaiA(fuzai == 1 ? true : false);
					} else if (address.equals(BondedDeviceUtil.get(2, getApplicationContext()))) {
						updateFuzaiB(fuzai == 1 ? true : false);
					}
				}
			});
		}

		@Override
		public void doMassageChanged(final String address, final MassageInfo info) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (address.equals(BondedDeviceUtil.get(1, getApplicationContext()))) {
						updateMassageAUI(info);
					} else if (address.equals(BondedDeviceUtil.get(2, getApplicationContext()))) {
						// B的按摩信息变化
						updateMassageBUI(info);
					}
				}
			});
		}

		@Override
		public void doTimeChanged(final String address, final int time) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (address.equals(BondedDeviceUtil.get(1, getApplicationContext()))) {
						Log.e("", "更新A时间");
						if (isSameModelForA()) {
							updateTimeA(time);
							if (time == 0) {
								// 按摩A结束
								int progress = ccA.getProgress();
								History h = new History(DateUtil.dateToString(new Date(), "yyyyMMdd hh:mm:ss"), progress, ems.getModel());
								h.save();

								updateTimeA(ProtoclWrite.TIME_15);
								updateStartA(0);
							}
						}
					} else if (address.equals(BondedDeviceUtil.get(2, getApplicationContext()))) {
						Log.e("", "更新B时间");
						if (isSameModelForB()) {
							updateTimeB(time);
							if (time == 0) {
								// 按摩B结束
								Log.e("xpl", "按摩器B结束了，保存数据！");
								// int progress = ccB.getProgress();
								// History h = new
								// History(DateUtil.dateToString(new
								// Date(),"yyyyMMdd hh:mm:ss"), progress,
								// ems.getModel());
								// h.save();

								updateTimeB(ProtoclWrite.TIME_15);
								updateStartB(0);
							}
						}
					} else {
						Log.e("", "更新A B 时间");
						// 一起时
						int currentModel = xBlueService.getCurrentModel();
						if (currentModel == ems.getModel()) {
							updateTimeA(time);
							if (time == 0) {
								// 按摩A结束
								int progress = ccA.getProgress();
								History h = new History(DateUtil.dateToString(new Date(), "yyyyMMdd hh:mm:ss"), progress, ems.getModel());
								h.save();

								updateTimeA(ProtoclWrite.TIME_15);
								updateStartA(0);
							}

							updateTimeB(time);
							if (time == 0) {
								// 按摩B结束
								int progress = ccB.getProgress();
								History h = new History(DateUtil.dateToString(new Date(), "yyyyMMdd hh:mm:ss"), progress, ems.getModel());
								h.save();
								updateTimeB(ProtoclWrite.TIME_15);
								updateStartB(0);
							}
						}

					}

				}
			});
		}
	};
	private MassageInfo infoForUiA;
	private MassageInfo infoForUiB;

	// private boolean isT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_massage);
		devices = new ArrayList<BluetoothDevice>();
		initViews();
		Intent intent = getIntent();
		if (intent != null) {
			ems = (Ems) intent.getExtras().get("ems");
			tvTitle.setText(ems.getText());
			Log.e("", "ems" + ems);
			Log.e("", "ems" + (ems == Ems.VIB));
			if (ems == Ems.VIB) {
				ccA.setIsCanTouch(false);
				ccB.setIsCanTouch(false);
			}
		}
		adapter = new DeviceAdapter(devices, this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(receiverLaputa, LaputaBroadcast.getIntentFilter());
		registerReceiver(receiver, LaputaBroadcast.getIntentFilter());
		initMassageInfo(ems.getModel());
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (xBlueService != null) {
		/*	if (!xBlueService.isAllConnected()) {
				xBlueService.startScan();
			}*/
			if (xBlueService.isAConnected()) {
				updateBlueAState(true);
			}
			if (xBlueService.isBConnected()) {
				updateBlueBState(true);
			}
		}

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiverLaputa);
		unregisterReceiver(receiver);
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		boolean isTongbu = false;
		int currentModel = -1;

		if (xBlueService != null) {
			isTongbu = xBlueService.getIsTogether();
			currentModel = xBlueService.getCurrentModel();
		}

		switch (v.getId()) {
		case R.id.img_start_btn_1:
			if (xBlueService != null && !xBlueService.isAConnected()) {
				showIodDialog(getString(R.string.device_a_disconnected));
				return;
			}
			/**
			 * 一、按摩A开始 1.infoA为空时，此时开关肯定是on0ff=0。开始时，会把service里的model改变，
			 * 并且同时会改变MassageInfoA 2.infoA不为空，
			 * 1>如果Service里的model和当前Activity的model不一致
			 * 2>如果Service里的model和当前Activity的model一致
			 * 
			 */
			MassageInfo infoA = null;
			if (xBlueService != null) {
				infoA = xBlueService.getCurrentMassageInfoForA();
			}

			if (currentModel == -1) {// 说明A+B还没有开始按摩
				if (isTongbu) {
					if (xBlueService.isAConnected()) {
						xBlueService.startMassageForA(ems.getModel());
						updateMassageAUI(xBlueService.getCurrentMassageInfoForA());
					}
					if (xBlueService.isBConnected()) {
						xBlueService.startMassageForB(ems.getModel());
						updateMassageBUI(xBlueService.getCurrentMassageInfoForB());
					}
				} else {
					if (xBlueService.isAConnected()) {
						xBlueService.startMassageForA(ems.getModel());
						updateMassageAUI(xBlueService.getCurrentMassageInfoForA());
					}
				}
			} else if (currentModel == ems.getModel()) { // 说明当前界面 和 开始按摩的模式 一致
				if (isTongbu) { // 同步模式
					if (infoA == null) { // 此时都还没开启过
						if (xBlueService.isAConnected()) {
							
							xBlueService.startMassageForA(ems.getModel());
							updateMassageAUI(xBlueService.getCurrentMassageInfoForA());
						
						} if (xBlueService.isBConnected()) {
							xBlueService.startMassageForB(ems.getModel());
							updateMassageBUI(xBlueService.getCurrentMassageInfoForB());
						}
					} else { // 此时A已经开启
						if (xBlueService.isAConnected()) {
							xBlueService.stopMassageForA();
						}
						if (xBlueService.isBConnected()) {
							xBlueService.stopMassageForB();
						}
						updateMassageBUI(getDefaultMassageInfo());
						updateMassageAUI(getDefaultMassageInfo());
					}
				} else {
					if (infoA == null) { // A没有开启
						if (xBlueService.isAConnected()) {
							xBlueService.startMassageForA(ems.getModel());
							updateMassageAUI(xBlueService.getCurrentMassageInfoForA());
						}
					} else { // A已经开启
						if (xBlueService.isAConnected()) {
							xBlueService.stopMassageForA();
						}
						updateMassageAUI(getDefaultMassageInfo());
					}
				}

			} else {// 其他情况 ， 当前界面和下位机按摩的模式不一致。
				//
			}

			break;
		case R.id.img_start_btn_2:
			if (xBlueService != null && !xBlueService.isBConnected()) {
				showIodDialog(getString(R.string.device_b_disconnected));
				return;
			}
			MassageInfo infoB = null;
			/**
			 * 一、按摩A开始 1.infoA为空时，此时开关肯定是on0ff=0。开始时，会把service里的model改变，
			 * 并且同时会改变MassageInfoA 2.infoA不为空，
			 * 1>如果Service里的model和当前Activity的model不一致
			 * 2>如果Service里的model和当前Activity的model一致
			 * 
			 */

			if (xBlueService != null) {
				infoB = xBlueService.getCurrentMassageInfoForB();
			}

			if (currentModel == -1) {// 说明还没有开始按摩
				if (isTongbu) {
					if (xBlueService.isAConnected()) {
						xBlueService.startMassageForA(ems.getModel());
						updateMassageAUI(xBlueService.getCurrentMassageInfoForA());
					}
					if (xBlueService.isBConnected()) {
						xBlueService.startMassageForB(ems.getModel());
						updateMassageBUI(xBlueService.getCurrentMassageInfoForB());
					}
				} else {
					if (xBlueService.isBConnected()) {
						xBlueService.startMassageForB(ems.getModel());
						updateMassageBUI(xBlueService.getCurrentMassageInfoForB());
					}
				}
			} else if (currentModel == ems.getModel()) { // 说明当前界面 和 开始按摩的模式 一致
				if (isTongbu) { // 同步模式
					if (infoB == null) { // 此时都还没开启过
						if (xBlueService.isAConnected()) {
							xBlueService.startMassageForA(ems.getModel());
							updateMassageAUI(xBlueService.getCurrentMassageInfoForA());
						}
						if (xBlueService.isBConnected()) {
							xBlueService.startMassageForB(ems.getModel());
							updateMassageBUI(xBlueService.getCurrentMassageInfoForB());
						}
					} else { // 此时B已经开启
						if (xBlueService.isAConnected()) {
							xBlueService.stopMassageForA();
						}
						if (xBlueService.isBConnected()) {
							xBlueService.stopMassageForB();
						}
						updateMassageBUI(getDefaultMassageInfo());
						updateMassageAUI(getDefaultMassageInfo());
					}
				} else {
					if (infoB == null) {
						if (xBlueService.isBConnected()) {
							xBlueService.startMassageForB(ems.getModel());
							updateMassageBUI(xBlueService.getCurrentMassageInfoForB());
						}
					} else {
						if (xBlueService.isBConnected()) {
							xBlueService.stopMassageForB();
						}
						updateMassageBUI(getDefaultMassageInfo());
					}
				}
			}

			break;
		case R.id.img_back:
			finish();
			break;
		case R.id.img_tongshi:
			/**
			 * 1.开启同步 1〉 当前Activity和Service的模式model不一致时，不可以点击。
			 * 2〉Service的modelA、modelA都为null或者
			 * 当前Activity和Service的模式model一致时，可以点击 《1》停止所有的按摩，并且回到初始状态
			 * 《2》uiB不可用，包括开始按钮B，不可以点击 2.关闭同步
			 * 1〉当前Activity和Service的模式model不一致时，不可以点击。 2〉一致时，可以点击
			 * 《1》停止所有的按摩，并且回到初始状态 《2》uiB可用，包括开始按钮B，可以点击
			 */

			MassageInfo infoAA = null;
			MassageInfo infoBB = null;
			if (xBlueService != null) {
				infoAA = xBlueService.getCurrentMassageInfoForA();
				infoBB = xBlueService.getCurrentMassageInfoForB();
				isTongbu = xBlueService.getIsTogether();
				currentModel = xBlueService.getCurrentModel();
			}

			// 此时模式为-1，说明还没有任何按摩开始
			if (currentModel == -1) {
				updateIsTogether(!isTongbu);
				xBlueService.setIsTogether(!isTongbu);
				break;
			}

			// 此时的模式和正在按摩的模式不一样，所以不能点击
			if (currentModel != ems.getModel()) {
				imgTongshi.setEnabled(false);
				showIodDialog("当前模式不能切换同步开关,正在按摩的模式为 ：" + (xBlueService.getModel() == null ? "未知" : getString(ems.getText())));
				break;
			}

			// 同步开启时false--〉true
			// 1。此时模式一致 ， 当正在按摩时 ，当A正在按摩时，B同步A。当B正在按摩时，A同步B。当一起按摩时，B同步A。
			// 2。此时B不能控制，由A来控制A B
			// 同步关闭时true--〉false
			// 1。A、B分开控制。
			//
			isTongbu = !isTongbu;
			if (isTongbu) {
				xBlueService.setIsTogether(isTongbu);

				if (infoAA == null && infoBB == null) {

				} else if (infoAA != null && infoBB == null) {
					xBlueService.setCurrentMassagerInfoForB(infoAA);
					if (xBlueService.isBConnected()) {
						byte[] protoclForStartAndEnd = ProtoclWrite.instance().protoclForStartAndEnd(infoAA.getOnOff(), infoAA.getModel(), infoAA.getPower(), infoAA.getTime());
						xBlueService.writeB(protoclForStartAndEnd);
					}
					updateMassageBUI(infoAA);
				} else if (infoAA == null && infoBB != null) {
					xBlueService.setCurrentMassagerInfoForB(infoBB);
					if (xBlueService.isAConnected()) {
						byte[] protoclForStartAndEnd = ProtoclWrite.instance().protoclForStartAndEnd(infoBB.getOnOff(), infoBB.getModel(), infoBB.getPower(), infoBB.getTime());
						xBlueService.writeA(protoclForStartAndEnd);
					}
					updateMassageAUI(infoBB);
				} else if (infoAA != null && infoBB != null) {
					xBlueService.setCurrentMassagerInfoForB(infoAA);
					if (xBlueService.isBConnected()) {
						byte[] protoclForStartAndEnd = ProtoclWrite.instance().protoclForStartAndEnd(infoAA.getOnOff(), infoAA.getModel(), infoAA.getPower(), infoAA.getTime());
						xBlueService.writeB(protoclForStartAndEnd);
					}
					updateMassageBUI(infoAA);
				}
				updateIsTogether(isTongbu);

			} else {
				xBlueService.setIsTogether(isTongbu);
				updateIsTogether(isTongbu);
			}
			break;
		case R.id.v_history:

			// int nextX = new Random().nextInt(30);
			// int nextY = new Random().nextInt(30);
			// int fX = new Random().nextInt(360);
			// int fY = new Random().nextInt(360);
			// ObjectAnimator c = ObjectAnimator.ofFloat(sanHistory,
			// "translationX",0,-nextX,0);
			// ObjectAnimator d = ObjectAnimator.ofFloat(sanHistory,
			// "translationY",0,-nextY,0);
			// ObjectAnimator e = ObjectAnimator.ofFloat(sanHistory,
			// "rotationX",0,fX,0);
			// ObjectAnimator f = ObjectAnimator.ofFloat(sanHistory,
			// "rotationY",0,fY,0);
			// c.setDuration(2000);
			// c.setInterpolator(new OvershootInterpolator());
			// d.setDuration(2000);
			// d.setInterpolator(new OvershootInterpolator());
			// e.setDuration(2000);
			// e.setInterpolator(new OvershootInterpolator());
			// f.setDuration(2000);
			// f.setInterpolator(new OvershootInterpolator());
			// c.start();
			// d.start();
			// e.start();
			// f.start();

			startActivity(new Intent(MassageActivity.this, HistoryActivity.class));
			overridePendingTransition(R.anim.history_in, R.anim.massager_out);

			break;
		case R.id.tv_ble_1:
			if (xBlueService != null) {
				xBlueService.startOnlyScan();
			}
			if (chooseBlueADialog == null) {

				chooseBlueADialog = new DeviceDialog(MassageActivity.this).builder(adapter).setOnLeftClickListener(getString(R.string.back), new OnClickListener() {

					@Override
					public void onClick(View v) {
						chooseBlueADialog.dismiss();
					}
				}).setOnRightClickListener(getString(R.string.refresh), new OnClickListener() {

					@Override
					public void onClick(View v) {
						xBlueService.stopScan();
						devices.clear();
						adapter.notifyDataSetChanged();
						xBlueService.startOnlyScan();
					}
				}).setOnButtonClickListener(new OnButtonClickListener() {
					@Override
					public void onListViewSelected(View v, int position) {
//						xBlueService.stopScan();
						BluetoothDevice device = devices.get(position);
						String address = device.getAddress();
						if (checkBlueIsAdd(address)) {
							Toast.makeText(getApplicationContext(), getString(R.string.device_added), Toast.LENGTH_SHORT).show();
							return;
						}
						tvBleA.setText(address);
						XLog.e("设置A的 address :" + address);
						BondedDeviceUtil.save(1, address, MassageActivity.this);
						xBlueService.connect(address);
//						XBlueUtils.saveBlue(MassageActivity.this, address, XBlueUtils.TYPE_A);
						// xBlueService.connect(device.getAddress());
//						xBlueService.startScan();
					}
				});
			}
			chooseBlueADialog.show();
			break;
		case R.id.tv_ble_2:
			
			
			
			
			if (xBlueService != null) {
				xBlueService.startOnlyScan();
			}
			if (chooseBlueBDialog == null) {

				chooseBlueBDialog = new DeviceDialog(MassageActivity.this).builder(adapter).setOnLeftClickListener(getString(R.string.back), new OnClickListener() {

					@Override
					public void onClick(View v) {
						chooseBlueBDialog.dismiss();
					}
				}).setOnRightClickListener(getString(R.string.refresh), new OnClickListener() {

					public void onClick(View v) {

						xBlueService.stopScan();
						devices.clear();
						adapter.notifyDataSetChanged();
						xBlueService.startOnlyScan();
					}
				}).setOnButtonClickListener(new OnButtonClickListener() {
					@Override
					public void onListViewSelected(View v, int position) {
//						xBlueService.stopScan();
						BluetoothDevice device = devices.get(position);
						String address = device.getAddress();
						if (checkBlueIsAdd(address)) {
							Toast.makeText(getApplicationContext(), R.string.device_added, Toast.LENGTH_SHORT).show();
							return;
						}
						XLog.e("设置B的 address :" + address);
						tvBleB.setText(address);
						
						BondedDeviceUtil.save(2, address, MassageActivity.this);
						xBlueService.connect(address);
//						XBlueUtils.saveBlue(MassageActivity.this, address, XBlueUtils.TYPE_B);
						// xBlueService.connect(device.getAddress());
//						xBlueService.startScan();
					}
				});
			}
			chooseBlueBDialog.show();
			break;

		default:
			break;
		}
	}

	private void updateIsTogether(boolean istogether) {
		if (istogether) {
			imgTongshi.setImageResource(R.drawable.ic_tongbu_on);
			imgStartB.setEnabled(false);
			ccB.setEnabled(false);
		} else {
			imgTongshi.setImageResource(R.drawable.ic_tongbu_off);
			imgStartB.setEnabled(true);
			ccB.setEnabled(true);
		}
	}

	private boolean checkBlueIsAdd(String address) {
	/*	String[] blueAB = XBlueUtils.getBlueAB(getApplicationContext());
		for (int i = 0; i < blueAB.length; i++) {
			if (blueAB[i].equals(address)) {
				return true;
			}
		}*/
		return false;
		

	}

	private void updateStartB(int isStart) {
		if (isStart == 1) {
			imgStartB.setImageResource(R.drawable.ic_stop_pressed);
			if (xBlueService != null && !xBlueService.getIsTogether()) {
				// ccB.setIsCanTouch(true);
				ccB.setIsCanTouch(ems.getModel() == 0 ? false : true);
			} else {
				ccB.setIsCanTouch(false);
			}
		} else {
			imgStartB.setImageResource(R.drawable.ic_start);
			ccB.setIsCanTouch(false);
		}
	}

	private void updateStartA(int isStart) {
		if (isStart == 1) {
			// ccA.setIsCanTouch(true);
			ccA.setIsCanTouch(ems.getModel() == 0 ? false : true);
			imgStartA.setImageResource(R.drawable.ic_stop_pressed);
		} else {
			ccA.setIsCanTouch(false);
			imgStartA.setImageResource(R.drawable.ic_start);
		}
	}

	private void updateBlueAAddress() {
//		String blueA = XBlueUtils.getBlueA(this);
		String blueA = BondedDeviceUtil.get(1, this);
		tvBleA.setText(blueA.equals("") ? getString(R.string.set_a) : blueA);
	}

	private void updateBlueBAddress() {
//		String blueB = XBlueUtils.getBlueB(this);
		String blueB = BondedDeviceUtil.get(2, this);
		tvBleB.setText(blueB.equals("") ? getString(R.string.set_b) : blueB);
	}


	private void updateBlueAState(boolean state) {
		if (state) {
			imgBlueAState.setImageResource(R.drawable.ic_ble_icon_1);
		} else {
			imgBlueAState.setImageResource(R.drawable.ic_ble_icon_grey);
		}
	}

	private void updateBlueBState(boolean state) {
		if (state) {
			imgBlueBState.setImageResource(R.drawable.ic_ble_icon_1);
		} else {
			imgBlueBState.setImageResource(R.drawable.ic_ble_icon_grey);
		}
	}

	private void updateFuzaiA(boolean isFuzai) {
		if (isFuzai) {
			imgFuzaiA.setImageResource(R.drawable.ic_electload_ok);
		} else {
			imgFuzaiA.setImageResource(R.drawable.ic_electload);
		}
	}

	private void updateFuzaiB(boolean isFuzai) {
		if (isFuzai) {
			imgFuzaiB.setImageResource(R.drawable.ic_electload_ok);
		} else {
			imgFuzaiB.setImageResource(R.drawable.ic_electload);
		}
	}

	private void updatePowerB(int i) {
		ccB.setProgress(i);
	}

	private void updatePowerA(int i) {
		ccA.setProgress(i);
	}

	private void updateTimeA(int time) {
		tvTimeA.setText(TimeUtil.getStringTime(time));
	}

	private void updateTimeB(int time) {
		tvTimeB.setText(TimeUtil.getStringTime(time));
	}

	private void updateMassageAUI(MassageInfo info) {
		updatePowerA(info.getPower() - 1 <= 0 ? 0 : info.getPower() - 1);
		updateStartA(info.getOnOff());
		updateTimeA(info.getTime());
	}

	private void updateMassageBUI(MassageInfo info) {
		updatePowerB(info.getPower() - 1 <= 0 ? 0 : info.getPower() - 1);
		updateStartB(info.getOnOff());
		updateTimeB(info.getTime());
	}

	private void initViews() {
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTimeA = (TextView) findViewById(R.id.tv_time1);
		tvBleA = (TextView) findViewById(R.id.tv_ble_1);
		tvBleB = (TextView) findViewById(R.id.tv_ble_2);
		sanHistory = (SanView) findViewById(R.id.v_history);
		tvTimeB = (TextView) findViewById(R.id.tv_time2);
		imgStartB = (ImageView) findViewById(R.id.img_start_btn_2);
		imgFuzaiA = (ImageView) findViewById(R.id.img_fuzai1);
		imgFuzaiB = (ImageView) findViewById(R.id.img_fuzai2);
		imgStartA = (ImageView) findViewById(R.id.img_start_btn_1);
		imgTongshi = (ImageView) findViewById(R.id.img_tongshi);
		imgBack = (ImageView) findViewById(R.id.img_back);
		imgBlueAState = (AlphaImageView) findViewById(R.id.img_ble_a_state);
		imgBlueBState = (AlphaImageView) findViewById(R.id.img_ble_b_state);
		imgBack = (ImageView) findViewById(R.id.img_back);
		ccA = (ColorCircleView) findViewById(R.id.cc_a);
		ccB = (ColorCircleView) findViewById(R.id.cc_b);
		ccA.setIsCanTouch(false);
		ccB.setIsCanTouch(false);

		updateBlueAAddress();
		updateBlueBAddress();
		updateBlueAState(false);
		updateBlueBState(false);

		// 蓝牙A力度
		ccA.setOnTimePointChangeListener(new OnTimePointChangeListener() {

			@Override
			public void onChanging(int progress) {

				if (xBlueService != null) {
					int currentModel = xBlueService.getCurrentModel();
					MassageInfo infoA = xBlueService.getCurrentMassageInfoForA();
					MassageInfo infoB = xBlueService.getCurrentMassageInfoForB() ;
					boolean isTong = xBlueService.getIsTogether();
					
					
					
					if (currentModel == ems.getModel()) {
						
						if (infoA == null) {
							
						}
					}
					
					
					if (infoA != null) {
						int model = infoA.getModel();
						if (model == ems.getModel()) {
							if (isTogether()) {
								ccB.setProgress(progress);
							}
						} else {
							if (isTogether()) {
								ccB.setProgress(progress);
							}
						}
					}
				}

			}

			@Override
			public void onChanged(int progress) {

				if (xBlueService != null) {
					MassageInfo infoA = xBlueService.getCurrentMassageInfoForA();
					boolean isTogether = xBlueService.getIsTogether();
					int model = infoA.getModel();
					if (model == ems.getModel()) {
						if (isTogether) {
							Log.e("", "____________xixixixiixixix : ");
							ccB.setProgress(progress);
							infoA.setPower(progress + 1);
							// xBlueService.setCurrentMassagerInfoForA(infoA);
							int onOff = infoA.getOnOff();
							if (onOff == 1) {
								xBlueService.startMassageForAll(ems.getModel());
							} else {
								// xBlueService.stopMassageForAll();
							}
						} else {
							Log.e("", "____________hahahahahhaha : ");
							xBlueService.printMassageInfo();
							xBlueService.printIsTogether();
							infoA.setPower(progress + 1);
							// xBlueService.setCurrentMassagerInfoForA(infoA);
							int onOff = infoA.getOnOff();
							if (onOff == 1) {
								xBlueService.startMassageForA(ems.getModel());
							} else {
								// xBlueService.stopMassageForA();
							}
						}
					} else {
						// ccB.setProgress(progress);
					}
				}

				xBlueService.printMassageInfo();
			}
		});

		// 蓝牙B力度
		ccB.setOnTimePointChangeListener(new OnTimePointChangeListener() {

			@Override
			public void onChanging(int progress) {

				if (xBlueService != null) {
					MassageInfo infoA = xBlueService.getCurrentMassageInfoForA();
					if (infoA != null) {
						int model = infoA.getModel();
						if (model == ems.getModel()) {
							if (isTogether()) {
								ccA.setProgress(progress);
							}
						} else {
							if (isTogether()) {
								ccB.setProgress(progress);
							}
						}
					}
				}
			}

			@Override
			public void onChanged(int progress) {

				if (xBlueService != null) {
					MassageInfo infoB = xBlueService.getCurrentMassageInfoForB();
					if (infoB != null) {

						int model = infoB.getModel();
						if (model == ems.getModel()) {
							if (isTogether()) {
								ccA.setProgress(progress);
								infoB.setPower(progress + 1);
								// xBlueService.setCurrentMassagerInfoForA(infoB);
								// xBlueService.setCurrentMassagerInfoForB(infoB);
								int onOff = infoB.getOnOff();
								if (onOff == 1) {
									xBlueService.startMassageForAll(ems.getModel());
								} else {
									// xBlueService.stopMassageForAll();
								}
							} else {
								infoB.setPower(progress + 1);
								// xBlueService.setCurrentMassagerInfoForB(infoB);
								int onOff = infoB.getOnOff();
								if (onOff == 1) {
									xBlueService.startMassageForB(ems.getModel());
								} else {
									// xBlueService.stopMassageForB();
								}
							}
						}
					} else {
						// if (isT) {
						// ccA.setProgress(progress);
						// }
					}
				}

				xBlueService.printMassageInfo();
			}
		});

		//
		sanHistory.setOnClickListener(this);
		imgBack.setOnClickListener(this);
		imgTongshi.setOnClickListener(this);
		imgStartA.setOnClickListener(this);
		imgStartB.setOnClickListener(this);
		tvBleA.setOnClickListener(this);
		tvBleB.setOnClickListener(this);
		tvBleA.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				removeDilog = new AlertDialog(MassageActivity.this).builder().setMsg(getString(R.string.check_delete_a)).setNegativeButton(getString(R.string.back), new OnClickListener() {
					@Override
					public void onClick(View v) {
						removeDilog.dismiss();
					}
				}).setPositiveButton(getString(R.string.delete), new OnClickListener() {

					@Override
					public void onClick(View v) {
//						String blueA = XBlueUtils.getBlueA(getApplicationContext());
						String blueA = BondedDeviceUtil.get(1, MassageActivity.this);
						if (!blueA.equals("")) {
							if (xBlueService != null) {
								xBlueService.close(blueA);
//								XBlueUtils.clearA(getApplicationContext());
								BondedDeviceUtil.save(1, "", MassageActivity.this);
								updateBlueAAddress();
								updateBlueAState(false);

							}
						} else {
							toast(getString(R.string.no_setting_for_a));
						}
					}
				});
				removeDilog.show();
				return false;
			}
		});
		tvBleB.setOnLongClickListener(new OnLongClickListener() {
			
			
			@Override
			public boolean onLongClick(View v) {
				removeDilog = new AlertDialog(MassageActivity.this).builder().setMsg(getString(R.string.check_delete_b)).setNegativeButton(getString(R.string.back), new OnClickListener() {
					@Override
					public void onClick(View v) {
						removeDilog.dismiss();
					}
				}).setPositiveButton(getString(R.string.delete), new OnClickListener() {

					@Override
					public void onClick(View v) {

//						String blueB = XBlueUtils.getBlueB(getApplicationContext());
						String blueB = BondedDeviceUtil.get(2, MassageActivity.this);
						if (!blueB.equals("")) {
							if (xBlueService != null) {
								xBlueService.close(blueB);
//								XBlueUtils.clearB(getApplicationContext());
								BondedDeviceUtil.save(2, "", MassageActivity.this);
								updateBlueBAddress();
								updateBlueBState(false);

							}
						} else {
							toast(getString(R.string.no_setting_for_b));
						}
					}
				});
				removeDilog.show();

				return false;
			}
		});
	}

	private boolean isTogether() {
		boolean isTogether = false;
		if (xBlueService != null) {
			isTogether = xBlueService.getIsTogether();
		}
		Log.e("ＭａｓｓａｇｅＡｃｔｉｖｉｔｙ", "是否同步　 ：　" + isTogether);
		return isTogether;
	}

	/**
	 * 加载按摩视图 根据当前service的MassageInfo 以及 当前model、是否一起isTogether 来加载视图 规定：A、B
	 * 只能同时在一个模式下工作。
	 * 
	 */
	private void initMassageInfo(int model) {
		MassageInfo infoA = null;
		MassageInfo infoB = null;
		boolean isTogether = false;
		int currentModel = -1;
		if (xBlueService != null) {
			currentModel = xBlueService.getCurrentModel();
			infoA = xBlueService.getCurrentMassageInfoForA();
			infoB = xBlueService.getCurrentMassageInfoForB();
			isTogether = xBlueService.getIsTogether();
		}

		if (currentModel == -1) {// 说明还没有开始按摩
			isTogether = false;
			infoA = getDefaultMassageInfo();
			infoB = getDefaultMassageInfo();
		} else if (currentModel == model) { // 说明当前界面 和 开始按摩的模式 一致
			if (infoA == null) {
				infoA = getDefaultMassageInfo();
			}

			if (infoB == null) {
				infoB = getDefaultMassageInfo();
			}
		} else {// 其他情况 ， 当前界面和下位机按摩的模式不一致。
			isTogether = false;
			infoA = getDefaultMassageInfo();
			infoB = getDefaultMassageInfo();
		}

		updateMassageAUI(infoA);
		updateMassageBUI(infoB);
		updateIsTogether(isTogether);

	}

	private boolean isSameModelForA() {
		if (xBlueService != null) {
			MassageInfo infoA = xBlueService.getCurrentMassageInfoForA();
			return infoA != null && ems.getModel() == infoA.getModel();
		}
		return false;
	}

	private boolean isSameModelForB() {
		if (xBlueService != null) {
			MassageInfo infoB = xBlueService.getCurrentMassageInfoForB();
			return infoB != null && ems.getModel() == infoB.getModel();
		}
		return false;
	}

	// @Override
	// public void onBackPressed() {
	//
	//
	// }

	private MassageInfo getDefaultMassageInfo() {
		MassageInfo info = new MassageInfo();
		info.setModel(ems.getModel());
		info.setOnOff(0);
		info.setPower(1);
		info.setTime(ems.getModel() == Ems.VIB.getModel() ? ProtoclWrite.TIME_10 * 60 : ProtoclWrite.TIME_15 * 60);
		return info;
	}

}
