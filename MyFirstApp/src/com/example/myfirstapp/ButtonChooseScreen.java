package com.example.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ButtonChooseScreen extends Activity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);
        
        //setup Button listener for Maps Button
        final Button mapsBtn = (Button) findViewById(R.id.openMaps);
    	
    	//setup onclick listener for storeBtn
    	//everytime it is pressed, info gets retrieved and sent to local DB
    	mapsBtn.setOnClickListener(new Button.OnClickListener() {
    		
    		 public void onClick(View v) {
    			 //start activity MyMapView
    			 Intent i = new Intent(v.getContext(), MyMapView.class);
    			 startActivity(i);
    		 }
    	});//listener closes
    	

    	//setup Button listener for WiFi Button
        final Button wiFiBtn = (Button) findViewById(R.id.openWiFiList);
    	
    	//setup onclick listener for storeBtn
    	//everytime it is pressed, info gets retrieved and sent to local DB
    	wiFiBtn.setOnClickListener(new Button.OnClickListener() {
    		 public void onClick(View v) {
    			//start activity MyFirstActivity
    			 Intent i = new Intent(v.getContext(), MyFirstActivity.class);
    			 startActivity(i);
    		 }
    	});//listener closes
	}
	
	  @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.activity_my_first, menu);
	        return true;
	    }
}
