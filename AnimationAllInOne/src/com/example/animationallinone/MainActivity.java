package com.example.animationallinone;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ImageView imageView1=(ImageView)findViewById(R.id.imageView1);
		final ImageView imageView2=(ImageView)findViewById(R.id.imageView2);
		final ImageView imageView3=(ImageView)findViewById(R.id.imageView3);
		
		final Animation anim_rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate);
		final Animation anim_zoominout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoominout);
		final Animation anim_fadeinout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fadeinout);


		imageView1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				imageView1.setAnimation(anim_rotate);
				imageView1.startAnimation(anim_rotate);
			}
		});
		
		imageView2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				imageView2.setAnimation(anim_zoominout);
				imageView2.startAnimation(anim_zoominout);
			}
		});
		imageView3.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				imageView3.setAnimation(anim_fadeinout);
				imageView3.startAnimation(anim_fadeinout);
	}	
});
		

	}

	
}
