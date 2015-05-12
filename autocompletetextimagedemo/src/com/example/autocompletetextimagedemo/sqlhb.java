package com.example.autocompletetextimagedemo;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class sqlhb extends SQLiteOpenHelper {
	//public static final String KEY_ROW = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_IMAGEURL = "imageurl";
	public sqlhb(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		arg0.execSQL("create table tabname(_id integer primary key" +","+ KEY_NAME 
				 + " varchar," + KEY_IMAGEURL + " varchar)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		arg0.execSQL("DROP TABLE IF EXISTS");
		onCreate(arg0);
	}
	
	public void addcontent(String name,String imageurl)
	{
		SQLiteDatabase db=getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put("name", name);
		cv.put("imageurl",imageurl);
		db.insert("tabname", null, cv);
	}
	
	public ArrayList<String> getnames()
	{
	ArrayList<String> ar=new ArrayList<String>();
	SQLiteDatabase db=getWritableDatabase();
	Cursor c=db.rawQuery("select * from tabname",null);
	c.moveToFirst();
	{
	do
	{
	String name=c.getString(c.getColumnIndex("name"));
	ar.add(name);
	}while(c.moveToNext());
	return ar;
	}
	}
	
	public Cursor fetchalldata() {	
		SQLiteDatabase db=getWritableDatabase();
		Cursor c=db.rawQuery("select * from tabname",null);
		return c;
		}
	
		 }
