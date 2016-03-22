package com.mycj.massage;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

public class NotifycationService extends NotificationListenerService {

	private static final String TAG = "SevenNLS";
	private static final String TAG_PRE = "["
			+ NotifycationService.class.getSimpleName() + "] ";
	private static final int EVENT_UPDATE_CURRENT_NOS = 0;
	
	public static final String ACTION_NLS_CONTROL = "com.seven.notificationlistenerdemo.NLSCONTROL";
	// 用于存储当前所有的Notification的StatusBarNotification对象数组
	public static List<StatusBarNotification[]> mCurrentNotifications = new ArrayList<StatusBarNotification[]>();
	public static int mCurrentNotificationsCounts = 0;
	// 收到新通知后将通知的StatusBarNotification对象赋值给mPostedNotification
	public static StatusBarNotification mPostedNotification;
	// 删除一个通知后将通知的StatusBarNotification对象赋值给mRemovedNotification
	public static StatusBarNotification mRemovedNotification;
	private CancelNotificationReceiver mReceiver = new CancelNotificationReceiver();

	private Handler mMonitorHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_UPDATE_CURRENT_NOS:
				updateCurrentNotifications();
				break;
			default:
				break;
			}
		}
	};

	class CancelNotificationReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action;
			if (intent != null && intent.getAction() != null) {
				action = intent.getAction();
				if (action.equals(ACTION_NLS_CONTROL)) {
					String command = intent.getStringExtra("command");
					if (TextUtils.equals(command, "cancel_last")) {
						if (mCurrentNotifications != null
								&& mCurrentNotificationsCounts >= 1) {
							// 每次删除通知最后一个
							StatusBarNotification sbnn = getCurrentNotifications()[mCurrentNotificationsCounts - 1];
							cancelNotification(sbnn.getPackageName(),
									sbnn.getTag(), sbnn.getId());
						}
					} else if (TextUtils.equals(command, "cancel_all")) {
						// 删除所有通知
						cancelAllNotifications();
					}
				}
			}
		}

	}

	@Override
	public void onCreate() {
		Log.e("xpl", "---------------------onCreate()---------------------");
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_NLS_CONTROL);
		registerReceiver(mReceiver, filter);
		// 在onCreate时第一次调用getActiveNotifications()
		mMonitorHandler.sendMessage(mMonitorHandler
				.obtainMessage(EVENT_UPDATE_CURRENT_NOS));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {

		updateCurrentNotifications();
		mRemovedNotification = sbn;
		Log.e("xpl", "onNotificationRemoved，删除通知");

	}

	@SuppressLint("NewApi") @Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		Log.e("xpl", "onNotificationPosted，接受通知");
		updateCurrentNotifications();
		mPostedNotification = sbn;

		Bundle extras = sbn.getNotification().extras;
		String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
		Bitmap notificationLargeIcon = ((Bitmap) extras
				.getParcelable(Notification.EXTRA_LARGE_ICON));
		Bitmap notificationSmallIcon = ((Bitmap) extras
				.getParcelable(Notification.EXTRA_SMALL_ICON));
		CharSequence notificationText = extras
				.getCharSequence(Notification.EXTRA_TEXT);
		CharSequence notificationSubText = extras
				.getCharSequence(Notification.EXTRA_SUB_TEXT);
		Log.e("xpl", "notificationTitle:" + notificationTitle);
		Log.e("xpl", "notificationText:" + notificationText);
		Log.e("xpl", "notificationSubText:" + notificationSubText);
		Log.e("xpl", "notificationLargeIcon is null:"
				+ (notificationLargeIcon == null));
		Log.e("xpl", "notificationSmallIcon is null:"
				+ (notificationSmallIcon == null));
	}

	private void updateCurrentNotifications() {
		try {
			StatusBarNotification[] activeNos = getActiveNotifications();
			if (mCurrentNotifications.size() == 0) {
				mCurrentNotifications.add(null);
			}
			mCurrentNotifications.set(0, activeNos);
			mCurrentNotificationsCounts = activeNos.length;
		} catch (Exception e) {
			Log.e("xpl", "Should not be here!!");
			e.printStackTrace();
		}
	}

	/**
	 * 获取当前状态栏显示通知总数
	 * 
	 * @return
	 */
	public static StatusBarNotification[] getCurrentNotifications() {
		if (mCurrentNotifications.size() == 0) {
			Log.e("xpl", "mCurrentNotifications size is ZERO!!");
			return null;
		}
		return mCurrentNotifications.get(0);
	}

}
