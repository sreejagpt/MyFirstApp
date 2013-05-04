package com.example.myfirstapp;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;



	public class MyOverlays extends ItemizedOverlay<OverlayItem> {

	
		  private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();

		  private Context context;
		 

		  public MyOverlays(Context context, Drawable defaultMarker) {
		    super(boundCenterBottom(defaultMarker));
		    this.context = context;
		  }

		  @Override
		  protected OverlayItem createItem(int i) {
		    return overlays.get(i);
		  }

		  @Override
		  public int size() {
		    return overlays.size();
		    
		  }

		  public void addOverlay(OverlayItem overlay) {
			  overlays.add(overlay);
		      populate();
		  }

		  protected boolean onTap(int index) {
		    
		    
		    
		    return true;
		  };

		
} 
