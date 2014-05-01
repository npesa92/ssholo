package com.nicky.schoolscout;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.common.api.ResultCallback;

public class SignintwoActivity extends Activity implements OnClickListener,
	com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks,
	com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener{
	
	// request code used to invoke sign in user interactions
	private static final int RC_SIGN_IN = 0;
	
	// client used to interact with googleapis
	private GoogleApiClient mGApiClient;
	
	// flag indicating pendingintent in progress and preventing others from starting
	private boolean mIntentProgress;
	
	private boolean mSignInClicked;
	
	private ConnectionResult mConnectionResult;
	
	private SignInButton butsignin;
	private Button creater, signer;
	
	String u="", p="", f="", l="", a="", s="";
	
	EditText ssus, sspw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signintwo);
		
		butsignin = (SignInButton) findViewById(R.id.sign_in_button2);
		creater = (Button) findViewById(R.id.createaccount2);
		signer = (Button) findViewById(R.id.schsignin2);
		ssus = (EditText) findViewById(R.id.userfield2);
		sspw = (EditText) findViewById(R.id.passfield2);
		butsignin.setOnClickListener(this);
		creater.setOnClickListener(this);
		signer.setOnClickListener(this);
		
		mGApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(Plus.API, null)
			.addScope(Plus.SCOPE_PLUS_LOGIN)
			.addScope(Plus.SCOPE_PLUS_PROFILE)
			.build();
		
		

	}
	
	protected void onStart() {
		super.onStart();
		mGApiClient.connect();
	}
	
	protected void onStop() {
		super.onStop();
		if (mGApiClient.isConnected())
			mGApiClient.disconnect();
		
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
			return;
		}
		
		if (!mIntentProgress) {
		    // Store the ConnectionResult so that we can use it later when the user clicks
		    // 'sign-in'.
		    mConnectionResult = result;

		    if (mSignInClicked) {
		      // The user has already clicked 'sign-in' so we attempt to resolve all
		      // errors until the user is signed in, or they cancel.
		      resolveSignInError();
		    }
		  }
	}
	
	@Override
	public void onConnected(Bundle arg0) {
		try {
		if (Plus.PeopleApi.getCurrentPerson(mGApiClient) != null) {
			Person curper = Plus.PeopleApi.getCurrentPerson(mGApiClient);
			String usrname = curper.getDisplayName();
			String gphotourl = curper.getImage().getUrl();
			//String gcoverurl = curper.getCover().getCoverPhoto().getUrl();
			String gmail = Plus.AccountApi.getAccountName(mGApiClient);
			
			SharedPreferences pref = getSharedPreferences("USR_INFO", 0);
			SharedPreferences.Editor editor = pref.edit();
			editor.putBoolean("isLoggedIn", true);
			editor.putString("usrname", usrname);
			editor.putString("gphotourl", gphotourl);
			//editor.putString("gcoverurl", gcoverurl);
			editor.putString("gmail", gmail);
			editor.commit();
			
			finish();
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}
			mIntentProgress = false;
			
			if (!mGApiClient.isConnecting()) {
				mGApiClient.connect();
			}
		}
	}
	
	@Override
	public void onConnectionSuspended(int cause) {
		mGApiClient.connect();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signintwo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.id.sign_in_button2:
            // Signin button clicked
            signInWithGplus();
            break;
        case R.id.createaccount2:
        	createUser crtv = new createUser();
        	getFragmentManager().beginTransaction().add(R.id.createcontent, crtv).commit();
        	break;
        case R.id.schsignin2:
    		new Schoolsignin(this).execute(ssus.getText().toString(), sspw.getText().toString());
        	break;
        }
	}

	
	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
	    if (!mGApiClient.isConnecting()) {
	        mSignInClicked = true;
	        resolveSignInError();
	    }
	}
	 
	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
	  if (mConnectionResult.hasResolution()) {
	    try {
	      mIntentProgress = true;
	      mConnectionResult.startResolutionForResult(this,  RC_SIGN_IN);
	    } catch (SendIntentException e) {
	      // The intent was canceled before it was sent.  Return to the default
	      // state and attempt to connect to get an updated ConnectionResult.
	      mIntentProgress = false;
	      mGApiClient.connect();
	    }
	  }
	}
	
	private class Schoolsignin extends AsyncTask<String, Void, String> {
		
		URL url;
		private String result = "";
		private String data = "";
		HttpURLConnection con;
		Context mctx;
		ProgressDialog dialog;
		
		public Schoolsignin(Context ctx) {
			mctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(mctx, "", 
		            "Signing in now...", true);
			dialog.show();
		}
		
		@Override
		protected String doInBackground(String... ids) {
			try {
				url = new URL("http://107.178.220.119/login.php");
				data = "id=" + URLEncoder.encode(ids[0], "UTF-8") + 
						"&pass=" + URLEncoder.encode(ids[1], "UTF-8");
				con = (HttpURLConnection) url.openConnection();
				
				try {
					con.setDoInput(true);
					con.setDoOutput(true);
					con.setUseCaches(false);
					con.setRequestMethod("POST");
					
					DataOutputStream dataOut = new DataOutputStream(con.getOutputStream());
			        dataOut.writeBytes(data);
			        dataOut.flush();
			        dataOut.close();
					
			        BufferedReader in = null;
					try {
						String line;
						in = new BufferedReader(new InputStreamReader(con.getInputStream()));
						while ((line = in.readLine()) != null) {
							result += line;
							
						}
					} finally {
						if (in != null) {
							in.close();
						}
					}
				} finally {
					con.disconnect();
			        System.out.println(result);
				}
			
			}
			catch (Exception e) {
				
			}
			finally {
				
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			if (result.matches("\\d+")) { 
				Toast.makeText(mctx, "Username or Password Incorrect", Toast.LENGTH_SHORT).show();
				System.out.println(result);
			} else {
				try{
					JSONArray arrobj = new JSONArray(result);
					for (int i=0; i<arrobj.length(); i++) {
						JSONObject obj = arrobj.getJSONObject(i);
						u = obj.get("id").toString();
						f = obj.get("fname").toString();
						l = obj.get("lname").toString();
						a = obj.get("age").toString();
						s = obj.get("school").toString();
						p = obj.get("pass").toString();
					}
				}catch (JSONException e) {
					e.printStackTrace();
				}
				System.out.println(u);
				SharedPreferences pref = getSharedPreferences("USR_INFO", 0);
    			SharedPreferences.Editor editor = pref.edit();
    			editor.putBoolean("isLoggedIn", true);
    			editor.putString("usrname", u);
    			editor.putString("fname", f);
    			editor.putString("lname", l);
    			editor.putString("age", a);
    			editor.putString("school", s);
    			editor.putString("pass", p);
    			editor.commit();
    			finish();
			}
		}
	}
	
	public static class createUser extends Fragment implements OnClickListener{
		
		EditText usrname;
		EditText pass;
		EditText fname;
		EditText lname;
		EditText age;
		EditText school;
		TextView usrcheck;
		Button create;
		boolean flag=false;
		int check =-1;
		
		
		private final TextWatcher usrwatch = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				new checkusrname().execute(usrname.getText().toString());
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View v = inflater.inflate(R.layout.hello_card, container,
					false);
			
			usrname = (EditText) v.findViewById(R.id.usrname);
			pass = (EditText) v.findViewById(R.id.pass);
			fname = (EditText) v.findViewById(R.id.fname);
			lname = (EditText) v.findViewById(R.id.lname);
			age = (EditText) v.findViewById(R.id.age);
			school = (EditText) v.findViewById(R.id.school);
			usrcheck = (TextView) v.findViewById(R.id.usrchecktv);
			create = (Button) v.findViewById(R.id.createaccntbut);
			create.setOnClickListener(this);
			usrname.addTextChangedListener(usrwatch);
			return v;
		}
		
		private class checkusrname extends AsyncTask<String, Void, String> {
			
			URL url;
			private String result = "";
			private String data = "";
			HttpURLConnection con;

			
			@Override
			protected String doInBackground(String... id) {
				try {
					url = new URL("http://107.178.220.119/checkusr.php");
					data = "id=" + URLEncoder.encode(id[0], "UTF-8");
					con = (HttpURLConnection) url.openConnection();
					
					try {
						con.setDoInput(true);
						con.setDoOutput(true);
						con.setUseCaches(false);
						con.setRequestMethod("POST");
						
						DataOutputStream dataOut = new DataOutputStream(con.getOutputStream());
				        dataOut.writeBytes(data);
				        dataOut.flush();
				        dataOut.close();
						
				        BufferedReader in = null;
						try {
							String line;
							in = new BufferedReader(new InputStreamReader(con.getInputStream()));
							while ((line = in.readLine()) != null) {
								result += line;
								
							}
						} finally {
							if (in != null) {
								in.close();
							}
						}
					} finally {
						con.disconnect();
				        System.out.println(result);
					}
				
				}
				catch (Exception e) {
					
				}
				finally {
					
				}
				
				return result;
			}
			
			@Override 
			protected void onPostExecute(String result) {
				check = Integer.parseInt(result);
				if (check < 1) {
					usrcheck.setText("username available");
					flag=true;
				} else {
					usrcheck.setText("username taken");
				}
			}
		}
		
		private class createssaccount extends AsyncTask<String, Void, String> {
			
			URL url;
			private String result = "";
			private String data = "";
			HttpURLConnection con;
			String id="", pass="", fname="", lname="", age="", school="";
			
			private createssaccount(String id, String pass, String fname, String lname, String age, String school) {
				this.id=id;
				this.pass=pass;
				this.fname=fname;
				this.lname=lname;
				this.age=age;
				this.school=school;
			}
			
			@Override
			protected String doInBackground(String... fields) {
				try {
					url = new URL("http://107.178.220.119/createuser.php");
					data = "id=" + URLEncoder.encode(id, "UTF-8") +
							"&pass=" + URLEncoder.encode(pass, "UTF-8") +
							"&fname=" + URLEncoder.encode(fname, "UTF-8") +
							"&lname=" + URLEncoder.encode(lname, "UTF-8") +
							"&age=" + URLEncoder.encode(age, "UTF-8") +
							"&school=" + URLEncoder.encode(school, "UTF-8");
					con = (HttpURLConnection) url.openConnection();
					
					try {
						con.setDoInput(true);
						con.setDoOutput(true);
						con.setUseCaches(false);
						con.setRequestMethod("POST");
						
						DataOutputStream dataOut = new DataOutputStream(con.getOutputStream());
				        dataOut.writeBytes(data);
				        dataOut.flush();
				        dataOut.close();
						
				        BufferedReader in = null;
						try {
							String line;
							in = new BufferedReader(new InputStreamReader(con.getInputStream()));
							while ((line = in.readLine()) != null) {
								result += line;
								
							}
						} finally {
							if (in != null) {
								in.close();
							}
						}
					} finally {
						con.disconnect();
				        System.out.println(result);
					}
				
				}
				catch (Exception e) {
					
				}
				finally {
					
				}
				
				return result;
			}
			
			@Override
			protected void onPostExecute(String result) {
				check = Integer.parseInt(result);
			}
		}
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.createaccntbut:
				if (flag=true) {
					String u = usrname.getText().toString(), p = pass.getText().toString(), f = fname.getText().toString(),
							l = lname.getText().toString(), a = age.getText().toString(), s=school.getText().toString(); 
					new createssaccount(u,p,f,l,a,s).execute();
					if (check == 0) {
						getFragmentManager().beginTransaction().remove(this).commit();
					}
				}
			}
		}
	}

}
