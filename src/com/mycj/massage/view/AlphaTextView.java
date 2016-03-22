package com.mycj.massage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/11/18.
 */
public class AlphaTextView extends TextView {

	private Drawable[] compoundDrawables;

	public AlphaTextView(Context context) {
		super(context);
		init(context);
	}

	public AlphaTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AlphaTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		setClickable(true);
		

	}

	public final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -50, 0, 1, 0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };
	public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			update(BT_SELECTED);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			update(BT_NOT_SELECTED);
			invalidate();
			break;
		}

		return super.onTouchEvent(event);
	}

	private void update(float[] colors) {
		compoundDrawables = getCompoundDrawables();
		for (int i = 0; i < compoundDrawables.length; i++) {
			Drawable drawable = compoundDrawables[i];
			Log.e("", "drawable :" + drawable);
			if (drawable != null) {
//				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				drawable.setColorFilter(new ColorMatrixColorFilter(colors));
			}
		}
		setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
	}
}
