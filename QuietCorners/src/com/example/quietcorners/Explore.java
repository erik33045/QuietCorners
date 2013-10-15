package com.example.quietcorners;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class Explore extends FragmentActivity {
    private GoogleMap map;
    private LocationManager locationManager = null;

    //Method which runs when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_activity);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        InstantiateMap();
    }

    //Start a location listener
    LocationListener onLocationChange = new LocationListener() {
        public void onLocationChanged(Location loc) {
            //Move and Zoom the Camera to the current location
            LatLng coordinate = LocationMethods.GetCoordinate(loc);
            LocationMethods.MoveAndZoomCameraToCoordinate(map, coordinate);
            LocationMethods.AddRandomMarkersToMapNearCurrentPosition(map, coordinate, 10);
        }

        public void onProviderDisabled(String provider) {
            // required for interface, not used
        }

        public void onProviderEnabled(String provider) {
            // required for interface, not used
        }

        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
            // required for interface, not used
        }
    };

    //pauses listener while app is inactive
    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(onLocationChange);
    }

    //reactivates listener when app is resumed
    @Override
    public void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10000.0f, onLocationChange);
    }

    private void InstantiateMap() {
        map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
    }
}