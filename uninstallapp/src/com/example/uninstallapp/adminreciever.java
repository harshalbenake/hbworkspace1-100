package com.example.uninstallapp;

import android.annotation.SuppressLint;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class adminreciever extends DeviceAdminReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("AdminReciver : onReceive");
		super.onReceive(context, intent);
	}

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		// TODO Auto-generated method stub
		return super.onDisableRequested(context, intent);
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onDisabled(context, intent);
	}

	@Override
	public void onEnabled(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onEnabled(context, intent);
	}

	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onPasswordChanged(context, intent);
	}

	@SuppressLint("NewApi")
	@Override
	public void onPasswordExpiring(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onPasswordExpiring(context, intent);
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onPasswordFailed(context, intent);
	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onPasswordSucceeded(context, intent);
	}
	
	
	
}
