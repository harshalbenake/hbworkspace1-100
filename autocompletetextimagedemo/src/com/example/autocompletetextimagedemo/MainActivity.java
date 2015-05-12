package com.example.autocompletetextimagedemo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	sqlhb shb;
	AutoCompleteTextView autoComplete;
	ImageLoader imgLoader;
	String image_url="http://www.almostsavvy.com/wp-content/uploads/2011/04/profile-photo.jpg";

	/* // Array of strings storing country names
    String[] countries = new String[] {
        "India",
        "Pakistan",
        "Sri Lanka",
        "China",
        "Bangladesh",
        "Nepal",
        "Afghanistan",
        "North Korea",
        "South Korea",
        "Japan"
    };
 
    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] flags = new int[]{
        R.drawable.india,
        R.drawable.pakistan,
        R.drawable.srilanka,
        R.drawable.china,
        R.drawable.bangladesh,
        R.drawable.nepal,
        R.drawable.afghanistan,
        R.drawable.nkorea,
        R.drawable.skorea,
        R.drawable.japan
    };
 
    // Array of strings to store currencies
    String[] currency = new String[]{
        "Indian Rupee",
        "Pakistani Rupee",
        "Sri Lankan Rupee",
        "Renminbi",
        "Bangladeshi Taka",
        "Nepalese Rupee",
        "Afghani",
        "North Korean Won",
        "South Korean Won",
        "Japanese Yen"
    };*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		autoComplete=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        // Image url
         
      // ImageLoader class instance
        imgLoader = new ImageLoader(this, null);
         
        // whenever you want to load an image from url
        // call DisplayImage function
        // url - image url to load
        // loader - loader image, will be displayed before getting image
        // image - ImageView
	
		shb=new sqlhb(getApplicationContext(), "mydb", null, 1);
		shb.addcontent("hellboy",image_url);
		
		//SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.autocomplete_layout, from, to);
		 
        // Getting a reference to CustomAutoCompleteTextView of activity_main.xml layout file
    //    autoComplete = ( CustomAutoCompleteTextView) findViewById(R.id.autocomplete);
	

		ArrayList<String> names=shb.getnames();
//		for(String s:names)
//		{
//			Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
//		}
		display();
	}
		
		
		/*// Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
        {
        for(int i=0;i<10;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", countries[i]);
            hm.put("flag", Integer.toString(flags[i]) );
            hm.put("cur", currency[i]);
            aList.add(hm);
        }
 
        // Keys used in Hashmap
        String[] from = { "flag","txt"};
 
        // Ids of views in listview_layout
        int[] to = { R.id.flag,R.id.txt};
        }
 
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
   //     SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.autocomplete_layout, from, to);
 
        // Getting a reference to CustomAutoCompleteTextView of activity_main.xml layout file
        autoComplete = ( CustomAutoCompleteTextView) findViewById(R.id.autocomplete);
 
        *//** Defining an itemclick event listener for the autocompletetextview *//*
        OnItemClickListener itemClickListener = new OnItemClickListener() {
          
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

                *//** Each item in the adapter is a HashMap object.
                *  So this statement creates the currently clicked hashmap object
                * *//*
                HashMap<String, String> hm = (HashMap<String, String>) arg0.getAdapter().getItem(arg2);
 
                *//** Getting a reference to the TextView of the layout file activity_main to set Currency *//*
                TextView tvCurrency = (TextView) findViewById(R.id.tv_currency) ;
 
                *//** Getting currency from the HashMap and setting it to the textview *//*
                tvCurrency.setText("Currency : " + hm.get("cur"));
			}
        };
 
        *//** Setting the itemclick event listener *//*
        autoComplete.setOnItemClickListener(new OnItemClickListener);
 
        *//** Setting the adapter to the listView *//*
        autoComplete.setAdapter(sca);
	}
*/
	@SuppressWarnings("deprecation")
	private void display() {
		// TODO Auto-generated method stub
		 Cursor cursor = shb.fetchalldata();
		 
	/*	  // The desired columns to be bound
		  String[] columns = new String[] {
		    shb.KEY_NAME,
		    shb.KEY_IMAGEURL
	};
		  int[] to = new int[] {
				  
				    R.id.flag,
				    R.id.txt,
				
				  };*/
		 autoComplete.setAdapter(new CursorAdapter(this, cursor) {
			
			@Override
			public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = LayoutInflater.from(arg0);
				View v = inflater.inflate(R.layout.autocomplete_layout, arg2, false);
				return v;
			}
			
			
			@Override
			public void bindView(View view, Context arg1, Cursor cursor) {
				// TODO Auto-generated method stub
				TextView trytv=(TextView)view.findViewById(R.id.tv_currency);
				TextView ct = (TextView)view.findViewById(R.id.txt);
				ct.setText(cursor.getString(cursor.getColumnIndex(shb.KEY_NAME)));
				ImageView img=(ImageView)view.findViewById(R.id.flag);
				imgLoader=new ImageLoader(arg1, null);
		        imgLoader.displayImage(image_url, img,R.drawable.india);
			}
		});
		
	/*//	sca=new SimpleCursorAdapter(this, R.layout.autocomplete_layout,cursor,columns,to,0);
        autoComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
					
				
	                TextView tvCurrency = (TextView) findViewById(R.id.tv_currency) ;
	                tvCurrency.setText("Currency : " + hm.get("cur"));
			}
		});
*/
		
	}
	
	/** A callback method, which is executed when this activity is about to be killed
	    * This is used to save the current state of the activity
	    * ( eg :  Configuration changes : portrait -> landscape )
	    */
	    @Override
	    protected void onSaveInstanceState(Bundle outState) {
	        TextView tvCurrency = (TextView) findViewById(R.id.tv_currency) ;
	        outState.putString("currency", tvCurrency.getText().toString());
	        super.onSaveInstanceState(outState);
	    }
	    /** A callback method, which is executed when the activity is recreated
	     * ( eg :  Configuration changes : portrait -> landscape )
	     */
	     @Override
	     protected void onRestoreInstanceState(Bundle savedInstanceState) {
	         TextView tvCurrency = (TextView) findViewById(R.id.tv_currency) ;
	         tvCurrency.setText(savedInstanceState.getString("currency"));
	         super.onRestoreInstanceState(savedInstanceState);
	     }
	
	     

}
