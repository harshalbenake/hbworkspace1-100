package com.example.layoutadddynamically;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

	ScrollView scrollview;
    LinearLayout  linearLayout;
    LinearLayout.LayoutParams layoutParams;
    static int i;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollview = (ScrollView)findViewById(R.id.scrollview);
        linearLayout = (LinearLayout)findViewById(R.id.linearlayout);
        Button button = (Button)findViewById(R.id.button);
        layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        button.setOnClickListener(new OnClickListener(){

            public void onClick(View v) {
                TextView view = new TextView(MainActivity.this);             
                view.setText(++i+" view");
                linearLayout.addView(view, layoutParams); 
            }

        });

}}
