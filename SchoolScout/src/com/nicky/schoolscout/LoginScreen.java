package com.nicky.schoolscout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Image;


public class LoginScreen extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, OnClickListener {
	
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private static final String TAG = "LoginActivity";

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
		 mPlusClient = new PlusClient.Builder(this, this, this)
         	.setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
         	.setScopes(Scopes.PLUS_LOGIN)  // recommended login scope for social features
         	// .setScopes("profile")       // alternative basic login scope
         	.build();
		 // Progress bar to be displayed if the connection failure is not resolved.
		 mConnectionProgressDialog = new ProgressDialog(this);
		 mConnectionProgressDialog.setMessage("Signing in...");
		 findViewById(R.id.sign_in_button).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_screen, menu);
		return true;
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
	       if (mConnectionProgressDialog.isShowing()) {
	               // The user clicked the sign-in button already. Start to resolve
	               // connection errors. Wait until onConnected() to dismiss the
	               // connection dialog.
	               if (result.hasResolution()) {
	                       try {
	                               result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
	                       } catch (SendIntentException e) {
	                               mPlusClient.connect();
	                       }
	               }
	       }

	       // Save the intent so that we can start an activity when the user clicks
	       // the sign-in button.
	       mConnectionResult = result;
	}

	@Override
	public void onConnected(Bundle connectionHint) {
	 // We've resolved any connection errors.
	  mConnectionProgressDialog.dismiss();
	  //String usrname = mPlusClient.getAccountName();
	  Person curper = mPlusClient.getCurrentPerson();
	  String usrname = curper.getDisplayName();
	  String gphotourl = curper.getImage().getUrl();
	  String gcoverurl = curper.getCover().getCoverPhoto().getUrl();
	  String usr_about;
	  if (curper.hasAboutMe() == false) {
		  usr_about = curper.getAboutMe();
	  }
	  else{
		  usr_about = "About me from G+ profile empty.";
	  }
	  //Image coverphoto = curper.getImage();
	  Toast.makeText(this, usrname + " is logged in.", Toast.LENGTH_LONG).show();
	  Intent startApp = new Intent();
	  //startApp.putExtra("usrname", usrname);
	  //startApp.putExtra("disname", disname);
	  //startApp.setClass(LoginScreen.this, MainActivity.class);
	  //startActivity(startApp);
	  
	  SharedPreferences preferences = getSharedPreferences("USR_INFO", 0);
	  SharedPreferences.Editor editor = preferences.edit();
	  editor.putBoolean("isLoggedIn", true);
	  editor.putString("usrname", usrname);
	  editor.putString("usrabout", usr_about);
	  editor.putString("gphotourl", gphotourl);
	  editor.commit();
	  
	  finish();
	  
	}
	
	@Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }
	
	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
	    if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
	        mConnectionResult = null;
	        mPlusClient.connect();
	    }
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.sign_in_button && !mPlusClient.isConnected()) {
	        if (mConnectionResult == null) {
	            mConnectionProgressDialog.show();
	        } else {
	            try {
	                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
	            } catch (SendIntentException e) {
	                // Try connecting again.
	                mConnectionResult = null;
	                mPlusClient.connect();
	            }
	        }
	    } else if (v.getId() == R.id.schsignin) {
	    	
	    } else if (v.getId() == R.id.createaccount) {
	    	
	    }
		
	}
	
	private class Schoolsignin extends AsyncTask<String, Void, String> {
		
		EditText userfield;
		EditText passfield;
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected String doInBackground(String... ids) {
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			
		}
	}
	
	public static class createUser extends Fragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.hello_card, container,
					false);
			return rootView;
		}
	}

}
