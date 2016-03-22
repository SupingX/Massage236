package com.mycj.massage.adapter;

import java.util.ArrayList;
import java.util.List;

import com.mycj.massage.R;
import com.mycj.massage.R.id;
import com.mycj.massage.R.layout;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class DeviceAdapter extends BaseAdapter {
	private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();;
	private Context mContext ;
	private String unknwon="";
	public DeviceAdapter(List<BluetoothDevice> devices,Context context) {
		super();
		this.devices = devices;
		this.mContext = context;
		unknwon = mContext.getResources().getString(R.string.unkown);
	}

	@Override
	public int getCount() {
		return devices.size();
	}

	@Override
	public Object getItem(int position) {
		return devices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device, parent, false);
			vh = new ViewHolder();
			vh.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			vh.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		BluetoothDevice device = devices.get(position);
		String name = device.getName();
		if (name == null || name.equals("")) {
			name = unknwon;
		}
		vh.tvName.setText(name);
		vh.tvAddress.setText(device.getAddress());
		return convertView;
	}

	class ViewHolder {
		TextView tvName;
		TextView tvAddress;
	}

}

