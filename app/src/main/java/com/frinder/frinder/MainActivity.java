package com.frinder.frinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.frinder.frinder.model.User;

import io.fabric.sdk.android.Fabric;

import static com.frinder.frinder.R.id.ivProfilePic;

public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_RESULT = 100;
    private User loggedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        // TODO: Move this to where you establish a user session
        logUser();

        setContentView(R.layout.activity_main);
        //TODO move inside logUser
        facebookUserLogin();

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
                //TODO remove after login works
                TextView tvName = (TextView) findViewById(R.id.tvName);
                tvName.setText(loggedUser.getName());

                if(!TextUtils.isEmpty(loggedUser.getProfilePicUrl())) {
                    ImageView ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
                    Glide.with(getApplicationContext())
                            .load(loggedUser.getProfilePicUrl())
                            .into(ivProfilePic);
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("Main", "Login failed!");
            }
        }
    }

    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        //Crashlytics.setUserIdentifier("12345");
        //Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName("Sanal Kumar");
    }

}
