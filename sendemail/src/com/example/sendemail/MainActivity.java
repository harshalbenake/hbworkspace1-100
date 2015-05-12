package com.example.sendemail;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button send = (Button)findViewById(R.id.button1);
        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                try {   
                    GmailSender sender = new GmailSender("username@gmail.com", "password");
                    sender.sendMail("This is Subject",   
                            "This is Body",   
                            "bits.qa21@gmail.com",   
                            "bits.qa.7@facebook.com");   
                    Toast.makeText(getBaseContext(), "sent", Toast.LENGTH_LONG).show();
                } catch (Exception e) {   
                    Log.e("SendMail", e.getMessage(), e);   
                } 

            }
        });

    }
}