package com.example.databasefromasset;

import android.app.Activity;
import android.database.SQLException;
import android.os.Bundle;

public class MainActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DataBaseHelper myDbHelper = new DataBaseHelper(MainActivity.this);
		myDbHelper = new DataBaseHelper(this);
		try {
			myDbHelper.createDatabase();
			System.out.println("Database successfully created");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		try {
			myDbHelper.openDatabase();
		} catch (SQLException sqle) {
			throw sqle;
		}
		
	}

}
