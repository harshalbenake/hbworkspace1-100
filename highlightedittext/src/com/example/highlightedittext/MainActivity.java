package com.example.highlightedittext;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.view.Menu;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		EditText editText=(EditText)findViewById(R.id.editText1);
		editText.setText("demo");
		editText.selectAll();
	}

}
