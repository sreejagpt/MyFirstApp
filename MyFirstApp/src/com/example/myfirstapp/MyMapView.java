package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyMapView extends Activity implements OnItemSelectedListener {

	  private GoogleMap map;
	  private Spinner spinner_wifi_cell, spinner_access_points;
	  private ArrayAdapter<Object> accessPointsAdapter;
	  List<String> list_wifi_cell = Arrays.asList("WiFi", "3G/4G");
	  List<Object> list_access_points = new ArrayList<Object>();
	  List<Object> prev_list_access_points = new ArrayList<Object>();
	  ArrayList<MapPoint> coords = new ArrayList<MapPoint>();
	  private String previousChoice = "";
	  private String currentChoice = "All";
	  private int showLabels = 0;
	  private ArrayList<Marker> markers = new ArrayList<Marker>();
	  private String currentWiFiCellChoice = "WiFi";
	  private String previousWiFiCellChoice = "";
	  private LatLngBounds old_bounds;
	  int startup = 1;
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
	    String provider = LocationManager.GPS_PROVIDER;
	    // Get the name of the best provider
	    provider = locationManager.getBestProvider(criteria, false);
	    
	    // Get Current Location
	    Location myLocation = locationManager.getLastKnownLocation(provider);


	    // Show the current location in Google Map        
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng
	    		(myLocation.getLatitude(), myLocation.getLongitude()), 16));

	    addItemsToSpinners();
	    old_bounds = map.getProjection().getVisibleRegion().latLngBounds;

	    //setup Button listener for show label button
        final Button labelBtn = (Button) findViewById(R.id.showLabels);
    	
    	//setup onclick listener for label button
    	//everytime it is pressed, showLabels gets set to 1. Button label 
        //changes to hide label. Toggle mechanism is set up.
    	labelBtn.setOnClickListener(new Button.OnClickListener() {
    		
    		 public void onClick(View v) {
    			 //start activity MyMapView
    			
    			 if (labelBtn.getText().equals("Show Labels")) {
    				 
    				 
    				 showLabels = 1;
    				 markers.clear();
    				 if (currentWiFiCellChoice.equals("WiFi")) {
	    				 for(int i = 0; i < coords.size(); i++){
	    					 LatLng latLng = new LatLng(coords.get(i).latitude, coords.get(i).longitude);
							 if (currentChoice.equals("All")) {
							//Post little floating notices
							   markers.add( map.addMarker(new MarkerOptions()
		                          .position(latLng)
		                          .title(coords.get(i).SSID)
		                          .snippet(coords.get(i).description)
		                          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ap_marker))));
							 } else {
								 if (coords.get(i).SSID.equals(currentChoice)) {
									 markers.add( map.addMarker(new MarkerOptions()
			                          .position(latLng)
			                          .title(coords.get(i).SSID)
			                          .snippet(coords.get(i).description)
			                          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ap_marker))));
								 }
							 }
						   }
    				 } else {
    					 //plot markers for cell coords
    					 CellDB_Helper cdbh = new CellDB_Helper(getApplicationContext());
    						ArrayList<CellPoint> coords1 = cdbh.getLatLong();
    						cdbh.close();
    						
    						for(int i = 0; i < coords1.size(); i++){
   	    					 LatLng latLng = new LatLng(coords1.get(i).latitude, coords1.get(i).longitude);
   							 if (currentChoice.equals("All")) {
   							//Post little floating notices
   							   markers.add( map.addMarker(new MarkerOptions()
   		                          .position(latLng)
   		                          .title(coords1.get(i).Provider)
   		                          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ap_marker))));
   							 } else {
   								 if (coords1.get(i).Provider.equals(currentChoice)) {
   									 markers.add( map.addMarker(new MarkerOptions()
   			                          .position(latLng)
   			                          .title(coords1.get(i).Provider)
   			                          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ap_marker))));
   								 }
   							 }
   						   }
    						
    				 }
    				 labelBtn.setText("Hide Labels");
    			 } else {
    				 showLabels = 0;
    				 for (int i = 0; i < markers.size(); i++) {
    					 markers.get(i).remove();
    				 }
    				 markers.clear();
    				 labelBtn.setText("Show Labels");
    			 }
    		 }
    	});//button listener closes


	    map.setOnCameraChangeListener(new OnCameraChangeListener() {

            
	    	public void onCameraChange(CameraPosition position) {

	    		int maxAllowedZoom = 18;
	    		if (showLabels == 1) {
	    			maxAllowedZoom = 24;
	    		}
	    		//rectify zoom level
	    		if(position.zoom > maxAllowedZoom) {
	    			//we should not be zoomed in more than level 24
	    			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position.target, maxAllowedZoom));
	    		}
	    		if (position.zoom < 16) {
	    			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position.target, 16));
	    		}
	    		LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

                
                EntryDB_Helper edbh = new EntryDB_Helper(getApplicationContext());
                
    		    coords = edbh.getLatLong();
    		    edbh.close();
                Map<String, String> accessPoints = new TreeMap<String, String>();
                accessPoints.clear();
                prev_list_access_points = list_access_points;
    		    
    		    for (int i = 0; i < coords.size(); i++) {
    		    	
    		    	LatLng latLng = new LatLng(coords.get(i).latitude, coords.get(i).longitude);
    		    	
    		    	if (bounds.contains(latLng)) {
    		    		
    		    		accessPoints.put(coords.get(i).SSID, "-"); //to ensure uniqueness
    					 
    		    	}
    		    	
    		    	accessPoints.put("All", "-");
    		    	
    		    	list_access_points = Arrays.asList(accessPoints.keySet().toArray());

    		    } //updating access points spinner ends here

    		    if (previousChoice.equals("")) {
    		    	set_spinner2_adapter();
    		    }
    		    
    		    //If map has moved but "All" is selected
    		    //we only want to do it if map has moved by a large amount
    		    LatLngBounds current_bounds = map.getProjection().getVisibleRegion().latLngBounds;
    		    
    		    //create "bounding box" window to decrease sensitivity of motion
    		    if (currentChoice.equals("All") && 
    		    		(!(prev_list_access_points.equals(list_access_points))) 
    		    		&& showLabels == 0
    		    		&& distance(bounds.northeast, old_bounds.northeast) > 0.0028) {
    		    	map.clear();
    		    	Toast.makeText( getApplicationContext(), "Recalculating...", Toast.LENGTH_SHORT ).show();
    		    	if (currentWiFiCellChoice.equals("WiFi")) {
    		    		plotHeatPoints(coords, map.getProjection().getVisibleRegion().latLngBounds);
    		    	}else {
    		    		handle_cell_plot();
    		    	}
    		    	old_bounds = current_bounds;
    		    	set_spinner2_adapter(); 
    		    	return;
    		    }
    		    
    		    if ((!(currentChoice.equals("All"))) && (!(list_access_points.contains(currentChoice)))) {
    		    	set_spinner2_adapter();
    		    }
    		    
    		    
            }

			private double distance(LatLng northeast, LatLng northeast2) {
				// Calculates Euclidean distance between two coordinates
				double x1 = northeast.latitude;
				double y1 = northeast.longitude;
				double x2 = northeast2.latitude;
				double y2 = northeast2.longitude;
				return Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1));
			}



        });

	  } 


	  private void addItemsToSpinners() {
		  spinner_wifi_cell = (Spinner) findViewById(R.id.wifi_or_cell_spinner);

		  ArrayAdapter<String> wifiCellAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list_wifi_cell);
		  wifiCellAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		  spinner_wifi_cell.setAdapter(wifiCellAdapter);
		  spinner_wifi_cell.setOnItemSelectedListener(this);

	}

	 private void set_spinner2_adapter() {

		 spinner_access_points=(Spinner) findViewById(R.id.access_points_spinner);
		 accessPointsAdapter = new ArrayAdapter<Object>(this, android.R.layout.simple_spinner_item, list_access_points); 
			  accessPointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			  spinner_access_points.setAdapter(accessPointsAdapter);

			//set listener for selecting items
			spinner_access_points.setOnItemSelectedListener(this);
	 }

	private void plotHeatPoints(List<MapPoint> coords1, LatLngBounds bounds) {


			 //extend bounds slightly
			bounds.including(new LatLng(bounds.northeast.latitude+7.0, bounds.northeast.longitude+7.0));
			bounds.including(new LatLng(bounds.southwest.latitude+7.0, bounds.southwest.longitude+7.0));

		    for (int i = coords1.size() - 1; i >= 0; i--) {

		    	LatLng latLng = new LatLng(coords1.get(i).latitude, coords1.get(i).longitude);

		    	if (bounds.contains(latLng)) {


		    		//plot the heatpoint

		    		//first, get resource 
		    		List<Integer> resourceInfo = 
		    				choose_resource(coords1.get(i).WifiStrength);




					try {

					   // Adds a ground overlay with transparency.
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

	
	
	

	private List<Integer> choose_resource(int strength) {
		//Function returns drawable resource and z_index at which
		//it must be drawn
		int w = strength * -1;


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


	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		if (startup == 0 && parent.getId()==R.id.wifi_or_cell_spinner) {
			//if first spinner has been accessed
			System.out.println("APPMESG You just changed wifi / cell");

			currentWiFiCellChoice = (String) list_wifi_cell.get((int) parent.getItemIdAtPosition(position));
			if (currentWiFiCellChoice.equals("3G/4G")) {
				//3g/4g chosen
				Object arr[] = {"All" , "Vodafone AU"};
				System.out.println("APPMESG: You chose "+currentWiFiCellChoice);
				list_access_points = Arrays.asList(arr);
				set_spinner2_adapter();
				handle_cell_plot();
				previousWiFiCellChoice = currentWiFiCellChoice;
				return;
			}else {
				System.out.println("WiFi chosen");
				EntryDB_Helper edbh = new EntryDB_Helper(getApplicationContext());
                
    		    coords = edbh.getLatLong();
    		    edbh.close();
                Map<String, String> accessPoints = new TreeMap<String, String>();
                accessPoints.clear();
                prev_list_access_points = list_access_points;
    		    
    		    for (int i = 0; i < coords.size(); i++) {
    		    	
    		    	LatLng latLng = new LatLng(coords.get(i).latitude, coords.get(i).longitude);
    		    	LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
    		    	if (bounds.contains(latLng)) {
    		    		accessPoints.put(coords.get(i).SSID, "-"); //to ensure uniqueness
    		    	}
    		    	
    		    	accessPoints.put("All", "-");
    		    	
    		    	list_access_points = Arrays.asList(accessPoints.keySet().toArray());
    		    	
    		    	if (previousWiFiCellChoice.equals("3G/4G") && currentWiFiCellChoice.equals("WiFi")) {
    		    		map.clear();
    		    		plotHeatPoints(coords, bounds);
    		    	}
    		    	previousWiFiCellChoice = currentWiFiCellChoice;
    		    	
    		    	

    		    } //updating access points spinner ends here
				set_spinner2_adapter();
				
			}
		}
		
		if (startup == 1) {
			startup = 0;
			return;
		}
		
		//however, if the item was not clicked , we don't want current choice to update
		try {
			currentChoice = (String) list_access_points.get((int) parent.getItemIdAtPosition(position));
		}catch (Exception e) {
			System.out.println("APPMESG: "+e.toString());
			return;
		}


		EntryDB_Helper edbh = new EntryDB_Helper(getApplicationContext());
		ArrayList<MapPoint> coords1 = edbh.getLatLong();
		edbh.close();

		//If "All" is selected, but it has recently changed, ie, the user has
		//explicitly clicked "All"
		if (currentChoice.equals("All")) {
			if (!(previousChoice.equals(currentChoice))) {
				map.clear();
				Toast.makeText( this, "Recalculating...", Toast.LENGTH_SHORT ).show();
				if (currentWiFiCellChoice.equals("WiFi")) {
					plotHeatPoints(coords1, map.getProjection().getVisibleRegion().latLngBounds);
		    	}else {
		    		handle_cell_plot();
		    	}
				
				previousChoice = currentChoice;
				return;
			}


			return; //this is important. Function must return at this point.
		}


		ArrayList<MapPoint> specialcoords = new ArrayList<MapPoint>();

		for(int i = 0; i < coords1.size(); i++) {

			if (coords1.get(i).SSID.equals(currentChoice)) {
				specialcoords.add(coords1.get(i));
			}
		}

		if (!(currentChoice.equals(previousChoice))) {
			map.clear();
			Toast.makeText( this, "Recalculating...", Toast.LENGTH_SHORT ).show();
			if (currentWiFiCellChoice.equals("WiFi")) {
				plotHeatPoints(specialcoords, map.getProjection().getVisibleRegion().latLngBounds);
	    	} else {
	    		handle_cell_plot();
	    	}
		}

		previousChoice = currentChoice;

	}


	private void handle_cell_plot() {
		//plots map is cell is selected
		map.clear();

		LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
		 //extend bounds slightly
		bounds.including(new LatLng(bounds.northeast.latitude+7.0, bounds.northeast.longitude+7.0));
		bounds.including(new LatLng(bounds.southwest.latitude+7.0, bounds.southwest.longitude+7.0));

		CellDB_Helper cdbh = new CellDB_Helper(getApplicationContext());
		ArrayList<CellPoint> coords1 = cdbh.getLatLong();
		cdbh.close();
		
		
	    for (int i = coords1.size() - 1; i >= 0; i--) {

	    	LatLng latLng = new LatLng(coords1.get(i).latitude, coords1.get(i).longitude);

	    	if (bounds.contains(latLng)) {


	    		//plot the heatpoint

	    		//first, get resource 
	    		List<Integer> resourceInfo = 
	    				choose_resource((2 * coords1.get(i).CellStrength) - 113);

				try {

				   // Adds a ground overlay with transparency.
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


	public void onNothingSelected(AdapterView<?> arg0) {


	}





}