package com.example.customlib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;


public class Utility {

	/**
	 * Returns font type used by the applicaion.
	 * 
	 * @return
	 */
	public static Typeface getFontTypeface(Context mContext)
	{
		return Typeface.createFromAsset(mContext.getAssets(), "font/MyriadPro-Regular.otf");
	}

	/**
	 * This method is used to get installed application info.
	 * @param context mContext
	 * @return ArrayList<PackageInfo>
	 */
	public static ArrayList<PackageInfo> getInstalledApps(Context mContext) {
		ArrayList<PackageInfo> res = new ArrayList<PackageInfo>();   
		List<PackageInfo> packs = mContext.getPackageManager().getInstalledPackages(0);
		String currentPackageName=mContext.getPackageName();
		System.out.println("Installed Apps");
		for(int i=0;i<packs.size();i++) {
			PackageInfo p = packs.get(i);

			ApplicationInfo ai=p.applicationInfo;

			if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				// System app - do something here
				//System.out.println("@@@@@ System @@@@@ "+p.packageName+" "+p.versionName+" "+p.versionCode);
			} else {
				// User installed app?
				//	        	System.out.println("#### Installed ######"+p.packageName+" "+p.versionName+" "+p.versionCode);
				System.out.println("Apps Pkg :"+p.packageName +" Version :"+p.versionCode);
				if(p.packageName.equalsIgnoreCase(currentPackageName)==false)//Skip Current Apps from listing.
					res.add(p);
			}



		}
		return res; 
	}

	/*public String getDeviceId(Context context){
		PreferncesManagerClass preferncesClass=new PreferncesManagerClass(context);
		String deviceID=preferncesClass.getRegisterDeviceIDPreference();
		return deviceID;
	}*/

	/**
	 * This method is used to get installed application package name from PackageManager. 
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> getInstalledPackageNames(Context mContext){
		ArrayList<String> packgeList=new ArrayList<String>();
		ArrayList<PackageInfo> pInfos=getInstalledApps(mContext);
		for(PackageInfo pInfo:pInfos){
			packgeList.add((pInfo.packageName));
		}
		return packgeList;
	}

	/**
	 * This method is used to get Screen Height & width.
	 * @param Activity
	 * @return float[]{screen_width,screen_height}
	 */
	public static float[] getScreenResolutions(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		final int height = dm.heightPixels;
		final int width = dm.widthPixels;

		System.out.println("Screen Resolutions: "+width+" "+height);

		return new float[]{width,height};
	}

	/**
	 * This method convets dp unit to equivalent device specific value in pixels. 
	 * 
	 * @param dp A value in dp(Device independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent Pixels equivalent to dp according to device
	 */
	public static float convertDpToPixel(float dp,Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi/160f);
		return px;
	}

	/**
	 * This method converts device specific pixels to device independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent db equivalent to px value
	 */
	public static float convertPixelsToDp(float px,Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;

	}

	/**
	 * This method is use to check the device internet connectivity.
	 * 
	 * @param context
	 * @return true :if your device is connected to internet.
	 *         false :if your device is not connected to internet. 
	 */
	public static boolean isConnected(Context context)
	{
		//		return true;
		//TODO un-comment when prototype demo done.

		ConnectivityManager manager = (ConnectivityManager)
		context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();

		if (info == null)
			return false;
		if (info.getState() != State.CONNECTED)
			return false;

		return true;
	}

	/**
	 * This method return device id from TelephonyManager.
	 * @return String deviceID
	 *//*
	public String getDeviceId(Context context){
		PreferncesManagerClass preferncesClass=new PreferncesManagerClass(context);
		String deviceID=preferncesClass.getRegisterDeviceIDPreference();
		return deviceID;
	}*/
	
	/**
	 * This method is used to get tower unique cid.
	 */
	public static String getDeviceProvider_CID(Context context){
		String cid="";
		final TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
			final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();

			if (location != null) {

				cid= "LAC: " + location.getLac() + " CID: " + location.getCid();

			}
		}
		return cid;
	}

	/**
	 * This method return device name like device Manufacture + Model name. 
	 * @return String deviceName
	 */
	public static String getDeviceName(){
		String deviceName="";
		//		deviceName=android.os.Build.MANUFACTURER+" "+android.os.Build.MODEL;
		deviceName=android.os.Build.MODEL;
		return deviceName;
	}

	/**
	 * This method return device name like device Manufacture + Model name. 
	 * @return String deviceName
	 */
	public static String getDeviceSDKVersion(){
		System.out.println(" SDK_INT "+android.os.Build.VERSION.SDK_INT);
		System.out.println(" RELEASE "+android.os.Build.VERSION.RELEASE);
		System.out.println(" CODENAME "+android.os.Build.VERSION.CODENAME);
		String deviceSDK=android.os.Build.VERSION.RELEASE;
		return deviceSDK;
	}

	/**
	 * This function returns whether the inputed string is valid or not.
	 * @param inputString
	 * @return
	 */
	public static boolean isValidText(CharSequence inputStr){

		String expression = "^[a-zA-Z]+[a-zA-Z0-9 '$._]*$";
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if(matcher.matches())
		{
			return true;
		}
		else {
			return false;
		}
	}


	/**
	 * This function returns whether the inputed string is valid or not.
	 * This is made for username spl validation.
	 * @param inputStr
	 * @return
	 */
	public static boolean isValidTextSpl(CharSequence inputStr){

		String expression = "^[0-9a-zA-Z]+$";
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if(matcher.matches())
		{
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * This function returns whether the inputed email ID is valid or not.
	 * @param inputString
	 * @return
	 */
	public static boolean isValidEmail(CharSequence inputStr){

		String expression = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";

		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if(matcher.matches())
		{
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * This method is used to hide soft keyboard.
	 * @param activity
	 */
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

	/**
	 * This method is used to show soft keyboard.
	 * @param activity
	 */
	public static void showSoftKeyboard(Activity activity) {
		InputMethodManager inputMgr = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	}

	/*public void copyDBIntoSDCard(Context context){
		try{

			File dbFilePath=context.getDatabasePath(DBHelper.DATABASE_NAME);
			File sdcardDBFile=context.getExternalCacheDir();
			System.out.println("Internal DB File Path :"+dbFilePath.getAbsolutePath()+" size "+dbFilePath.length());
			System.out.println("External DB File Path :"+sdcardDBFile.getAbsolutePath()+" size "+sdcardDBFile.length());
			FileInputStream fin=new FileInputStream(dbFilePath);
			byte[] buffer=new byte[(int) dbFilePath.length()];
			fin.read(buffer);
			FileOutputStream fout=new FileOutputStream(sdcardDBFile.getAbsolutePath()+"/"+DBHelper.DATABASE_NAME);
			fout.write(buffer);
			fout.close();
			fin.close();
			System.out.println("Internal DB File Path :"+dbFilePath.getAbsolutePath()+" size "+dbFilePath.length());
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/

	/**
	 * This function let you to convert string in Title Case.
	 * @param givenString
	 * @return
	 */
	public static String toTitleCase(String givenString) {
		String[] arr = givenString.split(" ");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
		}          
		return sb.toString().trim();
	}


	/**
	 * This function returns the device's unique serial number.
	 * @return
	 */
	public static String getDeviceSerialNumber() {
		String serial = null; 

		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			serial = (String) get.invoke(c, "ro.serialno");
			System.out.println("serial :"+serial);
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
		return serial;
	} 
	public static byte [] bitmapTobyteArray(Context context, Bitmap bitmap){
		ByteArrayOutputStream baos=new  ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
		byte [] rawBytes=baos.toByteArray();

		return rawBytes;
	}

	/**
	 * This method is used to write content into file.
	 * @param Context context
	 * @param String text
	 */
	public static void writeLogIntoFile(Context context,String path,String text){
		try{
			File file=new File(path);
			FileOutputStream outputStream=new FileOutputStream(file, true);

			outputStream.write(text.getBytes());
			outputStream.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to delete file from sd-card.
	 * @param Context context
	 * @param String text
	 */
	public static void deleteLogFile(Context context,String path){
		try{
			File file = new File(path);
			file.delete();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
