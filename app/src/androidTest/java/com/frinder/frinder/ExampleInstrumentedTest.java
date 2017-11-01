package com.frinder.frinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    final String TAG = "DBTest";
    FirebaseApp app;
    FirebaseFirestore db;
    Context appContext;

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getTargetContext();
        app = FirebaseApp.initializeApp(InstrumentationRegistry.getTargetContext());
        db = FirebaseFirestore.getInstance();

        assertEquals("com.frinder.frinder", appContext.getPackageName());
    }

    @Test
    public void geoFireTest() throws Exception {
        useAppContext();

        String GEO_FIRE_REF = "https://projectfrinder.firebaseio.com/users_location";
        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance(app).getReferenceFromUrl(GEO_FIRE_REF));

        final User user = new User();
        user.setUid("117993622291470");
        user.setName("Mallika Viswas");
        user.setAge(25);
        user.setDesc("Something about me");
        user.setInterests(new ArrayList<String>(Arrays.asList("ArtsCrafts", "Auto", "BoardGames")));
        user.setGender("female");
        user.setDiscoverable(true);
        user.setEmail("");
        user.setLinkUrl("");
        user.setProfilePicUrl("");
        user.setTimestamp(new Date());
        user.setLocation(new ArrayList<Double>(Arrays.asList(37.319525, -122.000640)));

        final ArrayList<String> userIds = new ArrayList<>();
        double searchRadius = 200;
        GeoLocation appUserLocation = new GeoLocation(user.getLocation().get(0), user.getLocation().get(1));
        final GeoQuery geoQuery = geoFire.queryAtLocation(appUserLocation, searchRadius);

        final CountDownLatch latch = new CountDownLatch(1);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //Log.d(TAG, String.format("User %s located at [%f,%f]", key, location.latitude, location.longitude));
                if (!key.contentEquals(user.getUid())) {
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
                geoQuery.removeAllListeners();
                latch.countDown();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                //Log.d(TAG, "There was an error with this query: " + error);
                //listener.onNearbyUserListReceived(null);
            }
        });

        latch.await();
        Log.d(TAG, "users: " + userIds.size());
        Log.d(TAG, userIds.toString());
        assertTrue(userIds.size() > 0);
    }

    @Test
    public void getUserWithFilters() throws Exception {
        Query query;

        useAppContext();

        final CountDownLatch latch = new CountDownLatch(1);
        Date dateNow = new Date();
        long checkTime = dateNow.getTime() - 86400000; //Get all users with timestamp within last 24 hours
        Date checkTimeStamp = new Date(checkTime);
        Log.d(TAG, "check Timestamp = " + checkTimeStamp.toString());

        query = db.collection("users")
                .whereEqualTo("discoverable", true)
                .whereGreaterThanOrEqualTo("timestamp", checkTimeStamp);
                //.whereEqualTo("id", "117993622291470")


                //.whereEqualTo("interests.BoardGames", true)
        /*query = db.collection("users")
                .whereEqualTo("id", "117993622291470")
                .whereEqualTo("discoverable", true)
                .whereGreaterThanOrEqualTo("timestamp", checkTimeStamp);*/

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        if (documents.size() > 0) {
                            Log.d(TAG, "got some result -> " + documents.get(0).getData().toString());
                        }
                        else {Log.d(TAG, "documents size is 0");}
                    }
                    else {
                        Log.d(TAG, "querySnapshot is empty");
                    }
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                latch.countDown();
            }
        });

        latch.await();
        assertTrue(true);
    }

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

    @Test
    public void checkDate() {
        Date d1 = new Date();
        Log.d("DateTest", "Date = " + d1.toString());
        long t = d1.getTime() - 900000;
        Date d2 = new Date(t);
        Log.d("DateTest", "Date = " + d2.toString());

        assertTrue(true);
    }
}
