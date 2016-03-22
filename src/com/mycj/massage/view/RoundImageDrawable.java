package com.mycj.massage.view;

import android.R;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

public class RoundImageDrawable extends Drawable{
//	private Bitmap bitmap;
	private Paint paint;
	private RectF rectF;
	private Path path;
	private Path pathText;
	private int width,height;
	public  RoundImageDrawable (int width , int height){
//		this.bitmap = bitmap;
//		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
		paint = new Paint();
		path = new Path();
		paint.setColor(Color.parseColor("#A73D75"));
		paint.setTextSize(30);
		this.width = width;
		this.height = height;
		paint.setAntiAlias(true);
//		paint.setShader(shader);
	}
	
	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
		rectF = new RectF(left, top, right, bottom);
	
		path.moveTo(left, bottom);
		path.lineTo(right, top);
		path.lineTo(right, bottom);
		
		pathText = new Path();
		pathText.moveTo(left+width/2, bottom);
		pathText.lineTo(right, top+height/2);
		
		
	}
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawPath(path, paint);
		paint.setColor(Color.WHITE);
		
		canvas.drawTextOnPath("历史记录", pathText, 20, 40, paint);
	}
	
	@Override
	public int getIntrinsicHeight() {
		return 200;
	}
	
	@Override
	public int getIntrinsicWidth() {
		return 200;
	}
	
	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
		paint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		paint.setColorFilter(cf);
	}

}
