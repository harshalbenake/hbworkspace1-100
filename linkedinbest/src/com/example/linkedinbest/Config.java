package com.example.linkedinbest;

import android.content.Context;
import android.content.SharedPreferences;



public class Config {

	public static String LINKEDIN_KEY = "7kl0wwckny93";
	public static String LINKED_SECRET = "c4jNeWoRXm4Lo0O4";

	public static String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
	public static String OAUTH_CALLBACK_HOST = "callback";
	public static final String OAUTH_CALLBACK_URL = String.format("%s://%s", OAUTH_CALLBACK_SCHEME, OAUTH_CALLBACK_HOST);

	public static String APP_NAME = "HB";
//	public static String OAuthUserToken="e22fea17-7525-48ff-b35f-c59c2b4da0c7";
//	public static String OAuthUserSecret=" 17bf11b7-031e-43b5-b5db-e143f35b1515";

	public static String PREFERENCENAME="linkedinpreference";


	public static final String OAUTH_PREF = "LIKEDIN_OAUTH";
//	public static final String PREF_TOKEN = "e22fea17-7525-48ff-b35f-c59c2b4da0c7";
//	public static final String PREF_TOKENSECRET = "17bf11b7-031e-43b5-b5db-e143f35b1515";
	public static final String PREF_TOKEN = "token";
	public static final String PREF_TOKENSECRET = "tokenSecret";
	
	public static final String PREF_REQTOKENSECRET = "requestTokenSecret";

	public static final String OAUTH_QUERY_TOKEN = "oauth_token";
	public static final String OAUTH_QUERY_VERIFIER = "oauth_verifier";
	public static final String OAUTH_QUERY_PROBLEM = "oauth_problem";

	public static final String ID_OPTION = "id";


	Context context;

	public Config(Context context){
		this.context=context;
	}

	/*public void storeLinkedInKeyDetails(String linkedinConsumerKey,String linkedinSecretKey) {
		SharedPreferences sharedPreferences=context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(LINKEDINCONSUMERKEY, linkedinConsumerKey);
		editor.putString(LINKEDINSECRETKEY, linkedinSecretKey);

		editor.commit();
		System.out.println("linkedin DATA SAVED");
	}


	public String  getLinkedInConsumerKey() {
		SharedPreferences sharedPreferences=context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		return sharedPreferences.getString(LINKEDINCONSUMERKEY, "");
	}

	public String  getLinkedInSecretKey() {
		SharedPreferences sharedPreferences=context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		return sharedPreferences.getString(LINKEDINSECRETKEY, "");
	}*/

}

