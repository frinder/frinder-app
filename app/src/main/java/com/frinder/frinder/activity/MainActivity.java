package com.frinder.frinder.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.User;
import com.frinder.frinder.utils.LocationUtils;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_FINE_LOCATION = 99;
    public static final int LOGIN_RESULT = 100;
    public static final String LOCATION_DENY_MSG = "Frinder requires your location!";
    private User loggedUser;
    private static final String TAG = "Main";
    private Profile profile;
    private UserFirebaseDas userFirebaseDas;
    private Location currentLocation = null;
    private LocationUtils locationUtilInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        Fabric.with(this, new Crashlytics());
        userFirebaseDas = new UserFirebaseDas(this);
        locationUtilInstance = LocationUtils.getInstance();
        locationUtilInstance.startLocationUpdates(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        requestLocationPermissions();
    }

    @Override
    protected void onDestroy() {
        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
        }
        super.onDestroy();
    }

    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

    private void logUser() {
        profile = Profile.getCurrentProfile();
        if (profile == null) {
            facebookUserLogin();
        } else {
            readProfile();
        }
    }

    private void readProfile() {
        profile = Profile.getCurrentProfile();
        userFirebaseDas.getUser(profile.getId(), new UserFirebaseDas.OnCompletionListener() {
            @Override
            public void onUserReceived(User user) {
                readUserComplete(user);
            }
        });
        Crashlytics.setUserName(profile.getName());
        //TODO sent profile user data with intent
        Intent discoverIntent = new Intent(this, DiscoverActivity.class);
        startActivity(discoverIntent);
    }

    private void facebookUserLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                loggedUser = (User) data.getExtras().getSerializable("loggedUser");
                Log.d(TAG, loggedUser.toString());
                //TODO persist user
                Profile profile = Profile.getCurrentProfile();
                Profile.setCurrentProfile(profile);
                userFirebaseDas.addUser(loggedUser);
                readProfile();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Login failed!");
            }
        }
    }

    public void logoutUser(View view) {
        LoginManager.getInstance().logOut();
        Toast.makeText(this, "User logged out ", Toast.LENGTH_LONG).show();
    }

    public void readUserComplete(User user) {
        Log.d(TAG, "Read user from firebase " + user.toString());
        //loggedUser has the user fetched from firebase
        loggedUser = user;
        if(currentLocation!=null) {
            ArrayList<Double> locationList = new ArrayList<>();
            locationList.add(currentLocation.getLatitude());
            locationList.add(currentLocation.getLongitude());
            Log.d(TAG, "Updating user " + loggedUser.getUid() + " location with " + locationList.toString());
            userFirebaseDas.updateUserLocation(loggedUser.getUid(), locationList);
        }
    }


    private void requestLocationPermissions() {
        int hasFineLocation = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocation = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if(hasCoarseLocation != PackageManager.PERMISSION_GRANTED || hasFineLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        };
        getCurrentLocation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    getCurrentLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, LOCATION_DENY_MSG, Toast.LENGTH_LONG)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getCurrentLocation() {
        locationUtilInstance.getLastLocation(this, new LocationUtils.LocationUpdate() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
                Log.d(TAG, "Current Location " + location.toString());
                logUser();
            }

            @Override
            public void onFailure() {
                String msg = "Unable to fetch location";
                Log.d(TAG, msg);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
//        Log.d(TAG, "Last know location set as " + LocationUtils.getStoredLocation().toString());
    }
}
