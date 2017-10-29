package com.frinder.frinder.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;

import java.util.ArrayList;
import java.util.HashMap;

public class EditProfileActivity extends BaseActivity {
    String userId;
    private UserFirebaseDas userFirebaseDas;
    private ArrayList<CheckBox> cbInterestList;
    private int numInterests;
    private String[] filterInterestLabel;
    private String[] filterInterestDBVal;
    private EditText etAboutMe;
    private HashMap<String, Boolean> interests;

    private static final String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userFirebaseDas = new UserFirebaseDas(this);

        userId = getIntent().getStringExtra("userId");

        etAboutMe = (EditText) findViewById(R.id.etAboutMe);

        //Get all interests and add Checkboxes to TableLayout
        TableLayout tlInterestLayout = (TableLayout) findViewById(R.id.tlInterestLayout);
        tlInterestLayout.setStretchAllColumns(true);


        filterInterestLabel = getResources().getStringArray(R.array.filter_interest_label);
        filterInterestDBVal = getResources().getStringArray(R.array.filter_interest_forDB);
        numInterests = filterInterestLabel.length;
        cbInterestList = new ArrayList<>();
        ArrayList<TableRow> trInterestLayoutList = new ArrayList<>();

        for (int i=0; i<numInterests; i++) {
            if ((i+1)%2 == 1) {
                TableRow trInterestLayout = new TableRow(this);
                trInterestLayoutList.add(trInterestLayout);
                tlInterestLayout.addView(trInterestLayout);
            }

            TableRow tableRow = trInterestLayoutList.get(i/2);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.TOP);

            CheckBox cbInterest = new CheckBox(this);
            cbInterest.setText(filterInterestLabel[i].replace("\n", "/"));
            cbInterestList.add(cbInterest);

            tableRow.addView(cbInterest);
        }

        interests = new HashMap<>();
        Button profileSubmit = (Button)findViewById(R.id.editProfileSubmit);

        profileSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interests.clear();
                for (int i=0; i<cbInterestList.size(); i++) {
                    CheckBox checkbox = cbInterestList.get(i);
                    if (checkbox.isChecked()) {
                        interests.put(filterInterestDBVal[i], true);
                    }
                }

                Log.d(TAG, interests.toString());

                updateDBAndClose();
            }
        });

        Button profileSkip = (Button)findViewById(R.id.editProfileSkip);
        profileSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDBAndClose();
            }
        });

    }

    private void updateDBAndClose() {
        userFirebaseDas.updateUserDescAndInterests(userId, etAboutMe.getText().toString(), interests);
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_action_edit_profile).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

}