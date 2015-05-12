package com.example.signature;

import java.io.File;
import java.io.FileOutputStream;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Esignature extends Activity {
	GestureOverlayView gestureView;
	String path;
	File file;
	Bitmap bitmap;
	public boolean gestureTouch=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.esign_main);
		
		
		
		Button donebutton = (Button) findViewById(R.id.DoneButton);
		donebutton.setText("Done");
		Button clearButton = (Button) findViewById(R.id.ClearButton);
		clearButton.setText("Clear");
		
		path=Environment.getExternalStorageDirectory()+"/signature.png";
		file = new File(path);
		file.delete();
		gestureView = (GestureOverlayView) findViewById(R.id.signaturePad);
		gestureView.setDrawingCacheEnabled(true);

		gestureView.setAlwaysDrawnWithCacheEnabled(true);
		gestureView.setHapticFeedbackEnabled(false);
		gestureView.cancelLongPress();
		gestureView.cancelClearAnimation();
		gestureView.addOnGestureListener(new OnGestureListener() {

			@Override
			public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGestureCancelled(GestureOverlayView arg0,
					MotionEvent arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGestureEnded(GestureOverlayView arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGestureStarted(GestureOverlayView arg0,
					MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (arg1.getAction()==MotionEvent.ACTION_MOVE){
                 	gestureTouch=false; 					
             }
             else 
             {
             		gestureTouch=true;
			}
			}});
		
		donebutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					bitmap = Bitmap.createBitmap(gestureView.getDrawingCache());
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					fos = new FileOutputStream(file);
					// compress to specified format (PNG), quality - which is
					// ignored for PNG, and out stream
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			if(gestureTouch==false)
			{
				setResult(0);
				finish();
			}
			else
			{
				setResult(1);
				finish();
			}
			}
		});
		
		clearButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gestureView.invalidate();
				gestureView.clear(true);
				gestureView.clearAnimation();
				gestureView.cancelClearAnimation();
			}
		});
	}

	

	
	
}
