package com.example.installedappnames;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.Menu;

public class MainActivity extends Activity {
	int n = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final PackageManager pm = getPackageManager();
		//get a list of installed apps.
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		for (ApplicationInfo packageInfo : packages) {
			n++;
		  
			writeLogIntoFile(
		    		getApplicationContext(),"/sdcard/hbdemo.txt", 
		    		"SR.NO: "+n+System.getProperty("line.separator")+
		    		"Package name: " + packageInfo.packageName+System.getProperty("line.separator")+
		    		"App name: " + pm.getApplicationLabel(packageInfo)+
		    		System.getProperty("line.separator")+
		    		System.getProperty("line.separator")
		    		);
		}
	
	}
	public void writeLogIntoFile(Context context,String path,String text){
		try{
			File file = new File(path);
			file.createNewFile();
			FileOutputStream outputStream=new FileOutputStream(file, true);
			outputStream.write(text.getBytes());
			outputStream.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
