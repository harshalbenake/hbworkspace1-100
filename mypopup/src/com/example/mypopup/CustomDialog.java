package com.example.mypopup;




import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


/**
 * @author harshalb
 *This class is used for custom dialog.
 */
public class CustomDialog {
	public Button mFirstButton;
	public Button mMiddleButton;
	public Button mLastButton;
	private TextView mTitleTextView;
	public ImageView dailogCloseImage;
	private LinearLayout mMiddleLayout;
	private LinearLayout mBottomLayout;

	Dialog dialog ;
	//private Context context;
	Activity activity;
	LayoutInflater inflater;

	private Typeface mTypeface;
	private String mTitle;

	public CustomDialog(Activity activity, String title) {
		this.activity=activity;
		this.mTitle=title;
		dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view=initalizeLayout();
		dialog.setContentView(view);
	}

	/*public CustomDialog(Activity context) {
		this.activity=context;
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view=initalizeLayout();
		dialog.setContentView(view);
	}*/
	

	public View initalizeLayout() {  
		inflater = LayoutInflater.from(activity);
		View rowView =inflater.inflate(R.layout.custom_dialog, null);
		   
		mTitleTextView=(TextView)rowView.findViewById(R.id.dailog_title);
		dailogCloseImage=(ImageView)rowView.findViewById(R.id.dailog_close_image);

		mFirstButton=(Button)rowView.findViewById(R.id.first_btn);
		mMiddleButton=(Button)rowView.findViewById(R.id.middle_btn);
		mLastButton=(Button)rowView.findViewById(R.id.last_btn);

		mMiddleLayout=(LinearLayout)rowView.findViewById(R.id.middle_layout);
		mBottomLayout=(LinearLayout)rowView.findViewById(R.id.bottom_layout);
		//set values...
		mTitleTextView.setText(mTitle);


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


	public void setDailogCloseImage(int resourceId){
		dailogCloseImage.setImageResource(resourceId);
//		mDailogButton.setBackgroundResource(R.drawable.invite_button);

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

		params.weight=100;

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

		params.weight=50;

		mFirstButton.setLayoutParams(params);
		mMiddleButton.setLayoutParams(params);

	}


	/**
	 * 
	 * @param name
	 */
	public void setLastButton(String name){
		mLastButton.setVisibility(View.VISIBLE);
		mLastButton.setText(name);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		params.weight=33;

		mFirstButton.setLayoutParams(params);
		mMiddleButton.setLayoutParams(params);
		mLastButton.setLayoutParams(params);

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

	public void setDailogCloseImageOnClickListener(OnClickListener onClickListener) {
		dailogCloseImage.setOnClickListener(onClickListener);
	}
	/**
	 * This method is used to set whether the dialog is canceled by Back key or not.
	 * @param flag
	 */
	public void setCancelable(boolean flag) {
		dialog.setCancelable(flag);
	}
}
