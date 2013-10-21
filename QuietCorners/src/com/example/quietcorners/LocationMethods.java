package com.example.quietcorners;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;

public class LocationMethods {
    public static void MoveAndZoomCameraToCoordinate(GoogleMap map, LatLng coordinate) {
        MoveCameraToCoordinate(map, coordinate);

        //Zooming to 15, seems to be a good level to indicate location
        ZoomCameraToLevel(map, 15);
    }

    public static void ZoomCameraToLevel(GoogleMap map, int level) {
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(level);
        map.animateCamera(zoom);
    }

    public static void MoveCameraToCoordinate(GoogleMap map, LatLng coordinate) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
        map.moveCamera(center);
    }

    public static LatLng GetCoordinate(Location loc) {
        return new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    public static void AddMarkerToMap(GoogleMap map, LatLng position, String title) {
        map.addMarker(new MarkerOptions().position(position).title(title));
    }

    public static void AddRandomMarkersToMapNearCurrentPosition(GoogleMap map, LatLng position, int numberToGenerate) {
        for (int i = 0; i < numberToGenerate; i++) {
            AddMarkerToMap(map, new LatLng(position.latitude + GenerateRandomDouble(), position.longitude + GenerateRandomDouble()), "" + (i + 1));
        }
    }

    private static double GenerateRandomDouble() {
        Random r = new Random();
        //Generate random number from 0 to 0.0075
        double randDouble = (((double) r.nextInt(75)) / 10000);
        //If true, make it negative, total range of -0.0075 to 0.0075
        return r.nextBoolean() ? -randDouble : randDouble;
    }

    public static void ShowUnavailableServicesDialogue(String errorMessage, Context c ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(errorMessage);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static boolean AreGooglePlayServicesValid(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }
}
