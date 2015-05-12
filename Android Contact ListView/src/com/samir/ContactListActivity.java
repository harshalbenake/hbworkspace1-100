package com.samir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;


/*
Android Read Contact and Display in ListView
How to display Contact No. in Android ?

You can get contact using ContactsContract.And its provide bridge between contact provider and your applications.That contains URIs  and columns to get contact with information.Such as name,street address,city,various type of phone number,email address,postal address,notes,organization.


While retrieving contact, Don't forgot to add uses-permission

<uses-permission android:name="android.permission.READ_CONTACTS" />

you can retrieve all contacts as a cursor.

Cursor cursor = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);

now we have cusror with contacts and get diffrent value from cusror.

while (cursor.moveToNext()) {
String name =cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
}


Above code,get all contact name and phone numbers from your phone and sim.



Get only SimCard's Contact ::
Cursor cursor = mContentResolver.query(RawContacts.CONTENT_URI,
  new String[]{RawContacts._ID,RawContacts.ACCOUNT_TYPE},
  RawContacts.ACCOUNT_TYPE + " <> 'com.anddroid.contacts.sim' "
   + " AND " + RawContacts.ACCOUNT_TYPE + " <> 'com.google' ",null,null);

we can get diffrent types of phone number from contacts.Like,Home,Mobile,Work.So first you need to get you contact id from cursor.using contact id you can get cursor of phone number.

Get diffrent type of phone number
String contact_Id = cursor.getString(cursor .getColumnIndex(ContactsContract.Contacts._ID));
Cursor cursor_phone = getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + contact_Id, null, null);
while (cursor_phone.moveToNext()) {
String phNumber = cursor_phone.getString(cursor_phone
.getColumnIndex(Phone.NUMBER));
System.out.println(phNumber);
int PHONE_TYPE =cursor_phone.getInt(cursor_phone.getColumnIndex(Phone.TYPE));

switch (PHONE_TYPE) {
case Phone.TYPE_HOME:
//home number
break;
case Phone.TYPE_MOBILE:
//mobile number
break;
case Phone.TYPE_WORK:
//work(office) number
break;
}
}



Get Address From your ContactId

while (cursor.moveToNext()) {
           String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
           Cursor address_cursror = getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,null,
                   ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID+ " = ?",
                   new String[] { id },null);
           while(address_cursror.moveToNext()) {
               String street = address_cursror.getString(address_cursror.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
               System.out.println("Street ::" +street);
               String city = address_cursror.getString(address_cursror.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
               System.out.println("City ::"+city);
               String state = address_cursror.getString(address_cursror.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
               System.out.println("State ::"+state);
               String postalCode = address_cursror.getString(address_cursror.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
               System.out.println("Postal Code ::"+postalCode);
           } 
           address_cursror.close();



How to call in android ?
when you want to call any number from application you have to add CALL_PHONE permission.

<uses-permission android:name="android.permission.CALL_PHONE" />

using Intent.ACTION_CALL you can call in android.

String phoneNumber = "tel:" + phoneNo;
Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
startActivity(intent);*/


public class ContactListActivity extends Activity implements
		OnItemClickListener {

	private ListView listView;
	private List<ContactBean> list = new ArrayList<ContactBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(this);

		Cursor phones = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		while (phones.moveToNext()) {

			String name = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

			String phoneNumber = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

			ContactBean objContact = new ContactBean();
			objContact.setName(name);
			objContact.setPhoneNo(phoneNumber);
			list.add(objContact);

		}
		phones.close();

		ContanctAdapter objAdapter = new ContanctAdapter(
				ContactListActivity.this, R.layout.alluser_row, list);
		listView.setAdapter(objAdapter);

		if (null != list && list.size() != 0) {
			Collections.sort(list, new Comparator<ContactBean>() {

				@Override
				public int compare(ContactBean lhs, ContactBean rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});
			AlertDialog alert = new AlertDialog.Builder(
					ContactListActivity.this).create();
			alert.setTitle("");

			alert.setMessage(list.size() + " Contact Found!!!");

			alert.setButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alert.show();

		} else {
			showToast("No Contact Found!!!");
		}
	}

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View v, int position,
			long id) {
		ContactBean bean = (ContactBean) listview.getItemAtPosition(position);
		showCallDialog(bean.getName(), bean.getPhoneNo());
	}

	private void showCallDialog(String name, final String phoneNo) {
		AlertDialog alert = new AlertDialog.Builder(ContactListActivity.this)
				.create();
		alert.setTitle("Call?");

		alert.setMessage("Are you sure want to call " + name + " ?");

		alert.setButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.setButton2("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String phoneNumber = "tel:" + phoneNo;
				Intent intent = new Intent(Intent.ACTION_CALL, Uri
						.parse(phoneNumber));
				startActivity(intent);
			}
		});
		alert.show();
	}
}
