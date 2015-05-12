package com.example.proximatysensordemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
    private PowerManager mPowerManager;
	private Sensor mSensor;
	private WakeLock mWakeLock;
	private Window window;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		
		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
		  mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "INFO");
		  
		
		  window = this.getWindow();
		 
	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@SuppressLint("Wakelock")
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.values[0] == 0)
		{
			Toast.makeText(getApplicationContext(), "value 0", Toast.LENGTH_LONG).show();
	
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
			mWakeLock.acquire();
		}
		else
		{
			
			Toast.makeText(getApplicationContext(), "value 1", Toast.LENGTH_LONG).show();
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

		}

	}
}
