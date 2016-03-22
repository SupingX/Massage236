package com.mycj.massage.view;

import java.util.Timer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.mycj.massage.R;
import com.mycj.massage.util.DisplayUtil;

public class ColorCircleView extends View {

	private Paint mCompletePaint;
	private Paint paint;
	private Paint mBackgroundPaint;
	private Paint mThumbPaint;
	private Paint mTextThumbPaint;
	private Paint mTextCenterPaint;
	private int times = 100;// 总的计时
	private float mmWidth;
	private Paint mLinePaint;
	private float strokeWidth;
	private float bgStrokeWidth;
	private boolean isCanTouch = true;

	public void setIsCanTouch(boolean isCanTouch) {
		this.isCanTouch = isCanTouch;
	}

	/**
	 * 是否可移动
	 */
	private boolean isTrag;
	private Bitmap bg;
	private float centerX;
	private float centerY;
	private Timer timer;
	private Bitmap thumb;
	private boolean drawOnce = false;

	// private static final int[] COLORS = new int[] { 0xFF00479D, 0xFF1D2088,
	// 0xFF601986, 0xFF920783, 0xFFBE0081, 0xFFE4007F, 0xFFE5006A, 0xFFE5004F,
	// 0xFF22AC38, 0xFF009944, 0xFF009B6B, 0xFF009E96,
	// 0xFF00A0C1, 0xFF00A0E9, 0xFF0086D1, 0xFF0068B7, 0xFFE60033, 0xFFE60012,
	// 0xFFEB6100, 0xFFF39800, 0xFFFCC800, 0xFFFFF100, 0xFFCFDB00, 0xFF8FC31F,
	// 0xFF00479D };
	private static final int[] COLORS = new int[] { 0xFFF16D12

	, 0xFFDF262E //
			, 0xFFFF00FE //
			, 0xFF536AD3//
			, 0xFF66BEED//
			, 0xFF2EA673//
			, 0xFF6CAC19//
			, 0xFFF3BB0B//
			, 0xFFF16D12 //

	};
	/** 当前分钟 0~20 **/
//	private int timePoint = 20;
	/** 当前角度 范围 [0,270] **/
	private double degree = 0.0;

	/**
	 * 圆环的半径
	 */
	private float radius;

//	private Handler mHandler = new Handler() {
//		public void handleMessage(Message msg) {
//			state = STATE_TIMING;
//			if (mOnTimerStateListener != null) {
//				mOnTimerStateListener.OnTimerSelected(state);
//			}
//			timePoint--;// 每1个分钟--
//			if (timePoint <= 0) {
//				stopTimer();
//				degree = 0;
//			}
//			degree = timePoint * perDegree;
//
//			// degree -= Math.ceil(270f / (20 * 60 ));//每秒角度减少量 = 总的角度
//			// /20分钟*60*1000
//			// if (degree <= 0) {
//			// stopTimer();
//			// degree = 0;
//			// }
//			invalidate();
//			mHandler.postDelayed(runTimer, 60 * 1000);
//		};
//	};

	public ColorCircleView(Context context) {
		super(context);
		init(context);
	}

	public ColorCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public ColorCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// int sizeWith = MeasureSpec.getMode(widthMeasureSpec);
		// int sizeMode = MeasureSpec.getSize(widthMeasureSpec);
		// int mWidth = sizeWith;
		// int mHeight = mWidth;
		// if (sizeMode == MeasureSpec.EXACTLY) {
		// Log.e("", "MeasureSpec.EXACTLY");
		// }else if(sizeMode == MeasureSpec.AT_MOST){
		// Log.e("", "MeasureSpec.AT_MOST");
		// }else if(sizeMode == MeasureSpec.UNSPECIFIED){
		// Log.e("", "MeasureSpec.UNSPECIFIED");
		// }else{
		// Log.e("", "MeasureSpec>>>>>>>>>>");
		// }

		// 屏幕的参数
		Point screenMetrics = DisplayUtil.getScreenMetrics(getContext());
		int mWidth = screenMetrics.x * 2 / 3;
		int mHeight = mWidth;
		if (thumb != null) {

			// 中心点
			centerX = mWidth / 2;
			centerY = mHeight / 2;
			// 圆环宽度
			bgStrokeWidth = (float) thumb.getWidth() / 2;
			strokeWidth = bgStrokeWidth - 8f;
			mBackgroundPaint.setStrokeWidth(bgStrokeWidth); // 设置进度条宽度
			mCompletePaint.setStrokeWidth(strokeWidth); // 设置进度条宽度
			setMeasuredDimension(mWidth, mHeight);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 整个view的宽度 （和高度一样）
		mmWidth = getWidth();
		// 获取圆环的半径 为整个View的宽度/2-thumb的宽度/2
		if (thumb != null) {
			radius = (mmWidth - thumb.getWidth()) / 2;
		}
		drawArc(canvas);

	}

	float oldX = 0;
	private boolean toRight;
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (isCanTouch) {
				downX = event.getX();
				float downY = event.getY();
				// Log.i("", "(downX,downX) :" + "(" + downX + "," + downX +
				// ")");
				// 当点在thumb上时
				// 1。求出当前progress对应的thumb坐标
				// float left = (float) ((radius) *
				// Math.cos(degreeToPi(progressToDeGree(progress))) + radius);
				// float top = (float) ((radius) *
				// Math.sin(degreeToPi(progressToDeGree(progress))) + radius);
				// float top = (float) ((radius) *
				// Math.abs(Math.sin(degreeToPi(degree))) + radius);
				// float left = (float) ((radius) *
				// Math.abs(Math.cos(degreeToPi(degree))) + radius);
				PointF p = getPointFromPi2(Math.PI * degree / 180f, radius);
				float left = p.x - thumb.getWidth() / 2;
				float top = p.y - thumb.getHeight() / 2;

				// if (degree > 225 && degree <= 270) {// 第4象限
				// float pi = (float) (Math.PI * degree / 180f - Math.PI / 4 -
				// Math.PI);
				// top = (float) ((radius) * Math.sin(pi) + radius);
				// left = (float) ((radius) * Math.cos(pi) + radius);
				// } else if (degree > 135 && degree <= 225) {// 第1象限
				// float pi = (float) (Math.PI / 4 + Math.PI - Math.PI * degree
				// / 180f);
				// top = (float) (radius - (radius) * Math.sin(pi));
				// left = (float) ((radius) * Math.cos(pi) + radius);
				// } else if (degree > 45 && degree <= 135) {// 第2象限
				// float pi = (float) (Math.PI * degree / 180f - Math.PI / 4);
				// top = (float) (radius - (radius) * Math.sin(pi));
				// left = (float) (radius - (radius) * Math.cos(pi));
				// } else if (degree >= 0 && degree <= 45) {// 第3象限
				// float pi = (float) (Math.PI / 4 - Math.PI * degree / 180f);
				// top = (float) (radius + (radius) * Math.sin(pi));
				// left = (float) (radius - (radius) * Math.cos(pi));
				// }

				// 2。是否按在 thmub上
//				if (!(downX < left || downX > left + thumb.getWidth() || downY < top || downY > top + thumb.getHeight())) {
					mThumbPaint.setAlpha(100);
					
					/**
					 * 点击 改变位置
					 */
//					double downDegree=0;
//					double pi = getPi(new PointF(downX, downY), new PointF(centerX, centerY));// 当前点对应的弧度（例如：Pi/2）
//					if (downX > centerX && downY > centerY) {// 第四象限
//						// Log.i("", "第四象限");
//						downDegree = (double) ((pi + Math.PI / 2) * 180 / Math.PI);
//					} else if (downX > centerX && downY < centerY) {
//						// Log.i("", "第一象限");
//						downDegree = (double) ((-pi + Math.PI / 2) * 180 / Math.PI);
//					} else if (downX < centerX && downY < centerY) {
//						// Log.i("", "第二象限");
//						downDegree = (double) ((pi + Math.PI / 2 + Math.PI) * 180 / Math.PI);
//					} else if (downX < centerX && downY > centerY) {
//						// Log.i("", "第三象限");
//						downDegree = (double) ((Math.PI + Math.PI / 2 - pi) * 180 / Math.PI);
//					} else {
//						break;
//					}
//					int downProgress = (int) Math.floor(downDegree / perDegree);
//					
//					if (Math.abs(downProgress-progress)<90) {
//						progress = downProgress;
//					}
//					if (mOnTimePointChange != null) {
//						mOnTimePointChange.onChanging(progress);
//					}
					
					
					isTrag = true;// 可以移动
					invalidate();
					return true;
//				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (isCanTouch) {
				float moveX = event.getX();
				float moveY = event.getY();
				if (isTrag) {// 当可以移动时，根据移动的坐标 改变progress
					double pi = getPi(new PointF(moveX, moveY), new PointF(centerX, centerY));// 当前点对应的弧度（例如：Pi/2）
					double moveDegree = 0;
					if (moveX > centerX && moveY > centerY) {// 第四象限
						// Log.i("", "第四象限");
						moveDegree = (double) ((pi + Math.PI / 2) * 180 / Math.PI);
					} else if (moveX > centerX && moveY < centerY) {
						// Log.i("", "第一象限");
						moveDegree = (double) ((-pi + Math.PI / 2) * 180 / Math.PI);
					} else if (moveX < centerX && moveY < centerY) {
						// Log.i("", "第二象限");
						moveDegree = (double) ((pi + Math.PI / 2 + Math.PI) * 180 / Math.PI);
					} else if (moveX < centerX && moveY > centerY) {
						// Log.i("", "第三象限");
						moveDegree = (double) ((Math.PI + Math.PI / 2 - pi) * 180 / Math.PI);
					} else {
						break;
					}
					
					int moveProgress = (int) Math.floor(moveDegree / perDegree);
					
					if (Math.abs(moveProgress-progress)<10) {
						progress = moveProgress;
					}
					invalidate();
					if (mOnTimePointChange != null) {
						mOnTimePointChange.onChanging(progress);
					}
					return true;
				}}
			break;
		case MotionEvent.ACTION_UP:
			if (isCanTouch) {
				mThumbPaint.setAlpha(255);
				isTrag = false;// 清空
				invalidate();
				if (mOnTimePointChange != null) {
					mOnTimePointChange.onChanged(progress);
				}
				if (mOnTouchCancelListener != null) {
					mOnTouchCancelListener.onCancel(progress);
				}
			}
			break;

		default:
			break;
		}

		// return super.onTouchEvent(event);
		return false;
	}

	/**
	 * 当前时间点
	 * 
	 * @param timePoint
	 * @return
	 */
	// private float progressToDeGree(int progress) {
	// float degree = 270 * progress / max;
	// //Log.i("", "当前progress对应的degree ：" + degree);
	// return degree;
	// }

//	public int getTimePoint() {
//		int number = (int) Math.floor (degree / perDegree);
//		return number;
//	}

	public void setProgress(int progress) {
		// Log.i("", "setTimePoint() :	" + timePoint);
//		this.degree = timePoint * perDegree;
		this.progress = progress;
		invalidate();
//		if (mOnTimePointChange != null) {
//			mOnTimePointChange.onChanged(timePoint);
//		}
	}
	
	public int getProgress(){
		return this.progress;
	}

	/**
	 * 开始倒计时
	 * 
	 * @param degree
	 */
//	public void startTimer() {
//		state = STATE_START;
//		isCanTouch = false;
//		if (mOnTimerStateListener != null) {
//			mOnTimerStateListener.OnTimerSelected(state);
//		}
//		runTimer = new Runnable() {
//			public void run() {
//				Message message = new Message();
//				message.what = 1;
//				mHandler.sendMessage(message);
//			}
//		};
//		mHandler.postDelayed(runTimer, 60 * 1000);
//		// timer = new Timer(true);
//		// timer.schedule(runTimer, 1000, 1000);// 延迟1000ms后执行，1000ms执行一次
//
//	}

	/**
	 * 结束倒计时
	 * 
	 * @param degree
	 */
//	public void stopTimer() {
//		isCanTouch = true;
//		// if (timer != null) {
//		// timer.cancel();
//		// timer = null;
//		//
//		// state = STATE_CANCEL;
//		// if (mOnTimerStateListener!=null) {
//		// mOnTimerStateListener.OnTimerState(state);
//		// }
//		// }
//		if (runTimer != null) {
//			mHandler.removeCallbacks(runTimer);
//
//			state = STATE_CANCEL;
//			if (mOnTimerStateListener != null) {
//				mOnTimerStateListener.OnTimerSelected(state);
//			}
//		}
//	}

	private void init(Context context) {
		setClickable(true);
		setLongClickable(true);
		setFocusable(true);

		mCompletePaint = new Paint();
		mCompletePaint.setAntiAlias(true); // 消除锯齿
		mCompletePaint.setColor(Color.parseColor("#EF7601"));
		mCompletePaint.setStyle(Paint.Style.STROKE); // 绘制空心圆

		mTextThumbPaint = new Paint();
		mTextThumbPaint.setAntiAlias(true); // 消除锯齿
		mTextThumbPaint.setColor(Color.WHITE);
		mTextThumbPaint.setStrokeWidth(10);
		mTextThumbPaint.setStyle(Paint.Style.FILL); // 绘制空心圆
		mTextThumbPaint.setTextSize(60);

		mTextCenterPaint = new Paint();
		mTextCenterPaint.setAntiAlias(true); // 消除锯齿
		mTextCenterPaint.setColor(Color.WHITE);
		mTextCenterPaint.setStrokeWidth(10);
		mTextCenterPaint.setStyle(Paint.Style.FILL); // 绘制空心圆
		mTextCenterPaint.setTextSize(200);

		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true); // 消除锯齿
		mLinePaint.setColor(Color.parseColor("#66dddddd"));
		mLinePaint.setStrokeWidth(2);
		mLinePaint.setStyle(Paint.Style.FILL); // 绘制空心圆

		mCompletePaint.setStrokeJoin(Paint.Join.ROUND);
		mCompletePaint.setColor(Color.argb(160, 255, 255, 255));

		mCompletePaint.setStrokeCap(Paint.Cap.ROUND); // 设置圆角

		mBackgroundPaint = new Paint();
		mBackgroundPaint.setAntiAlias(true); // 消除锯齿
		mBackgroundPaint.setStyle(Paint.Style.STROKE); // 绘制空心圆

		mBackgroundPaint.setStrokeJoin(Paint.Join.ROUND);
		mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND); // 设置圆角

		paint = new Paint();
		paint.setAntiAlias(true); // 消除锯齿

		mThumbPaint = new Paint();
		mThumbPaint.setAntiAlias(true); // 消除锯齿
		mThumbPaint.setStyle(Paint.Style.FILL); // 绘制空心圆
		thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_time_bar_button);
		// bg = BitmapFactory.decodeResource(getResources(),
		// R.drawable.ic_jingdian_bar_bg);
		if (isPad(context)) {
			Matrix m = new Matrix();
			m.setScale(2f, 2f);
			thumb = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(), thumb.getHeight(), m, false);
		}
		perDegree = 360F / times;
		
	}

	private int measureHeight(int heightMeasureSpec) {
		int result = 0;
		int mode = MeasureSpec.getMode(heightMeasureSpec);
		int size = MeasureSpec.getSize(heightMeasureSpec);
		if (mode == MeasureSpec.EXACTLY) {
			result = size;
		} else if (mode == MeasureSpec.AT_MOST) {
			result = getHeight() - getPaddingTop() - getPaddingBottom();
			result = Math.min(result, size);
		}
		return result;
	}

	private int measureWidth(int widthMeasureSpec) {
		int result = 0;
		int mode = MeasureSpec.getMode(widthMeasureSpec);
		int size = MeasureSpec.getSize(widthMeasureSpec);
		if (mode == MeasureSpec.EXACTLY) {
			result = size;
		} else if (mode == MeasureSpec.AT_MOST) {
			result = getWidth() - getPaddingLeft() - getPaddingRight();
			result = Math.min(result, size);
		}
		return result;
	}
	
	private Rect rectBtn = new Rect();
	private float perDegree;
	
	private void drawArc(Canvas canvas) {
		if (thumb != null) {
			// 画背景
			RectF oval = new RectF(thumb.getWidth() / 2, thumb.getWidth() / 2, mmWidth - thumb.getWidth() / 2, mmWidth - thumb.getWidth() / 2);
			SweepGradient shader = new SweepGradient(centerX, centerY, COLORS, null);
			mBackgroundPaint.setShader(shader);
			canvas.drawArc(oval, -90, 360, false, mBackgroundPaint);
			// 画进度
			canvas.drawArc(oval, -90, (float) (perDegree * progress == 0 ? 1 : perDegree * progress), false, mCompletePaint);
			// 画蚯蚓
			drawFushe(canvas);
			// 画游标
			float pi = (float) (Math.PI * perDegree * progress / 180f);
			PointF p = getPointFromPi2(pi, radius);
			
			float left = p.x - thumb.getWidth() / 2;
			float top = p.y - thumb.getHeight() / 2;
			rectBtn.set((int)left, (int)top, (int)(left+thumb.getWidth()), (int)(top + thumb.getHeight()));
			canvas.drawBitmap(thumb, left, top, mThumbPaint);
			// 画游标数字
		
			int number = (int) (degree / perDegree);
			String text = String.valueOf(progress + 1);
			Rect rectText = new Rect();
			mTextThumbPaint.getTextBounds(text, 0, text.length(), rectText);
			canvas.drawText(text, left + thumb.getWidth() / 2 - rectText.width() / 2, top + thumb.getHeight() / 2 + rectText.height() / 2, mTextThumbPaint);
			// 画中间数字
			if (isTrag) {
				Rect rectTextCenter = new Rect();
				mTextCenterPaint.getTextBounds(text, 0, text.length(), rectTextCenter);
				canvas.drawText(text, centerX - rectTextCenter.centerX(), centerY - rectTextCenter.centerY(), mTextCenterPaint);
			}
		}
	}

	private void drawFushe(Canvas canvas) {
		int per = 360 / 2;
		for (int i = 0; i < per; i++) {
			PointF pointFromPi1 = getPointFromPi2((Math.PI * 2 * i) / 180f, radius - bgStrokeWidth / 2 + 4);
			PointF pointFromPi2 = getPointFromPi2((Math.PI * 2 * i) / 180f, radius + bgStrokeWidth / 2 - 4);
			canvas.drawLine(pointFromPi1.x, pointFromPi1.y, pointFromPi2.x, pointFromPi2.y, mLinePaint);
		}
	}

	/**
	 * 获取离中心点radius距离 角度Pi的点
	 * 
	 * @param pi
	 * @param radius
	 * @return
	 */
	private PointF getPointFromPi2(double pi, float radius) {
		float x = 0F;
		float y = 0F;
		if (pi >= 0 && pi <= Math.PI / 2) {
			x = (float) (mmWidth / 2 + radius * Math.sin(pi));
			y = (float) (mmWidth / 2 - radius * Math.cos(pi));
		} else if (pi >= Math.PI / 2 && pi <= Math.PI) {
			x = (float) (mmWidth / 2 + radius * Math.sin(Math.PI - pi));
			y = (float) (mmWidth / 2 + radius * Math.cos(Math.PI - pi));
		} else if (pi >= Math.PI && pi <= Math.PI + Math.PI / 2) {
			x = (float) (mmWidth / 2 - radius * Math.cos(Math.PI + Math.PI / 2 - pi));
			y = (float) (mmWidth / 2 + radius * Math.sin(Math.PI + Math.PI / 2 - pi));
		} else if (pi >= Math.PI + Math.PI / 2 && pi <= 2 * Math.PI) {
			x = (float) (mmWidth / 2 - radius * Math.sin(2 * Math.PI - pi));
			y = (float) (mmWidth / 2 - radius * Math.cos(2 * Math.PI - pi));
		}
		// //Log.e("", "(x,y)  :" + "(" + x+","+y+")");
		return new PointF(x, y);
	}

	private double getPi(PointF pointF1, PointF pointF2) {
		double _x = Math.abs(pointF1.x - pointF2.x);
		double _y = Math.abs(pointF1.y - pointF2.y);
		if (_x == 0) {
			if (pointF1.y - pointF2.y > 0) {

				return Math.PI / 4 + Math.PI;
			} else {
				return Math.PI / 4;
			}
		}
		if (_y == 0) {
			return Math.PI / 4 + Math.PI / 2;
		}
		// Log.i("", "移动后的pi ：" + Math.atan2(_y, _x));
		// Math.atan2(centerY, centerX);//ATAN2(a,b)的取值范围介于 -pi 到 pi 之间（不包括
		// -pi），
		return Math.atan2(_y, _x);
	}

	/** 回调接口 **/
	private OnTimePointChangeListener mOnTimePointChange;

	public void setOnTimePointChangeListener(OnTimePointChangeListener l) {
		this.mOnTimePointChange = l;
	}

	public interface OnTimePointChangeListener {
		public void onChanged(int progress);
		public void onChanging(int progress);
	}

	public interface onTouchCancelListener {
		public void onCancel(int time);
	}

	public void setonTouchCancelListener(onTouchCancelListener l) {
		mOnTouchCancelListener = l;
	}

	private onTouchCancelListener mOnTouchCancelListener;

	public interface OnTimerStateListener {
		public void OnTimerSelected(int state);
		public void OnTimerSelecting(int state);
	}

	private OnTimerStateListener mOnTimerStateListener;
	public static final int STATE_START = 0x01;
	public static final int STATE_TIMING = 0x02;
	public static final int STATE_CANCEL = 0x00;
	private int state = STATE_CANCEL;
	private Runnable runTimer;
	private float downX;
	private int progress;

	public void setOnTimerStateListener(OnTimerStateListener l) {
		this.mOnTimerStateListener = l;
	}

	/**
	 * 判断是否为平板
	 *
	 * @return
	 */
	private boolean isPad(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		// 屏幕宽度
		float screenWidth = display.getWidth();
		// 屏幕高度
		float screenHeight = display.getHeight();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		// 屏幕尺寸
		double screenInches = Math.sqrt(x + y);
		// 大于6尺寸则为Pad
		if (screenInches >= 6.0) {
			return true;
		}
		return false;
	}
}
