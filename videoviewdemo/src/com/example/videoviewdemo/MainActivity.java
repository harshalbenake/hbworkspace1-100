package com.example.videoviewdemo;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;





public class MainActivity extends Activity {
	VideoView vv;
	String url;
	Intent i;
	MediaController mc;
	String URL="http://www.youtube.com/watch?v=A0FRGlKEZJM";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		String rtspURL=getUrlVideoRTSP(URL);
		System.out.println("-- YT URL "+URL+" rtspURL "+rtspURL);
		mc=new MediaController(this);
		vv=(VideoView)findViewById(R.id.videoview1);
		 
//		vv.setVideoURI(Uri.parse("rtsp://v6.cache5.c.youtube.com/CjYLENy73wIaLQnMJySvT9_ivRMYDSANFEIJbXYtZ29vZ2xlSARSBXdhdGNoYMfekcm0kuvtUQw=/0/0/0/video.3gp"));
		vv.setVideoURI(Uri.parse(rtspURL));
		
		
		
		vv.setMediaController(mc); 
		
		vv.requestFocus();
		 
		vv.start();
		
	}

	public static String getUrlVideoRTSP(String urlYoutube)
    {
        try
        {
            String gdy = "http://gdata.youtube.com/feeds/api/videos/";
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String id = extractYoutubeId(urlYoutube);
            URL url = new URL(gdy + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Document doc = documentBuilder.parse(connection.getInputStream());
            Element el = doc.getDocumentElement();
            NodeList list = el.getElementsByTagName("media:content");///media:content
            String cursor = urlYoutube;
            for (int i = 0; i < list.getLength(); i++)
            {
                Node node = list.item(i);
                if (node != null)
                {
                    NamedNodeMap nodeMap = node.getAttributes();
                    HashMap<String, String> maps = new HashMap<String, String>();
                    for (int j = 0; j < nodeMap.getLength(); j++)
                    {
                        Attr att = (Attr) nodeMap.item(j);
                        maps.put(att.getName(), att.getValue());
                    }
                    if (maps.containsKey("yt:format"))
                    {
                        String f = maps.get("yt:format");
                        if (maps.containsKey("url"))
                        {
                            cursor = maps.get("url");
                        }
                        if (f.equals("1"))
                            return cursor;
                    }
                }
            }
            return cursor;
        }
        catch (Exception ex)
        {
            Log.e("Get Url Video RTSP Exception======>>", ex.toString());
        }
        return urlYoutube;

    }
	
	protected static String extractYoutubeId(String url) 
    {
        String id = null;
        try
        {
            String query = new URL(url).getQuery();
            if (query != null)
            {
                String[] param = query.split("&");
                for (String row : param)
                {
                    String[] param1 = row.split("=");
                    if (param1[0].equals("v"))
                    {
                        id = param1[1];
                    }
                }
            }
            else
            {
                if (url.contains("embed"))
                {
                    id = url.substring(url.lastIndexOf("/") + 1);
                }
            }
        }
        catch (Exception ex)
        {
            Log.e("Exception", ex.toString());
        }
        return id;
    }

}
