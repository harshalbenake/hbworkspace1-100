package com.example.videodemo;


import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import android.app.Activity;
import android.content.Intent;


public class MainActivity extends Activity {

	VideoView vv;
	Intent i;
	MediaController mc;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mc=new MediaController(this);
		vv=(VideoView)findViewById(R.id.videoView1);
		vv.setVideoURI(Uri.parse("rtsp://v8.cache7.c.youtube.com/CjYLENy73wIaLQmpSxsmuuQZLhMYDSANFEIJbXYtZ29vZ2xlSARSBXdhdGNoYMfekcm0kuvtUQw=/0/0/0/video.3gp"));
		vv.setMediaController(mc); 
		vv.requestFocus();
		vv.start();
	}

	

}
