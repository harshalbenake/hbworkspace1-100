package com.example.viewpagerindialogpopup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class MainActivity extends Activity {
	private CustomDialog customLocationDialog;
	ViewPager mViewPager;
	 private int imageArray[] = { R.drawable.locationsettings, R.drawable.one,
			   R.drawable.two, R.drawable.three,
			   R.drawable.four };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button=(Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View arg0) {
				customLocationDialog=new CustomDialog(MainActivity.this, "Location Settings",true);
				LayoutInflater inflater=LayoutInflater.from(getApplicationContext());
				View view=inflater.inflate(R.layout.popup_locationcustomeview, null);

				TextView txt1=(TextView)view.findViewById(R.id.popup_locationsetting_text1);
			
				txt1.setText(R.string.txtshieldmode_dialog_locationsetting_message1);
				txt1.setTextSize(20);
				txt1.setTextColor(Color.WHITE);
				txt1.setPadding(20, 20, 20, 20);

				TextView txt2=(TextView)view.findViewById(R.id.popup_locationsetting_text2);
				//			ImageView img=(ImageView)view.findViewById(R.id.popup_locationsetting_image);
				//			img.setImageResource(R.drawable.locationsettings);
				txt2.setText(R.string.txtshieldmode_dialog_locationsetting_message2);
				txt2.setTextSize(20);
				txt2.setTextColor(Color.WHITE);
				txt2.setPadding(20, 20, 20, 20);

				
			ViewPagerAdapter adapter = new ViewPagerAdapter(MainActivity.this, imageArray);
				mViewPager = (ViewPager)view.findViewById(R.id.view_pager1);
				mViewPager.setAdapter(adapter);
				
				mViewPager.setCurrentItem(0);
				  
				
				ImageView view_pager_previous=(ImageView)view.findViewById(R.id.view_pager_previous);
				ImageView view_pager_next=(ImageView)view.findViewById(R.id.view_pager_next);

				view_pager_previous.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mViewPager.setCurrentItem(getItemNext(+1));
					}
				});
				
				view_pager_next.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mViewPager.setCurrentItem(getItemPrevious(+1));
					}
				});
				  
				customLocationDialog.setContentView(view);

				customLocationDialog.setFirstButton("OK");
				customLocationDialog.setFirstButtonOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						customLocationDialog.dismiss();
						Intent locationSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
						startActivity(locationSettingIntent);
					}
				});
				customLocationDialog.setCancelable(false);
				customLocationDialog.show();
			}
		});
	}
	
	private int getItemNext(int position) {
		return mViewPager.getCurrentItem() - position;
	}
	
	private int getItemPrevious(int position) {
		return mViewPager.getCurrentItem() + position;
	}
	

	public class ViewPagerAdapter extends PagerAdapter {

		 Activity activity;
		 int imageArray[];

		 public ViewPagerAdapter(Activity act, int[] imgArra) {
		  imageArray = imgArra;
		  activity = act;
		 }

		 public int getCount() {
		  return imageArray.length;
		 }
		 @Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		 public Object instantiateItem(View collection, int position) {
		  ImageView view = new ImageView(activity);
		//  view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		  view.setScaleType(ScaleType.FIT_XY);
		  view.setBackgroundResource(imageArray[position]);
		  ((ViewPager) collection).addView(view, 0);
		  return view;
		 }

		 @Override
		 public void destroyItem(View arg0, int arg1, Object arg2) {
		  ((ViewPager) arg0).removeView((View) arg2);
		 }

		 @Override
		 public boolean isViewFromObject(View arg0, Object arg1) {
		  return arg0 == ((View) arg1);
		 }

		 @Override
		 public Parcelable saveState() {
		  return null;
		 }
		}

}
