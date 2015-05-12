package com.example.sharemyapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.util.ArrayList; 
import java.util.List; 
 
import android.app.ListActivity; 
import android.app.ProgressDialog; 
import android.content.pm.ApplicationInfo; 
import android.content.pm.PackageManager; 
import android.database.CursorJoiner.Result;
import android.graphics.drawable.Drawable; 
import android.hardware.Camera.Parameters;
import android.os.AsyncTask; 
import android.os.Bundle; 
import android.os.Process;
import android.view.LayoutInflater; 
import android.view.View; 
import android.view.View.OnClickListener; 
import android.view.ViewGroup; 
import android.widget.BaseAdapter; 
import android.widget.Button; 
import android.widget.ImageView; 
import android.widget.TextView; 
import android.widget.Toast; 
public class MainActivity extends ListActivity { 
	 
	List<ApplicationInfo> appInfo; 
	PackageManager pm; 
	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_main); 
 
		pm = getPackageManager(); 
		//get a list of installed apps. 
		List<ApplicationInfo> info =  pm.getInstalledApplications(PackageManager.GET_META_DATA); 
		appInfo=new ArrayList<ApplicationInfo>(); 
		for (ApplicationInfo packageInfo : info) { 
 
			if (((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)==false) { 
				System.out.println("** Installed package :" + packageInfo.packageName); 
				System.out.println("-- Apk file path:" + packageInfo.sourceDir); 
				appInfo.add(packageInfo); 
			} 
 
		} 
 
		setListAdapter(new ListAdapter()); 
 
	} 
 
	class ListAdapter extends BaseAdapter { 
		LayoutInflater inflater; 
		public ListAdapter() { 
			inflater=LayoutInflater.from(getApplicationContext()); 
		} 
		@Override 
		public int getCount() { 
			// TODO Auto-generated method stub 
			return appInfo.size(); 
		} 
 
		@Override 
		public Object getItem(int arg0) { 
			// TODO Auto-generated method stub 
			return appInfo.get(arg0); 
		} 
 
		@Override 
		public long getItemId(int position) { 
			// TODO Auto-generated method stub 
			return position+1; 
		} 
 
		@Override 
		public View getView(int position, View convertView, ViewGroup parent) { 
			if(convertView==null){ 
				convertView=inflater.inflate(R.layout.devicelistitem, null); 
			} 
			ApplicationInfo packageInfo = appInfo.get(position); 
			ImageView device_list_image=(ImageView)convertView.findViewById(R.id.imageView1); 
			Button device_list_btn=(Button)convertView.findViewById(R.id.button1); 
			TextView device_list_name=(TextView)convertView.findViewById(R.id.textView1); 
 
			String appName=packageInfo.loadLabel(pm).toString(); 
			device_list_name.setText(appName); 
 
			Drawable icon = packageInfo.loadIcon(pm); 
			device_list_image.setImageDrawable(icon); 
 
			device_list_btn.setTag(packageInfo); 
 
			device_list_btn.setOnClickListener(new OnClickListener() { 
 
				@Override 
				public void onClick(View v) { 
					ApplicationInfo packageInfo =(ApplicationInfo) v.getTag(); 
					System.out.println("** Installed package :" + packageInfo.packageName); 
					System.out.println("-- Apk file path:" + packageInfo.sourceDir); 
					new Async_ToCopyAPK().execute(packageInfo); 
					 
				} 
			}); 
 
			return convertView; 
		} 
		 
		/** 
		 * This class is used to copy APK file into external memory. 
		 * @author Amol Wadekar 
		 * 
		 */ 
		class Async_ToCopyAPK extends AsyncTask<ApplicationInfo, String, String>{ 
			ProgressDialog progressDialog; 
			String filePath="/sdcard/"; 
			@Override 
			protected void onPreExecute() { 
				progressDialog=new ProgressDialog(MainActivity.this); 
				progressDialog.setTitle("Coping APK"); 
				progressDialog.setMessage("Please wait..."); 
				progressDialog.show(); 
				super.onPreExecute(); 
			} 
			 
			@Override 
			protected String doInBackground(ApplicationInfo... params) { 
				filePath=writeAPKFileonToDeviceSDCards(params[0]); 
				return null; 
			} 
			 
			@Override 
			protected void onPostExecute(String result) { 
				if(progressDialog.isShowing()) 
					progressDialog.dismiss(); 
				Toast.makeText(getApplicationContext(), "Copied File Path : "+filePath, Toast.LENGTH_SHORT).show(); 
				super.onPostExecute(result); 
			} 
			 
			/** 
			 * This method is used to write file onto device SD Card. 
			 */ 
			private String writeAPKFileonToDeviceSDCards(ApplicationInfo packageInfo){ 
				String copyFilePath=""; 
				try{ 
					String appName=packageInfo.loadLabel(pm).toString(); 
					String aplFilePath=packageInfo.sourceDir; 
					copyFilePath="/sdcard/" +"/"+ appName+".apk"; 
					 
					File apkFile=new File(aplFilePath); 
					/**Write bytes into buffer.*/ 
					FileInputStream fin=new FileInputStream(apkFile); 
					System.out.println("APK File Size "+apkFile.length()); 
					FileOutputStream outputStream=new FileOutputStream(copyFilePath); 
 
					int count=0; 
					int buffSize=1024; 
					byte [] buffer=new byte[buffSize]; 
					while(fin.read(buffer,0,buffer.length) != -1){ 
						outputStream.write(buffer, 0, buffer.length); 
						System.out.println("File Size "+(++count)+"kb"); 
						System.out.println("Write byte "+(buffer.length*count)); 
						buffer=new byte[buffSize]; 
					} 
					fin.close(); 
					outputStream.close(); 
					/***/ 
 
				}catch(Exception e){ 
					e.printStackTrace(); 
				} 
				return copyFilePath; 
			} 
		} 
	} 
 
} 
