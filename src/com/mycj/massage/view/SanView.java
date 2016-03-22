package com.mycj.massage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SanView extends View{

	private RectF rectF;
	private Path path;
	private Path pathText;
	private Paint paint;
	private Paint paintText;

	public SanView(Context context) {
		super(context);
		init(context);
	}

	public SanView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public SanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		
		rectF.set(0, 0, width, height);
		path.moveTo(0, height);
		path.lineTo(width, 0);
		path.lineTo(width, height);
		canvas.drawPath(path, paint);
		
		pathText.moveTo(width/3, height);
		pathText.lineTo(width, height/3);
		canvas.drawTextOnPath("历史记录", pathText, width /6, width /6, paintText);
		
//		pathText.moveTo(left+width/2, bottom);
//		pathText.lineTo(right, top+height/2);
	}
	
	private void init(Context context) {
		paint = new Paint();
		paint.setColor(Color.parseColor("#A73D75"));
	
		paint.setAntiAlias(true);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(5);
		
		paintText = new Paint();
		paintText.setColor(Color.WHITE);
		paintText.setAntiAlias(true);
		paintText.setTextSize(30);
		
		path = new Path();
		rectF = new RectF();
		pathText = new Path();

	}
	
	
	

}
