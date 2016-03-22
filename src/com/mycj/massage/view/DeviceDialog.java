package com.mycj.massage.view;

import java.util.ArrayList;
import java.util.List;
















import com.mycj.massage.R;
import com.mycj.massage.adapter.DeviceAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class DeviceDialog {
	private Context context;
	private Dialog dialog;
	private TextView txt_title;
	private TextView txt_cancel;
	private LinearLayout lLayout_content;
	private boolean showTitle = false;
	private Display display;
	private TextView tvLeft;
	private TextView tvRight;
	private ListView lvDevice;
	
	public DeviceDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}

	public DeviceDialog builder(DeviceAdapter adapter) {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_wheel, null);
		tvLeft = (TextView) view.findViewById(R.id.tv_negative);
		tvRight = (TextView) view.findViewById(R.id.tv_positive);
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll);
		lvDevice = (ListView) view.findViewById(R.id.lv_device);
		lvDevice.setAdapter(adapter);
		// 设置Dialog最小宽度为屏幕宽度
		// 获取View控件
		// 定义Dialog布局和参数
		dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.height= (int) (display.getHeight() * 0.5);
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
//		ll.setLayoutParams(new FrameLayout.LayoutParams((int) (display
//				.getWidth() * 0.85), (int) (display
//						.getHeight() * 0.5)));
		return this;
	}
	

	
//	public WheelDialog setNumber(int index){
//		if (index<min) {
//			index=min;
//		}
//		if (max>index) {
//			max=index;
//		}
//		wv.setCurrentItem(index-min);
//		return this;
//	}
//	
//	public int getNumber (){
//		return wv.getCurrentItem()+min;
//	}
//	
//	public WheelDialog setOnButtonClickListener(OnButtonClickListener l){
//		this.mOnButtonClickListener = l;
//		return this;
//	}
//	
	public DeviceDialog setOnLeftClickListener(String left ,OnClickListener l){
		tvLeft.setText(left);
		tvLeft.setOnClickListener(l);
		dismiss();
		return this;
	}
	public DeviceDialog setOnRightClickListener(String right ,OnClickListener l){
		tvRight.setText(right);
		tvRight.setOnClickListener(l);
		dismiss();
		return this;
	}
	
	public DeviceDialog setOnItemClickListener(){
		lvDevice.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mOnButtonClickListener!=null) {
					mOnButtonClickListener.onListViewSelected(view, position);
					dismiss();
				}
			}
		});
		return this;
	}
	
	
	public DeviceDialog setTitle(String title) {
		return this;
	}

	public DeviceDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public DeviceDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}


	public void show() {
		setOnItemClickListener();
		dialog.show();
	}
	public void dismiss() {
		dialog.dismiss();
	}

	
	private OnButtonClickListener mOnButtonClickListener;
	public DeviceDialog setOnButtonClickListener(OnButtonClickListener l){
		this.mOnButtonClickListener = l;
		return this;
	}
	public interface OnButtonClickListener {
		public void onListViewSelected(View v ,int position );
	}
	
	public enum SheetItemColor {
		Blue("#037BFF"), Red("#FD4A2E");
		private String name;

		private SheetItemColor(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
