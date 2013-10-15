package com.example.quietcorners;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;

public class LocationMethods {
    public static void MoveAndZoomCameraToCoordinate(GoogleMap map, LatLng coordinate) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        map.moveCamera(center);
        map.animateCamera(zoom);
    }

    public static LatLng GetCoordinate(Location loc) {
        return new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    public static void AddMarkerToMap(GoogleMap map, LatLng position, String title) {
        map.addMarker(new MarkerOptions().position(position).title(title));
    }

    public static void AddRandomMarkersToMapNearCurrentPosition(GoogleMap map, LatLng position, int numberToGenerate) {
        for (int i = 0; i < numberToGenerate; i++) {
            map.addMarker(new MarkerOptions().position(new LatLng(position.latitude + GenerateRandomDouble(), position.longitude + GenerateRandomDouble())));
        }
    }

    private static double GenerateRandomDouble() {
        Random r = new Random();
        //Generate random number from 0 to 0.0075
        double randDouble = (((double) r.nextInt(75)) / 10000);
        //If true, make it negative, total range of -0.0075 to 0.0075
        return r.nextBoolean() ? -randDouble : randDouble;
    }
}
