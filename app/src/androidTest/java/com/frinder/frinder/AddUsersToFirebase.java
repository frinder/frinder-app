package com.frinder.frinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.frinder.frinder.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * This script is to add 10 users into the Firebase Users DB
 * All the users are close to Mallika's current position
 * If you change any of the values and run test again, Users will get updated.
 */
@RunWith(AndroidJUnit4.class)
public class AddUsersToFirebase {
    FirebaseFirestore db;
    Object syncObject;

    @Test
    public void add10Users() throws Exception {
        Map<String, Object> firebaseUser;
        String userId, userName, userEmail, userGender, userDesc, userLinkUrl, userProfilePicUrl;
        int userAge;
        ArrayList<String> userInterests;
        ArrayList<Double> userLocation;
        Date userTimestamp;

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        FirebaseApp.initializeApp(appContext);
        db = FirebaseFirestore.getInstance();
        Assert.assertTrue("Got Firestore db instance", db != null);
        System.out.println("Got Firestore db instance");

        syncObject = new Object();


        //User 1
        System.out.println("User 1");

        userId = "6106331625502695";
        userName = "Test User A";
        userEmail = "test_nqfqmut_a@tfbnw.net";
        userGender = "female";
        userProfilePicUrl = "";
        userLinkUrl = "";
        userAge = 30;
        userDesc = "Fun loving";
        userInterests = new ArrayList<>(Arrays.asList("Basketball", "Music"));
        userLocation = new ArrayList<>(Arrays.asList(37.319747, -121.999864));
        userTimestamp = new Date();

        addUserToFirebase( userId, userName, userEmail, userGender, userDesc, userAge, userInterests, userLocation, userTimestamp, userProfilePicUrl, userLinkUrl);

        //User 2
        System.out.println("User 2");

        userId = "2281121703684697";
        userName = "Test User2";
        userEmail = "testuser2@gmail.com";
        userGender = "female";
        userProfilePicUrl = "";
        userLinkUrl = "";
        userAge = 22;
        userDesc = "";
        userInterests = new ArrayList<>(Arrays.asList("Music"));
        userLocation = new ArrayList<>(Arrays.asList(37.320180, -121.999856));
        userTimestamp = new Date();

        addUserToFirebase( userId, userName, userEmail, userGender, userDesc, userAge, userInterests, userLocation, userTimestamp, userProfilePicUrl, userLinkUrl);

        //User 3
        System.out.println("User 3");

        userId = "0846694724902639";
        userName = "Test User3";
        userEmail = "testuser3@gmail.com";
        userGender = "female";
        userProfilePicUrl = "";
        userLinkUrl = "";
        userAge = 35;
        userDesc = "Love trying out new restaurants";
        userInterests = new ArrayList<>(Arrays.asList("Baking", "Bicycling"));
        userLocation = new ArrayList<>(Arrays.asList(37.319743, -122.000134));
        userTimestamp = new Date();

        addUserToFirebase( userId, userName, userEmail, userGender, userDesc, userAge, userInterests, userLocation, userTimestamp, userProfilePicUrl, userLinkUrl);

        //User 4
        System.out.println("User 4");

        userId = "8669819463207706";
        userName = "Test User4";
        userEmail = "testuser4@gmail.com";
        userGender = "male";
        userProfilePicUrl = "";
        userLinkUrl = "";
        userAge = 26;
        userDesc = "";
        userInterests = new ArrayList<>(Arrays.asList("Running"));
        userLocation = new ArrayList<>(Arrays.asList(37.319829, -121.999649));
        userTimestamp = new Date();

        addUserToFirebase( userId, userName, userEmail, userGender, userDesc, userAge, userInterests, userLocation, userTimestamp, userProfilePicUrl, userLinkUrl);

        //User 5
        System.out.println("User 5");

        userId = "5297555853828119";
        userName = "Test User5";
        userEmail = "testuser5@gmail.com";
        userGender = "male";
        userProfilePicUrl = "";
        userLinkUrl = "";
        userAge = 30;
        userDesc = "Nothing to say";
        userInterests = new ArrayList<>();
        userLocation = new ArrayList<>(Arrays.asList(37.320102, -121.999710));
        userTimestamp = new Date();

        addUserToFirebase( userId, userName, userEmail, userGender, userDesc, userAge, userInterests, userLocation, userTimestamp, userProfilePicUrl, userLinkUrl);

        //User 6
        System.out.println("User 6");

        userId = "1013849523389352";
        userName = "Test User6";
        userEmail = "testuser6@gmail.com";
        userGender = "male";
        userProfilePicUrl = "";
        userLinkUrl = "";
        userAge = 43;
        userDesc = "An architect. Like outdoor activities, camping, sports";
        userInterests = new ArrayList<>(Arrays.asList("Tennis", "Swimming"));
        userLocation = new ArrayList<>(Arrays.asList(37.320010, -122.000123));
        userTimestamp = new Date();

        addUserToFirebase( userId, userName, userEmail, userGender, userDesc, userAge, userInterests, userLocation, userTimestamp, userProfilePicUrl, userLinkUrl);

        //User 7
        System.out.println("User 7");

        userId = "9872303333261654";
        userName = "Test User7";
        userEmail = "testuser7@gmail.com";
        userGender = "female";
        userProfilePicUrl = "";
        userLinkUrl = "";
        userAge = 38;
        userDesc = "Software Engineer";
        userInterests = new ArrayList<>(Arrays.asList("Music", "Books"));
        userLocation = new ArrayList<>(Arrays.asList(37.319943, -121.999840));
        userTimestamp = new Date();

        addUserToFirebase( userId, userName, userEmail, userGender, userDesc, userAge, userInterests, userLocation, userTimestamp, userProfilePicUrl, userLinkUrl);

        //User 8
        System.out.println("User 8");

        userId = "2096321138081588";
        userName = "Test User8";
        userEmail = "testuser8@gmail.com";
        userGender = "female";
        userProfilePicUrl = "";
        userLinkUrl = "";
        userAge = 24;
        userDesc = "from South Bay";
        userInterests = new ArrayList<>(Arrays.asList(""));
        userLocation = new ArrayList<>(Arrays.asList(37.320107, -121.999711));
        userTimestamp = new Date();

        addUserToFirebase( userId, userName, userEmail, userGender, userDesc, userAge, userInterests, userLocation, userTimestamp, userProfilePicUrl, userLinkUrl);

        //User 9
        System.out.println("User 9");

        userId = "8428107557021076";
        userName = "Test User9";
        userEmail = "testuser9@gmail.com";
        userGender = "male";
        userProfilePicUrl = "";
        userLinkUrl = "";
        userAge = 26;
        userDesc = "";
        userInterests = new ArrayList<>(Arrays.asList("Tennis", "Music"));
        userLocation = new ArrayList<>(Arrays.asList(37.320194, -121.998765));
        userTimestamp = new Date();

        addUserToFirebase( userId, userName, userEmail, userGender, userDesc, userAge, userInterests, userLocation, userTimestamp, userProfilePicUrl, userLinkUrl);

        //User 10
        System.out.println("User 10");

        userId = "0114436811185862";
        userName = "Test User10";
        userEmail = "testuser10@gmail.com";
        userGender = "male";
        userProfilePicUrl = "";
        userLinkUrl = "";
        userAge = 33;
        userDesc = "";
        userInterests = new ArrayList<>(Arrays.asList("Music", "Roadtrips"));
        userLocation = new ArrayList<>(Arrays.asList(37.318400, -121.999714));
        userTimestamp = new Date();

        addUserToFirebase( userId, userName, userEmail, userGender, userDesc, userAge, userInterests, userLocation, userTimestamp, userProfilePicUrl, userLinkUrl);
    }

    private void addUserToFirebase(String uid, String name, String email, String gender, String desc, int age, ArrayList<String> interests, ArrayList<Double> location, Date timestamp, String profilePicUrl, String linkUrl) throws InterruptedException {
        Map<String, Object> user = new HashMap<>();
        user.put(Constants.USER_COLUMN_ID, uid);
        user.put(Constants.USER_COLUMN_NAME, name);
        user.put(Constants.USER_COLUMN_EMAIL, email);
        user.put(Constants.USER_COLUMN_GENDER, gender);
        user.put(Constants.USER_COLUMN_AGE, age);
        user.put(Constants.USER_COLUMN_DESC, desc);
        user.put(Constants.USER_COLUMN_INTERESTS, interests);
        user.put(Constants.USER_COLUMN_LOCATION, location);
        user.put(Constants.USER_COLUMN_TIMESTAMP, timestamp);
        user.put(Constants.USER_COLUMN_PROFILE_PIC_URL, profilePicUrl);
        user.put(Constants.USER_COLUMN_LINK_URL, linkUrl);

        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Assert.assertTrue("User was added to Firestore", true);
                        System.out.println("User was ADDED to Firestore");
                        synchronized (syncObject){
                            syncObject.notify();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Assert.assertFalse("User was not added to Firestore", false);
                        System.out.println("User was NOT ADDED to Firestore");
                        synchronized (syncObject){
                            syncObject.notify();
                        }
                    }
                });

        synchronized (syncObject){
            syncObject.wait();
        }
    }
}