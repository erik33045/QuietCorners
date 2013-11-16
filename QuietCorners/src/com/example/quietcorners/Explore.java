package com.example.quietcorners;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Explore extends FragmentActivity {
    private GoogleMap map;
    private LocationManager locationManager = null;

    //Method which runs when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_activity);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //If the map is instantiated, do stuff related to the map
        if (AttemptToInstantiateMap()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, onLocationChange);
        }
    }

    //Start a location listener
    LocationListener onLocationChange = new LocationListener() {
        public void onLocationChanged(Location loc) {
            //Move and Zoom the Camera to the current location and add markers to the map
            LatLng coordinate = LocationMethods.GetCoordinate(loc);
            LocationMethods.MoveAndZoomCameraToCoordinate(map, coordinate);
            LocationMethods.AddCornersToMapNearCurrentPosition(map, coordinate);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, onLocationChange);
    }

    public boolean AttemptToInstantiateMap() {
        boolean mapHasStarted = false;

        if (LocationMethods.AreGooglePlayServicesValid(this)) {
            map = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            map.setMyLocationEnabled(true);
            SetMarkerClickListenerEvent();
            MoveCameraToTampa();
            mapHasStarted = true;
        } else {
            LocationMethods.ShowUnavailableServicesDialogue(getResources().getString(R.string.error_getting_maps), Explore.this);
        }

        return mapHasStarted;
    }

    private void SetMarkerClickListenerEvent() {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Intent i = new Intent(Explore.this, LocationDetails.class);
                i.putExtra("CornerId", marker.getTitle());
                startActivity(i);
                return true;
            }

        });
    }

    private void MoveCameraToTampa() {
        LatLng tampa = new LatLng(27.9710, -82.4650);
        LocationMethods.MoveCameraToCoordinate(map, tampa);
    }
}

