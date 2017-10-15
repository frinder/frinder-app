package com.frinder.frinder.dataaccess;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.frinder.frinder.model.User;
import com.frinder.frinder.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserFirebaseDas {
    FirebaseFirestore db;
    private static final String TAG = "UserFirebaseDAS";
    private Context context;

    public UserFirebaseDas(Context context) {
        this.context = context;
        FirebaseApp.initializeApp(context);
        db = FirebaseFirestore.getInstance();
    }

    public void addUser(User user){
        Map<String, Object> usr = convertToFirebaseObject(user);

        db.collection("users").document(user.getUid())
                .set(usr)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }



    public void getUser(String id, final OnCompletionListener listener) {
        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                        if(task.getResult() != null && task.getResult().getData() != null) {
                            User user = convertFromFirebaseObject(task.getResult().getData());
                            Log.d(TAG, user.toString());
                            listener.onUserReceived(user);
                            return;
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                listener.onUserReceived(null);
            }
        });
    }

    // Get all records in users table in Frinder Firebase Firestore
    public void getAllUsers(final OnCompletionListener listener) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<User> userList = new ArrayList<User>();
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document != null && document.exists()) {
                                    User user = convertFromFirebaseObject(document.getData());
                                    userList.add(user);
                                }
                                else {
                                    Log.d(TAG, "No such document");
                                }
                            }

                            listener.onUsersReceived(userList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onUsersReceived(null);
                        }
                    }
                });
    }


    public static class OnCompletionListener {
        public void onUserReceived(User user) {
            // override if required
        }

        public void onUsersReceived(ArrayList<User> users){
            // override if required
        }
    }

    @NonNull
    private Map<String, Object> convertToFirebaseObject(User user) {
        Map<String, Object> usr = new HashMap<>();
        usr.put(Constants.USER_COLUMN_ID, user.getUid());
        usr.put(Constants.USER_COLUMN_NAME, user.getName());
        usr.put(Constants.USER_COLUMN_EMAIL, user.getEmail());
        usr.put(Constants.USER_COLUMN_GENDER, user.getGender());
        //TODO get from fb
        usr.put(Constants.USER_COLUMN_AGE,25);
        //TODO get from user, via new activty
        usr.put(Constants.USER_COLUMN_DESC, "from SJ");

        //TODO get from user, via new activty
        List<String> interests = new ArrayList<>();
        interests.add("Movies");
        interests.add("Football");
        usr.put(Constants.USER_COLUMN_INTERESTS, interests);

        //TODO get from the phone location
        List<Double> location = new ArrayList<>();
        location.add(37.41);
        location.add(-121.87);
        usr.put(Constants.USER_COLUMN_LOCATION, location);

        usr.put(Constants.USER_COLUMN_TIMESTAMP, new Date());
        usr.put(Constants.USER_COLUMN_PROFILE_PIC_URL, user.getProfilePicUrl());
        usr.put(Constants.USER_COLUMN_LINK_URL, user.getLinkUrl());
        return usr;
    }

    @NonNull
    private User convertFromFirebaseObject(Map<String, Object> usr) {
        User user = new User();
        user.setUid((String) usr.get(Constants.USER_COLUMN_ID));
        user.setName((String) usr.get(Constants.USER_COLUMN_NAME));
        user.setEmail((String)usr.get(Constants.USER_COLUMN_EMAIL));
        user.setGender((String)usr.get(Constants.USER_COLUMN_GENDER));
        user.setAge(((Long) usr.get(Constants.USER_COLUMN_AGE)).intValue());
        user.setDesc((String)usr.get(Constants.USER_COLUMN_DESC));
        ArrayList interests = (ArrayList) usr.get(Constants.USER_COLUMN_INTERESTS);
        user.setInterests(interests);
        ArrayList location = (ArrayList) usr.get(Constants.USER_COLUMN_LOCATION);
        user.setLocation((Double[]) location.toArray(new Double[2]));
        user.setTimestamp((Date)usr.get(Constants.USER_COLUMN_TIMESTAMP));
        user.setProfilePicUrl((String)usr.get(Constants.USER_COLUMN_PROFILE_PIC_URL));
        user.setLinkUrl((String)usr.get(Constants.USER_COLUMN_LINK_URL));
        return user;
    }
}
