package com.mycj.massage.bean;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.litepal.crud.DataSupport;

import android.util.Log;

import com.mycj.massage.util.DateUtil;

public class LitPalManager {
	private static LitPalManager litePal;

	private LitPalManager() {
	}

	public static LitPalManager instance() {
		if (litePal == null) {
			litePal = new LitPalManager();
		}
		return litePal;
	}
	
	public List<History> getAllHistiryList(){
		List<History> list = DataSupport.findAll(History.class);
		return list;
	}
	
	public List<History> getHistiryListByMonth(Date date){
		String dateStr = DateUtil.dateToString(date, "yyyyMMdd")+"%";
		Log.e("xpl", "dateStr :" + dateStr);
		List<History> list = DataSupport.where("date like ?",dateStr).find(History.class);
		Log.e("xpl", "list :" + list);
//		int size = new Random().nextInt(10);
//		for (int i = 0; i < size; i++) {
//			list.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), new Random().nextInt(100),new Random().nextInt(5)));
//		}
		
//		list.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), new Random(100).nextInt()));
//		list.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), new Random(100).nextInt()));
//		list.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), new Random(100).nextInt()));
//		list.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), new Random(100).nextInt()));
//		list.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), new Random(100).nextInt()));
		return list;
	}
	public List<History> getHistiryListByTime(String date){
		List<History> list = DataSupport.where("date like ?",date).find(History.class);
//		int size = new Random().nextInt(10);
//		for (int i = 0; i < size; i++) {
//			list.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), new Random().nextInt(100),new Random().nextInt(5)));
//		}
//		list.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), new Random(100).nextInt()));
//		list.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), new Random(100).nextInt()));
//		list.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), new Random(100).nextInt()));
//		list.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), new Random(100).nextInt()));
//		list.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), new Random(100).nextInt()));
		return list;
	}
}
