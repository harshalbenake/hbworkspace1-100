package com.example.hideappfromlauncher;


import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button=(Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try{
				    PackageManager p = getPackageManager();
				    p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
				}catch (Exception e) {
				    e.printStackTrace();
				}
			finish();
			}
		});
	}

	
}
