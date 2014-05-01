package com.nicky.schoolscout;

import java.io.InputStream;

import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RiverFragment extends Fragment {
	
	//PlusClient mPlusClient;
	//ImageView iv;
	Context ctx;
	final int PROFILE_PIC_SIZE = 72;

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		 
		 	ctx = getActivity().getApplicationContext();
	 
	        // Retrieving the currently selected item number
	        int position = getArguments().getInt("position");
	        //String disname = getArguments().getString("disname");
	        
	        SharedPreferences prefs = this.getActivity().getSharedPreferences("USR_INFO", 0);
	        String name = prefs.getString("usrname", "");
	        //String[] s = name.split(" ");
	        //String prophoto = preferences.getString("gphotourl", "");
	        //prophoto = prophoto.substring(0,
            //        prophoto.length() - 2)
             //       + PROFILE_PIC_SIZE;
	        String fname = prefs.getString("fname", "");
	        String lname = prefs.getString("lname", "");
	        String age = prefs.getString("age", "");
	        String school = prefs.getString("school", "");
	 
	        // List of rivers
	        String[] title = getResources().getStringArray(R.array.drawer);
	 
	        // Creating view correspoding to the fragment
	        View v = inflater.inflate(R.layout.fragment_layout, container, false);
	 
	        // Getting reference to the TextView of the Fragment
	        TextView tv = (TextView) v.findViewById(R.id.tv_content);
	        TextView displayname = (TextView) v.findViewById(R.id.tv_about);
	        tv.setTypeface( Typeface.createFromAsset( ctx.getAssets(), "Roboto-Thin.ttf" ) );
	        displayname.setTypeface( Typeface.createFromAsset( ctx.getAssets(), "Roboto-Thin.ttf" ) );
	        
	        ImageView iv = (ImageView) v.findViewById(R.id.imageView1);
	        
	        //new LoadProfileImage(iv).execute(prophoto);
	 
	        // Setting currently selected river name in the TextView
	        tv.setText(fname + " " + lname + "\n" +
	        			age + "\n" + school);
	        displayname.setText(name);
	 
	        // Updating the action bar title
	        getActivity().getActionBar().setTitle(title[position]);
	 
	        return v;
	    }
	 /**
	  * Background Async task to load user profile picture from url
	  * */
	 private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
	     ImageView bmImage;
	  
	     public LoadProfileImage(ImageView bmImage) {
	         this.bmImage = bmImage;
	     }
	  
	     protected Bitmap doInBackground(String... urls) {
	         String urldisplay = urls[0];
	         Bitmap mIcon11 = null;
	         try {
	             InputStream in = new java.net.URL(urldisplay).openStream();
	             mIcon11 = BitmapFactory.decodeStream(in);
	         } catch (Exception e) {
	             Log.e("Error", e.getMessage());
	             e.printStackTrace();
	         }
	         return mIcon11;
	     }
	  
	     protected void onPostExecute(Bitmap result) {
	         bmImage.setImageBitmap(result);
	     }
	 }

}
