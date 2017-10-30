package com.frinder.frinder.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.frinder.frinder.R;
import com.frinder.frinder.adapters.EditInterestsAdapter;
import com.frinder.frinder.adapters.InterestsAdapter;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.Interest;

import java.util.ArrayList;
import java.util.HashMap;

import static com.frinder.frinder.R.id.rvInterests;

public class EditProfileActivity extends BaseActivity {
    String userId;
    private UserFirebaseDas userFirebaseDas;
    private EditText etAboutMe;
    private HashMap<String, Boolean> interests;
    EditInterestsAdapter editInterestsAdapter;
    private static final String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userFirebaseDas = new UserFirebaseDas(this);

        userId = getIntent().getStringExtra("userId");

        etAboutMe = (EditText) findViewById(R.id.etAboutMe);

        RecyclerView rvInterests = (RecyclerView) findViewById(R.id.rvEditInterests);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvInterests.setLayoutManager(gridLayoutManager);
        final ArrayList<Interest>  editInterests = Interest.createFilterInterestList(getResources().getStringArray(R.array.filter_interest_label),
                getResources().obtainTypedArray(R.array.filter_interest_icon),
                getResources().obtainTypedArray(R.array.filter_interest_pics),
                getResources().getIntArray(R.array.filter_interest_color),
                getResources().getStringArray(R.array.filter_interest_forDB));
        editInterestsAdapter = new EditInterestsAdapter(this, editInterests);
        rvInterests.setAdapter(editInterestsAdapter);

        editInterestsAdapter.setOnItemClickListener(new EditInterestsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Interest interestClicked = editInterests.get(position);
                if(interests.containsKey(interestClicked.getDBValue()) && interests.get(interestClicked.getDBValue()).equals(true)) {
                    interests.put(interestClicked.getDBValue(),false);
                    interestClicked.setSelected(false);
                } else {
                    interests.put(interestClicked.getDBValue(),true);
                    interestClicked.setSelected(true);
                }
                editInterestsAdapter.notifyItemChanged(position);
            }
        });
        interests = new HashMap<>();
        Button profileSubmit = (Button)findViewById(R.id.editProfileSubmit);

        profileSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etAboutMe.getText())) {
                    Toast.makeText(getApplicationContext(), "AboutMe is empty!",Toast.LENGTH_SHORT).show();
                } else if(interests.size()==0) {
                    Toast.makeText(getApplicationContext(), "Select any interest!",Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG, interests.toString());
                    updateDBAndClose();
                }
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