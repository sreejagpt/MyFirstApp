package com.example.myfirstapp;


import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;



public class MyFirstActivity extends Activity implements LocationListener {
	
	//public variables within the class
	WifiManager wifi;	//to find the available wifi networks in region
	ArrayList<String> resultList = new ArrayList<String>();	//to display results in list view
	private ListView mainListView ;
	private ArrayAdapter<String> listAdapter ;
	TelephonyManager        Tel;
	MyPhoneStateListener    MyListener;
	LocationManager locationManager;
	Location location;
	String provider = LocationManager.GPS_PROVIDER;
	float latitude, longitude;
	int cellStrength = 0;
	String CellId = "", LAC = "", Provider = "";
	List<ScanResult> results;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_first);
        
        //Finding the list view
        mainListView = (ListView) findViewById( R.id.list );
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, resultList); 
    	mainListView.setAdapter(listAdapter); 
        
        //on click listener for list view
        mainListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
        	 public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        		 
        		 Intent i = new Intent(v.getContext(), DisplayMessageActivity.class);
        		 //i.putStringArrayListExtra("WiFi Info", resultList);
        		 //i.putExtra("Position", position);
        		 i.putExtra("Latitude", latitude);
        		 i.putExtra("Longitude", longitude);
        		 getCellIDLAC();
        		 i.putExtra("CellID", CellId);
        		 i.putExtra("LAC", LAC);
        		 i.putExtra("SSID", results.get(position).SSID);
        		 i.putExtra("MAC", results.get(position).BSSID);
        		 i.putExtra("wStrength", results.get(position).level);
        		 i.putExtra("CellStrength", cellStrength);
        		 i.putExtra("Provider", Provider);
                 startActivity(i);
             }
        });
       
    	//setting up code so that individual items may be selected
    	
    	
        if (isWIFIEnabled() == false) {
        	enableWiFi();
        }
        if(isGPSEnabled() == false) {
        	enableGPS();
        }
    	
        //setting up Location Listener
        
    	// Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);
        
        onLocationChanged(location);
        MyListener   = new MyPhoneStateListener();
        Tel       = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);
        Tel.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        System.out.println("APPMESG: "+Tel.getNetworkOperatorName());
    	printWiFiStats();
   
    }
    
	  /* Request updates at startup */
	  @Override
	  protected void onResume() {
	    super.onResume();
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
	    Tel.listen(MyListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	  }
	  
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	    
	    Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
	  }
	  
	 
	  public void onLocationChanged(Location location) {
		if (location == null) {
			latitude = 0.0f;
			longitude = 0.0f;
			return;
			
		}
	    latitude = (float) location.getLatitude();
	    longitude = (float) location.getLongitude();
	  
	  }

  
    
    
    public void printWiFiStats() {
    	
    		
    		//Print WiFi info for the WiFi network we are on
            wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            
          
            // Scan all WiFi networks and print their statistics
            results = wifi.getScanResults();
            if(results == null) {
            	System.out.println("AppMesg: Not fetching results!");
            	
            }
            
            
           //Fill the List View with local WiFi Access Points
           
            String entry = "";
            
            for(int i = 0; i < results.size(); i++) {
            	//creating a string list with each WiFi network as a separate element
            	System.out.println("Appmesg: Iteration Number " + i);
            	entry = "SSID: " + results.get(i).SSID + " \nBSSID: " + 
            			results.get(i).BSSID + " \nSignal Strength (dBm): " + 
            			results.get(i).level;
            	
            	resultList.add(entry);
            	
            	entry = "";
            	
            
        	 }
        
    }
    

    
    private void enableGPS() {
    	
       Toast.makeText( this, "Please turn on GPS", Toast.LENGTH_SHORT ).show();
        SystemClock.sleep(1000);
       	Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
       	startActivity(myIntent);
        
    }
    
    private void enableWiFi() {

       Toast.makeText( this, "Please turn on WiFi", Toast.LENGTH_SHORT).show();
       	SystemClock.sleep(1000);
       	Intent myIntent = new Intent( Settings.ACTION_WIFI_SETTINGS );
       	startActivity(myIntent);
        
        return;
    }
    
    private boolean isGPSEnabled() {
    	LocationManager alm =
        		(LocationManager)this.getSystemService( Context.LOCATION_SERVICE );
            if( alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ) )
            {
            	return true;
            }
            else
            {
            	return false;
            }
    	
    }
    
    private boolean isWIFIEnabled() {
    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnectedOrConnecting();
    }
    

   
    
    /**
     * Function returns a formatted string with padded cellid <space> LAC, and
     * modifies the global variables CellID and LAC which are then read from*/
    public String getCellIDLAC() {
    	
    	TelephonyManager tm;
    	GsmCellLocation location;
    	int cid, lac, cellPadding;
    	try{
	    	tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	    	
	
	    	location = (GsmCellLocation) tm.getCellLocation();
	    	String operatorName = Tel.getNetworkOperatorName();

	        cid = location.getCid();
	        lac = location.getLac();
	        
	        CellId = cid + "";
	        LAC = lac + "";
	        Provider = operatorName + "";
	        /*
	         * Check if the current cell is a UMTS (3G) cell. If a 3G cell the cell id
	         * padding will be 8 numbers, if not 4 numbers.
	         */
	        if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS) {
	        	cellPadding = 8;
	        } else {
	        	cellPadding = 4;
	        	
	        }
	      //return with appropriate padding
	        return (getPaddedHex(cid, cellPadding) + " " + getPaddedHex(lac, 4));
    	}catch(Exception e) {
    		System.out.println("APPMESG: " + e.toString());
    	}
        
        return "";
    }
    

    
    /**
     * Convert an int to an hex String and pad with 0's up to minLen.
     */
    private String getPaddedHex(int nr, int minLen) {
	    String str = Integer.toHexString(nr);
	    if (str != null) {
	    	while (str.length() < minLen) {
	    		str = "0" + str;
	    	}
	    }
	    return str;
    }
    

    
    
    /* —————————– */
    /* Start the PhoneState listener */
   /* —————————– */
    @TargetApi(7)
	private class MyPhoneStateListener extends PhoneStateListener
    {
      /* Get the Signal strength from the provider, each time there is an update */
      @TargetApi(7)
	@Override
      public void onSignalStrengthsChanged(SignalStrength signalStrength)
      {
         super.onSignalStrengthsChanged(signalStrength);
         cellStrength = signalStrength.getGsmSignalStrength(); //update
         
      }

    };/* End of private Class */
	
	//auto generated methods:

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	
    	
    	
} //class closes
    
 

