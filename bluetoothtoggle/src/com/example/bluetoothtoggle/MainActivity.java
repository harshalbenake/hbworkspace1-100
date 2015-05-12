package com.example.bluetoothtoggle;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		    this.registerReceiver(mReceiver, filter);
		
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        final String action = intent.getAction();

	        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
	            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
	                                                 BluetoothAdapter.ERROR);
	            switch (state) {
	            case BluetoothAdapter.STATE_OFF:
	    			Toast.makeText(getApplicationContext(), "BluetoothAdapter.STATE_OFF", Toast.LENGTH_LONG).show();
	                break;
	            case BluetoothAdapter.STATE_TURNING_OFF:
	    			Toast.makeText(getApplicationContext(), "BluetoothAdapter.STATE_TURNING_OFF", Toast.LENGTH_LONG).show();
	                break;
	            case BluetoothAdapter.STATE_ON:
	    			Toast.makeText(getApplicationContext(), "BluetoothAdapter.STATE_ON", Toast.LENGTH_LONG).show();
	                break;
	            case BluetoothAdapter.STATE_TURNING_ON:
	    			Toast.makeText(getApplicationContext(), "BluetoothAdapter.STATE_TURNING_ON", Toast.LENGTH_LONG).show();
	                break;
	            }
	        }
	    }
	};
}
