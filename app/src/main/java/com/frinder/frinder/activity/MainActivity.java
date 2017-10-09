package com.frinder.frinder.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.frinder.frinder.R;
import com.frinder.frinder.model.User;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_RESULT = 100;
    private User loggedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fabric.with(this, new Crashlytics());
        // TODO: Move this to where you establish a user session
        logUser();
    }



    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

    private void logUser() {
        TextView tvName = (TextView) findViewById(R.id.tvName);
        ImageView ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        Profile profile = null;

        if(Profile.getCurrentProfile()==null) {
            facebookUserLogin();
        } else {
            profile = Profile.getCurrentProfile();
            // You can call any combination of these three methods
            //Crashlytics.setUserIdentifier("12345");
            //Crashlytics.setUserEmail("user@fabric.io");
            Crashlytics.setUserName(profile.getName());

            tvName.setText(profile.getName());
            Glide.with(getApplicationContext())
                    .load(profile.getProfilePictureUri(200,200))
                    .into(ivProfilePic);
        }
    }

    private void facebookUserLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== LOGIN_RESULT) {
            if(resultCode == Activity.RESULT_OK){
                loggedUser = (User) data.getExtras().getSerializable("loggedUser");
                Log.d("Main",loggedUser.toString());
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("Main", "Login failed!");
            }
        }
    }

    public void logoutUser(View view) {
        LoginManager.getInstance().logOut();
        Toast.makeText(this, "User logged out ",Toast.LENGTH_LONG).show();
    }
}
