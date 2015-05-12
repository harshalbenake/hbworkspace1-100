package com.example.notification_count;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setBadge(getApplicationContext(), 3);
		 
		 
	
	}

	public static void setBadge(Context context, int count) {
	    String launcherClassName = getLauncherClassName(context);
	    if (launcherClassName == null) {
	        return;
	    }
	    Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
	    intent.putExtra("badge_count", count);
	    intent.putExtra("badge_count_package_name", context.getPackageName());
	    intent.putExtra("badge_count_class_name", launcherClassName);
	    context.sendBroadcast(intent);
	}

	public static String getLauncherClassName(Context context) {

	    PackageManager pm = context.getPackageManager();

	    Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_LAUNCHER);

	    List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
	    for (ResolveInfo resolveInfo : resolveInfos) {
	        String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
	        if (pkgName.equalsIgnoreCase(context.getPackageName())) {
	            String className = resolveInfo.activityInfo.name;
	            return className;
	        }
	    }
	    return null;
	}

	
}
