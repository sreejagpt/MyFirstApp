package com.example.myfirstapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MyMapView extends Activity {

	  private GoogleMap map;

	  @SuppressLint("NewApi")
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mapview_v2);
	    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	        .getMap();

	    map.setMyLocationEnabled(true);

	    // Get LocationManager object from System Service LOCATION_SERVICE
	    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

	    // Create a criteria object to retrieve provider
	    Criteria criteria = new Criteria();

	    // Get the name of the best provider
	    String provider = locationManager.getBestProvider(criteria, true);

	    // Get Current Location
	    Location myLocation = locationManager.getLastKnownLocation(provider);
	    
	    // Get latitude of the current location
	    double latitude = myLocation.getLatitude();

	    // Get longitude of the current location
	    double longitude = myLocation.getLongitude();

	    // Create a LatLng object for the current location
	    LatLng latLng = new LatLng(latitude, longitude);      

	    // Show the current location in Google Map        
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));

	  } 

}