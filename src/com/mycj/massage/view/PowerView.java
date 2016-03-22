package com.mycj.massage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class PowerView extends View{

	private Paint paint;
	private Rect rect;

	public PowerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public PowerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}



	public PowerView(Context context) {
		super(context);
		init(context);
	}
	
	
	private int max = 100 ;
	private int progress = 30;
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getHeight();
		int left = 0;
		int top = height/4;
		int right = getRightByProgress(progress);
		int bottom = getHeight()-height/4;
		rect.set(left, top, right, bottom);
		canvas.drawRect(rect, paint);
	}
	private int getRightByProgress(int progress) {
		
		int right = (int) (getWidth() * progress * 1.0f/max) ;
		return right;
	}
	
	public void setProgress(int progress){
		this.progress = progress;
		invalidate();
	}
	private void init(Context context) {
		paint = new Paint();
		paint.setColor(Color.parseColor("#ffffff"));
		rect = new Rect();
	}
}
