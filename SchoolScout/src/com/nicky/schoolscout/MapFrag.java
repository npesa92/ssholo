package com.nicky.schoolscout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MapFrag extends FragmentActivity implements LocationListener, OnMapClickListener, OnMapLongClickListener{
	
	GoogleMap googleMap;
	int longs[] = {34069636, 34412900, 32879119, 32774899, 37776490, 39729221};
	int lats[] = {-118443968, -119843610, -117235880, -117070570, -122450352, -121847048};
	String schools[] = {"Cal Poly San Luis Obispo", "California State University Long Beach",
			"California State University Los Angeles", "California State University Monterey Bay", "California State University Chico",
			"California State University Fresno", "California State University Humboldt", "University of Oregon",
			"Sacramento State University", "San Diego State University",
			"San Francisco State University","San Jose State University", "Sonoma State University",
			"UC Berkeley", "UC Davis", "UC Irvine",
			"UC Los Angeles", "UC Merced", "UC Riverside", "UC San Diego",
			"UC San Francisco", "UC Santa Barbara", "UC Santa Cruz"};
	LatLng latlng;
	MarkerOptions markerOptions;
	ArrayList<LatLng> markerPoints;
	LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_frag);
		
		markerPoints = new ArrayList<LatLng>();
		
		Button btnDraw = (Button)findViewById(R.id.btn_draw);
		
		// Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        int position = getIntent().getIntExtra("position", -1);
        
     // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
 
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }
        else { // Google Play Services are available
        	 
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
 
            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();
 
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);
 
            // Getting LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
 
            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);
 
            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);
 
            if(location!=null){
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
            
            //LatLng schlatlng = new LatLng(lats[position], longs[position]);
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(schlatlng));
            //googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            
            new GeocoderTask().execute(schools[position-1]);
        }
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
		 
		getActionBar().setDisplayShowHomeEnabled(true);
        
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
    
        
     
    }
	
	@Override
	public void onStop() {
		super.onStop();
		locationManager.removeUpdates(this);
	}
	
	private String getDirectionsUrl(LatLng origin,LatLng dest){
		 
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
 
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Waypoints
        String waypoints = "";
        for(int i=2;i<markerPoints.size();i++){
            LatLng point  = (LatLng) markerPoints.get(i);
            if(i==2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }
 
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
 
        return url;
    }
	
	 /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb  = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{
 
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
 
            // For storing data from web service
 
            String data = "";
 
            try{
                // Fetching the data from web service
                 data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
 
            ParserTask parserTask = new ParserTask();
 
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
 
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
 
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
 
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
 
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
 
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
 
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
 
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
 
                    points.add(position);
                }
 
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }
 
             // Drawing polyline in the Google Map for the i-th route
             googleMap.addPolyline(lineOptions);
         }
    }
	
	 @Override
	    public void onLocationChanged(Location location) {
	 
	        //TextView tvLocation = (TextView) findViewById(R.id.tv_location);
	 
	        // Getting latitude of the current location
	        double latitude = location.getLatitude();
	 
	        // Getting longitude of the current location
	        double longitude = location.getLongitude();
	 
	        // Creating a LatLng object for the current location
	        LatLng latLng = new LatLng(latitude, longitude);
	 
	        // Showing the current location in Google Map
	        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
	 
	        // Zoom in the Google Map
	        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	 
	        // Setting latitude and longitude in the TextView tv_location
	        //tvLocation.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );
	 
	    }
	 
	 @Override
	    public void onProviderDisabled(String provider) {
	        Toast.makeText(getBaseContext(), "GPS has been disabled", Toast.LENGTH_LONG).show();
	    }
	 
	    @Override
	    public void onProviderEnabled(String provider) {
	    	Toast.makeText(getBaseContext(), "GPS has been enabled", Toast.LENGTH_LONG).show();
	    }
	 
	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras) {
	        // TODO Auto-generated method stub
	    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
	
	 // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{
 
        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;
 
            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }
 
        @Override
        protected void onPostExecute(List<Address> addresses) {
 
            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }
 
            // Clears all the existing markers on the map
            googleMap.clear();
 
            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){
 
                Address address = (Address) addresses.get(i);
 
                // Creating an instance of GeoPoint, to display in Google Map
                latlng = new LatLng(address.getLatitude(), address.getLongitude());
 
                String addressText = String.format("%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                address.getCountryName());
 
                markerOptions = new MarkerOptions();
                markerOptions.position(latlng);
                markerOptions.title(addressText);
 
                googleMap.addMarker(markerOptions);
 
                // Locate the first location
                if(i==0) {
                    
                   googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                   //googleMap.animateCamera(CameraUpdateFactory.zoomTo(15)); 
                }
                
            }
        }
    }

	@Override
	public void onMapLongClick(LatLng arg0) {
		 // Removes all the points from Google Map
        googleMap.clear();

        // Removes all the points in the ArrayList
        markerPoints.clear();
		
	}

	@Override
	public void onMapClick(LatLng point) {
		 // Already 10 locations with 8 waypoints and 1 start location and 1 end location.
        // Upto 8 waypoints are allowed in a query for non-business users
        if(markerPoints.size()>=10){
            return;
        }

        // Adding new item to the ArrayList
        markerPoints.add(point);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        /**
        * For the start location, the color of marker is GREEN and
        * for the end location, the color of marker is RED and
        * for the rest of markers, the color is AZURE
        */
        if(markerPoints.size()==1){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }else if(markerPoints.size()==2){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }else{
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }

        // Add new marker to the Google Map Android API V2
        googleMap.addMarker(options);
		
	}
	
	public void buttonHandler(View v) {
		// Checks, whether start and end locations are captured
        if(markerPoints.size() >= 2){
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
	}

}
