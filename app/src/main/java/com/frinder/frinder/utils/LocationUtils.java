package com.frinder.frinder.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class LocationUtils {
    private static final String TAG = "LocationUtil";
    //TODO change to 5mins later
    private long UPDATE_INTERVAL = 2 * 60 * 1000;  /* 2 mins */
    private long FASTEST_INTERVAL = 1 * 1000; /* 60 sec */
    public static LocationUtils locationUtilInstance = null;
    public static Location cachedLocation;
    LocationRequest mLocationRequest;

    public static LocationUtils getInstance() {
        if(locationUtilInstance == null )
            return new LocationUtils();
        return locationUtilInstance;
    }

    public interface LocationUpdate {
        void onLocationChanged(Context context, Location lastLocation);
    }

    private void setCachedLocation(Location location) {
        if (location != null) {
            cachedLocation = location;
        }
    }

    private void requestLocation(final Context context, final LocationUpdate callback) {
        final FusedLocationProviderClient locationClient = getFusedLocationProviderClient(context);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        setCachedLocation(location);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    // Trigger new location updates at interval
    public void startLocationUpdates(final Context context, final LocationUpdate callback) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location location = locationResult.getLastLocation();
                        setCachedLocation(location);
                        callback.onLocationChanged(context, location);
                    }
                },
                Looper.myLooper());
    }

    public void stopLocationUpdates(final Context context) {
        getFusedLocationProviderClient(context).removeLocationUpdates(new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG,"Stop location updates!");
            }
        });
    }
}
