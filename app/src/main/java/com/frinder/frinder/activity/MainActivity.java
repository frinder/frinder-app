package com.frinder.frinder.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements UserFirebaseDas.UserDasInterface {

    public static final int LOGIN_RESULT = 100;
    private User loggedUser;
    private static final String TAG = "Main";
    private Profile profile;
    UserFirebaseDas userFirebaseDas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        Fabric.with(this, new Crashlytics());
        userFirebaseDas = new UserFirebaseDas(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        logUser();
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
        userFirebaseDas.getUser(profile.getId());
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

    @Override
    public void readUserComplete(User user) {
        Log.d(TAG, "Read user from firebase " + user.toString());
        //loggedUser has the user fetched from firebase
        loggedUser = user;
        LocationUtils locationUtils = new LocationUtils(getBaseContext());
        locationUtils.startLocationUpdates();
        locationUtils.getLastLocation(new LocationUtils.LocationUpdate() {
            @Override
            public void onSuccess(Location location) {
                ArrayList<Double> locationList = new ArrayList<>();
                locationList.add(location.getLatitude());
                locationList.add(location.getLongitude());
                Log.d(TAG, "Updating user " + loggedUser.getUid() + " location with " + locationList.toString());
                userFirebaseDas.updateUserLocation(loggedUser.getUid(), locationList);
            }
        });
    }

    @Override
    public void readAllUsersComplete(ArrayList<User> userList) {

    }
}
