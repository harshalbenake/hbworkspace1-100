package com.example.call;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TelephonyManager telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceNumber=telephonyManager.getLine1Number();
        
      
   
   	
        Toast.makeText(getApplicationContext(), "deviceNumber:"+deviceNumber, Toast.LENGTH_LONG).show();      
        String output = deviceNumber.substring( 0,3 ) +"-" + deviceNumber.substring( 3,6 ) + "-" + deviceNumber.substring( 6,10 );  
        Toast.makeText(getApplicationContext(), "output:"+output, Toast.LENGTH_LONG).show();
        TextView textView=(TextView)findViewById(R.id.textnumber);
        EditText editText1=(EditText)findViewById(R.id.editText1);
        EditText editText2=(EditText)findViewById(R.id.editText2);
        EditText editText3=(EditText)findViewById(R.id.editText3);

       	textView.setText("deviceNumber:"+output);
       	editText1.setText(deviceNumber.substring( 0,3 ));
       	editText2.setText(deviceNumber.substring( 3,6 ));
       	editText3.setText(deviceNumber.substring( 6,10 ));
    }


}
