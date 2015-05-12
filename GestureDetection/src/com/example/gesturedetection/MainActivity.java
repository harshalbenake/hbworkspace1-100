package com.example.gesturedetection;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class MainActivity extends Activity {

	   private ImageView img;
	   private Matrix matrix = new Matrix();
	   private float scale = 1f;
	   private ScaleGestureDetector SGD;
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_main);
	      img = (ImageView)findViewById(R.id.imageView1);
	      SGD = new ScaleGestureDetector(this,new ScaleListener());
	   }

	   @Override
	   public boolean onTouchEvent(MotionEvent ev) {
	      SGD.onTouchEvent(ev);
	      return true;
	   }

	   private class ScaleListener extends ScaleGestureDetector.
	   SimpleOnScaleGestureListener {
	   @Override
	   public boolean onScale(ScaleGestureDetector detector) {
	      scale *= detector.getScaleFactor();
	      scale = Math.max(0.1f, Math.min(scale, 5.0f));
	      matrix.setScale(scale, scale);
	      img.setImageMatrix(matrix);
	      return true;
	   }
}
}
