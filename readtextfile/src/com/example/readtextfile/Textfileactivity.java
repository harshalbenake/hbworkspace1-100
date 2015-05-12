package com.example.readtextfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

public class Textfileactivity extends Activity{
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.textfile);
	readtextfile();
}

	private void readtextfile() {
	    //Find the directory for the SD Card using the API
		//*Don't* hardcode "/sdcard"
		File sdcard = Environment.getExternalStorageDirectory();

		//Get the text file
		File file = new File(sdcard,"hbfile.txt");

		//Read text from file
		StringBuilder text = new StringBuilder();

		try {
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		        text.append(line);
		        text.append('\n');
		    }
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
		}

		//Find the view by its id
		TextView tv = (TextView)findViewById(R.id.textView1);

		//Set the text
		tv.setText(text);
	}


}
