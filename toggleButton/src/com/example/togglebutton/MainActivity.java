package com.example.togglebutton;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ToggleButton toggleButton=(ToggleButton)findViewById(R.id.toggleButton);
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked==true)
				{
					Toast.makeText(getApplicationContext(), "Toggle is on.", Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Toggle is off.", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

}
