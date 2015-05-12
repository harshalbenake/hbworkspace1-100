package com.example.buttonpressed;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class MainActivity extends Activity {
	Button button1;
	Button button2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button1 = (Button) findViewById(R.id.button1);
	    button2 = (Button) findViewById(R.id.button2);
		button1.setOnTouchListener(new OnTouchListener() {

         

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				button1.setPressed(true);
				button2.setPressed(false);
				return true;
			}
        });
		button2.setOnTouchListener(new OnTouchListener() {

	         

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				button2.setPressed(true);
				button1.setPressed(false);
				return true;
			}
        });
	}

}
