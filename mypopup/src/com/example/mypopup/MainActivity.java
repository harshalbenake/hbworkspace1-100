package com.example.mypopup;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button=(Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final CustomDialog customDialog=new CustomDialog(MainActivity.this, "TXT Shield");
				customDialog.setDailogCloseImage(R.drawable.ic_launcher);
				View view=customDialog.setContentView(R.layout.txtshield_itemview);
				TextView txtViewMessage=(TextView)view.findViewById(R.id.txt_view_message);
				
				txtViewMessage.setText("TXT shield has been successfully installed on this device");
				customDialog.setDailogCloseImageOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						customDialog.dismiss();
					}
				});

				customDialog.show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
