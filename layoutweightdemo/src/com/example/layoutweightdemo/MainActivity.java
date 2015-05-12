package com.example.layoutweightdemo;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.app.Activity;


public class MainActivity extends Activity {
	LinearLayout linearLayout1,linearLayout2,linearLayout3,linearLayout4,linlaymain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		linlaymain =(LinearLayout)findViewById(R.id.linlaymain);
		
		linearLayout1=(LinearLayout)findViewById(R.id.linearLayout1);
		linearLayout2=(LinearLayout)findViewById(R.id.linearLayout2);
		linearLayout3=(LinearLayout)findViewById(R.id.linearLayout3);
		linearLayout4=(LinearLayout)findViewById(R.id.linearLayout4);

		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 0.25f);
		linearLayout1.setLayoutParams(params1);
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 0.25f);
		linearLayout2.setLayoutParams(params2);
		LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT,0.25f);
		linearLayout3.setLayoutParams(params3);
		LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT,0.25f);
		linearLayout4.setLayoutParams(params4);
      	}	
}
