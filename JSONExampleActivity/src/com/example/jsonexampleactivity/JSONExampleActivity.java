package com.example.jsonexampleactivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class JSONExampleActivity extends Activity {
	HttpClient httpclient;
	HttpPost httppost;
	HttpResponse response;
	String jsonResult;
	JSONObject object;
	ListView lv;
	ArrayList<String> a;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		setContentView(R.layout.main);
		try 
		{
			lv=(ListView)findViewById(R.id.list);
			httpclient = new DefaultHttpClient();
			httppost = new HttpPost("http://192.168.1.245:9006/flashre/working/development/version1/webservices/api.php?");
			// method=getAllFlashUsers&user_id=210&step=smartSearch&group_id=&name=&group_name=&flash_id=dggg3566
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("method", "getAllFlashUsers"));
			nameValuePairs.add(new BasicNameValuePair("user_id", "210"));
			nameValuePairs.add(new BasicNameValuePair("step", "smartSearch"));
//			nameValuePairs.add(new BasicNameValuePair("group_id", ""));
//			nameValuePairs.add(new BasicNameValuePair("name", ""));
//			nameValuePairs.add(new BasicNameValuePair("group_name", ""));
			nameValuePairs.add(new BasicNameValuePair("flash_id", "dggg3566"));
			a=new ArrayList<String>();
			
			ArrayAdapter<String> aa=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, a);
			lv.setAdapter(aa);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = httpclient.execute(httppost);
			jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
			System.out.println(response);
			System.out.println("jsonResult "+jsonResult);
			object = new JSONObject(jsonResult);
			JSONArray jArray = object.getJSONArray("groupInfo");
			for (int i=0; i < jArray.length(); i++)
			{
				try {
					JSONObject oneObject = jArray.getJSONObject(i);
					// Pulling items from the array
					a.add(oneObject.getString("group_name"));
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
