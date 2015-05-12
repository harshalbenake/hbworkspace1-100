package com.example.linkedinbest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.media.MediaRouter.Callback;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientException;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Connections;
import com.google.code.linkedinapi.schema.Person;



/**
 * @author harshalb
 *This is main class of linkedin.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class LinkedinWebviewDialog  {
	private Context mContext;
	private WebView mWebView;
	private final String TAG = "LinkedinWebviewDialog";
	private static final EnumSet<ProfileField> ProfileParameters = EnumSet.allOf(ProfileField.class);

	private final LinkedInOAuthService oAuthService = 
			LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(Config.LINKEDIN_KEY, Config.LINKED_SECRET);
	private final LinkedInApiClientFactory factory = 
			LinkedInApiClientFactory.newInstance(Config.LINKEDIN_KEY, Config.LINKED_SECRET);

	public LinkedInRequestToken linkedinToken;
	public LinkedInApiClient linkedinClient;

	private String callFrom="";
	public final static String SYNC_FRIENDS="SYNC_FRIENDS",SHARE_STATUS="SHARE_STATUS";
	private String statusText="";

	/**
	 * @param context
	 * @param callFrom
	 */
	public LinkedinWebviewDialog(Context context, String callFrom) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		this.mContext=context;
		this.callFrom=callFrom;
		operations();
	}

	/**
	 * @param context
	 * @param callFrom
	 * @param shareStatus
	 */
	public LinkedinWebviewDialog(Context context, String callFrom, String shareStatus) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		this.mContext=context;
		this.callFrom=callFrom;
		this.statusText=shareStatus;
		operations();
	}

	/**
	 * This method is used for using share preference.
	 */
	protected void operations() {
		final SharedPreferences pref = mContext.getSharedPreferences(Config.OAUTH_PREF,	Context.MODE_PRIVATE);
		final String token = pref.getString(Config.PREF_TOKEN, null);
		final String tokenSecret = pref.getString(Config.PREF_TOKENSECRET, null);
		fetchInfo(token, tokenSecret);
	}

	/**
	 * This method is used for access token and webview dialog show.
	 */
	void authenticationStart() {
		final LinkedInRequestToken liToken = oAuthService.getOAuthRequestToken(Config.OAUTH_CALLBACK_URL);
		final String url = liToken.getAuthorizationUrl();
		mContext.getSharedPreferences(Config.OAUTH_PREF, Context.MODE_PRIVATE)
		.edit()
		.putString(Config.PREF_REQTOKENSECRET, liToken.getTokenSecret())
		.commit();

		WebviewDialog webviewDialog=new WebviewDialog(mContext,url);
		webviewDialog.show();
	}

	/**
	 * @param token
	 * @param tokenSecret
	 * This method is used for verification of tokens.
	 */
	private void fetchInfo(String token, String tokenSecret) {
		if (token == null || tokenSecret == null) {
			authenticationStart();
		} else {
			new AsyncGetCurrentUserInfo().execute(new LinkedInAccessToken(token, tokenSecret));
		}
	}

	/**
	 * @param uri
	 * This method is used for access token and asyn task execution.
	 */
	void authenticationFinish(final Uri uri) {
		if (uri != null && uri.getScheme().equals(Config.OAUTH_CALLBACK_SCHEME)) {
			final String problem = uri.getQueryParameter(Config.OAUTH_QUERY_PROBLEM);
			if (problem == null) {
				final SharedPreferences pref = mContext.getSharedPreferences(Config.OAUTH_PREF, Context.MODE_PRIVATE);
				final LinkedInAccessToken accessToken = oAuthService
						.getOAuthAccessToken(
								new LinkedInRequestToken(
										uri.getQueryParameter(Config.OAUTH_QUERY_TOKEN),
										pref.getString(Config.PREF_REQTOKENSECRET, null)),
										uri.getQueryParameter(Config.OAUTH_QUERY_VERIFIER));
				pref.edit()
				.putString(Config.PREF_TOKEN, accessToken.getToken())
				.putString(Config.PREF_TOKENSECRET, accessToken.getTokenSecret())
				.remove(Config.PREF_REQTOKENSECRET).commit();

				new AsyncGetCurrentUserInfo().execute(accessToken);
			} else {
				
			}
		}else{
			//error
		}
	}


	/**
	 * @author harshalb
	 *
	 *This is asyntask for linkedin.
	 */
	class AsyncGetCurrentUserInfo extends AsyncTask<LinkedInAccessToken, Integer, Person> {
		ProgressDialog dialog;
		private LinkedInAccessToken accessToken;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(mContext);
			dialog.setMessage("Please wait...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Person doInBackground(LinkedInAccessToken... arg0) {
			try{
				accessToken=arg0[0];
				linkedinClient = factory.createLinkedInApiClient(accessToken);
				linkedinClient.setAccessToken(accessToken);
				return linkedinClient.getProfileForCurrentUser(ProfileParameters);
			} catch (LinkedInApiClientException ex){
				Log.e(TAG, "LinkedInApiClientException: ", ex);
				return null;
			}
		}

		@Override
		protected void onPostExecute(Person person) {

			if(person == null){
				Toast.makeText(
						mContext,
						"application_down_due_to_linkedinapiclientexception",
						Toast.LENGTH_LONG).show();
				dialog.dismiss();
			}else{
				System.out.println("person info :");
				System.out.println("person name"+person.getFirstName() +" "+person.getLastName());
				//				mCurrentPerson = person;
				//				populateAll(person);
				if(callFrom.equalsIgnoreCase(SHARE_STATUS)){
					new AsyncTask<String, String, String>() {
						ProgressDialog progressDialog;
						@Override
						protected void onPreExecute() {
							super.onPreExecute();
							progressDialog=new ProgressDialog(mContext);
						}

						@Override
						protected String doInBackground(String... params) {
							try {
							linkedinClient.updateCurrentStatus("This is Myappsco website:"+"www.myappsco.com");
							linkedinClient.postNetworkUpdate("This is Myappsco website:"+"www.myappsco.com");
							}
							catch (Exception e) {
								// TODO: handle exception
								System.out.println("throttle::"+e);
							}
							return null;
						}

						@Override
						protected void onPostExecute(String result) {
							Toast.makeText(
									mContext,
									"Posted successfully...",
									Toast.LENGTH_LONG).show();
							super.onPostExecute(result);
						}
					}.execute("");
				}else if (callFrom.equalsIgnoreCase(SYNC_FRIENDS)) {
					LinkedInFriends friends=new LinkedInFriends(mContext,accessToken);
					friends.show();
				}
				dialog.dismiss();
			}
		}


	}

	/**
	 * List of listener.
	 */
	private List<OnVerifyListener> listeners = new ArrayList<OnVerifyListener>();

	/**
	 * Register a callback to be invoked when authentication have finished.
	 * 
	 * @param data
	 *            The callback that will run
	 */
	public void setVerifierListener(OnVerifyListener data) {
		listeners.add(data);
	}

	/**
	 * 
	 */
	public void doOprations() {
		if(callFrom.equalsIgnoreCase(SYNC_FRIENDS)){
			setVerifierListener(new OnVerifyListener() {
				@Override
				public void onVerify(String verifier) {
					try {
						Log.i("LinkedinSample", "verifier: " + verifier);

						LinkedInAccessToken accessToken = oAuthService.getOAuthAccessToken(linkedinToken,
								verifier);
						linkedinClient = factory.createLinkedInApiClient(accessToken);

						LinkedInFriends linkedInFriends=new LinkedInFriends(mContext,accessToken);
						linkedInFriends.show();
					}catch (Exception e) {
						// TODO: handle exception
						
					}
				}
			});
		}else if(callFrom.equalsIgnoreCase(SHARE_STATUS)){
			setVerifierListener(new OnVerifyListener() {
				@Override
				public void onVerify(String verifier) {
					try {
						Log.i("LinkedinSample", "verifier: " + verifier);

						LinkedInAccessToken accessToken = oAuthService.getOAuthAccessToken(linkedinToken,
								verifier);
						linkedinClient = factory.createLinkedInApiClient(accessToken);

						//							linkedinClient.updateCurrentStatus(status);
						//							currentStatus.setText("");
						linkedinClient.postNetworkUpdate(statusText);

						Person profile = linkedinClient.getProfileForCurrentUser(EnumSet
								.of(ProfileField.FIRST_NAME,
										ProfileField.LAST_NAME,
										ProfileField.PICTURE_URL));

						Log.e("Name:",
								"" + profile.getFirstName() + " "
										+ profile.getLastName());
						// Log.e("BirthDate:", "" + profile.getDateOfBirth());
						Log.e("Headline:", "" + profile.getHeadline());
						Log.e("Summary:", "" + profile.getSummary());
						Log.e("Industry:", "" + profile.getIndustry());
						Log.e("Picture url:", "" + profile.getPictureUrl());

						String stringUrl = profile.getPictureUrl().toString();
						System.out.println("url test" + stringUrl);


						Connections connections =linkedinClient.getConnectionsForCurrentUser(); 
						List<String> list = new ArrayList<String>(); 

						for(Person p :connections.getPersonList()) {

							Log.e("Name", "" + p.getLastName() + " " +p.getFirstName()); 
							Log.e("Industry      ", "" + p.getIndustry()); 
							Log.e("      ", "" +  "*****************");
							Log.e("currentStatus ",""+p.getCurrentStatus());
							Log.e("link          ",""+p.getPublicProfileUrl());
							Log.e("position      ",""+p.getEducations());
							Log.e("id","" +p.getId());
							list.add(p.getId());

						}
						System.out.println("list::"+list);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			//			LinkedinWebviewDialog.this.dismiss();
		}
	}

	/**
	 * Listener for oauth_verifier.
	 */
	public interface OnVerifyListener {
		/**
		 * invoked when authentication have finished.
		 * 
		 * @param verifier
		 *            oauth_verifier code.
		 */
		public void onVerify(String verifier);
	}

	/**
	 * @author harshalb
	 *This class is used for firends and message.
	 */
	public class LinkedInFriends extends Dialog{
		//Activity activity;
		LayoutInflater inflater;
		ListView listViewFriends;
		List<Person> arraylistFriends;
		Connections connections;
		ImageLoader imageLoader;
		LinkedInFriendsAdapter linkedInFriendsAdapter;
		private Context context;
		public LinkedInFriends(Context context, LinkedInAccessToken arg0) {
			super(context);
			this.context=context;
			linkedinClient = factory.createLinkedInApiClient(arg0);
			linkedinClient.setAccessToken(arg0);
		}


		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			//	activity=getOwnerActivity();
			setContentView(R.layout.friends_dialog_main);
			listViewFriends=(ListView)findViewById(R.id.listview_friends);

			arraylistFriends=new ArrayList<Person>();

			connections = linkedinClient.getConnectionsForCurrentUser(); 

			arraylistFriends=connections.getPersonList();


			setAdapter();
			
					
				
		}


		public void setAdapter() {
			linkedInFriendsAdapter=new LinkedInFriendsAdapter(arraylistFriends);
			listViewFriends.setAdapter(linkedInFriendsAdapter);
			listViewFriends.setOnItemClickListener(linkedInFriendsAdapter);
		}
		
		/**
		 * @author harshalb
		 *This class is adapter class used for friends.
		 */
		public class LinkedInFriendsAdapter extends BaseAdapter implements OnItemClickListener{
			List<Person> arrayList;
			public LinkedInFriendsAdapter(List<Person> arraylistFriends) {
				// TODO Auto-generated constructor stub
				arrayList=arraylistFriends;
				inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return arrayList.size();
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return arrayList.get(arg0);
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}

			/**
			 * @author harshalb
			 *This is holder class.
			 */
			class ViewHolder
			{
				ImageView friendImage;
				TextView friendText;
				Button friendSendMessage;
			}
			
			/**
			 * @author harshalb
			 *This class is used for sending message from dialog list.
			 */
			class SendMessageOnClick implements android.view.View.OnClickListener{
				
				int position;
				
				
				SendMessageOnClick(int position){
					this.position=position;
				}
				
				@Override
				public void onClick(View v) {
					System.out.println("send message click..."+position);
					Person p=(Person) v.getTag();
					

					  String idValue =p.getId();
						System.out.println("id array list" +Arrays.asList(idValue));

				//	linkedinClient.sendMessage(Collections.addAll(arrayList, person.getId()), "HARSHAL BENAKE DEMO", "my app demo HELLO"); 
			try
			{
						linkedinClient.sendMessage(Arrays.asList(idValue), "Myappsco-abo","This is Myappsco website:"+"www.myappsco.com"); 
						Toast.makeText(mContext, "Sent message successfully", Toast.LENGTH_LONG).show();
			}
				catch (Exception e) {
					// TODO: handle exception
					System.out.println("throttle::"+e);
					Toast.makeText(
							mContext,
							"Throttle limit for calls to this resource is reached",
							Toast.LENGTH_LONG).show();
				}
						
				}
				
			}
			
			@Override
			public View getView(int position, View view, ViewGroup viewGroup) {
				// TODO Auto-generated method stub
				 Person person = arrayList.get(position);
				//arrayList =  arrayList.get(position);

				ViewHolder viewHolder;

				if(view==null){
					view=inflater.inflate(R.layout.friends_list, null);
					viewHolder=new ViewHolder();
					viewHolder.friendImage=(ImageView)view.findViewById(R.id.friend_image);
					viewHolder.friendText=(TextView)view.findViewById(R.id.friend_text);
					viewHolder.friendSendMessage=(Button)view.findViewById(R.id.friend_sendmessage);
					viewHolder.friendSendMessage.setOnClickListener(new SendMessageOnClick(position));
					
				
					view.setTag(viewHolder);

				}
				else{
					viewHolder=(ViewHolder)view.getTag();
				}

				
				viewHolder.friendText.setText(person.getFirstName());
				imageLoader=new ImageLoader(context,person.getPictureUrl());
				imageLoader.displayImage(person.getPictureUrl(), viewHolder.friendImage, false);
				viewHolder.friendSendMessage.setTag(person);
				//	imageLoader.bind(viewHolder.friendImage, person.getPictureUrl(), callback);
				return view;
			}


			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				arrayList.get(arg2);
				Toast.makeText(context, "try", Toast.LENGTH_LONG).show();
			}

		}
	}

	/**
	 * @author harshalb
	 *This class is main webview dialog.
	 */
	class WebviewDialog extends Dialog {

		private Context mContext;
		private String mUrl;

		/**
		 * @param context
		 * @param url
		 */
		public WebviewDialog(Context context, String url) {
			super(context);
			this.mContext=context;
			this.mUrl=url;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);// must call before super.
			super.onCreate(savedInstanceState);

			setContentView(R.layout.linkedin_webview);
			setWebView(mUrl);
		}

		/**
		 * set webview.
		 * @param url 
		 */
		@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
		public void setWebView(String url) {
			mWebView = (WebView) findViewById(R.id.linkedin_webview);
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.getSettings().setAppCacheEnabled(true);
			mWebView.getSettings().setBuiltInZoomControls(true);
			mWebView.loadUrl(url);
			mWebView.setWebViewClient(new LinkedInWebViewClient());
		}

		/**
		 * @author harshalb
		 *This class is webview client.
		 */
		class LinkedInWebViewClient extends WebViewClient {

			ProgressDialog dialog;
			LinkedInWebViewClient(){
				dialog=new ProgressDialog(mContext);
				dialog.setMessage("Please wait...");
				dialog.setCancelable(false);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				dialog.show();
				super.onPageStarted(view, url, favicon);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if(dialog!=null && dialog.isShowing())
					dialog.dismiss();
				System.out.println("onPageFinished URL :"+url);
				authenticationFinish(Uri.parse(url));
				//			doOprations();
			}


			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				if(dialog!=null && dialog.isShowing())
					dialog.dismiss();

				System.out.println("onReceivedError errorCode :"+errorCode +" description : "+description);

				System.out.println("onReceivedError failingUrl :"+failingUrl);

				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler,
					SslError error) {
				if(dialog!=null && dialog.isShowing())
					dialog.dismiss();

				System.out.println("onReceivedSslError error :"+error.getPrimaryError());
				super.onReceivedSslError(view, handler, error);
			}



			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.contains(Config.OAUTH_CALLBACK_URL)) {
					Uri uri = Uri.parse(url);
					String verifier = uri.getQueryParameter("oauth_verifier");
					for (OnVerifyListener d : listeners) {
						// call listener method
						d.onVerify(verifier);
					}
					cancel();
				} else if (url
						.contains("www.linkedIn.com")) {
					cancel();
				} else {
					Log.i("LinkedinSample", "url: " + url);
					view.loadUrl(url);
				}

				return true;
			}
		}
	}
}
