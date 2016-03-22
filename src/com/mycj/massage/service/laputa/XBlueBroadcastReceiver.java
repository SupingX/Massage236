package com.mycj.massage.service.laputa;


import com.mycj.massage.bean.MassageInfo;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class XBlueBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(XBlueBroadcastUtils.ACTION_FU_ZAI_CHANGED.equals(action)){
			int fuzai = intent.getExtras().getInt(XBlueBroadcastUtils.EXTRA_FU_ZAI);
			BluetoothDevice device = intent.getParcelableExtra(XBlueBroadcastUtils.EXTRA_DEVICE);
			doFuzaiChanged(device,fuzai);
		}
		
		else if(XBlueBroadcastUtils.ACTION_MASSAGE_CHANGED.equals(action)){
			MassageInfo info = intent.getParcelableExtra(XBlueBroadcastUtils.EXTRA_MASSAGE);
			String  address = intent.getExtras().getString(XBlueBroadcastUtils.EXTRA_ADDRESS);
			doMassageChanged(address,info);
		}
		
		else if(XBlueBroadcastUtils.ACTION_TIME_CHANGED.equals(action)){
			int time = intent.getExtras().getInt(XBlueBroadcastUtils.EXTRA_TIME);
			String  address = intent.getExtras().getString(XBlueBroadcastUtils.EXTRA_ADDRESS);
			doTimeChanged(address,time);
		}
	}

	public abstract void doTimeChanged(String address, int time);
	public abstract void doFuzaiChanged(BluetoothDevice device,int fuzai);
	public abstract void doMassageChanged(String address,MassageInfo info);

}
