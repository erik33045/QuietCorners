package com.example.quietcorners;

import android.app.Application;
import android.graphics.Bitmap;

public class Variables extends Application {
    public int soundRating = -1;
    public int lightRating = -1;
    public int internetRating = -1;
    public boolean openNetwork = false;
    public Bitmap cornerBitmap;
    double longitude;
    double latitude;
    int overallRating;
}