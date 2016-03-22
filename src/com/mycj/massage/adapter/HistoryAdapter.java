package com.mycj.massage.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mycj.massage.R;
import com.mycj.massage.R.id;
import com.mycj.massage.R.layout;
import com.mycj.massage.bean.Ems;
import com.mycj.massage.bean.History;
import com.mycj.massage.util.DateUtil;
import com.mycj.massage.view.PowerView;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;


public class HistoryAdapter extends BaseAdapter {
	private List<History> historys = new ArrayList<History>();
	private Context context;
	private String VIB;
	private String VIB_EMS;
	private String EMS1;
	private String EMS2;
	private String EMS3;
	private String unknown="";

	public HistoryAdapter(List<History> historys,Context context) {
		super();
		this.historys = historys;
		this.context  = context;
		VIB = context.getString(Ems.VIB.getText());
		VIB_EMS = context.getString(Ems.VIB_EMS.getText());
		EMS1 = context.getString(Ems.EMS1.getText());
		EMS2 = context.getString(Ems.EMS2.getText());
		EMS3 = context.getString(Ems.EMS3.getText());
		unknown = context.getResources().getString(R.string.unkown);
	}

	@Override
	public int getCount() {
		return historys.size();
	}

	@Override
	public Object getItem(int position) {
		return historys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
			vh = new ViewHolder();
			vh.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
			vh.tvPower = (TextView) convertView.findViewById(R.id.tv_power);
			vh.tvModel =  (TextView) convertView.findViewById(R.id.tv_model);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		
		History history = historys.get(position);
		String date = history.getDate();
		Date d = DateUtil.stringToDate(date, "yyyyMMdd hh:mm:ss");
		String text = DateUtil.dateToString(d, "hh:mm:ss");
		vh.tvDate.setText(text);
		vh.tvPower.setText(String.valueOf(history.getPower()));
		String model = unknown;
		
		
		
		switch (history.getModel()) {
		case 0:
//			model = parent.getContext().getString(Ems.VIB.getText());//这个会导致卡顿
//			model = "VIB";
			model = VIB;
			break;
		case 1:
//			model = parent.getContext().getString(Ems.VIB_EMS.getText());
//			model = "VIB/EMS";
			model = VIB_EMS;
			break;
		case 2:
//			model = parent.getContext().getString(Ems.EMS1.getText());
			model = "EMS1";
			model = EMS1;
			break;
		case 3:
//			model = parent.getContext().getString(Ems.EMS2.getText());
			model = EMS2;
			break;
		case 4:
//			model = parent.getContext().getString(Ems.EMS3.getText());
			model = EMS3;
			break;
		default:
			break;
		}
		
		vh.tvModel.setText(model);
		return convertView;
	}

	class ViewHolder {
		TextView tvDate;
		TextView tvPower;
		TextView tvModel;
//		ProgressBar pbHistory;
		PowerView pvPower;
	}

}

