package com.example.linkedin;

import java.io.File;

import com.example.linkedin.SocialAuthAdapter.Provider;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	SocialAuthAdapter adapter;
	Button linkedinButton;
	
	boolean status;

	// Android Components
	Button update;
	EditText edit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		update = (Button) findViewById(R.id.update);
		edit = (EditText) findViewById(R.id.editTxt);
		linkedinButton=(Button)findViewById(R.id.twitter_button);
		adapter= new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.LINKEDIN, R.drawable.ic_launcher);
		
		// Add Bar to library
		adapter = new SocialAuthAdapter(new ResponseListener());

		// Please note : Update status functionality is only supported by
		// Facebook, Twitter, Linkedin, MySpace, Yahoo and Yammer.

		// Add providers
		adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
		adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);
		adapter.addProvider(Provider.MYSPACE, R.drawable.myspace);

		// Add email and mms providers
		adapter.addProvider(Provider.EMAIL, R.drawable.email);
		adapter.addProvider(Provider.MMS, R.drawable.mms);

		// For twitter use add callback method. Put your own callback url here.
		adapter.addCallBack(Provider.TWITTER, "http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");

		adapter.enable(linkedinButton);

	}
	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {

			// Variable to receive message status
			Log.d("Share-Bar", "Authentication Successful");

			// Get name of provider after authentication
			final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Log.d("Share-Bar", "Provider Name = " + providerName);
			Toast.makeText(MainActivity.this, providerName + " connected", Toast.LENGTH_SHORT).show();
/*
			update = (Button) findViewById(R.id.update);
			edit = (EditText) findViewById(R.id.editTxt);
*/
			// Please avoid sending duplicate message. Social Media Providers
			// block duplicate messages.

			update.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Call updateStatus to share message via oAuth providers
					// adapter.updateStatus(edit.getText().toString(), new
					// MessageListener(), false);

					// call to update on all connected providers at once
					adapter.updateStatus(edit.getText().toString(), new MessageListener(), true);
				}
			});

			// Share via Email Intent
			if (providerName.equalsIgnoreCase("share_mail")) {
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
						"vineet.aggarwal@3pillarglobal.com", null));
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test");
				File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
						"image5964402.png");
				Uri uri = Uri.fromFile(file);
				emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
				startActivity(Intent.createChooser(emailIntent, "Test"));
			}

			// Share via mms intent
			if (providerName.equalsIgnoreCase("share_mms")) {
				File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
						"image5964402.png");
				Uri uri = Uri.fromFile(file);

				Intent mmsIntent = new Intent(Intent.ACTION_SEND, uri);
				mmsIntent.putExtra("sms_body", "Test");
				mmsIntent.putExtra(Intent.EXTRA_STREAM, uri);
				mmsIntent.setType("image/png");
				startActivity(mmsIntent);
			}
		}

		@Override
		public void onError(SocialAuthError error) {
			error.printStackTrace();
			Log.d("Share-Bar", error.getMessage());
		}

		@Override
		public void onCancel() {
			Log.d("Share-Bar", "Authentication Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("Share-Bar", "Dialog Closed by pressing Back Key");

		}
	}

	// To get status of message after authentication
	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
			Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
				Toast.makeText(MainActivity.this, "Message posted on " + provider, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(MainActivity.this, "Message not posted on" + provider, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}
	
}
