package com.example.myfirstapp;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
public class DisplayMessageActivity extends Activity {
	
	//public instance variables
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	String SSID, MAC, CellID, LAC, timestamp;
	float latitude, longitude;
	int wStrength, cStrength;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        //set layout as defined in ListItemClick.xml
        setContentView(R.layout.onlistclick);
        Intent i = getIntent();
        
        //extracting values from Intent
        //ArrayList<String> info = i.getStringArrayListExtra("WiFi Info");
        //int position = i.getIntExtra("Position", 0);
        latitude = i.getFloatExtra("Latitude", 0.0f);
        longitude = i.getFloatExtra("Longitude", 0.0f);
        SSID = i.getStringExtra("SSID");
        MAC = i.getStringExtra("MAC");
        CellID = i.getStringExtra("CellID");
        LAC = i.getStringExtra("LAC");
        
        wStrength = i.getIntExtra("wStrength", 0);
        cStrength = i.getIntExtra("CellStrength", 0);
        
        //extract position-th row from arraylist and put it in activity header
        TextView tvId = (TextView) findViewById(R.id.wifiTextView);
        String display = "SSID: "+SSID+"\n\nMAC Address: "+MAC
        		+"\n\nWiFi Strength (dBm): "+wStrength+" Cell Signal " +
        				"Strength (asu): "+cStrength+"\n\nLongitude: "+longitude+
        		" Latitude: "+ latitude+ "\n\nCell ID: "+ CellID+ " LAC: "+LAC+"\n\n";
        tvId.setText(display);
        
        //upon buttonclick, we read description from the editText, and store to
        //local DB
      //find button that sends stats to local DB - 'storeBtn'
    	final Button storeBtn = (Button) findViewById(R.id.storeBtn);
    	
    	//setup onclick listener for storeBtn
    	//everytime it is pressed, info gets retrieved and sent to local DB
    	storeBtn.setOnClickListener(new Button.OnClickListener() {
    		
    		 public void onClick(View v) {
    			 try{
    				 //extract description
    				 
    				 final EditText descrField = (EditText) findViewById(R.id.editDescr);
    				 String desc = descrField.getText().toString();
    				 timestamp = getTimeStamp();
    				 storeToDB(desc, timestamp);
    				 
    			 }catch(Exception e) {
    				 e.printStackTrace();
    			 }
    		 }
    	});//listener closes
    }
  
    
    @TargetApi(9)
	public String getTimeStamp() {
    	
    	Date d = new Date();
    	CharSequence s  = DateFormat.format("yyyy-MM-dd hh:mm:ss.sss", d.getTime());
    	timestamp = s.toString();
    	
    	return timestamp;
    	
    }
    
    
	private void connect() {
		
		//connecting, creating socket and streams
		try {
			
			socket = new Socket("10.1.1.103", 8084);
		
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			

	        	
	      } catch (Exception e) {
	        	
	        	System.out.println("AppMesg: ERROR: " + e.toString());
	            return;
	        }
	        
			return;
	}
	
	private void storeToDB(String desc, String timestamp) {
		WiFiDB_Helper wifiDbHelper = new WiFiDB_Helper(getApplicationContext());
		CellDB_Helper cellDbHelper = new CellDB_Helper(getApplicationContext());
		EntryDB_Helper entryDbHelper = new EntryDB_Helper(getApplicationContext());
		
		wifiDbHelper.addRow(SSID, MAC, desc);
		cellDbHelper.addRow(CellID, LAC);
		entryDbHelper.addRow(timestamp, CellID, MAC, latitude, longitude, wStrength, cStrength, SSID, desc, LAC);
		

		wifiDbHelper.close();
		cellDbHelper.close();
		entryDbHelper.close();
		Toast.makeText( this, "Entry Saved", Toast.LENGTH_SHORT ).show();
		finish();
	}
	
       
}
