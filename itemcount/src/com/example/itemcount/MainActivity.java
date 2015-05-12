package com.example.itemcount;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static int counter = 0;
    private String stringValue;
	Button buttonPlus;
	Button buttonMinus;
	EditText editTextcountItem;
	CheckBox checkboxCount;
	TextView textviewCount;
	String stringadding;
	int temp;
	int price=10;
	int limit=50;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		 buttonPlus=(Button)findViewById(R.id.button_plus);
		 buttonMinus=(Button)findViewById(R.id.button_minus);
		 editTextcountItem=(EditText)findViewById(R.id.countitem);
		 checkboxCount=(CheckBox)findViewById(R.id.checkboxcount);
		 textviewCount=(TextView)findViewById(R.id.textviewCount);
		 textviewCount.setText("Available Reward Points :"+"0");
		 editTextcountItem.setText("0");
		buttonPlus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 if(temp!=limit)
                 {
				if(counter<5)
				{
				counter++;
				stringValue = Integer.toString(counter);
                editTextcountItem.setText(stringValue);
                 temp=counter*price;
                
                stringadding=Integer.toString(temp);
                textviewCount.setText("Available Reward Points :"+stringadding);
                if(counter!=0)
                	checkboxCount.setChecked(true);
                 
				}
				else
				{
					Toast.makeText(getBaseContext(), "You do not have sufficient reward point", Toast.LENGTH_SHORT).show();
					return;
				}
				
                 }
                 else
                 {
                	 Toast.makeText(getBaseContext(), "You do not have sufficient reward point", Toast.LENGTH_SHORT).show();
 					return;
                 }
			}
		});
		
		buttonMinus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(counter>0)
				{
                counter--;
                stringValue = Integer.toString(counter);
                editTextcountItem.setText(stringValue);
                temp=temp-price;
                stringadding=Integer.toString(temp);
                textviewCount.setText("Available Reward Points :"+stringadding);
                if(counter==0)
                	checkboxCount.setChecked(false);
				}
				else
				{
					return;
				}
			}
		});
	
		
		
		
	}

	

}
