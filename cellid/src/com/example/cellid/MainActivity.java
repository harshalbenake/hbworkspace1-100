package com.example.cellid;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
		    final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
		    
		    if (location != null) {
		        Toast.makeText(getApplicationContext(), "LAC: " + location.getLac() + " CID: " + location.getCid(), Toast.LENGTH_LONG).show();

		   
		    }
		}
		 TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		    String networkOperator = tel.getNetworkOperator();

		    if (networkOperator != null) {
		        int mcc = Integer.parseInt(networkOperator.substring(0, 3));
		        int mnc = Integer.parseInt(networkOperator.substring(3));
		        Toast.makeText(getApplicationContext(),"mcc:  "+mcc+"mnc: "+mnc, Toast.LENGTH_LONG).show();

		    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
