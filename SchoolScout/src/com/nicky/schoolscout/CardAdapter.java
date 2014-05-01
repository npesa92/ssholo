package com.nicky.schoolscout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SimpleAdapter;
import android.os.Build;

public class CardAdapter extends SimpleAdapter {
	
	private List<HashMap<String, String>> schools;
	private int lastPos = -1;
	
	
	public CardAdapter(Context ctx, List<HashMap<String, String>> schools,
			int resource, String from[], int to[]) {
		super(ctx, schools, resource, from, to);
		this.schools = schools;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		
		Animation animation = AnimationUtils.loadAnimation(v.getContext(), (position > lastPos) ? R.animator.up_from_bottom : R.animator.down_from_top);
	    v.startAnimation(animation);
	    lastPos = position;
	    
	    return v;
	}

	/*@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.card_adapter, menu);
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
	}*/

}
