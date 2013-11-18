package com.example.quietcorners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.Assert;

public class Record extends Activity {
    LocationManager locationManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GetSoundButtonAndBindClickEvent();
        GetLightButtonAndBindClickEvent();
        GetPictureButtonAndBindClickEvent();
        GetOpenNetworksButtonAndBindClickEvent();
        GetConfirmButtonAndBindClickEvent();
    }

    //Start a location listener
    LocationListener onLocationChange = new LocationListener() {
        public void onLocationChanged(Location loc) {
            SetTextToCurrentLocation(loc);
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

    private void SetTextToCurrentLocation(Location loc) {
        LatLng coordinate = LocationMethods.GetCoordinate(loc);
        TextView currentLocation = (TextView) findViewById(R.id.txtCurrentLocation);
        currentLocation.setText(getResources().getString(R.string.current_location) + " Lat: " + coordinate.latitude + " Lon: " + coordinate.longitude);
        Variables application = (Variables)getApplication();
        application.longitude = coordinate.longitude;
        application.latitude = coordinate.latitude;
    }

    //pauses listener while app is inactive
    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(onLocationChange);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    //reactivates listener when app is resumed
    @Override
    public void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10000.0f, onLocationChange);
    }

    private void GetSoundButtonAndBindClickEvent() {
        Button button = (Button) findViewById(R.id.soundButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Record.this, SoundRecord.class);
                startActivity(i);
            }
        });
    }

    private void GetLightButtonAndBindClickEvent() {
        Button button = (Button) findViewById(R.id.lightButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Record.this, LumenRecord.class);
                startActivity(i);
            }
        });
    }

    private void GetOpenNetworksButtonAndBindClickEvent() {
        Button button = (Button) findViewById(R.id.openNetworksButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Record.this, WifiTest.class);
                startActivity(i);
            }
        });
    }

    private void GetPictureButtonAndBindClickEvent() {
        Button button = (Button) findViewById(R.id.pictureButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Record.this, PicRecord.class);
                startActivity(i);
            }
        });
    }

    private void GetConfirmButtonAndBindClickEvent() {
        Button button = (Button) findViewById(R.id.confirmButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Record.this, Confirm.class);
                startActivity(i);
            }
        });
    }
}
