package com.krish.horizontalscrollview.CenterLockHorizontalScrollview;

import java.util.ArrayList;

import com.krish.horizontalscrollview.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class HorizontalScrollViewActivity extends Activity {
	CenterLockHorizontalScrollview centerLockHorizontalScrollview,hbtry,hbthree;
	CustomListAdapter customListAdapter;
	Button btnPrev, btnNext;
	int currIndex = 0;

	ArrayList<String> list = new ArrayList<String>() {

		{
			add("aaa");
			add("bbb");
			add("ccc");
			add("ddd");
			add("eee");
			add("fff");
			add("ggg");
			add("hhh");
			add("iii");
			add("jjj");
			add("kkk");
			add("lll");
			add("mmm");

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_horizontal_scroll_view);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnPrev = (Button) findViewById(R.id.btnPrev);
	
		centerLockHorizontalScrollview = (CenterLockHorizontalScrollview) findViewById(R.id.scrollView);
		customListAdapter = new CustomListAdapter(this,
				R.layout.news_list_item, list);
		centerLockHorizontalScrollview.setAdapter(this, customListAdapter);
		
		hbtry = (CenterLockHorizontalScrollview) findViewById(R.id.hbtrysv);
		customListAdapter = new CustomListAdapter(this,
				R.layout.hbhorizontallistview, list);
		hbtry.setAdapter(this, customListAdapter);
		
		hbthree = (CenterLockHorizontalScrollview) findViewById(R.id.hbsvthree);
		customListAdapter = new CustomListAdapter(this,
				R.layout.hbtrythree, list);
		hbthree.setAdapter(this, customListAdapter);
		
		btnNext.setOnClickListener(onClickListener);
		btnPrev.setOnClickListener(onClickListener);

	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btnPrev) {
				if (currIndex != 0) {
					currIndex--;
					centerLockHorizontalScrollview.setCenter(currIndex);
				}
			} else if (v.getId() == R.id.btnNext) {

				if (currIndex < list.size()) {
					centerLockHorizontalScrollview.setCenter(currIndex);
					currIndex++;
				}
			}

		}
	};

}
