package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


public class MyMapView extends MapActivity {

	  private MapController mapController;
	  private MapView mapView;
	  private LocationManager locationManager;
	  private MyOverlays itemizedoverlay;
	  private MyLocationOverlay myLocationOverlay;

	  public void onCreate(Bundle bundle) {
	    super.onCreate(bundle);
	    setContentView(R.layout.map_layout); // bind the layout to the activity

	    // Configure the Map
	    mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    mapView.setSatellite(true);
	    mapController = mapView.getController();
	    mapController.setZoom(20); // Zoom 1 is world view
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
	        0, (LocationListener) new GeoUpdateHandler());

	    myLocationOverlay = new MyLocationOverlay(this, mapView);
	    mapView.getOverlays().add(myLocationOverlay);

	    myLocationOverlay.runOnFirstFix(new Runnable() {
	      public void run() {
	        mapView.getController().animateTo(myLocationOverlay.getMyLocation());
	      }
	    });

	   Drawable drawable = this.getResources().getDrawable(R.drawable.point);
	   itemizedoverlay = new MyOverlays(this, drawable);
	   //createMarker();
	    
	    //we have an array of lats and longs from our database, we use those
	    EntryDB_Helper edbh = new EntryDB_Helper(getApplicationContext());
	    ArrayList<MapPoint> coords = edbh.getLatLong();
	    edbh.close();
	    
	    
   
	    
	    System.out.println(coords.size() + " Records");
	    for (int i = 0; i < coords.size(); i++) {
	    	
	    		
	    		int a = (int)(coords.get(i).latitude * 1E6);
	    		int b = (int)(coords.get(i).longitude * 1E6);
	    		System.out.println(a + " APPMESG " +b);
	    		GeoPoint p = new GeoPoint(a, b);
	    		createMarker(p);
	    	
	    }
	    
	    
	  }

	  @Override
	  protected boolean isRouteDisplayed() {
	    return false;
	  }

	  public class GeoUpdateHandler implements LocationListener {

		
		    public void onLocationChanged(Location location) {
		      

		    }

			public void onProviderDisabled(String provider) {
			
				
			}

			public void onProviderEnabled(String provider) {
				
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				
				
			}

		 
		    
		  }

	  private void createMarker(GeoPoint p) {
	    
	    OverlayItem overlayitem = new OverlayItem(p, "", "");
	    itemizedoverlay.addOverlay(overlayitem);
	  
	    mapView.getOverlays().add(itemizedoverlay);
	    
	  }

	  @Override
	  protected void onResume() {
	    super.onResume();
	    myLocationOverlay.enableMyLocation();
	    myLocationOverlay.enableCompass();
	  }

	  @Override
	  protected void onPause() {
	    super.onPause();
	    myLocationOverlay.disableMyLocation();
	    myLocationOverlay.disableCompass();
	  }
	}
