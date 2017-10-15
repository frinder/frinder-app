package com.frinder.frinder.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.frinder.frinder.R;
import com.frinder.frinder.adapters.DiscoverUsersAdapter;
import com.frinder.frinder.adapters.SpacesItemDecoration;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

public class DiscoverActivity extends AppCompatActivity {
    private static final String TAG = "DiscoverActivity";
    ArrayList<User> users;
    DiscoverUsersAdapter adapter;
    Profile profile;
    User currentUser;
    UserFirebaseDas userFirebaseDas;

    //ToDo Mallika - Remove this constant when filters/settings screen is ready
    //Assuming that we are looking for people in a radius of 150m which is about 574.147ft.
    private final static int SEARCH_RADIUS = 175; //unit is meters

    //ToDo Mallika - Remove this constant when User location is current and user discoverability has been handled
    //This is used to filter nearby users to show users active/with timestamp within 15 minutes
    private final static long TIME_USER_LAST_ACTIVE = 15; //unit is minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView rvDiscoverusers = (RecyclerView) findViewById(R.id.rvDiscoverUsers);
        users = new ArrayList<>();
        adapter = new DiscoverUsersAdapter(this, users);
        rvDiscoverusers.setAdapter(adapter);
        rvDiscoverusers.setLayoutManager(new LinearLayoutManager(this));
        SpacesItemDecoration decoration = new SpacesItemDecoration(24);
        rvDiscoverusers.addItemDecoration(decoration);

        userFirebaseDas = new UserFirebaseDas(DiscoverActivity.this);
        profile = Profile.getCurrentProfile();
        userFirebaseDas.getUser(profile.getId(), new UserFirebaseDas.OnCompletionListener() {
            @Override
            public void onUserReceived(User user) {
                currentUser = user;
                Log.d(TAG, "in onUserReceived");
                Log.d(TAG, currentUser.toString());
                getNearbyUsers();
            }
        });
    }

    public void getNearbyUsers() {
        //Clear the list each time discover menu button is clicked as user list will change based on who is nearby
        if (!users.isEmpty()) {
            users.clear();
            adapter.notifyDataSetChanged();
        }

        // ToDo Mallika - change userFirebaseDas.getAllUsers() to return users based on current filters
        userFirebaseDas.getAllUsers(new UserFirebaseDas.OnCompletionListener() {
            @Override
            public void onUsersReceived(ArrayList<User> users) {
                readAllUsersComplete(users);
            }
        });
    }



    public void readAllUsersComplete(ArrayList<User> userList) {
        Log.d(TAG, "in readAllUsersComplete");
        Log.d(TAG, userList.toString());

        //ToDo - transfer this logic to Firebase if possible

        ArrayList<User> nearbyUsers = new ArrayList<>();
        ArrayList<Float> distanceFromCurrentUser = new ArrayList<>();
        TreeSet<Float> distances = new TreeSet<>();

        for (User user : userList) {
            //Check that current app user is not added to nearby users list
            if (!user.getUid().contentEquals(currentUser.getUid())) {
                //Check that user was nearby/active (timestamp) within past 15 mins
                Date currentTimestamp = new Date();
                if (currentTimestamp.getTime() - user.getTimestamp().getTime() < (TIME_USER_LAST_ACTIVE * 60000)) {
                    float[] results = new float[1];
                    Location.distanceBetween(currentUser.getLocation().get(0), currentUser.getLocation().get(1),
                            user.getLocation().get(0), user.getLocation().get(1), results);
                    float distance = results[0];

                    if (distance <= 150F) {
                        Log.d(TAG, user.getName() + ", distance = " + distance);
                        nearbyUsers.add(user);
                        distanceFromCurrentUser.add(distance);
                        distances.add(distance);
                    }
                }
            }
        }

        Log.d(TAG, "Sorted List");
        if (distanceFromCurrentUser != null && !distanceFromCurrentUser.isEmpty()) {
            for (Float distance : distances) {
                for (int i=0; i<distanceFromCurrentUser.size(); i++) {
                    Float tDistance = distanceFromCurrentUser.get(i);
                    if (tDistance.floatValue() == distance.floatValue()) {
                        Log.d(TAG, nearbyUsers.get(i).getName());
                        users.add(nearbyUsers.get(i));
                    }
                }
            }

            adapter.notifyDataSetChanged();
        }
        else {
            Toast.makeText(this, "No one closeby right now. Consider increasing the radius", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_discover, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_action_discover) {
            getNearbyUsers();
            return true;
        }

        if (id == R.id.menu_action_notifications) {
            Intent i = new Intent(this, NotificationsActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.menu_action_settings) {
            //ToDo Create Filter DialogFragment
            return true;
        }
        if (id == R.id.menu_action_logout) {
            Toast.makeText(this, "User logged out ", Toast.LENGTH_LONG).show();
            LoginManager.getInstance().logOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
