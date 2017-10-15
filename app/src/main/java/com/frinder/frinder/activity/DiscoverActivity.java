package com.frinder.frinder.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.frinder.frinder.utils.LocationUtils;
import com.google.android.gms.location.LocationCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

import static com.frinder.frinder.activity.MainActivity.LOCATION_DENY_MSG;

public class DiscoverActivity extends AppCompatActivity {
    private static final String TAG = "DiscoverActivity";
    ArrayList<User> users;
    DiscoverUsersAdapter adapter;
    Profile profile;
    User currentUser;
    UserFirebaseDas userFirebaseDas;
    private static final int REQUEST_FINE_LOCATION = 99;
    private LocationUtils locationUtilInstance;
    SwipeRefreshLayout srlDiscoverContainer;

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
        locationUtilInstance = LocationUtils.getInstance();
        if(requestLocationPermissions()) {
            getCurrentLocation();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        srlDiscoverContainer = (SwipeRefreshLayout) findViewById(R.id.srlDiscoverContainer);
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
                if(user.getLocation()!=null) {
                    getNearbyUsers();
                }
            }
        });

        srlDiscoverContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNearbyUsers();
            }
        });
    }

    private void getCurrentLocation() {
        locationUtilInstance.startLocationUpdates(this,locationUtilsCallback);
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
            srlDiscoverContainer.setRefreshing(false);
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

    private boolean requestLocationPermissions() {
        int hasFineLocation = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocation = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if(hasCoarseLocation != PackageManager.PERMISSION_GRANTED || hasFineLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    getCurrentLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(this, LOCATION_DENY_MSG, Toast.LENGTH_LONG)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onPause() {
        locationUtilInstance.stopLocationUpdates(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        locationUtilInstance.stopLocationUpdates(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        locationUtilInstance.startLocationUpdates(this,locationUtilsCallback);
        super.onResume();
    }

    LocationUtils.LocationUpdate locationUtilsCallback = new LocationUtils.LocationUpdate(){
        @Override
        public void onLocationChanged(Context context, Location location) {
            // New location has now been determined
            String msg = "Updated Location: " +
                    Double.toString(location.getLatitude()) + "," +
                    Double.toString(location.getLongitude());
            Log.d(TAG, msg);
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show();

            if(location!=null) {
                ArrayList<Double> locationList = new ArrayList<>();
                locationList.add(location.getLatitude());
                locationList.add(location.getLongitude());
                Log.d(TAG, "Updating user " + Profile.getCurrentProfile().getId() + " location with " + locationList.toString());
                userFirebaseDas.updateUserLocation(profile.getId(), locationList);
            }
        }
    };
}
