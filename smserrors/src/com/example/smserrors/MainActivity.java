package com.example.smserrors;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private int mMessageSentParts;
	private int mMessageSentTotalParts;
	private int mMessageSentCount;
	 String SENT = "SMS_SENT";
     String DELIVERED = "SMS_DELIVERED";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button=(Button)findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//htc String phoneNumber = "9028639801";
				//amol
				String phoneNumber = "9975715415";
		        String message = "Hello World!";
				sendSMS(phoneNumber,message);
				
				  
			}
		});
        
        
        
    }
   
    
    public void sendSMS(String phoneNumber,String message) {
       /* SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);*/
    	
    	 String SENT = "AMOL_SMS_SENT";
    	    String DELIVERED = "AMOL_SMS_DELIVERED";

    	    SmsManager sms = SmsManager.getDefault();
    	    ArrayList<String> parts = sms.divideMessage(message);
    	    int messageCount = parts.size();

    	    Log.i("Message Count", "Message Count: " + messageCount);

    	    ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
    	    ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

    	    PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
    	    PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

    	    for (int j = 0; j < messageCount; j++) {
    	        sentIntents.add(sentPI);
    	        deliveryIntents.add(deliveredPI);
    	    }

    	    // ---when the SMS has been sent---
    	    registerReceiver(new BroadcastReceiver() {
    	        @Override
    	        public void onReceive(Context arg0, Intent arg1) {
    	            switch (getResultCode()) {
    	            case Activity.RESULT_OK:
    	                Toast.makeText(getBaseContext(), "SMS sent",
    	                        Toast.LENGTH_SHORT).show();
    	                break;
    	            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
    	                Toast.makeText(getBaseContext(), "Generic failure",
    	                        Toast.LENGTH_SHORT).show();
    	                break;
    	            case SmsManager.RESULT_ERROR_NO_SERVICE:
    	                Toast.makeText(getBaseContext(), "No service",
    	                        Toast.LENGTH_SHORT).show();
    	                break;
    	            case SmsManager.RESULT_ERROR_NULL_PDU:
    	                Toast.makeText(getBaseContext(), "Null PDU",
    	                        Toast.LENGTH_SHORT).show();
    	                break;
    	            case SmsManager.RESULT_ERROR_RADIO_OFF:
    	                Toast.makeText(getBaseContext(), "Radio off",
    	                        Toast.LENGTH_SHORT).show();
    	                break;
    	            }
    	        }
    	    }, new IntentFilter(SENT));

    	    // ---when the SMS has been delivered---
    	    registerReceiver(new BroadcastReceiver() {
    	        @Override
    	        public void onReceive(Context arg0, Intent arg1) {
    	            switch (getResultCode()) {

    	            case Activity.RESULT_OK:
    	                Toast.makeText(getBaseContext(), "SMS delivered",
    	                        Toast.LENGTH_SHORT).show();
    	                break;
    	            case Activity.RESULT_CANCELED:
    	                Toast.makeText(getBaseContext(), "SMS not delivered",
    	                        Toast.LENGTH_SHORT).show();
    	                break;
    	            }
    	        }
    	    }, new IntentFilter(DELIVERED));

    	    sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);
    }
}
