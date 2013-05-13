package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyMapView extends Activity {

	  private GoogleMap map;
	  private Bitmap heatlayer;
	  private HashMap<LatLng, Integer> hm = new HashMap<LatLng, Integer>();
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
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
	    
	    map.setOnCameraChangeListener(new OnCameraChangeListener() {
	    	
            
	    	public void onCameraChange(CameraPosition position) {
	    		LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
	    		
                plotHeatPoints(bounds);
            }
        });

	  } 

	 
	  private void plotHeatPoints(LatLngBounds bounds) {
		//we have an array of lats and longs from our database, we use those
		    EntryDB_Helper edbh = new EntryDB_Helper(getApplicationContext());
		    ArrayList<MapPoint> coords = edbh.getLatLong();
		    edbh.close();
		    
		    
		 
		    
		    for (int i = coords.size() - 1; i >= 0; i--) {
		    	
		    	LatLng latLng = new LatLng(coords.get(i).latitude, coords.get(i).longitude);
		    	int strength = coords.get(i).getWiFiStrength();
		    	if (bounds.contains(latLng) && hm.get(latLng) == null) {
		    		hm.put(latLng, strength);
		    		
		    		//plot the heatpoint
		    		
		    		//first, get resource 
		    		int resourceInfo = 
		    				choose_resource(coords.get(i).WifiStrength);
		    		Marker tempMarker = map.addMarker(new MarkerOptions()
                      .position(latLng) //add position here 
                      .icon(BitmapDescriptorFactory.fromResource(resourceInfo)));
		    		//need to set z level of this marker
		    		
		    		
		    		
		    	}

		    }
		    
		  
		   
		   
		    
	  }


	private int choose_resource(int wifiStrength) {
		//Function returns drawable resource and z_index at which
		//it must be drawn
		int w = wifiStrength * -1;
		
		if (w > 90) {
			return R.drawable.hp_deep_blue;
		}
		
		if (w <= 90 && w > 86) {
			return R.drawable.hp_blue;
		}
		
		if (w <= 86 && w > 82) {
			return R.drawable.hp_turquoise; 
		}
		
		if (w <= 82 && w > 78) {
			return R.drawable.hp_green;
		}
		
		if (w <= 78 && w > 73) {
			return R.drawable.hp_golden;
		}
		
		if (w <= 73 && w > 65) {
			return R.drawable.hp_orange;
		}
		
		if (w <= 65 && w > 61) {
			return R.drawable.hp_reddish_orange;
		}
		
		if (w <= 61) {
			return R.drawable.hp_red;
		}
		
		return R.drawable.hp_deep_blue; //default
	}


	
	  
	  
}