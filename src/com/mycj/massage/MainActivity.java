package com.mycj.massage;

import com.laputa.blue.core.AbstractSimpleLaputaBlue;
import com.laputa.dialog.AbstractLaputaDialog;
import com.laputa.dialog.LaputaAlert2Dialog;
import com.mycj.massage.base.BaseActivity;
import com.mycj.massage.bean.Ems;
import com.mycj.massage.service.laputa.BlueService;
import com.mycj.massage.ui.activity.MassageActivity;
import com.mycj.massage.util.NotifacationUtil;
import com.mycj.massage.view.ActionSheetDialog;
import com.mycj.massage.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.massage.view.ActionSheetDialog.SheetItemColor;
import com.mycj.massage.view.AlertDialog;
import com.mycj.massage.view.AlphaTextView;
import com.mycj.massage.view.MainLinearLayout;
import com.mycj.massage.view.LoadingDialog;
import com.mycj.massage.view.MainLinearLayout.OnMenuClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends BaseActivity implements OnClickListener {
	
	
    private BlueService xBlueService;
    private Handler mHandler = new Handler (){};
	private AbstractSimpleLaputaBlue simpleLaputaBlue;
	private AbstractLaputaDialog checkDialog;
    
   
    
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     
        
        
        
        
        
        MainLinearLayout mainLinearLayout = new MainLinearLayout(this);
        	setContentView(R.layout.activity_main);
        	
        AlphaTextView tvEms1 = (AlphaTextView) findViewById(R.id.tv_ems_1);
        AlphaTextView tvEms2 = (AlphaTextView) findViewById(R.id.tv_ems_2);
        AlphaTextView tvEms3 = (AlphaTextView) findViewById(R.id.tv_ems_3);
        AlphaTextView tvEms4 = (AlphaTextView) findViewById(R.id.tv_ems_4);
        AlphaTextView tvEms5 = (AlphaTextView) findViewById(R.id.tv_ems_5);
        tvEms1.setOnClickListener(this);
        tvEms2.setOnClickListener(this);
        tvEms3.setOnClickListener(this);
        tvEms4.setOnClickListener(this);
        tvEms5.setOnClickListener(this);
        
        setListener();
        xBlueService = getXBlueService();
        
//        checkBlue();
        
    }
	
	
	@Override
	protected void onResume() {
		super.onResume();
		checkBlue();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this,MassageActivity.class);
		switch (v.getId()) {
		case R.id.tv_ems_1:
			intent.putExtra("ems", Ems.VIB);
			boolean notificationAccessEnable = NotifacationUtil.isNotificationAccessEnable(MainActivity.this);
			if (notificationAccessEnable) {
				showIodDialog("获取通知权限已经打开！");
			}else{
				NotifacationUtil.openNotificationAccess(MainActivity.this);
			}
			
			return;
//			break;
		case R.id.tv_ems_2:
			intent.putExtra("ems", Ems.VIB_EMS);
			break;
		case R.id.tv_ems_3:
			intent.putExtra("ems", Ems.EMS1);
			break;
		case R.id.tv_ems_4:
			intent.putExtra("ems", Ems.EMS2);
			break;
		case R.id.tv_ems_5:
			intent.putExtra("ems", Ems.EMS3);
			break;
		default:
			break;
		}
		startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {

		// MusicManager.instance(getApplicationContext()).start(R.raw.crystal);

		ActionSheetDialog exitDialog = new ActionSheetDialog(this).builder();
		exitDialog.setTitle(getString(R.string.exit_app));
		exitDialog.addSheetItem(getString(R.string.confirm), SheetItemColor.Red, new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
//						if (xplBluetoothService != null) {
//							xplBluetoothService.setIsExit(true);
//							xplBluetoothService.close();
//						}
						if (xBlueService!=null) {
							xBlueService.closeAll();
						}
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								finish();
								System.exit(0);

							}
						}, 1000);
					}
				});

			}
		}).show();
	}

	private void checkBlue() {
//		if (simpleLaputaBlue == null) {
//			return;
//		}
		simpleLaputaBlue = xBlueService.getSimpleLaputaBlue();
		if (!simpleLaputaBlue.isEnable()) {
			checkDialog = new LaputaAlert2Dialog(this, R.layout.view_laputa_alert2_dialog)
			.builder()
			.setMsg("按摩器需要蓝牙打开才能工作，请选择是否打开蓝牙？")	
			.setCancel("取消", new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					checkDialog.dismiss();
				}
			})
			.setConfirm("确定", new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					simpleLaputaBlue.enableBluetooth();
					checkDialog.dismiss();
				}
			});
			checkDialog.show();
		}
	}
	
	

	
	
	private void setListener() {
	//    	mainLinearLayout.setOnMenuClickListener(new OnMenuClickListener() {
	//			
	//			@Override
	//			public void onMenuClick(View v) {
	//				int tag = (Integer) v.getTag();
	//				Intent intent = new Intent();
	//				intent.setClass(MainActivity.this,MassageActivity.class);
	//				switch (tag) {
	//				case 0:
	//					intent.putExtra("ems", Ems.VIB);
	//					break;
	//				case 1:
	//					intent.putExtra("ems", Ems.VIB_EMS);
	//					break;
	//				case 2:
	//					intent.putExtra("ems", Ems.EMS1);
	//					break;
	//				case 3:
	//					intent.putExtra("ems", Ems.EMS2);
	//					break;
	//				case 4:
	//					intent.putExtra("ems", Ems.EMS3);
	//					break;
	//
	//				default:
	//					break;
	//				}
	//				startActivity(intent);
	//				
	//			}
	//		});
		}
}
