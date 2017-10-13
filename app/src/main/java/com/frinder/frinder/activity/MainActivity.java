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
import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.User;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements UserFirebaseDas.UserDasInterface {

    public static final int LOGIN_RESULT = 100;
    private User loggedUser;
    private static final String TAG = "Main";
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        Fabric.with(this, new Crashlytics());
    }


    @Override
    protected void onStart() {
        super.onStart();

        logUser();
    }

    @Override
    protected void onDestroy() {
        LoginManager.getInstance().logOut();
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
            profile = Profile.getCurrentProfile();
            //TODO Sanal to fix
            UserFirebaseDas userFirebaseDas = new UserFirebaseDas(this);
            userFirebaseDas.getUser(profile.getId());
            /*TextView tvName = (TextView) findViewById(R.id.tvName);
            ImageView ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
            // You can call any combination of these three methods
            //Crashlytics.setUserIdentifier("12345");
            //Crashlytics.setUserEmail("user@fabric.io");
            Crashlytics.setUserName(profile.getName());
            //get user
            tvName.setText(profile.getName());
            Glide.with(getApplicationContext())
                    .load(profile.getProfilePictureUri(200, 200))
                    .into(ivProfilePic);*/

            //Open discover screen after login
            Intent discoverIntent = new Intent(this, DiscoverActivity.class);
            startActivity(discoverIntent);
        }
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
                UserFirebaseDas userFirebaseDas = new UserFirebaseDas(getApplicationContext());
                userFirebaseDas.addUser(loggedUser);
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
    }

    @Override
    public void readAllUsersComplete(ArrayList<User> userList) {

    }
}
