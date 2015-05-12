package com.example.recentrunningbackgroundapplist;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActivityManager activity_manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RecentTaskInfo> recent_tasks = activity_manager.getRecentTasks (Integer.MAX_VALUE, ActivityManager.RECENT_WITH_EXCLUDED);
		String output = "";
		for (int i = 0; i < recent_tasks.size(); i++) { 
		    String LocalApp = recent_tasks.get(i).baseIntent + "";              
		    int indexPackageNameBegin = LocalApp.indexOf("cmp=")+4;
		    int indexPackageNameEnd = LocalApp.indexOf("/", indexPackageNameBegin);
		    String PackageName = LocalApp.substring(indexPackageNameBegin, indexPackageNameEnd);
		    output += PackageName + " ";
		    if(PackageName.equalsIgnoreCase("com.android.settings"))
		    {
				Toast.makeText(getApplicationContext(), "com.android.settings", Toast.LENGTH_LONG).show();
		    }
		}
		Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
	}
	}


