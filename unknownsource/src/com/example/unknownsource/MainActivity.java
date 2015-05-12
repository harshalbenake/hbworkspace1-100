package com.example.unknownsource;

import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	boolean success;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button=(Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Unknown source activated", Toast.LENGTH_LONG).show();
				int result = Settings.Secure.getInt(getContentResolver(),
				Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
				if (result == 0) {
				    success = Settings.Secure.putString(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, "1");    
				}
			}
		});
	}

	

}
