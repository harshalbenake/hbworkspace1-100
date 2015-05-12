package com.example.rfile;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView textView=(TextView)findViewById(R.id.textView1);
		int int_value=R.id.textView1;
		int int_value_button=R.id.button1;
		String string=Integer.toHexString(int_value).toString();
		String string_button=Integer.toHexString(int_value_button).toString();
		textView.setText(string+" "+string_button);
	}

	
}
