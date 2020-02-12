package com.matthew.reversegeocoding;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends FragmentActivity {
	//Use the new Google Maps V2 API we need to do 3 things
	//Use a SupportMapFragment instead of a MapFragment in your layout
	//Use a FragmentActivity instead of an activity
	//Use the SupportFragmentManager instead of the FragmentManager in your FragmentActivity
	//SupportMapFragment cannot run in an Activity but only run in a FragmentActivity
	GoogleMap googleMap;
	MarkerOptions markerOptions;
	LatLng latLng;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SupportMapFragment supportMapFragment = (SupportMapFragment) 
				getSupportFragmentManager().findFragmentById(R.id.map);

		// Getting a reference to the map
		googleMap = supportMapFragment.getMap();
		
		// Setting a click event handler for the map
		googleMap.setOnMapClickListener(new OnMapClickListener() {
		
			@Override
			public void onMapClick(LatLng arg0) {				
				
				// Getting the Latitude and Longitude of the touched location
				latLng = arg0;
				
				// Clears the previously touched position
				googleMap.clear();
				
				// Animating to the touched position                				
				googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));				
				
				// Creating a marker
				markerOptions = new MarkerOptions();
				
				// Setting the position for the marker
				markerOptions.position(latLng);						
				
				// Placing a marker on the touched position
				googleMap.addMarker(markerOptions);
				
				// Adding Marker on the touched location with address
		    	new ReverseGeocodingTask(getBaseContext()).execute(latLng);		    	
				
			}
		});
	}
	
	private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String>{
		Context mContext;
		
		public ReverseGeocodingTask(Context context){
			super();
			mContext = context;
		}

		// Finding address using reverse geocoding
		@Override
		protected String doInBackground(LatLng... params) {
			Geocoder geocoder = new Geocoder(mContext);
			double latitude = params[0].latitude;
			double longitude = params[0].longitude;
			
			List<Address> addresses = null;
			String addressText="";
			
			try {
				addresses = geocoder.getFromLocation(latitude, longitude,1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(addresses != null && addresses.size() > 0 ){
				Address address = addresses.get(0);
				
				addressText = String.format("%s, %s, %s",
	                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
	                    address.getLocality(),	                    
	                    address.getCountryName());	
				Log.d("Address", "Address name is " + addressText);
			}
			
			return addressText;
		}		
		
		@Override
		protected void onPostExecute(String addressText) {
			// Setting the title for the marker. 
			// This will be displayed on taping the marker
			markerOptions.title(addressText);
			
			// Placing a marker on the touched position
			googleMap.addMarker(markerOptions);
									
		}
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
