package com.example.linkedinbest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	Button buttonfriends,buttonshare;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		buttonfriends=(Button)findViewById(R.id.button1);
		buttonshare=(Button)findViewById(R.id.button2);
		
		buttonfriends.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "Sync with Linkedin", Toast.LENGTH_SHORT).show();
//				Intent linkedinintent=new Intent(activity,LinkedinMainActivity.class);
//				startActivity(linkedinintent);
				
				LinkedinWebviewDialog dialog=new LinkedinWebviewDialog(MainActivity.this, LinkedinWebviewDialog.SYNC_FRIENDS);
//				dialog.show();
			}
		});
		
		buttonshare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "Share on LinkedIn", Toast.LENGTH_SHORT).show();
//				LinkedinMainActivity.linkedinClient.updateCurrentStatus("Myappsco");
//				LinkedinMainActivity.linkedinClient.postNetworkUpdate("Myappsco");
	
	LinkedinWebviewDialog dialog=new LinkedinWebviewDialog(MainActivity.this, LinkedinWebviewDialog.SHARE_STATUS,"This is Myappsco website:"+"www.myappsco.com");
//	dialog.show();
			}
		});
	}

	

}
