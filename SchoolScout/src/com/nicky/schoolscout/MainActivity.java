package com.nicky.schoolscout;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.color;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	SharedPreferences pref;
    SharedPreferences.Editor editor;
    boolean checkLogIn;
    //private boolean flag = false;
	
    int mPosition = 0;
	String mTitle = "";
	 
	 // Array of strings storing country names
	 String[] listnames;
	 
	 int[] listlogos = {R.drawable.ss_home, R.drawable.ss_schools, R.drawable.ss_about};
	 
	 private DrawerLayout mDrawerLayout;
	 private ListView mDrawerList;
	 private ActionBarDrawerToggle mDrawerToggle;
	 private LinearLayout mDrawer ;
	 private List<HashMap<String,String>> mList ;
	 private SimpleAdapter mAdapter;
	 private TextView usr_spc;
	 final private String LISTNAME = "listname";
	 final private String LOGO = "logo";
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkLogIn();
		setContentView(R.layout.activity_main);
		
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.RED));
		
		pref = getSharedPreferences("USR_INFO", 0);
		editor = pref.edit();
		
		//checkLogIn = pref.getBoolean("isLoggedIn", false);
		
		// Getting an array of country names
		 listnames = getResources().getStringArray(R.array.drawer);
		 
		 // Title of the activity
		 mTitle = (String)getTitle();
		 
		 usr_spc = (TextView) findViewById(R.id.user_space);
		 //usr_spc.setText(pref.getString("disname", ""));
		 
		 // Getting a reference to the drawer listview
		 mDrawerList = (ListView) findViewById(R.id.drawer_list);
		 
		 // Getting a reference to the sidebar drawer ( Title + ListView )
		 mDrawer = ( LinearLayout) findViewById(R.id.drawer);
		 
		 mList = new ArrayList<HashMap<String,String>>();
		 for(int i=0;i<3;i++){
			 HashMap<String, String> hm = new HashMap<String,String>();
			 hm.put(LISTNAME, listnames[i]);
			 hm.put(LOGO, Integer.toString(listlogos[i]) );
			 mList.add(hm);
		 }
		 
		// Keys used in Hashmap
		 String[] from = { LOGO,LISTNAME };
		 
		// Ids of views in listview_layout
		 int[] to = { R.id.logo , R.id.listname};
		 
		 mAdapter = new SimpleAdapter(this, mList, R.layout.drawer_layout, from, to);
		 
		 mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		 
		 // Creating a ToggleButton for NavigationDrawer with drawer event listener
		 mDrawerToggle = new ActionBarDrawerToggle(this,
				 		mDrawerLayout, R.drawable.ic_drawer,
				 		R.string.drawer_open,
				 		R.string.drawer_close){
		 
		 /** Called when drawer is closed */
		 public void onDrawerClosed(View view) {
			 
			 highlightSelectedCountry();
			 invalidateOptionsMenu();
		 }
		 
		/** Called when a drawer is opened */
		 public void onDrawerOpened(View drawerView) {
			 getActionBar().setTitle(R.string.app_name);
			 invalidateOptionsMenu();
		 }
		 };
		 
		// Setting event listener for the drawer
		 mDrawerLayout.setDrawerListener(mDrawerToggle);
		 
		 // ItemClick event handler for the drawer items
		 mDrawerList.setOnItemClickListener(new OnItemClickListener() {
		 
		@Override
		 public void onItemClick(AdapterView<?> arg0, View arg1, int position,
		 long arg3) {
		 
		 // Increment hit count of the drawer list item
		 //incrementHitCount(position);
		 
			switch(position) { // Show fragment for countries : 0 to 4
			case 0:
			 	showFragment(position);
			 	//flag=true;
			 	break;
			case 1:
		 		SchoolFragList schools = new SchoolFragList();
		 		mTitle = getResources().getStringArray(R.array.drawer)[position];
		 		Bundle data = new Bundle();
		 		data.putInt("position", position);
		 		schools.setArguments(data);
		 		FragmentManager fmanager = getFragmentManager();
				FragmentTransaction ftransaction = fmanager.beginTransaction();
				
				ftransaction.replace(R.id.content_frame, schools);
				ftransaction.commit();
				//mDrawerLayout.closeDrawer(mDrawerList);
				//flag=true;
				break;
			case 2:
			 	showFragment(position);
			 	//flag=true;
			 	break;
			default:
				showFragment(0);
			 	break;
			}

		 // Closing the drawer
			mDrawerLayout.closeDrawer(mDrawer);
		 }
		});
		 
		 // Enabling Up navigation
		 getActionBar().setDisplayHomeAsUpEnabled(true);
		 
		 getActionBar().setDisplayShowHomeEnabled(true);
		 
		// Setting the adapter to the listView
		 mDrawerList.setAdapter(mAdapter);
		 
		 showFragment(0);
	}

	@Override
	 protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	 
	 }
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 if (mDrawerToggle.onOptionsItemSelected(item)) {
			 return true;
		 }
		 switch(item.getItemId()) {
		 case R.id.action_settings:
			 Intent intent = new Intent();
			 intent.setClass(getBaseContext(), Settings.class);
			 startActivity(intent);
			 break;
		 }
		 return super.onOptionsItemSelected(item);
	 }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	 // Inflate the menu; this adds items to the action bar if it is present.
		 getMenuInflater().inflate(R.menu.main, menu);
		 return true;
	 }
	 
	 /*public void incrementHitCount(int position){
	 HashMap<String, String> item = mList.get(position);
	 String count = item.get(COUNT);
	 item.remove(COUNT);
	 if(count.equals("")){
	 count = " 1 ";
	 }else{
	 int cnt = Integer.parseInt(count.trim());
	 cnt ++;
	 count = " " + cnt + " ";
	 }
	 item.put(COUNT, count);
	 mAdapter.notifyDataSetChanged();
	 }*/
	 
	 public void showFragment(int position){
	 
		 	String[] title = getResources().getStringArray(R.array.drawer);
	    	mTitle = title[position];
	    	//SchoolFragList schools = new SchoolFragList();
	    	RiverFragment rfrag = new RiverFragment();
	    	// Creating a Bundle object
	        Bundle data = new Bundle();
	        Bundle extras = getIntent().getExtras();

	        // Setting the index of the currently selected item of mDrawerList
	        data.putInt("position", position);

	        // Setting the position to the fragment
	        rfrag.setArguments(data);
	        
	    	FragmentManager fmanager = getFragmentManager();
	    	FragmentTransaction ftransaction = fmanager.beginTransaction();
	    	ftransaction.replace(R.id.content_frame, rfrag);
	    	ftransaction.commit();
	    	//mDrawerLayout.closeDrawer(mDrawerList);
	 }
	 
	 // Highlight the selected country : 0 to 4
	 public void highlightSelectedCountry(){
		 int selectedItem = mDrawerList.getCheckedItemPosition();
	 	 mPosition = selectedItem;
	 	 if (mPosition != -1) {
	 		 mDrawerList.setItemChecked(mPosition, true);
	 		 getActionBar().setTitle(listnames[mPosition]);
	 	 }
	 }
	 
	 public void checkLogIn() {
		 pref = getSharedPreferences("USR_INFO", 0);
		 editor = pref.edit();
		 if (pref.contains("isLoggedIn")) {
			 
		 } else {
				Intent login = new Intent();
				login.setClass(MainActivity.this, SignintwoActivity.class);
				startActivity(login);
				
			}
	 }

}
