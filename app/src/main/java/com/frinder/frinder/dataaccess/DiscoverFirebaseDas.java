package com.frinder.frinder.dataaccess;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.frinder.frinder.model.User;
import com.frinder.frinder.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class DiscoverFirebaseDas {
    FirebaseFirestore db;
    GeoFire geoFire;
    FirebaseApp app;
    private static final String TAG = "DiscoverFirebaseDAS";
    private static final String GEO_FIRE_REF = "https://projectfrinder.firebaseio.com/users_location";
    private Context context;

    //ToDo Mallika - Remove this constant when User location is current and user discoverability has been handled
    //This is used to filter nearby users to show users active/with timestamp within 15 minutes
    private final static long TIME_USER_LAST_ACTIVE = 15; //unit is minutes

    public DiscoverFirebaseDas(Context context) {
        this.context = context;
        this.app = FirebaseApp.initializeApp(context);
        this.db = FirebaseFirestore.getInstance();
        this.geoFire = new GeoFire(FirebaseDatabase.getInstance(app).getReferenceFromUrl(GEO_FIRE_REF));
    }

    public void getUser(String id, final OnCompletionListener listener) {
        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //Log.d(TAG, "GetUser: DocumentSnapshot data: " + task.getResult().getData());
                        if (task.getResult() != null && task.getResult().getData() != null) {
                            User user = convertFromFirebaseObject(task.getResult().getData());
                            //Log.d(TAG, user.toString());
                            listener.onUserReceived(user);
                            return;
                        }
                    } else {
                        //Log.d(TAG, "GetUser: No such document");
                        listener.onUserReceived(null);
                    }
                } else {
                    //Log.d(TAG, "GetUser: get failed with ", task.getException());
                    listener.onUserReceived(null);
                }
                listener.onUserReceived(null);
            }
        });
    }

    //TODO
    //get nearby users using Geoquery and then appy filters to result
    public void getNearbyUsers(final User currentAppUser, double searchRadius, final OnCompletionListener listener){
        final ArrayList<String> userIds = new ArrayList<>();

        final GeoLocation appUserLocation = new GeoLocation(currentAppUser.getLocation().get(0), currentAppUser.getLocation().get(1));

        final GeoQuery geoQuery = geoFire.queryAtLocation(appUserLocation, searchRadius);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //Log.d(TAG, String.format("User %s located at [%f,%f]", key, location.latitude, location.longitude));
                if (!key.contentEquals(currentAppUser.getUid())) {
                    userIds.add(key);
                }
            }

            @Override
            public void onKeyExited(String key) {
                //Not monitoring key existing radius
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //Not monitoring key moving in radius
            }

            @Override
            public void onGeoQueryReady() {
                //All initial data has been loaded
                //Cancel geoQuery listeners. A fresh query will be done whenever the user requires nearby users
                geoQuery.removeAllListeners();
                //Log.d(TAG, "Users nearby = " + userIds.size());
                if (userIds.size() > 0) {
                    //getUsers(userIds, listener);
                    listener.onNearbyUserListReceived(userIds);
                }
                else {
                    listener.onNearbyUserListReceived(null);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                //Log.d(TAG, "There was an error with this query: " + error);
                listener.onNearbyUserListReceived(null);
            }
        });
    }

    public static class OnCompletionListener {
        /*public void onNearbyUserListReceived(ArrayList<User> users) {
            // override if required
        }*/

        public void onNearbyUserListReceived(ArrayList<String> nearbyUserIDs) {
            // override if required
        }


        public void onUserReceived(User user) {
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
