package com.example.roatation;

import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (android.provider.Settings.System.getInt(getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION, 0) == 1){
			 Toast.makeText(getApplicationContext(), "Rotation ON", Toast.LENGTH_SHORT).show();

			}
			else{
			 Toast.makeText(getApplicationContext(), "Rotation OFF", Toast.LENGTH_SHORT).show();
			}
	}

	

}
