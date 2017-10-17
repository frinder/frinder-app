package com.frinder.frinder.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;

import java.util.HashMap;

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
        final CheckBox cbOutdoor = (CheckBox) findViewById(R.id.cbOutdoor);
        final CheckBox cbFoodie = (CheckBox) findViewById(R.id.cbFoodie);
        final CheckBox cbGames = (CheckBox) findViewById(R.id.cbGames);
        final CheckBox cbCreative = (CheckBox) findViewById(R.id.cbCreative);
        final CheckBox cbParty = (CheckBox) findViewById(R.id.cbParty);
        final CheckBox cbReading = (CheckBox) findViewById(R.id.cbReading);

        final HashMap<String, Boolean> interests = new HashMap<>();
        Button profileSubmit = (Button)findViewById(R.id.editProfileSubmit);

        profileSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbOutdoor.isChecked())
                    interests.put("Outdoor",true);
                if(cbFoodie.isChecked())
                    interests.put("Foodie", true);
                if(cbGames.isChecked())
                    interests.put("Games", true);
                if(cbCreative.isChecked())
                    interests.put("Creative",true);
                if(cbParty.isChecked())
                    interests.put("Party", true);
                if(cbReading.isChecked())
                    interests.put("Reading", true);
                userFirebaseDas.updateUserDescAndInterests(userId,  etAboutMe.getText().toString(),interests);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }
}
