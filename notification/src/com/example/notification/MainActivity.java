package com.example.notification;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	final int mId = 0;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button=(Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shownotification(getApplicationContext());
			}
		});
	
	}

	@SuppressLint("NewApi")
	public void shownotification(Context context)
	{
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("My notification")
		        .setContentText("Hello World!");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(getApplicationContext(), MYDEMOACTIVITY.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MYDEMOACTIVITY.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
	}
}
