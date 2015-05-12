package com.example.myandroidappactivity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
public class MyAndroidAppActivity extends Activity {
	 
	  private CheckBox chkIos, chkAndroid, chkWindows,chktry;
	  private Button btnDisplay;
	 
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	 
		addListenerOnChkIos();
		addListenerOnButton();
	  }
	 
	  public void addListenerOnChkIos() {
	 
		chkIos = (CheckBox) findViewById(R.id.chkIos);
	 
		chkIos.setOnClickListener(new OnClickListener() {
	 
		  @Override
		  public void onClick(View v) {
	                //is chkIos checked?
			if (((CheckBox) v).isChecked()) {
				Toast.makeText(MyAndroidAppActivity.this,
			 	   "Bro, try Android :)", Toast.LENGTH_LONG).show();
			}
	 
		  }
		});
	 
	  }
	 
	  public void addListenerOnButton() {
	 
		chkIos = (CheckBox) findViewById(R.id.chkIos);
		chkAndroid = (CheckBox) findViewById(R.id.chkAndroid);
		chkWindows = (CheckBox) findViewById(R.id.chkWindows);
		chktry=(CheckBox) findViewById(R.id.chkhbid);
		btnDisplay = (Button) findViewById(R.id.btnDisplay);
		
	 
		btnDisplay.setOnClickListener(new OnClickListener() {
	 
	          //Run when button is clicked
		  @Override
		  public void onClick(View v) {
	 
			StringBuffer result = new StringBuffer();
			result.append("IPhone check : ").append(chkIos.isChecked());
			result.append("\nAndroid check : ").append(chkAndroid.isChecked());
			result.append("\nWindows Mobile check :").append(chkWindows.isChecked());
			result.append("\nhbtry check:").append(chktry.isChecked());
			Toast.makeText(MyAndroidAppActivity.this, result.toString(),
					Toast.LENGTH_LONG).show();
	 
		  }
		});
	 
	  }
	}