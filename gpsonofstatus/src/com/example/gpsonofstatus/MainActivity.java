package com.example.gpsonofstatus;

import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LocationManager locMgr =
				(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
				locMgr.addGpsStatusListener(new Listener() {
					
					@Override
					public void onGpsStatusChanged(int event) {
						// TODO Auto-generated method stub
						switch(event) {
						case GpsStatus.GPS_EVENT_STARTED:
						Log.d("POWER", "GPS ON > "+event);
						Toast.makeText(getApplicationContext(), "gps on", Toast.LENGTH_LONG).show();
						break ;
						case GpsStatus.GPS_EVENT_STOPPED:
						Log.d("POWER", "GPS OFF > "+event);
						Toast.makeText(getApplicationContext(), "gps off", Toast.LENGTH_LONG).show();

						break ;
						}
					}
				});
						
						
						
	}
}
	
	
		
