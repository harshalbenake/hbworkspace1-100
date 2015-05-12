package com.example.accelormetersensor;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.app.Activity;

import android.content.Context;

import android.hardware.Sensor;

import android.hardware.SensorEvent;

import android.hardware.SensorEventListener;

import android.hardware.SensorManager;

import android.os.Bundle;

import android.view.View;

import android.widget.ImageView;

import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	private float mLastX, mLastY, mLastZ;

	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float) 2.0;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mInitialized = false;

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	protected void onResume() {

		super.onResume();

		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	protected void onPause() {

		super.onPause();

		mSensorManager.unregisterListener(this);

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

		// can be safely ignored for this demo

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		TextView tvX = (TextView) findViewById(R.id.x_axis);

		TextView tvY = (TextView) findViewById(R.id.y_axis);

		TextView tvZ = (TextView) findViewById(R.id.z_axis);

		ImageView iv = (ImageView) findViewById(R.id.image);

		float x = event.values[0];

		float y = event.values[1];

		float z = event.values[2];

		if (!mInitialized) {

			mLastX = x;

			mLastY = y;

			mLastZ = z;

			tvX.setText("0.0");

			tvY.setText("0.0");

			tvZ.setText("0.0");

			mInitialized = true;

		} else {

			float deltaX = Math.abs(mLastX - x);

			float deltaY = Math.abs(mLastY - y);

			float deltaZ = Math.abs(mLastZ - z);

			if (deltaX < NOISE)
				deltaX = (float) 0.0;

			if (deltaY < NOISE)
				deltaY = (float) 0.0;

			if (deltaZ < NOISE)
				deltaZ = (float) 0.0;

			mLastX = x;

			mLastY = y;

			mLastZ = z;

			tvX.setText(Float.toString(deltaX));

			tvY.setText(Float.toString(deltaY));

			tvZ.setText(Float.toString(deltaZ));

			iv.setVisibility(View.VISIBLE);

			if (deltaX > deltaY) {

				

			} else if (deltaY > deltaX) {

				
			} else {

			

			}

		}
	}
}