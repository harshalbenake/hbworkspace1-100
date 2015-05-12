package com.example.webviewnonxss;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.annotation.SuppressLint;
import android.app.Activity;


@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		setContentView(R.layout.main);
	
		final WebView video =(WebView)findViewById(R.id.videoView);
		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setText("Play Video");
		btnPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			video.getSettings().setJavaScriptEnabled(true);
			video.getSettings().setPluginState(WebSettings.PluginState.ON);
			video.setWebChromeClient(new WebChromeClient() {
			});	
			

			//youtube video url
			////http://www.youtube.com/watch?v=WM5HccvYYQg
			final String mimeType = "text/html";
			final String encoding = "UTF-8";
			String videoidfunc=getYoutubeVideoId("http://www.youtube.com/watch?v=G43tYXIlUV4&feature=player_detailpage#t=2s");
			String html = getHTML(videoidfunc);
			video.loadDataWithBaseURL("",  html, mimeType, encoding, "");
			}
			});
	}
	public String getHTML(String videoId) {
		
		String html =
		"<iframe class=\"youtube-player\" "
		+ "style=\"border: 0; width: 100%; height: 95%;"
		+ "padding:0px; margin:0px\" "
		+ "id=\"ytplayer\" type=\"text/html\" "
		+ "src=\"http://www.youtube.com/embed/" + videoId
		+ "?fs=0\" frameborder=\"0\" " + "allowfullscreen autobuffer "
		+ "controls onclick=\"this.play()\">\n" + "</iframe>\n";
		 
		/**
		 * <iframe id="ytplayer" type="text/html" width="640" height="360"
		 * src="https://www.youtube.com/embed/WM5HccvYYQg" frameborder="0"
		 * allowfullscreen>
		 ***/		
/*		
		String html= "<object width=\"100%\" height=\"100%\">"+
				   "<param name=\"allowFullScreen\" value=\"true\"></param>"+
				    "<param name=\"allowscriptaccess\" value=\"always\"></param>"+
				    "<embed src=\"http://www.youtube.com/v/"+videoId+"?version=3&amp;hl=en_US&amp;rel=0\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"100%\" height=\"100%\"></embed></object>";
	*/
		
	
		return html;
		}
		
	public static String getYoutubeVideoId(String youtubeUrl)
	 {
	 String video_id="";
	  if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http"))
	 {
	 
	 String expression = "^.*((youtu.be"+ "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
	 CharSequence input = youtubeUrl;
	 Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
	 Matcher matcher = pattern.matcher(input);
	 if (matcher.matches())
	 {
	String groupIndex1 = matcher.group(7);
	 if(groupIndex1!=null && groupIndex1.length()==11)
	 video_id = groupIndex1;
	 }
	 }
	 return video_id;
	 }
}
