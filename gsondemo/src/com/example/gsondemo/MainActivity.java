package com.example.gsondemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import com.google.gson.Gson;

import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


@SuppressLint("NewApi")
public class MainActivity extends Activity {
	HttpClient httpclient;
	HttpPost httppost;
	HttpResponse response;
	String inputResult;
	ListView lv;
	ArrayList<String> a;
	String url;
	dbhelper db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.main);
		
		try 
		{
			url="http://192.168.1.245:9006/flashre/working/development/version1/webservices/api.php?";
			lv=(ListView)findViewById(R.id.list);
			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("method", "getAllFlashUsers"));
			nameValuePairs.add(new BasicNameValuePair("user_id", "210"));
			nameValuePairs.add(new BasicNameValuePair("step", "smartSearch"));
			nameValuePairs.add(new BasicNameValuePair("flash_id", "dggg3566"));
			a=new ArrayList<String>();
			
			ArrayAdapter<String> aa=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, a);
			lv.setAdapter(aa);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = httpclient.execute(httppost);
			inputResult = inputStreamToString(response.getEntity().getContent()).toString();
			
			Gson gson=new Gson();
			searchresponse model=gson.fromJson(inputResult,searchresponse.class);
			List<Results> results = model.groupInfo;
			 
			        for (Results re : results) {
			        	a.add(re.group_name);
			       }
		}
			        catch (Exception e) {
						// TODO: handle exception
					}
		

    final Dbadapter db=new Dbadapter(this);
	lv.setOnItemClickListener(new OnItemClickListener() {

		

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Object o=arg0.getItemAtPosition(arg2);
			if(arg2>=0)
			{
			String s=o.toString();
			String res=db.insertgroup(s);
			Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
			}
		}
	});
	
		
	}
	private StringBuilder inputStreamToString(InputStream is) {
		String rLine = "";
		StringBuilder answer = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try
		{
			while ((rLine = rd.readLine()) != null) {
				answer.append(rLine);
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return answer;
	}
	
	
	
}




