package com.example.zoomtry;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class MainzoomActivity extends Activity {
	ImageView iv;
	Button on,off;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainzoom);
		
		on=(Button)findViewById(R.id.button1);
		on.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Animation ani=AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomon);
				iv=(ImageView)findViewById(R.id.imageView1);
				iv.startAnimation(ani);
			}
		});
		off=(Button)findViewById(R.id.button2);
		off.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Animation ani=AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomoff);
				iv=(ImageView)findViewById(R.id.imageView1);
				iv.startAnimation(ani);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mainzoom, menu);
		return true;
	}

}
