package com.example.jasonparsedemo;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("NewApi")
public class MainActivity extends ListActivity {
	  // url to make request
    private static String url = "http://api.androidhive.info/contacts/";
     
    // JSON Node names
    private static final String TAG_CONTACTS = "contacts";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_PHONE_MOBILE = "mobile";
    private static final String TAG_PHONE_HOME = "home";
    private static final String TAG_PHONE_OFFICE = "office";
 
    // contacts JSONArray
    JSONArray contacts = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		  StrictMode.ThreadPolicy policy = new StrictMode.
				    ThreadPolicy.Builder().permitAll().build();
				    StrictMode.setThreadPolicy(policy); 
				    
		setContentView(R.layout.main);
	
		// Hashmap for ListView
        ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
 
        // Creating JSON Parser instance
        JSONParser jParser = new JSONParser();
 
        // getting JSON string from URL
        JSONObject json = jParser.getJSONFromUrl(url);
 
        try {
            // Getting Array of Contacts
            contacts = json.getJSONArray(TAG_CONTACTS);
             
            // looping through All Contacts
            for(int i = 0; i < contacts.length(); i++){
                JSONObject c = contacts.getJSONObject(i);
                 
                // Storing each json item in variable
                String id = c.getString(TAG_ID);
                String name = c.getString(TAG_NAME);
                String email = c.getString(TAG_EMAIL);
                String address = c.getString(TAG_ADDRESS);
                String gender = c.getString(TAG_GENDER);
                // Phone number is agin JSON Object
                JSONObject phone = c.getJSONObject(TAG_PHONE);
                String mobile = phone.getString(TAG_PHONE_MOBILE);
                String home = phone.getString(TAG_PHONE_HOME);
                String office = phone.getString(TAG_PHONE_OFFICE);
                 
                // creating new HashMap
                HashMap<String, String> map = new HashMap<String, String>();
                 
                // adding each child node to HashMap key => value
                map.put(TAG_ID, id);
                map.put(TAG_NAME, name);
                map.put(TAG_EMAIL, email);
                map.put(TAG_PHONE_MOBILE, mobile);
 
                // adding HashList to ArrayList
                contactList.add(map);
                Toast.makeText(getApplicationContext(),id+name+email+mobile, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
         
       
     
     
	}
}
