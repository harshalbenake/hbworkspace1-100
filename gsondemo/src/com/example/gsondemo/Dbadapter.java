package com.example.gsondemo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class Dbadapter extends Activity{
	public static final String DB_name="dbname";
	public static final String DB_table="tabname";
	public static final int DB_version=1;
	public static final String DATABASE_CREATE ="create table tabname (group_name text not null);";
	private final Context context;
	SQLiteDatabase db;
	DatabaseHelper dbhelper;

	public Dbadapter(Context cxt) {
		// TODO Auto-generated constructor stub
		this.context = cxt;
        dbhelper = new DatabaseHelper(context);
	}
	
	
	 private static class DatabaseHelper extends SQLiteOpenHelper 
	    {
	        DatabaseHelper(Context context) 
	        {
	            super(context, DB_name, null, DB_version);
	        }

	        @Override
	        public void onCreate(SQLiteDatabase db) 
	        {
	            db.execSQL(DATABASE_CREATE);
	        }

			@Override
			public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
				// TODO Auto-generated method stub
				db.execSQL("DROP TABLE IF EXIST Info");
				onCreate(db);
			}
	    }
	
	 public String insertgroup(String gname) 
	    {
		 db = dbhelper.getWritableDatabase();
	        ContentValues initialValues = new ContentValues();
	        initialValues.put("group_name",gname);
	         db.insert(DB_table, null, initialValues);
	         return gname;
	    }

}
