package com.mycj.massage.view;

import com.mycj.massage.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * 
 * @author Administrator
 *
 */
public class LoadingDialog {
	private Context context;
	private Dialog dialog;
	private Display display;

	public LoadingDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}

	public LoadingDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_x_dialog, null);
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll);
		FreshCircleView fcv = (FreshCircleView) view.findViewById(R.id.fcv);
		fcv.startLoading();
		// 设置Dialog最小宽度为屏幕宽度
		// 获取View控件
		// 定义Dialog布局和参数
		dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		// lp.height= (int) (display.getHeight() * 0.5);
		// lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// lp.width = (int) (display.getWidth() * 0.85);
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		// ll.setLayoutParams(new FrameLayout.LayoutParams((int) (display
		// .getWidth() * 0.85), (int) (display
		// .getHeight() * 0.5)));
		return this;
	}

	public LoadingDialog setTitle(String title) {
		return this;
	}

	public LoadingDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public LoadingDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}

	public void show() {
		// initListener();
		dialog.show();
	}

	public void dismiss() {
		dialog.dismiss();
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
