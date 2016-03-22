package com.mycj.massage.base;


import com.laputa.dialog.AbstractLaputaDialog;
import com.laputa.dialog.LaputaAlert2Dialog;
import com.laputa.dialog.LaputaAlertDialog;
import com.mycj.massage.R;
import com.mycj.massage.service.laputa.BlueService;
import com.mycj.massage.ui.activity.MassageActivity;
import com.mycj.massage.view.AlertDialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class BaseActivity extends Activity {

	private Handler mHandler = new Handler(){};
	
	
	public BlueService xBlueService;
	public BlueService getXBlueService() {
		return getBaseApp().getXBlueService();
	}
	

	public BaseApp getBaseApp() {
		return (BaseApp) getApplication();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		xBlueService = getXBlueService();
	}

	@Override
	protected void onResume() {
		/*if (xBlueService != null) {
			if (xBlueService.isAllConnected()) {

			} else {
				xBlueService.startScan();
			}
		}*/
		
		super.onResume();
	
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}
	
	

	public void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public AbstractLaputaDialog showIodDialog(String msg){
		AbstractLaputaDialog dialog = new LaputaAlertDialog(this, R.layout.view_laputa_alert_dialog)
    	.builder()
    	.setCancelable(true)
//    	.setCanceledOnTouchOutside(true)
    	.setMsg(msg)
    	;
		dialog.show();
		return dialog;
	}
	
}
