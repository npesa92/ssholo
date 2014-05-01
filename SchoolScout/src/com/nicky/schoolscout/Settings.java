package com.nicky.schoolscout;

import java.io.File;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
import com.google.android.gms.plus.model.people.Person;



public class Settings extends Activity implements OnClickListener, com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks,
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
private Button signout;

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_settings);
	
	butsignin = (SignInButton) findViewById(R.id.sign_in_button);
	signout = (Button) findViewById(R.id.revoke_access_button);
	butsignin.setOnClickListener(this);
	signout.setOnClickListener(this);
	
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
	/*if (Plus.PeopleApi.getCurrentPerson(mGApiClient) != null) {
		Person curper = Plus.PeopleApi.getCurrentPerson(mGApiClient);
		String usrname = curper.getDisplayName();
		String gphotourl = curper.getImage().getUrl();
		String gcoverurl = curper.getCover().getCoverPhoto().getUrl();
		String gmail = Plus.AccountApi.getAccountName(mGApiClient);
		
		SharedPreferences pref = getSharedPreferences("USR_INFO", 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean("isLoggedIn", true);
		editor.putString("usrname", usrname);
		editor.putString("gphotourl", gphotourl);
		editor.putString("gcoverurl", gcoverurl);
		editor.putString("gmail", gmail);
		editor.commit();
		
		finish();
	}*/
	/*String filepath = getApplicationContext().getFilesDir().getPath()+"/shared_prefs/USR_INFO.xml";
	File deleteuserinfo = new File(filepath);
	deleteuserinfo.delete();*/
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
    case R.id.revoke_access_button:
    	revokeGplusAccess();
    	
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
private void revokeGplusAccess() {
    if (mGApiClient.isConnected()) {
        Plus.AccountApi.clearDefaultAccount(mGApiClient);
        Plus.AccountApi.revokeAccessAndDisconnect(mGApiClient)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status arg0) {
                    	String filepath = getApplicationContext().getFilesDir().getPath()+"/shared_prefs/USR_INFO.xml";
                    	File deleteuserinfo = new File(filepath);
                    	deleteuserinfo.delete();
                        mGApiClient.connect();
                        //updateUI(false);
                    }
 
                });
    }
}

}
