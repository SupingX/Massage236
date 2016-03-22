package com.mycj.massage.ui.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.litepal.crud.DataSupport;
import org.litepal.util.DBUtility;

import com.mycj.massage.R;
import com.mycj.massage.R.anim;
import com.mycj.massage.R.id;
import com.mycj.massage.R.layout;
import com.mycj.massage.adapter.HistoryAdapter;
import com.mycj.massage.bean.History;
import com.mycj.massage.bean.LitPalManager;
import com.mycj.massage.util.DateUtil;
import com.mycj.massage.view.AlertDialog;
import com.mycj.massage.view.AlphaImageView;
import com.mycj.massage.view.LoadingDialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryActivity extends Activity implements OnClickListener {

	private ListView lvHistory;
	private List<History> historys;
	private HistoryAdapter adapter;
	private TextView tvDate;
	private AlphaImageView imgPrevious;
	private AlphaImageView imgNext;
	private AlphaImageView imgBack;
	private Date date;
	private AlertDialog clearDialog;
	private AlphaImageView imgClear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		historys = new ArrayList<History>();
		initViews();
		lvHistory = (ListView) findViewById(R.id.lv_history);

		loadHistroy();
		adapter = new HistoryAdapter(historys,this);
		lvHistory.setAdapter(adapter);
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.massager_in, R.anim.history_out);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			overridePendingTransition(R.anim.massager_in, R.anim.history_out);
			break;
		case R.id.img_previous:
			date = DateUtil.getDateOfDiffDay(date, -1);
			updateDate(date);
			new LoadHistoryAsyncTask().execute(date);
			break;
		case R.id.img_next:
			date = DateUtil.getDateOfDiffDay(date, 1);
			updateDate(date);
			new LoadHistoryAsyncTask().execute(date);
			break;
		case R.id.img_clear:
			
//			History h = new History(DateUtil.dateToString(new Date(), "yyyyMMdd hh:mm:ss"), 12, 2);
//			h.save();
//			
//			List<History> list = DataSupport.findAll(History.class);
//			Log.i("xpl","list : " +  list);
			
			clearDialog = new AlertDialog(HistoryActivity.this).builder().setMsg(getString(R.string.check_for_clear_history)).setNegativeButton(getString(R.string.cancel), new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearDialog.dismiss();
				}
			}).setPositiveButton(getString(R.string.clear), new OnClickListener() {

				@Override
				public void onClick(View v) {
					DataSupport.deleteAll(History.class);
					historys.clear();
					adapter.notifyDataSetChanged();
					clearDialog.dismiss();
				}
			});
			clearDialog.show();
			break;
		default:
			break;
		}
	}

	private void initViews() {
		tvDate = (TextView) findViewById(R.id.tv_date);
		imgPrevious = (AlphaImageView) findViewById(R.id.img_previous);
		imgNext = (AlphaImageView) findViewById(R.id.img_next);
		imgBack = (AlphaImageView) findViewById(R.id.img_back);
		imgClear = (AlphaImageView) findViewById(R.id.img_clear);

		date = new Date();
		updateDate(date);
		tvDate.setText(DateUtil.dateToString(date, "yyyy/MM/dd"));
		imgPrevious.setOnClickListener(this);
		imgClear.setOnClickListener(this);
		imgBack.setOnClickListener(this);
		imgNext.setOnClickListener(this);
	}

	private void loadHistroy() {
//		for (int i = 0; i < 100; i++) {
//			historys.add(new History(DateUtil.dateToString(date, "yyyyMMdd hh:mm:ss"), i,new Random().nextInt(5)));
//		}
		
		new LoadHistoryAsyncTask().execute(date);
	}

	private void updateDate(Date date) {
		tvDate.setText(DateUtil.dateToString(date, "yyyy/MM/dd"));
	}
	private LoadingDialog loadingDialog;
	private class LoadHistoryAsyncTask extends AsyncTask<Date, Void, List<History>> {
		
	

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadingDialog = new LoadingDialog(HistoryActivity.this).builder().setCancelable(false)
					.setCanceledOnTouchOutside(false);
			loadingDialog.show();
			
		}
		
		@Override
		protected List<History> doInBackground(Date... params) {
			Date date = params[0];
			List<History> list = LitPalManager.instance().getHistiryListByMonth(date);
			Log.e("", "list :" + list);
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<History> result) {
			loadingDialog.dismiss();
			historys.clear();
			if (result != null && result.size() > 0) {
				historys.addAll(result);
			}
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}
}
