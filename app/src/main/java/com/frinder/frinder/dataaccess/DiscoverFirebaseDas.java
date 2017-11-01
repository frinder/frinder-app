package com.frinder.frinder.dataaccess;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.frinder.frinder.model.DiscoverUser;
import com.frinder.frinder.model.User;
import com.frinder.frinder.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class DiscoverFirebaseDas {
    private Context context;
    private FirebaseFirestore db;
    private GeoFire geoFire;
    private FirebaseApp app;

    private static final String TAG = "DiscoverFirebaseDas";
    private static final String GEO_FIRE_REF = "https://projectfrinder.firebaseio.com/users_location";

    //This is used to filter nearby users to show users active/with timestamp within 15 minutes
    private final static long TIME_USER_LAST_ACTIVE = 86400000; //unit is milliseconds - 24 hours

    private ArrayList<DiscoverUser> nearbyUsers;

    public DiscoverFirebaseDas(Context context) {
        this.context = context;
        this.app = FirebaseApp.initializeApp(context);
        this.db = FirebaseFirestore.getInstance();
        this.geoFire = new GeoFire(FirebaseDatabase.getInstance(app).getReferenceFromUrl(GEO_FIRE_REF));
        nearbyUsers = new ArrayList<>();
    }

    //get all users and find nearby and apply filters in app
    public void getNearbyUsers(final User currentAppUser, final double searchRadius, final String filterInterest, final OnCompletionListener listener) {
        nearbyUsers.clear();

        // Use timestamp to check if users are currently active
        Date dateNow = new Date();
        //Log.d(TAG, "now Timestamp: " + dateNow.toString());
        long tCheck = dateNow.getTime() - TIME_USER_LAST_ACTIVE;
        final Date checkTimeStamp = new Date(tCheck);
        //Log.d(TAG, "check Timestamp: " + checkTimeStamp.toString());

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && !task.getResult().isEmpty() && !task.getResult().getDocuments().isEmpty()) {
                        for (DocumentSnapshot documentSnapshot: task.getResult().getDocuments()) {
                            User user = convertFromFirebaseObject(documentSnapshot.getData());

                            // Find distance from current user
                            float[] results = new float[1];
                            Location.distanceBetween(currentAppUser.getLocation().get(0), currentAppUser.getLocation().get(1),
                                    user.getLocation().get(0), user.getLocation().get(1), results);
                            double distanceInMtr = Double.parseDouble(Float.toString(results[0]));

                            if (!user.getId().contentEquals(currentAppUser.getUid())
                                    && distanceInMtr <= searchRadius
                                    && (filterInterest.isEmpty() || user.getInterests().contains(filterInterest))
                                    && user.getDiscoverable()
                                    && user.getTimestamp().getTime() >= checkTimeStamp.getTime()) {

                                ArrayList<String> commonInterests = new ArrayList<String>(user.getInterests());
                                commonInterests.retainAll(currentAppUser.getInterests());

                                //Log.d(TAG, "User timestamp: " + user.getTimestamp());

                                nearbyUsers.add(new DiscoverUser(user, false, distanceInMtr, commonInterests));
                            }
                        }

                        if (nearbyUsers.size() > 0) {
                            Log.d(TAG, "Nearby filtered users: " + nearbyUsers.size());
                            listener.onNearbyUserListReceived(nearbyUsers);
                            return;
                        }
                    }
                }
                Log.d(TAG, "Get allUsers failed. Error: " + task.getException());
                listener.onNearbyUserListReceived(null);
            }
        });
    }

    public static class OnCompletionListener {
        public void onNearbyUserListReceived(ArrayList<DiscoverUser> nearbyUsers) {
            // override if required
        }
    }

    @NonNull
    private User convertFromFirebaseObject(Map<String, Object> usr) {
        User user = new User();
        user.setUid((String) usr.get(Constants.USER_COLUMN_ID));
        user.setName((String) usr.get(Constants.USER_COLUMN_NAME));
        user.setEmail((String) usr.get(Constants.USER_COLUMN_EMAIL));
        user.setGender((String) usr.get(Constants.USER_COLUMN_GENDER));
        user.setAge(((Long) usr.get(Constants.USER_COLUMN_AGE)).intValue());
        user.setDesc((String) usr.get(Constants.USER_COLUMN_DESC));
        user.setDiscoverable((Boolean) usr.get(Constants.USER_COLUMN_DISCOVERABLE));
        Map<String, Boolean> interests = (Map<String, Boolean>) usr.get(Constants.USER_COLUMN_INTERESTS);
        ArrayList<String> interestList = new ArrayList<>();
        if(interests!=null) {
            for (String interest : interests.keySet()) {
                interestList.add(interest);
            }
        }
        user.setInterests(interestList);
        ArrayList location = (ArrayList) usr.get(Constants.USER_COLUMN_LOCATION);
        user.setLocation(location);
        user.setTimestamp((Date) usr.get(Constants.USER_COLUMN_TIMESTAMP));
        user.setProfilePicUrl((String) usr.get(Constants.USER_COLUMN_PROFILE_PIC_URL));
        user.setLinkUrl((String) usr.get(Constants.USER_COLUMN_LINK_URL));
        return user;
    }
}
