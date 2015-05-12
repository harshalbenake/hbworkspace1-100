package com.example.viewpagerindialogpopup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.myappsco.utils.Utility;

public class CustomDialog {
	public Button mFirstButton;
	public Button mMiddleButton;
	public Button mLastButton;
	private TextView mTitleTextView;
	private LinearLayout mMiddleLayout;
	private LinearLayout mBottomLayout;

	Dialog dialog ;
	private Context context;
	LayoutInflater inflater;

	//private Typeface mTypeface;
	private String mTitle;

	public CustomDialog(Activity context, String title,boolean isLargePopup) {
		this.context=context;
		this.mTitle=title;
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view=initalizeLayout(isLargePopup);
		dialog.setContentView(view);
	}

	public CustomDialog(Activity context,boolean isLargePopup) {
		this.context=context;
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view=initalizeLayout(isLargePopup);
		dialog.setContentView(view);
	}
	

	public View initalizeLayout(boolean isLargePopup) {  
		inflater = LayoutInflater.from(context);
		View rowView;
		if(isLargePopup==true)
		{
		rowView =inflater.inflate(R.layout.custom_dialog_islargepopup, null);
		}
		else {
		rowView =inflater.inflate(R.layout.custom_dialog_islargepopup, null);
		}

		mTitleTextView=(TextView)rowView.findViewById(R.id.dailog_title);
		mFirstButton=(Button)rowView.findViewById(R.id.first_btn);
		mMiddleButton=(Button)rowView.findViewById(R.id.middle_btn);
		mLastButton=(Button)rowView.findViewById(R.id.last_btn);

		mMiddleLayout=(LinearLayout)rowView.findViewById(R.id.middle_layout);
		mBottomLayout=(LinearLayout)rowView.findViewById(R.id.bottom_layout);
		//set values...
		mTitleTextView.setText(mTitle);

		//set Helvetica font...
//		mTypeface = new Utility().getFontTypeface(context.getApplicationContext());
//
//		mTitleTextView.setTypeface(mTypeface);
//		mFirstButton.setTypeface(mTypeface);
//		mMiddleButton.setTypeface(mTypeface);
//		mLastButton.setTypeface(mTypeface);

		return rowView;
	}

	/**
	 * @param viewList
	 */
	public void show(){
		dialog.show();
	}

	public void dismiss(){
		dialog.dismiss();
	}



	/**
	 * @param view
	 */
	public void addView(View view){
		mMiddleLayout.addView(view);
	}

	/**
	 * @param viewList
	 */
	public View setContentView(int resourceId){
		View view =inflater.inflate(resourceId, null);
		addView(view);
		return view;
	}
	/**
	 * @param view
	 */
	public void setContentView(View view){
		addView(view);
	}


	/**
	 * 
	 * @param name
	 */
	public void setFirstButton(String name){
		mBottomLayout.setVisibility(View.VISIBLE);
		mFirstButton.setVisibility(View.VISIBLE);
		mFirstButton.setText(name);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		params.weight=99;

		mFirstButton.setLayoutParams(params);

	}

	/**
	 * 
	 * @param name
	 */
	public void setMiddleButton(String name){
		mMiddleButton.setVisibility(View.VISIBLE);
		mMiddleButton.setText(name);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		params.weight=49.5f;
		mFirstButton.setLayoutParams(params);
		
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		params1.weight=49.5f;
		params1.leftMargin=2;
		
		mMiddleButton.setLayoutParams(params1);

	}


	/**
	 * 
	 * @param name
	 */
	public void setLastButton(String name){
		mLastButton.setVisibility(View.VISIBLE);
		mLastButton.setText(name);

		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		params1.weight=33;
		
		mFirstButton.setLayoutParams(params1);
		
		
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		params2.weight=33;
		params2.leftMargin=2;
		
		mMiddleButton.setLayoutParams(params2);
		
		LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		params3.weight=33;
		params3.leftMargin=2;
		
		
		mLastButton.setLayoutParams(params3);

	}

	/**
	 * This method is used set First Button onClickListener.
	 * @param onClickListener
	 */
	public void setFirstButtonOnClickListener(OnClickListener onClickListener) {
		mFirstButton.setOnClickListener(onClickListener);
	}

	/**
	 * This method is used set Second Button onClickListener.
	 * @param onClickListener
	 */
	public void setMiddleButtonOnClickListener(OnClickListener onClickListener) {
		mMiddleButton.setOnClickListener(onClickListener);
	}

	/**
	 * This method is used set Third Button onClickListener.
	 * @param onClickListener
	 */
	public void setThirdButtonOnClickListener(OnClickListener onClickListener) {
		mLastButton.setOnClickListener(onClickListener);
	}

	/**
	 * This method is used to set whether the dialog is canceled by Back key or not.
	 * @param flag
	 */
	public void setCancelable(boolean flag) {
		dialog.setCancelable(flag);
	}
}
