package com.nicky.schoolscout;

import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RiverFragment extends Fragment {
	
	//PlusClient mPlusClient;
	ImageView iv;

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	 
	        // Retrieving the currently selected item number
	        int position = getArguments().getInt("position");
	        String disname = getArguments().getString("disname");
	        
	        SharedPreferences preferences = this.getActivity().getSharedPreferences("USR_PREFS.xml", 0);
	        String name = preferences.getString("disname", "");
	        String usr_about = preferences.getString("usrabout", "");
	        
	        //Person currentPerson = mPlusClient.getCurrentPerson();
	 
	        // List of rivers
	        String[] title = getResources().getStringArray(R.array.drawer);
	 
	        // Creating view correspoding to the fragment
	        View v = inflater.inflate(R.layout.fragment_layout, container, false);
	 
	        // Getting reference to the TextView of the Fragment
	        TextView tv = (TextView) v.findViewById(R.id.tv_content);
	        TextView about = (TextView) v.findViewById(R.id.tv_about);
	        
	        //iv = (ImageView) iv.findViewById(R.id.imageView1);
	 
	        // Setting currently selected river name in the TextView
	        tv.setText(name);
	        about.setText(usr_about);
	        
	        //iv.setImageAlpha(currentPerson.getImage());
	 
	        // Updating the action bar title
	        getActivity().getActionBar().setTitle(title[position]);
	 
	        return v;
	    }

}
