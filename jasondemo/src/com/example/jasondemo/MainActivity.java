package com.example.jasondemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		  StrictMode.setThreadPolicy(
	                new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
	        writeJSON();
	        String readTwitterFeed = readTwitterFeed();
	        try {
	            JSONArray jsonArray = new JSONArray(readTwitterFeed);
	            Log.i(MainActivity.class.getName(),
	                    "Number of entries " + jsonArray.length());
	            for (int i = 0; i < jsonArray.length(); i++) {
	                JSONObject jsonObject = jsonArray.getJSONObject(i);
	                Log.i(MainActivity.class.getName(), jsonObject.getString("text"));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	       
	}

	private void writeJSON() {
		// TODO Auto-generated method stub
		JSONObject object = new JSONObject();
        try {
            object.put("name", "EDUMobile");
            object.put("score", new Integer(200));
            object.put("current", new Double(152.32));
            object.put("nickname", "Android Tutor");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), (CharSequence) object, Toast.LENGTH_LONG).show();
        System.out.println(object);
	}

	private String readTwitterFeed() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(
                "http://192.168.1.245:9006/flashre/working/development/version1/webservices/api.php?method=getAllFlashUsers&user_id=210&step=smartSearch&group_id=&name=&group_name=&flash_id=dggg3566");
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(MainActivity.class.toString(), "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
		
	}

	
}
