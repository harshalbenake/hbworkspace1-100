package com.example.arrayloop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button=(Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				List<String> list = new ArrayList<String>();
				list.add("harshal 1");
				list.add("harshal 2");
				list.add("harshal 3");
				list.add("harshal 4");
				list.add("harshal 5");
				
				System.out.println("#1 normal for loop");
				for (int i = 0; i < list.size(); i++) {
					System.out.println(list.get(i));
				}
		 
				System.out.println("#2 advance for loop");
				for (String temp : list) {
					System.out.println(temp);
				}
		 
				System.out.println("#3 while loop");
				int i = 0;
				while (list.size() > i) {
					System.out.println(list.get(i));
					i++;
				}
		 
				System.out.println("#4 iterator");
				Iterator<String> iterator = list.iterator();
				while (iterator.hasNext()) {
					System.out.println(iterator.next());
				}
			}
		});
		
	}


}
