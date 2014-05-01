package com.nicky.schoolscout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*import com.tipsandtricks.HelloCard.R;
import com.tipsandtricks.HelloCard.adapters.BaseInflaterAdapter;
import com.tipsandtricks.HelloCard.adapters.CardItemData;
import com.tipsandtricks.HelloCard.adapters.inflaters.CardInflater;*/




import android.os.Bundle;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class SchoolFragList extends ListFragment {
	
	String schools[] = {"Cal Poly San Luis Obispo", "CSU Long Beach", "CSU Los Angeles", "CSU Monterey Bay", "Chico State",
			"Fresno State", "Humboldt State", "University of Oregon", "Sacramento State", "San Diego State", "San Francisco State",
			"San Jose State", "Sonoma State", "UC Berkeley", "UC Davis", "UC Irvine", "UC Los Angeles", "UC Merced", 
			"UC Riverside", "UC San Diego", "UC San Francisco", "UC Santa Barbara", "UC Santa Cruz"};
	int logos[] = new int[]{R.drawable.calpolyslo, R.drawable.csulb, R.drawable.csula, R.drawable.ic_launcher, R.drawable.chicostate,
			R.drawable.fresnostate, R.drawable.humboldtstate, R.drawable.ic_launcher, R.drawable.sacstate, R.drawable.sdsu, 
			R.drawable.sfsu, R.drawable.sjsu, R.drawable.sonoma, R.drawable.ucberkeley, R.drawable.ucdavis, R.drawable.ucirvine, R.drawable.ucla, 
			R.drawable.ic_launcher, R.drawable.ucriverside, R.drawable.ucsd, R.drawable.ic_launcher, R.drawable.ucsb, R.drawable.ucsc};
	int longs[] = {34069636, 34412900, 32879119, 32774899, 37776490, 39729221};
	int lats[] = {-118443968, -119843610, -117235880, -117070570, -122450352, -121847048};
	String[] currency = new String[]{
	        "Los Angeles", "Long Beach", "Los Angeles", "Monterey Bay", "Chico", "Fresno", "Humboldt", "Eugene",
	        "Sacramento", "San Diego", "San Francisco", "San Jose", "Sonoma", "Berkeley", "Davis", "Irvine",
	        "Los Angeles", "Merced", "Riverside", "San Diego", "SanFrancisco", "Santa Barbara", "Santa Cruz"
	    };
	Context ctx;

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	 
	       // Each row in the list stores country name, currency and flag
	        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
	        
	        ctx = getActivity().getApplicationContext();
	        
	        View v = inflater.inflate(R.layout.listview, container, false);
	        
	        ListView lv = (ListView) v.findViewById(android.R.id.list);
	        lv.addHeaderView(new View(ctx));
		 	lv.addFooterView(new View(ctx));
	 
	        for(int i=0;i<23;i++){
	            HashMap<String, String> hm = new HashMap<String,String>();
	            hm.put("txt", schools[i]);
	            hm.put("cur","City : " + currency[i]);
	            hm.put("flag", Integer.toString(logos[i]) );
	            aList.add(hm);
	        }
	 
	        // Keys used in Hashmap
	        String[] from = { "flag","txt","cur" };
	 
	        // Ids of views in listview_layout
	        int[] to = { R.id.logo,R.id.txt,R.id.cur};
	 
	        // Instantiating an adapter to store each items
	        // R.layout.listview_layout defines the layout of each item
	        CardAdapter adapter = new CardAdapter(getActivity().getBaseContext(),
	        		aList, R.layout.school_frag_list, from, to);
	 
	        lv.setAdapter(adapter); 
		 	
		 	/*ctx = getActivity().getApplicationContext();
		 
		 	View v = inflater.inflate(R.layout.listview, container, false);
		 	ListView list = (ListView) v.findViewById(android.R.id.list);
		 	list.addHeaderView(new View(ctx));
		 	list.addFooterView(new View(ctx));
		 	

			BaseInflaterAdapter<CardItemData> adapter = new BaseInflaterAdapter<CardItemData>(new CardInflater());
			for (int i = 0; i < schools.length; i++)
			{
				CardItemData data = new CardItemData(schools[i],
						currency[i],
						"Item " + i + " Line 3", 
						logos[i]);
				adapter.addItem(data, false);
			}

			list.setAdapter(adapter);*/
			
			
	 
	        return v;
	    }
	 
	 @Override
	 public void onListItemClick(ListView l, View v, int position, long id) {
		 ctx = getActivity().getApplicationContext();
		 String x=" " + l.getPositionForView(v);
		 Toast.makeText(ctx, x, Toast.LENGTH_SHORT).show();
		 Intent intent = new Intent();
		 intent.putExtra("position", l.getPositionForView(v));
		 intent.setClass(getActivity(), MapFrag.class);
		 startActivity(intent);
	     super.onListItemClick(l, v, position, id);
	     
	 }

}
