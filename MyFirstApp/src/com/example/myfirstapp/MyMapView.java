package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MyMapView extends Activity {

	  private GoogleMap map;
	  private HashMap<LatLng, Integer> hm = new HashMap<LatLng, Integer>();
	  private Spinner spinner_wifi_cell, spinner_access_points;
	  List<String> list_wifi_cell = Arrays.asList("WiFi", "3G/4G", "All");
	  List<Object> list_access_points = new ArrayList<Object>();
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
	   
	    
	    // Show the current location in Google Map        
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng
	    		(myLocation.getLatitude(), myLocation.getLongitude()), 16));
	    
	    addItemsToSpinners();
	    
	    
	    map.setOnCameraChangeListener(new OnCameraChangeListener() {
	    	
            
	    	public void onCameraChange(CameraPosition position) {
	    		
	    	    
	    		//rectify zoom level
	    		if(position.zoom > 18) {
	    			//we should not be zoomed in more than level 16
	    			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position.target, 18));
	    		}
	    		if (position.zoom < 16) {
	    			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position.target, 16));
	    		}
	    		LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
	    		
                
                EntryDB_Helper edbh = new EntryDB_Helper(getApplicationContext());
    		    ArrayList<MapPoint> coords = edbh.getLatLong();
    		    edbh.close();
                HashMap<String, String> accessPoints = new HashMap<String, String>();
    		    
    		    for (int i = coords.size() - 1; i >= 0; i--) {
    		    	
    		    	LatLng latLng = new LatLng(coords.get(i).latitude, coords.get(i).longitude);
    		    	
    		    	if (bounds.contains(latLng) && hm.get(latLng) == null) {
    		    		
    		    		accessPoints.put(coords.get(i).SSID, "-"); //to ensure uniqueness
    		    		list_access_points = Arrays.asList(accessPoints.keySet().toArray());
    				    spinner_access_points=(Spinner) findViewById(R.id.access_points_spinner);
    				    ArrayAdapter<Object> accessPointsAdapter = new ArrayAdapter<Object>(getApplicationContext(),
    							android.R.layout.simple_spinner_item, list_access_points);
    					  accessPointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    					  spinner_access_points.setAdapter(accessPointsAdapter);
    		    	}
    		    } //updating access points spinner ends here
    		    
    		    plotHeatPoints(bounds);
            }
        });

	  } 

	 
	  private void addItemsToSpinners() {
		  spinner_wifi_cell = (Spinner) findViewById(R.id.wifi_or_cell_spinner);
		  
		  
		  ArrayAdapter<String> wifiCellAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list_wifi_cell);
		  wifiCellAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		  spinner_wifi_cell.setAdapter(wifiCellAdapter);
		  
		  /////////
		  
		  
		
	}


	private void plotHeatPoints(LatLngBounds bounds) {
		//we have an array of lats and longs from our database, we use those
		    EntryDB_Helper edbh = new EntryDB_Helper(getApplicationContext());
		    ArrayList<MapPoint> coords = edbh.getLatLong();
		    edbh.close();
		    
		    
		    
		    
			  
		    for (int i = coords.size() - 1; i >= 0; i--) {
		    	int strength = coords.get(i).getWiFiStrength();
		    	LatLng latLng = new LatLng(coords.get(i).latitude, coords.get(i).longitude);
		    	
		    	if (bounds.contains(latLng) && hm.get(latLng) == null) {
		    		hm.put(latLng, strength); 
		    		
		    		//plot the heatpoint
		    		
		    		//first, get resource 
		    		List<Integer> resourceInfo = 
		    				choose_resource(coords.get(i).WifiStrength);
		    		
		    		
		    		
					
					try {
						
					   // Adds a ground overlay with 50% transparency.
					   GroundOverlay groundOverlay = map.addGroundOverlay(new GroundOverlayOptions()
			    		     .image(BitmapDescriptorFactory.fromResource(resourceInfo.get(0)))
			    		     .zIndex(resourceInfo.get(1))
			    		     .position(latLng, resourceInfo.get(2) * 20)//possibly scale this according to zoom level
			    		     .transparency((1 - 0.03f * (resourceInfo.get(1) % 10))));
					   
			    		 	
					} catch (Exception ex) {
					   Log.e("Error", ex.getMessage()); 
					}
					
					
		    		
		    		
		    	}

		    }
		    
		  
		   
		   
		    
	  }


	private List<Integer> choose_resource(int wifiStrength) {
		//Function returns drawable resource and z_index at which
		//it must be drawn
		int w = wifiStrength * -1;
		
		
		if (w > 90) {
			return Arrays.asList(R.drawable.hp_blue, 4, 18);
		}
		
		if (w <= 90 && w > 88) {
			return Arrays.asList(R.drawable.hp_turquoise, 4, 16); 
		}
		
		if (w <= 88 && w > 84) {
			return Arrays.asList(R.drawable.hp_green, 5, 14);
		}
		
		if (w <= 84 && w > 79) {
			return Arrays.asList(R.drawable.hp_lime_green, 5, 12);
		}
		
		if (w <= 79 && w > 74) {
			return Arrays.asList(R.drawable.hp_golden, 7, 9);
		}
		
		if (w <= 74 && w > 65) {
			return Arrays.asList(R.drawable.hp_orange, 7, 6);
		}
		
		if (w <= 65 && w > 60) {
			return Arrays.asList(R.drawable.hp_reddish_orange, 7, 4);
		}
		
		if (w <= 60) {
			return Arrays.asList(R.drawable.hp_red, 7, 2);
		}
		
		return Arrays.asList(R.drawable.hp_deep_blue, 10, 0); //default
	}



	  
	  
}