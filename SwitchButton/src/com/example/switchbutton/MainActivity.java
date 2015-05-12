package com.example.switchbutton;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Switch onOffSwitch = (Switch)  findViewById(R.id.on_off_switch); 
		onOffSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),"isChecked"+isChecked, Toast.LENGTH_LONG).show();
			}
		});

	}



}
