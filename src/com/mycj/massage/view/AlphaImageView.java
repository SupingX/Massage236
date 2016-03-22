package com.mycj.massage.view;



import com.mycj.massage.util.AnimationUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/11/18.
 */
public class AlphaImageView extends ImageView{
	
	
    public AlphaImageView(Context context) {
        super(context);
//        AnimationUtils.addTouchDrak(this, true);
    }

    public AlphaImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        AnimationUtils.addTouchDrak(this, true);
    }

    public AlphaImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        AnimationUtils.addTouchDrak(this, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                this.setAlpha(0.5f);
             
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                this.setAlpha(1f);
                invalidate();
                break;
        }

        return super.onTouchEvent(event);
    }
}
