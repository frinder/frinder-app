package com.frinder.frinder.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.User;
import com.google.firebase.FirebaseApp;


import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {
    public static final int LOGIN_RESULT = 100;
    public static final int EDIT_PROFILE_RESULT = 200;
    public static final String LOCATION_DENY_MSG = "Frinder requires your location!";
    private User loggedUser;
    private static final String TAG = "Main";
    private Profile profile;
    private UserFirebaseDas userFirebaseDas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        Fabric.with(this, new Crashlytics());
        userFirebaseDas = new UserFirebaseDas(this);
        logUser();
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

    private void editProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("userId", Profile.getCurrentProfile().getId());
        startActivityForResult(intent, EDIT_PROFILE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                loggedUser = (User) data.getExtras().getSerializable("loggedUser");
                Log.d(TAG, loggedUser.toString());
                //TODO persist user
                Profile profile = Profile.getCurrentProfile();
                if(profile!=null) {
                    userFirebaseDas.addUser(loggedUser);
                    editProfile();
                } else {
                    Toast.makeText(this,"Profile is null",Toast.LENGTH_SHORT).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Login failed!");
            }
        } else if (requestCode == EDIT_PROFILE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                readProfile();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Profile Edit failed!");
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
    }
}
