package com.example.flightmode;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		IntentFilter intentFilter = new IntentFilter("android.intent.action.SERVICE_STATE");

		BroadcastReceiver receiver = new BroadcastReceiver() {
		      @Override
		      public void onReceive(Context context, Intent intent) {
		            Log.d("AirplaneMode", "Service state changed");
		        	Toast.makeText(getApplicationContext(), "Service state changed", Toast.LENGTH_LONG).show();
		        	boolean isEnabled = isAirplaneModeOn(context);
		        /*	 setSettings(context, isEnabled?1:0);
		 	        Intent intent_mode = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		 	       intent_mode.putExtra("state", !isEnabled);
		 	        context.sendBroadcast(intent_mode);*/
		 	        
			        if(isEnabled==true)
			        { setSettings(context, isEnabled?1:0);
			        	Toast.makeText(getApplicationContext(), "Flight mode on", Toast.LENGTH_LONG).show();
			        	Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
			        	Intent newIntent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			        	newIntent.putExtra("state", false);
			        	sendBroadcast(newIntent);
			        }
			        else
			        { setSettings(context, isEnabled?1:0);
			        	Toast.makeText(getApplicationContext(), "Flight mode off", Toast.LENGTH_LONG).show();
			        }

		      }

			@SuppressLint("NewApi")
			private void setSettings(Context context, int value) {
				// TODO Auto-generated method stub

		        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
		            Settings.System.putInt(
		                      context.getContentResolver(),
		                      Settings.System.AIRPLANE_MODE_ON, value);
		        } else {
		            Settings.Global.putInt(
		                      context.getContentResolver(),
		                      Settings.Global.AIRPLANE_MODE_ON, value);
		        }       
		    
			}

			@SuppressLint("NewApi")
			public boolean isAirplaneModeOn(Context context) {
	        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
	            return Settings.System.getInt(context.getContentResolver(), 
	                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;          
	        } else {
	            return Settings.Global.getInt(context.getContentResolver(), 
	                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
	        }       
	    }
		};
		
		registerReceiver(receiver, intentFilter);
		
		
	}
/*@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
	Intent newIntent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
	newIntent.putExtra("state", false);
	sendBroadcast(newIntent);
}*/
}
