package com.example.textlink;

import android.os.Bundle;
import android.app.Activity;
import android.text.util.Linkify;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*TextView textviewLink=(TextView)findViewById(R.id.textview_link);
		textviewLink.setClickable(true);
		textviewLink.setMovementMethod(LinkMovementMethod.getInstance());
		String text = "<a href='http://www.google.com'>This is my demo text </a>";
		textviewLink.setText(Html.fromHtml(text));*/
		
		TextView textviewLink=(TextView)findViewById(R.id.textview_link);
		textviewLink.setText("www.google.com");
		Linkify.addLinks(textviewLink, Linkify.ALL);
		
		TextView textviewNumber=(TextView)findViewById(R.id.textview_number);
		textviewNumber.setText("7588871488");
		Linkify.addLinks(textviewNumber, Linkify.ALL);
		
		
	}

	

}
