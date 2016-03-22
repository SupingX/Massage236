package com.mycj.massage.view;

import com.mycj.massage.R;
import com.mycj.massage.bean.Ems;
import com.mycj.massage.util.DisplayUtil;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainLinearLayout extends LinearLayout{

	private static final int DEFF = 240;
	private LayoutParams params5;
	private AlphaTextView tv5;
	private AlphaTextView tv1;
	private AlphaTextView tv2;
	private AlphaTextView tv3;
	private AlphaTextView tv4;

	public MainLinearLayout(Context context) {
		super(context);
		init(context);
	}

	public MainLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MainLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}


	
	public void init(Context context){
		setBackgroundResource(R.drawable.ic_bg_main);
		setOrientation(LinearLayout.VERTICAL);
		Point p = DisplayUtil.getScreenMetrics(context);
		float desy = DisplayUtil.getScreenRate(context);
		Ems[] values = Ems.values();
		for (int i = 0; i < values.length; i++) {
			ems = values[i];
			AlphaTextView tv = new AlphaTextView(context);
			Resources resources = context.getResources();
			tv.setText(resources.getString(ems.getText()));
			Drawable d = context.getResources().getDrawable(ems.getImg());
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			tv.setCompoundDrawables(null, d, null, null);
			tv.setTag(i);
		
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mOnMenuClickListener!=null) {
						mOnMenuClickListener.onMenuClick(v);
					}
				}
			});
		
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.weight=1;
			if (i==0 ) {
				params.topMargin = DisplayUtil.px2dip(DEFF,desy);
			}if (i==values.length-1) {
				Log.e("MainLinearLayout", "height : " + tv.getTextSize());
				params.bottomMargin = DisplayUtil.px2dip(240-tv.getTextSize(),desy);
			}
			params.gravity = Gravity.CENTER;
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			tv.setLayoutParams(params);
			addView(tv);
		}
		

		
		
	}
	
	public interface OnMenuClickListener{
		public void onMenuClick(View v );
	}
	
	private OnMenuClickListener mOnMenuClickListener;
	private Ems ems;
	public void setOnMenuClickListener(OnMenuClickListener mOnMenuClickListener){
		this.mOnMenuClickListener = mOnMenuClickListener;
	}

}
