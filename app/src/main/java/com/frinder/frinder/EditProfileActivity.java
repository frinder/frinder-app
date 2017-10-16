package com.frinder.frinder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.frinder.frinder.dataaccess.UserFirebaseDas;

import java.util.ArrayList;
import java.util.Arrays;

public class EditProfileActivity extends AppCompatActivity {
    String userId;
    private UserFirebaseDas userFirebaseDas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userFirebaseDas = new UserFirebaseDas(this);

        userId = getIntent().getStringExtra("userId");

        final EditText etAboutMe = (EditText) findViewById(R.id.etAboutMe);
        final EditText etInterests = (EditText) findViewById(R.id.etInterests);
        Button profileSubmit = (Button)findViewById(R.id.editProfileSubmit);

        profileSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String interestString = etInterests.getText().toString();
                ArrayList<String> interests = null;
                try {
                    interests = new ArrayList<String>(Arrays.asList(interestString.split(",")));
                }catch (Exception e) {
                    e.printStackTrace();
                }
                userFirebaseDas.updateUserDescAndInterests(userId,  etAboutMe.getText().toString(),interests);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }
}
