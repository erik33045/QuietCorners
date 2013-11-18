package com.example.quietcorners;

import android.app.Application;
import android.graphics.Bitmap;

public class Variables extends Application {
    public int soundRating = -1;
    public int lightRating = -1;
    public int internetRating = -1;
    public boolean openNetwork = false;
    public Bitmap cornerBitmap = null;
    public double longitude = 0;
    public double latitude = 0;
    public int overallRating = -1;
}