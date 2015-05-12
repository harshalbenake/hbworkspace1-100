package com.example.gridviewsimple;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends Activity {

	GridView gridView;
	 
	static final String[] numbers = new String[] { 
			"A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O",
			"P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z"};
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		setContentView(R.layout.activity_main);
 
		gridView = (GridView) findViewById(R.id.gridView1);
 
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, numbers);
 
		gridView.setAdapter(adapter);
 
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
				int position, long id) {
			   Toast.makeText(getApplicationContext(),((TextView) v).getText(), Toast.LENGTH_SHORT).show();
			}

			
		});
		}
	}

