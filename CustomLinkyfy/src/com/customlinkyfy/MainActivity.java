package com.customlinkyfy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TextView txt=(TextView)findViewById(R.id.txt);
		
		//TextView t3 = (TextView) findViewById(R.id.text3);
		txt.setText(Html.fromHtml("This is the Simple Text with<a href=\'http://www.google.com\'>Keyword</a> and the<a href='startActivityFromLinkTest://some_info'>Link</a> to browse"));
		    		txt.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	class myTextView extends TextView{

		public myTextView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
		}
		
		public myTextView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		
		
	}
}
