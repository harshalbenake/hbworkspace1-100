package com.example.selectspeed;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.app.Activity;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final TextView speedlimit_value=(TextView)findViewById(R.id.speedlimit_value);
		
		final View speep_limit_10=(View)findViewById(R.id.speed_limit_10);
		getViewuser(speep_limit_10,"10 Mph");
		final View speep_limit_15=(View)findViewById(R.id.speed_limit_15);
		getViewuser(speep_limit_15,"15 Mph");
		final View speep_limit_20=(View)findViewById(R.id.speed_limit_20);
		getViewuser(speep_limit_20,"20 Mph");
		final View speep_limit_25=(View)findViewById(R.id.speed_limit_25);
		getViewuser(speep_limit_25,"25 Mph");
		final View speep_limit_30=(View)findViewById(R.id.speed_limit_30);
		getViewuser(speep_limit_30,"30 Mph");
		final View speep_limit_35=(View)findViewById(R.id.speed_limit_35);
		getViewuser(speep_limit_35,"35 Mph");
		final View speep_limit_40=(View)findViewById(R.id.speed_limit_40);
		getViewuser(speep_limit_40,"40 Mph");
		
		speep_limit_10.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				speedlimit_value.setText("10");
				speep_limit_10.setSelected(true);
				speep_limit_15.setSelected(false);
				speep_limit_20.setSelected(false);
				speep_limit_25.setSelected(false);
				speep_limit_30.setSelected(false);
				speep_limit_35.setSelected(false);
				speep_limit_40.setSelected(false);

			}
		});
		speep_limit_15.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				speedlimit_value.setText("15");
				speep_limit_10.setSelected(false);
				speep_limit_15.setSelected(true);
				speep_limit_20.setSelected(false);
				speep_limit_25.setSelected(false);
				speep_limit_30.setSelected(false);
				speep_limit_35.setSelected(false);
				speep_limit_40.setSelected(false);

			}
		});
		speep_limit_20.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				speedlimit_value.setText("20");
				speep_limit_10.setSelected(false);
				speep_limit_15.setSelected(false);
				speep_limit_20.setSelected(true);
				speep_limit_25.setSelected(false);
				speep_limit_30.setSelected(false);
				speep_limit_35.setSelected(false);
				speep_limit_40.setSelected(false);

			}
		});
		speep_limit_25.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				speedlimit_value.setText("25");
				speep_limit_10.setSelected(false);
				speep_limit_15.setSelected(false);
				speep_limit_20.setSelected(false);
				speep_limit_25.setSelected(true);
				speep_limit_30.setSelected(false);
				speep_limit_35.setSelected(false);
				speep_limit_40.setSelected(false);

			}
		});
		speep_limit_30.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				speedlimit_value.setText("30");
				speep_limit_10.setSelected(false);
				speep_limit_15.setSelected(false);
				speep_limit_20.setSelected(false);
				speep_limit_25.setSelected(false);
				speep_limit_30.setSelected(true);
				speep_limit_35.setSelected(false);
				speep_limit_40.setSelected(false);

			}
		});
		speep_limit_35.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				speedlimit_value.setText("35");
				speep_limit_10.setSelected(false);
				speep_limit_15.setSelected(false);
				speep_limit_20.setSelected(false);
				speep_limit_25.setSelected(false);
				speep_limit_30.setSelected(false);
				speep_limit_35.setSelected(true);
				speep_limit_40.setSelected(false);

		}
		});
		speep_limit_40.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				speedlimit_value.setText("40");
				speep_limit_10.setSelected(false);
				speep_limit_15.setSelected(false);
				speep_limit_20.setSelected(false);
				speep_limit_25.setSelected(false);
				speep_limit_30.setSelected(false);
				speep_limit_35.setSelected(false);
				speep_limit_40.setSelected(true);

			}
		});
	}

	private void getViewuser(View container,String number) {
		// TODO Auto-generated method stub
		TextView speed_limit=(TextView)container.findViewById(R.id.number);
		speed_limit.setText(number);
	}


}
