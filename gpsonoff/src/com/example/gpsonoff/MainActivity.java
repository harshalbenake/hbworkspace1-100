package com.example.gpsonoff;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button=(Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			            Toast.makeText(getApplicationContext(), "GPS is enabled in your device", Toast.LENGTH_SHORT).show();
			        }else{
			            Toast.makeText(getApplicationContext(), "GPS is disable in your device", Toast.LENGTH_SHORT).show();

			        }
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
