package com.frinder.frinder.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.frinder.frinder.R;
import com.frinder.frinder.adapters.DiscoverUsersAdapter;
import com.frinder.frinder.adapters.InterestsAdapter;
import com.frinder.frinder.adapters.SpacesItemDecoration;
import com.frinder.frinder.dataaccess.DiscoverFirebaseDas;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.fragments.SettingsFragment;
import com.frinder.frinder.model.DiscoverUser;
import com.frinder.frinder.model.Interest;
import com.frinder.frinder.model.User;
import com.frinder.frinder.utils.LocationUtils;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Date;

import static com.frinder.frinder.R.id.pbDiscoverUser;
import static com.frinder.frinder.activity.MainActivity.LOCATION_DENY_MSG;

public class DiscoverActivity extends BaseActivity {
    private static final String TAG = "DiscoverActivity";
    ArrayList<DiscoverUser> nearbyUsers;
    DiscoverUsersAdapter adapter;
    Profile profile;
    User currentUser;
    UserFirebaseDas userFirebaseDas;
    DiscoverFirebaseDas discoverFirebaseDas;
    private static final int REQUEST_FINE_LOCATION = 99;
    private LocationUtils locationUtilInstance;
    SwipeRefreshLayout srlDiscoverContainer;
    ImageView ivDiscoverUserIcon;
    RippleBackground rippleBackground;
    Handler handler;
    String filterInterest = "";
    ArrayList<Interest> interests;
    RecyclerView rvInterests;
    InterestsAdapter interestsAdapter;

    //Counts required for nearbyUsers
    int unFilteredNearbyUsersCount;
    int filteredNearbyUsersCount;

    //ToDo Mallika - Remove this constant when filters/settings screen is ready
    //Assuming that we are looking for people in a radius of 150m which is about 574.147ft.
    private final static int SEARCH_RADIUS = 175; //unit is meters

    //ToDo Mallika - Remove this constant when User location is current and user discoverability has been handled
    //This is used to filter nearby users to show users active/with timestamp within 15 minutes
    private final static long TIME_USER_LAST_ACTIVE = 15; //unit is minutes

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        locationUtilInstance = LocationUtils.getInstance();
        if(requestLocationPermissions()) {
            getCurrentLocation();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Note: This is to support filtering by multiple interests
        //filterInterestList = new ArrayList<>();

        // Lookup the recyclerview in activity layout
        rvInterests = (RecyclerView) findViewById(R.id.rvInterests);
        interests = Interest.createFilterInterestList(getResources().getStringArray(R.array.filter_interest_label),
                getResources().obtainTypedArray(R.array.filter_interest_icon),
                getResources().obtainTypedArray(R.array.filter_interest_color),
                getResources().getStringArray(R.array.filter_interest_forDB));
        interestsAdapter = new InterestsAdapter(this, interests);
        rvInterests.setAdapter(interestsAdapter);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvInterests.setLayoutManager(horizontalLayoutManagaer);

        interestsAdapter.setOnItemClickListener(new InterestsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getSelectedInterest(position);
            }
        });

        srlDiscoverContainer = (SwipeRefreshLayout) findViewById(R.id.srlDiscoverContainer);
        RecyclerView rvDiscoverusers = (RecyclerView) findViewById(R.id.rvDiscoverUsers);
        nearbyUsers = new ArrayList<>();
        adapter = new DiscoverUsersAdapter(this, nearbyUsers);
        rvDiscoverusers.setAdapter(adapter);
        rvDiscoverusers.setLayoutManager(new LinearLayoutManager(this));
        SpacesItemDecoration decoration = new SpacesItemDecoration(24);
        rvDiscoverusers.addItemDecoration(decoration);

        unFilteredNearbyUsersCount = 0;
        filteredNearbyUsersCount = 0;

        profile = Profile.getCurrentProfile();

        userFirebaseDas = new UserFirebaseDas(DiscoverActivity.this);
        userFirebaseDas.getUser(profile.getId(), new UserFirebaseDas.OnCompletionListener() {
            @Override
            public void onUserReceived(User user) {
                currentUser = user;
                Log.d(TAG, "in onUserReceived");
                Log.d(TAG, currentUser.toString());
                if(currentUser.getLocation()!=null && currentUser.getLocation().size() > 0) {
                    getdiscoverUsers();
                }
                else {
                    repeatGetDiscoverUsers();
                }
            }
        });

        discoverFirebaseDas = new DiscoverFirebaseDas(DiscoverActivity.this);

        srlDiscoverContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(currentUser.getLocation()!=null && currentUser.getLocation().size() > 0) {
                    getdiscoverUsers();
                }
                else {
                    repeatGetDiscoverUsers();
                }
            }
        });

        rippleBackground=(RippleBackground)findViewById(R.id.pbDiscoverUser);
        ivDiscoverUserIcon = (ImageView) findViewById(R.id.ivDiscoverUserIcon);
        ivDiscoverUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
                ivDiscoverUserIcon.setVisibility(View.VISIBLE);
            }
        });
        handler = new Handler();
    }

    private void getSelectedInterest(int position) {
        //Note: This is to support filtering by multiple interests
        //But the clicked interests will remain in original position and not jump to beginning of list
                /*Interest interestClicked = interests.get(position);
                String interestClickedDBValue = interestClicked.getDBValue();

                if (filterInterestList.contains(interestClickedDBValue)) {
                    filterInterestList.remove(interestClickedDBValue);
                    interestClicked.setSelected(false);
                }
                else {
                    filterInterestList.add(interestClickedDBValue);
                    interestClicked.setSelected(true);
                }

                interestsAdapter.notifyItemChanged(position);

                Log.d(TAG, "Interests picked = " + filterInterestList.toString());
                */

        Interest interestClicked = interests.get(position);
        String interestClickedDBValue = interestClicked.getDBValue();

        if (filterInterest.isEmpty()) {
            interests.remove(interestClicked);
            interestClicked.setSelected(true);
            interests.add(0, interestClicked);
            filterInterest = interestClickedDBValue;
        }
        else {
            if (interestClickedDBValue.contentEquals(filterInterest)) {
                interests.remove(interestClicked);
                interestClicked.setSelected(false);
                interests.add(interestClicked.getOrigArrayPosition(), interestClicked);
                filterInterest = "";
            } else {
                Interest previousInterest = interests.remove(0);
                previousInterest.setSelected(false);
                interests.add(previousInterest.getOrigArrayPosition(), previousInterest);

                interests.remove(interestClicked);
                interestClicked.setSelected(true);
                interests.add(0, interestClicked);
                filterInterest = interestClickedDBValue;
            }
        }

        interestsAdapter.notifyDataSetChanged();
        rvInterests.scrollToPosition(0);

        Log.d(TAG, "Interest picked = " + filterInterest);

        //ToDo Call method to refresh Discover UI
        if(currentUser.getLocation()!=null && currentUser.getLocation().size() > 0) {
            getdiscoverUsers();
        }
        else {
            repeatGetDiscoverUsers();
        }
    }

    private void repeatGetDiscoverUsers() {
        handler.removeCallbacksAndMessages(null);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if(currentUser.getLocation()!=null && currentUser.getLocation().size() > 0) {
                    getdiscoverUsers();
                }
                else {
                    repeatGetDiscoverUsers();
                }
            }
        }, 2000);

    }

    private void getCurrentLocation() {
        locationUtilInstance.startLocationUpdates(this,locationUtilsCallback);
    }

    public void getdiscoverUsers() {
        rippleBackground.startRippleAnimation();
        ivDiscoverUserIcon.setVisibility(View.VISIBLE);

        //Clear the list each time discover menu button is clicked as user list will change based on who is nearby
        if (!nearbyUsers.isEmpty()) {
            nearbyUsers.clear();
            adapter.notifyDataSetChanged();
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        double searchRadius = pref.getInt("radius", 200);

        //get filterInterests
        final ArrayList<String> filterInterests = new ArrayList<>();
        if (!filterInterest.isEmpty()) {
            filterInterests.add(filterInterest);
        }

        unFilteredNearbyUsersCount = 0;
        filteredNearbyUsersCount = 0;

        discoverFirebaseDas.getNearbyUsers(currentUser, searchRadius, new DiscoverFirebaseDas.OnCompletionListener() {
            @Override
            public void onNearbyUserListReceived(ArrayList<String> userIdList) {
                if (userIdList != null) {
                    unFilteredNearbyUsersCount = userIdList.size();
                    for (String userId : userIdList) {
                        discoverFirebaseDas.getUser(userId, new DiscoverFirebaseDas.OnCompletionListener() {
                            @Override
                            public void onUserReceived(User user) {
                                Log.d(TAG, "Checking if user " + user.getName() + " matches filters");

                                //check if user matches filters

                                boolean isDiscoverable = user.getDiscoverable();

                                boolean correctTimestamp = false;
                                Date currentTimestamp = new Date();
                                if (currentTimestamp.getTime() - user.getTimestamp().getTime() < (TIME_USER_LAST_ACTIVE * 60000)) {
                                    correctTimestamp = true;
                                }

                                boolean interestMatch = false;
                                Log.d(TAG, user.getName() + ", user interests =" + user.getInterests().toString());
                                Log.d(TAG, user.getName() + ", filter interests =" + filterInterests.toString());
                                if (filterInterests.size() > 0) {
                                    if (user.getInterests() != null && user.getInterests().size() > 0) {
                                        ArrayList<String> filterInterestsCopy = new ArrayList<String>(filterInterests);
                                        filterInterestsCopy.retainAll(user.getInterests());
                                        if (filterInterestsCopy.size() > 0) interestMatch = true;
                                        Log.d(TAG, user.getName() + ", common interests =" + filterInterestsCopy.toString());
                                    }
                                }
                                else {
                                    interestMatch = true;
                                }

                                if (isDiscoverable && correctTimestamp && interestMatch) {
                                    //find distance from AppUser
                                    float[] results = new float[1];
                                    Location.distanceBetween(currentUser.getLocation().get(0), currentUser.getLocation().get(1),
                                            user.getLocation().get(0), user.getLocation().get(1), results);
                                    double dInMtr = Double.parseDouble("" + results[0]);

                                    nearbyUsers.add(new DiscoverUser(user, dInMtr, filterInterests));
                                    Log.d(TAG, "Added " + user.getName() + "to recyclerview");
                                }

                                ++filteredNearbyUsersCount;
                                checkAllNearbyUsers(unFilteredNearbyUsersCount, filteredNearbyUsersCount);
                                Log.d(TAG, "unFilteredNearbyUsersCount=" + unFilteredNearbyUsersCount + ", filteredNearbyUsersCount=" + filteredNearbyUsersCount);
                            }
                        });
                    }
                }
                else {
                    displayMsgRepeatDiscover();
                }
            }
        });
    }

    private void checkAllNearbyUsers(int checkUnFilteredNearbyUsersCount, int checkFilteredNearbyUsersCount) {
        Log.d(TAG, "checkUnFilteredNearbyUsersCount=" + checkUnFilteredNearbyUsersCount + ", checkFilteredNearbyUsersCount=" + checkFilteredNearbyUsersCount);
        if (checkUnFilteredNearbyUsersCount == checkFilteredNearbyUsersCount) {
            if (nearbyUsers.size() > 0) {
                adapter.notifyDataSetChanged();
                srlDiscoverContainer.setRefreshing(false);
                rippleBackground.stopRippleAnimation();
                ivDiscoverUserIcon.setVisibility(View.GONE);
            } else {
                displayMsgRepeatDiscover();
            }
        }
    }

    private void displayMsgRepeatDiscover() {
        //TODO Show message in snackbar
        Toast.makeText(DiscoverActivity.this, "Found nobody. Trying increasing search radius.", Toast.LENGTH_SHORT).show();
        srlDiscoverContainer.setRefreshing(false);
        repeatGetDiscoverUsers();
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

        switch (id) {
            case R.id.menu_action_notifications:
                startActivity(new Intent(this, NotificationsActivity.class));
                return true;
            case R.id.menu_action_messages:
                startActivity(new Intent(this, MessagesListActivity.class));
                return true;
            case R.id.menu_action_edit_profile:
                Intent intent = new Intent(this,EditProfileActivity.class);
                intent.putExtra("userId",Profile.getCurrentProfile().getId());
                startActivity(intent);
                return true;
            case R.id.menu_action_settings:
                FragmentManager fm = getSupportFragmentManager();
                SettingsFragment settingsDialogFrament = SettingsFragment.newInstance();
                settingsDialogFrament.show(fm, "fragment_settings");
                return true;
            case R.id.menu_action_logout:
                Toast.makeText(this, "User logged out ", Toast.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        handler.removeCallbacksAndMessages(null);
        locationUtilInstance.stopLocationUpdates(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
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
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();

            if(location!=null) {
                ArrayList<Double> locationList = new ArrayList<>();
                locationList.add(location.getLatitude());
                locationList.add(location.getLongitude());
                Log.d(TAG, "Updating user " + Profile.getCurrentProfile().getId() + " location with " + locationList.toString());
                userFirebaseDas.updateUserLocation(profile.getId(), locationList);

                if(currentUser!=null && (currentUser.getLocation()==null || currentUser.getLocation().size() == 0)) {
                    userFirebaseDas.getUser(profile.getId(), new UserFirebaseDas.OnCompletionListener() {
                        @Override
                        public void onUserReceived(User user) {
                            currentUser = user;
                            Log.d(TAG, "in onUserReceived");
                            Log.d(TAG, currentUser.toString());
                            if (currentUser.getLocation() != null && currentUser.getLocation().size() > 0) {
                                getdiscoverUsers();
                            } else {
                                repeatGetDiscoverUsers();
                            }
                        }
                    });
                }
            }
        }
    };
}
