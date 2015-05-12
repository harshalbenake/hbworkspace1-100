package com.example.jsonparsefromtxtfile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	  JSONObject jsonResponse;
	  String firstName,lastName,height_cm,type,number,postalCode, state,city,streetAddress;
	int age;
	boolean isAlive;
	  ArrayList<HashMap<String, String>> arrayList;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView listView=(ListView)findViewById(R.id.listView1);
		
				try {
					//default response
					jsonResponse = new JSONObject(loadJSONFromAsset());
					System.out.println("Response: "+jsonResponse);
					
					//parsing strings
					firstName = jsonResponse.getString("firstName");
					lastName = jsonResponse.getString("lastName");
					isAlive = jsonResponse.getBoolean("isAlive");
					age = jsonResponse.getInt("age");
					height_cm = jsonResponse.getString("height_cm");
					
					System.out.println("firstName: "+ firstName +
							"lastName: "+ lastName + 
							"isAlive: " + isAlive +
							 "age: " + age+
							 "height_cm: " + height_cm);

					//parsing array type o
					JSONObject jsonObject = jsonResponse.getJSONObject("address");
					streetAddress = jsonObject.optString("streetAddress");
					city = jsonObject.optString("city");
					state = jsonObject.optString("state");
					postalCode = jsonObject.optString("postalCode");
		
					System.out.println("streetAddress: "+ streetAddress +
										"city: "+ city + 
										"state: " + state +
										 "postalCode: " + postalCode);
					
					//parsing array type two
					List<String> mylist = new ArrayList<String>();

					JSONArray jsonArray = jsonResponse.getJSONArray("phoneNumbers");
					
					for (int i=0; i<jsonArray.length(); i++) {
					    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
					    type= jsonObject2.getString("type");
					    number= jsonObject2.getString("number");
					
					    System.out.println("type: "+type+
					    		"number: "+number);
					    mylist.add(type+"::"+number);
					    
					    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						        android.R.layout.simple_list_item_1, mylist);
					    listView.setAdapter(adapter);
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
	   
	}
	
	
	/**
	 * This method is used to load json file from asset folder.
	 * @return
	 */
	public String loadJSONFromAsset() {
	    String json = null;
	    try {

	        InputStream is = getAssets().open("hb.json");

	        int size = is.available();

	        byte[] buffer = new byte[size];

	        is.read(buffer);

	        is.close();

	        json = new String(buffer, "UTF-8");


	    } catch (IOException ex) {
	        ex.printStackTrace();
	        return null;
	    }
	    return json;

	}
}
