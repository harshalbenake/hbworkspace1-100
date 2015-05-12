package com.example.hbdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.customlib.CustomActionBar;
import com.example.customlib.Hbdemo;
import com.example.customlib.Utility;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CustomActionBar.higtVersionActionbar();
		CustomActionBar.lowVersionActionbar();
		
		Button button=(Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Hbdemo.hbMethod();
				System.out.println("button clicked"+Utility.getDeviceName());
			}
		});
	}

}
