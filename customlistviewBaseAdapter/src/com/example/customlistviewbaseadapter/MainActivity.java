package com.example.customlistviewbaseadapter;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	 public static final String[] titles = new String[] { "Strawberry",
         "Banana", "Orange", "Mixed" };

 public static final String[] descriptions = new String[] {
         "It is an aggregate accessory fruit",
         "It is the largest herbaceous flowering plant", "Citrus Fruit",
         "Mixed Fruits" };

 public static final Integer[] images = { R.drawable.ic_launcher,
         R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher };

 ListView listView;
 List<RowItem> rowItems;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 rowItems = new ArrayList<RowItem>();
	        for (int i = 0; i < titles.length; i++) {
	            RowItem item = new RowItem(images[i], titles[i], descriptions[i]);
	            rowItems.add(item);
	        }
	 
	        listView = (ListView) findViewById(R.id.listview1);
	        CustomBaseAdapter adapter = new CustomBaseAdapter(this, rowItems);
	        listView.setAdapter(adapter);
	        listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Item " + (position + 1) + ": " + rowItems.get(position),
							Toast.LENGTH_SHORT);
					toast.show();
					
				}
	              
			});
	}
	
	
	
}
