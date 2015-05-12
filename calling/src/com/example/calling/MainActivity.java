package com.example.calling;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_main);
	      Button startBtn = (Button) findViewById(R.id.button1);
	      startBtn.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View view) {
	         makeCall();
	      }
	   });

	   }
	   protected void makeCall() {

	      Intent phoneIntent = new Intent(Intent.ACTION_CALL);
	      phoneIntent.setData(Uri.parse("tel:7588871488"));

	      try {
	         startActivity(phoneIntent);
	         finish();
	      } catch (android.content.ActivityNotFoundException ex) {
	         Toast.makeText(MainActivity.this, "Call faild, please try again later.", Toast.LENGTH_SHORT).show();
	      }
	   }
	}
