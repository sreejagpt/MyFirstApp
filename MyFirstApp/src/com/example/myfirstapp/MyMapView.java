package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MyMapView extends Activity {

	  private GoogleMap map;
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
		    
		    
		 
		    
		    for (int i = 0; i < coords.size(); i++) {
		    	
		    	LatLng latLng = new LatLng(coords.get(i).latitude, coords.get(i).longitude);
		    	int strength = coords.get(i).getWiFiStrength();
		    	if (bounds.contains(latLng) && hm.get(latLng) == null) {
		    		hm.put(latLng, strength);
		    		int[] colours = getColourByStrength(strength);
		    		//plot the heatpoint
		    		//drawPoint(latLng, 0x1F00FF00, 0x0F00FF00);
		    		
		    		drawPoint(latLng, colours[0], colours[1]);
		    		
		    		
		    		
		    	}

		    }
		    
		  
		   
		   
		    
	  }


	private int[] getColourByStrength(int strength) {
		int[] colours = new int[2];
		strength = -1 * strength; //to make things easier
		//strength roughly ranges from -30 to -99
		int blue = 0x0000FF;

		int colourincr = 835571;
		int scaleincr = 4;
		int colour1= Math.round((strength - 20)/scaleincr)*colourincr + blue;
		
		colours[0] = 0x1F000000 | colour1;
		colours[1] = 0x0F000000 | colour1;
		return colours;
	}


	private void drawPoint(LatLng latLng, int dark, int light) {
		
		CircleOptions outer = new CircleOptions();
	    CircleOptions inner = new CircleOptions();
	    outer.fillColor(light);
	    outer.strokeColor(Color.TRANSPARENT);
	    outer.radius(1.7);
	    outer.visible(true);
	    
	    inner.fillColor(dark);
	    inner.radius(1);
	    inner.visible(true);
	    inner.strokeColor(Color.TRANSPARENT);
		System.out.println("Sample:: "+latLng.toString());
		outer.center(latLng);
		inner.center(latLng);
		Circle co = map.addCircle(outer);
		Circle ci = map.addCircle(inner);
		
	}
	  
	  
}