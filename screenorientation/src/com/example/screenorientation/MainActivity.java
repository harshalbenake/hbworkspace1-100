package com.example.screenorientation;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		onConfigurationChanged(new Configuration());
	}
@Override
public void onConfigurationChanged(Configuration newConfig) {
	// TODO Auto-generated method stub
	super.onConfigurationChanged(newConfig);
	 
	if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
	{
		Toast.makeText(getApplicationContext(), "portrait", Toast.LENGTH_SHORT).show();
        System.out.println("portrait");
	}
	else if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE) {
		 Toast.makeText(getApplicationContext(), "landscape", Toast.LENGTH_SHORT).show();
	        System.out.println("landscape");
	}
	else
	{
		 Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
	        System.out.println("none");
	}
}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
