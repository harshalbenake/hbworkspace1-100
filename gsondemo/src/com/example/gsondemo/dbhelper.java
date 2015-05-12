package com.example.gsondemo;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbhelper extends SQLiteOpenHelper {
	
	 SQLiteDatabase db;
	 
    Context helperContext;
	public dbhelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		 helperContext = context;
		// TODO Auto-generated constructor stub
	}
	
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Name
    private static final String DATABASE_NAME = "dbname";
 
    // table name
    private static final String TABLE_NAME = "groupdb";
    
 // Table Columns names
    private static final String KEY_GROUP = "grp";
    
	

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_GROUP + " TEXT,"
                 + ")";
        arg0.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		 arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		 
	        // Create tables again
	        onCreate(arg0);
	}
	
	
	 // Getting All 
	 public List<groupdb> getallgroup() 
	 {
	    List<groupdb> gList = new ArrayList<groupdb>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_NAME;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	groupdb gc = new groupdb();
	            gc.setgname(cursor.getString(0));
	            // Adding contact to list
	            gList.add(gc);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return gList;
	}

}
