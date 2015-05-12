package com.example.webviewyoutubeapi;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class MainActivity extends Activity {
	public static final int USER_MOBILE = 0;
	public static final int USER_DESKTOP = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			String html = getHTML("VSDqDOLupNc");
			video.loadDataWithBaseURL("", html, mimeType, encoding, "");
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
			 **/
			
			
			return html;
			}
			
			
}