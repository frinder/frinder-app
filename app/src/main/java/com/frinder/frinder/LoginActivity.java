package com.frinder.frinder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.frinder.frinder.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static android.R.attr.id;
import static android.R.attr.name;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        loginButton = (LoginButton)findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("LoginScreen", "Login Success " + loginResult.toString());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String name = object.getString("name");
                                    String email = object.getString("email");
                                    String id = object.getString("id");
                                    String picUrl = "http://graph.facebook.com/"+id+"/picture?type=large";

                                    User user = new User();
                                    user.setName(name);
                                    user.setEmail(email);
                                    user.setProfilePicUrl(picUrl);
                                    Log.d("LoginScreen", "ProfilePic :: " + picUrl);
                                    Toast.makeText(getApplicationContext(), name + " " + " " + email + " " + id, Toast.LENGTH_LONG).show();

                                    //On Successful login
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("loggedUser", user);
                                    setResult(Activity.RESULT_OK,returnIntent);
                                    finish();
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday, picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("LoginScreen","Cancelled");
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }

            @Override
            public void onError(FacebookException e) {
                Log.e("LoginScreen", "Error " + e.toString());
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

}
