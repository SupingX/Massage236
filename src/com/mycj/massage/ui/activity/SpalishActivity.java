package com.mycj.massage.ui.activity;

import com.mycj.massage.MainActivity;
import com.mycj.massage.R;
import com.mycj.massage.R.id;
import com.mycj.massage.R.layout;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

public class SpalishActivity extends Activity {

	private TextView tvMassage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spalish);
		tvMassage = (TextView) findViewById(R.id.tv_massage);
		startAnimator();
	}

	private void startAnimator() {
		ObjectAnimator animator1 = ObjectAnimator.ofFloat(tvMassage, "alpha", 0f,1.0f);
		animator1.setDuration(2000);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(tvMassage, "scaleX", 0.7f,1.2f);
		animator2.setDuration(2000);
		animator2.setInterpolator(new OvershootInterpolator());
		ObjectAnimator animator3 = ObjectAnimator.ofFloat(tvMassage, "scaleY", 0.7f,1.2f);
		animator3.setDuration(2000);
		animator3.setInterpolator(new OvershootInterpolator());
		AnimatorSet set1 = new AnimatorSet();
		set1.play(animator2).with(animator3);
		
		AnimatorSet set2 = new AnimatorSet();
		set2.play(animator1).with(set1);
		set2.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				startActivity(new Intent(SpalishActivity.this,MainActivity.class));
				finish();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
//				startActivity(new Intent(SpalishActivity.this,MainActivity.class));
//				finish();
			}
		});
		set2.start();
		
	}
	
	
}
