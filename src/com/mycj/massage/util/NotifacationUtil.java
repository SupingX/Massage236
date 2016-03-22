package com.mycj.massage.util;

import java.util.List;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

public class NotifacationUtil {
	public enum Orz{
		WeChat("com.tencent.mm"),
		QQ("com.tencent.mqq"),
		QQ2012("com.tencent.mobileqq"),
		Weibo("com.sina.weibo"),
		FaceBook("com.facebook.katana"),
		Skype("com.skype.rover"),
		Whatsapp("com.whatsapp"),
		Twitter("com.twitter.android");
		public  String packageName;
		Orz(String packageName){
			this.packageName = packageName;
		}
		
		public String getPackageName(){
			return this.packageName;
		}
	}
	
	private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
	/**
	 * 判断应用是否打开了Notification Access（获取应用通知权限）
	 * @param context
	 * @return
	 */
	public static boolean isNotificationAccessEnable(Context context){
		String packageName = context.getPackageName();
		ContentResolver resolver = context.getContentResolver();
		//获取所有的获得通知的应用
		  final String flat = Settings.Secure.getString(resolver, ENABLED_NOTIFICATION_LISTENERS);
		  if (!TextUtils.isEmpty(flat)) {
			String[] names = flat.split(":");
			for (int i = 0; i < names.length; i++) {
				ComponentName cn = ComponentName.unflattenFromString(names[i]);
				if (cn != null) {
					if (TextUtils.equals(packageName, cn.getPackageName())) {
						return true;
					}
				}
			}
		  }
		return false;
	}
	
	private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";  
	
	/**
	 * 打开通知获取权限设置窗口
	 * @param context
	 */
	public static void openNotificationAccess(Context context){
		context.startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
	}
	
	/**
	 * 关闭通知
	 * @param service
	 * @param sn
	 */
	public static void enableNotificationAccess(NotificationListenerService service,StatusBarNotification sn){
		service.cancelNotification(sn.getPackageName(), sn.getTag(), sn.getId());  
	}
	
	public static void clearAllNotificationAccess(NotificationListenerService service,List<StatusBarNotification> sns){
		for (StatusBarNotification statusBarNotification : sns) {
			enableNotificationAccess(service, statusBarNotification);
		}
	}
}

