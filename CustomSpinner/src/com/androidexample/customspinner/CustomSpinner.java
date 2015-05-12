package com.androidexample.customspinner; 

import java.util.ArrayList;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;  
 
public class CustomSpinner extends Activity {
 
    public  ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<SpinnerModel>();
    TextView output = null;
    CustomAdapter adapter;
    CustomSpinner activity = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_spinner);
        
        activity  = this;
        
        Spinner  SpinnerExample = (Spinner)findViewById(R.id.spinner);
        output         = (TextView)findViewById(R.id.output);
        
        setListData();
        
        Resources res = getResources(); 
        adapter = new CustomAdapter(activity, R.layout.spinner_rows, CustomListViewValuesArr,res);
        SpinnerExample.setAdapter(adapter);
        
        SpinnerExample.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                // your code here
            	
            	String Company    = ((TextView) v.findViewById(R.id.company)).getText().toString();
            	String CompanyUrl = ((TextView) v.findViewById(R.id.sub)).getText().toString();
            	
            	String OutputMsg = "Selected Company : \n\n"+Company+"\n"+CompanyUrl;
            	output.setText(OutputMsg);
            	
            	Toast.makeText(
						getApplicationContext(),OutputMsg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }
 
    /****** Function to set data in ArrayList *************/
    public void setListData()
    {
    	
		for (int i = 0; i < 11; i++) {
			
			final SpinnerModel sched = new SpinnerModel();
			    
			  /******* Firstly take data in model object ******/
			   sched.setCompanyName("Company "+i);
			   sched.setImage("image"+i);
			   sched.setUrl("http:\\\\www."+i+".com");
			   
			/******** Take Model Object in ArrayList **********/
			CustomListViewValuesArr.add(sched);
		}
		
    }
    
   }