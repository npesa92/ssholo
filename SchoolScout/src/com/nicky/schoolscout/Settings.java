package com.nicky.schoolscout;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;



public class Settings extends Activity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener, OnAccessRevokedListener {
	
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    private static final String TAG = "SchoolScout";
    String scopes = "https://www.googleapis.com/auth/plus.login";
    TextView status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		status = (TextView) findViewById(R.id.sign_in_status);
		
		
		mPlusClient = new PlusClient.Builder(this, this, this)
		.setActions("http://schemas.google.com/AddActivity")
		
        .build();
		// Progress bar to be displayed if the connection failure is not resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");
		findViewById(R.id.sign_in_button).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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
	      // Save the result and resolve the connection failure upon a user click.
	      mConnectionResult = result;
	    }

	    @Override
	    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
	        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
	            mConnectionResult = null;
	            mPlusClient.connect();
	        }
	    }

	    @Override
	    public void onConnected(Bundle connectionHint) {
	        status = (TextView) findViewById(R.id.sign_in_status);
	    	String accountName = mPlusClient.getAccountName();
	    	status.setText(accountName);
	        Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG).show();
	    }

	    @Override
	    public void onDisconnected() {
	        Log.d(TAG, "disconnected");
	    }

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.sign_in_button:
				int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                if (available != ConnectionResult.SUCCESS) {
                    Toast.makeText(this, "GPlay Serving you up biotch", Toast.LENGTH_SHORT).show();
                	return;
                }

                try {
                    status.setText("Signing in...");
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    // Fetch a new result to start.
                    mPlusClient.connect();
                }
                break;
			case R.id.sign_out_button:
                if (mPlusClient.isConnected()) {
                    mPlusClient.clearDefaultAccount();
                    mPlusClient.disconnect();
                    mPlusClient.connect();
                }
                break;
			case R.id.revoke_access_button:
                if (mPlusClient.isConnected()) {
                    mPlusClient.revokeAccessAndDisconnect(this);
                    //updateButtons(false /* isSignedIn */);
                }
                break;
			}
			
		}

		@Override
		public void onAccessRevoked(ConnectionResult status) {
			// TODO Auto-generated method stub
			
		}
	    

}
