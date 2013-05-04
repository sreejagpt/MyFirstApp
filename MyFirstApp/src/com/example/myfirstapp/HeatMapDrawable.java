package com.example.myfirstapp;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class HeatMapDrawable extends Drawable {

  private Paint paint;

  public HeatMapDrawable() {

    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setAlpha(50);
    paint.setColor(Color.RED);
    
  }

  @Override
  public void draw(Canvas canvas) {
    int height = getBounds().height();
    int width = getBounds().width();
    RectF rect = new RectF(0.0f, 0.0f, width, height);
    canvas.drawRoundRect(rect, 30, 30, paint);
  }

  @Override
  public void setAlpha(int alpha) {

  }

  @Override
  public void setColorFilter(ColorFilter cf) {

  }

  @Override
  public int getOpacity() {
    return 255;
  }

}