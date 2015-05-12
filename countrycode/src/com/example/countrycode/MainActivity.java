package com.example.countrycode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	 
	private Spinner country;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        country = (Spinner) findViewById(R.id.spinner1);

	      
	        
	        Locale[] locales = Locale.getAvailableLocales();
	        ArrayList<String> countries = new ArrayList<String>();
	        for (Locale locale : locales) {
	            String countryname = locale.getDisplayCountry();
	            String countrycode = locale.getISO3Country();

	            String country=countryname+" "+countrycode;
	            if (country.trim().length() > 0 && !countries.contains(country)) {
	                countries.add(country);
	            }
	        }
	        Collections.sort(countries);
	        for (String country : countries) {
	            System.out.println(country);
	        }

	        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
	                android.R.layout.simple_spinner_item, countries);
	        // set the view for the Drop down list
	        dataAdapter
	                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        // set the ArrayAdapter to the spinner
	        country.setAdapter(dataAdapter);
	        country.setSelection(37);

	        System.out.println("# countries found: " + countries.size());

	    }
	    
	    public String getOperator()
	       {
		       	TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		       	String opeartorName = manager.getSimOperator();
		       	return opeartorName; 
	       }

}
