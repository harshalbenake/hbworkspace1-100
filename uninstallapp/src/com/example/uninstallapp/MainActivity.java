package com.example.uninstallapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		devicemanager devicemanager=new devicemanager();
		devicemanager.activateDeviceAdmin(MainActivity.this, devicemanager.REQUEST_CODE_ENABLE_ADMIN);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case devicemanager.REQUEST_CODE_ENABLE_ADMIN:
			devicemanager adminManager=new devicemanager();
			if(adminManager.isDeviceAdminActive(getApplicationContext())){
				Toast.makeText(getApplicationContext(), "hbuninstalldemo", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
				startActivity(intent);
			}
		}
	}
}
