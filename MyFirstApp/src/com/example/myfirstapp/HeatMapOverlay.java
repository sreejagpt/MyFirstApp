package com.example.myfirstapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class HeatMapOverlay extends Overlay {


  private final GeoPoint geoPoint;
  private final Context context;
  private final int drawable;

  /**
   * @param context the context in which to display the overlay
   * @param geoPoint the geographical point where the overlay is located
   * @param drawable the ID of the desired drawable
   */
  public HeatMapOverlay(Context context, MapPoint mapPoint, int drawable) {
    this.context = context;
    int a = (int)(mapPoint.latitude * 1E6);
	int b = (int)(mapPoint.longitude * 1E6);
    this.geoPoint = new GeoPoint(a, b);
    this.drawable = drawable;
  }

  @Override
  public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
    super.draw(canvas, mapView, shadow);

    // Convert geo coordinates to screen pixels
    Point screenPoint = new Point();
    mapView.getProjection().toPixels(geoPoint, screenPoint);
    System.out.println("MSG: About to read");
    // Read the image
    Bitmap markerImage = BitmapFactory.decodeResource(context.getResources(), drawable);
    
    // Draw it, centered around the given coordinates
    canvas.drawBitmap(markerImage,
        screenPoint.x - markerImage.getWidth() / 2,
        screenPoint.y - markerImage.getHeight() / 2, null);
    System.out.println("MSG: Returning now");
    return true;
  }

  @Override
  public boolean onTap(GeoPoint p, MapView mapView) {
    // Handle tapping on the overlay here
    return true;
  }
}



