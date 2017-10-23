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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class UserFirebaseDas {
    FirebaseFirestore db;
    FirebaseApp app;
    private static final String TAG = "UserFirebaseDAS";
    private Context context;

    public UserFirebaseDas(Context context) {
        this.context = context;
        this.app = FirebaseApp.initializeApp(context);
        this.db = FirebaseFirestore.getInstance();
    }

    public void updateUserLocation(String id, ArrayList location) {
        DocumentReference userRef = db.collection("users").document(id);
        Map<String, Object> userData = new HashMap();
        userData.put("location",location);
        userData.put("timestamp",new Date());
        userRef.update(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "LocationUpdate: DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "LocationUpdate: Error updating document", e);
                    }
                });
    }

    public void addUser(final User user) {
        Map<String, Object> usr = convertToFirebaseObject(user);
        db.collection("users").document(user.getUid())
                .set(usr)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "AddUser: DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "AddUser: Error writing document", e);
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
                        Log.d(TAG, "GetUser: DocumentSnapshot data: " + task.getResult().getData());
                        if (task.getResult() != null && task.getResult().getData() != null) {
                            User user = convertFromFirebaseObject(task.getResult().getData());
                            Log.d(TAG, user.toString());
                            listener.onUserReceived(user);
                            return;
                        }
                    } else {
                        Log.d(TAG, "GetUser: No such document");
                    }
                } else {
                    Log.d(TAG, "GetUser: get failed with ", task.getException());
                }
                listener.onUserReceived(null);
            }
        });
    }

    public void updateUserDescAndInterests(String userId, String aboutMe, HashMap<String, Boolean> interests) {
        Map<String,Object> userData = new HashMap<>();
        userData.put("desc",aboutMe);
        userData.put("interests",interests);

        DocumentReference userRef = db.collection("users").document(userId);
        userRef.update(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "UpdateDescAndInterests: DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "UpdateDescAndInterests: Error updating document", e);
                    }
                });
    }

    public void updateUserToken(String id, String token) {
        if (id == null || token == null) {
            Log.e(TAG, "Skipping updating token since either the user or token is null");
            return;
        }
        DocumentReference userRef = db.collection("users").document(id);
        Map<String, Object> userData = new HashMap();
        userData.put(Constants.USER_COLUMN_TOKEN, token);
        userRef.update(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "UpdateDiscoverability: DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "UpdateDiscoverability: Error updating document", e);
                    }
                });
    }

    public void updateUserDiscoverability(String id, Boolean isDiscoverable) {
        DocumentReference userRef = db.collection("users").document(id);
        Map<String, Object> userData = new HashMap();
        userData.put("discoverable", isDiscoverable);
        userData.put("timestamp",new Date());
        userRef.update(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "UpdateDiscoverability: DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "UpdateDiscoverability: Error updating document", e);
                    }
                });
    }

    public static class OnCompletionListener {
        public void onUserReceived(User user) {
            // override if required
        }

 /*       public void onUsersReceived(ArrayList<User> users) {
            // override if required
        }

        public void onDiscoverUsersReceived(ArrayList<DiscoverUser> users) {
            // override if required
        }*/
    }

    @NonNull
    private Map<String, Object> convertToFirebaseObject(User user) {
        Map<String, Object> usr = new HashMap<>();
        usr.put(Constants.USER_COLUMN_ID, user.getUid());
        usr.put(Constants.USER_COLUMN_NAME, user.getName());
        usr.put(Constants.USER_COLUMN_EMAIL, user.getEmail());
        usr.put(Constants.USER_COLUMN_GENDER, user.getGender());
        //TODO get from fb
        usr.put(Constants.USER_COLUMN_AGE, 25);
        //TODO get from user, via new activty
//        usr.put(Constants.USER_COLUMN_DESC, "from SJ");

        //TODO get from user, via new activty
/*        List<String> interests = new ArrayList<>();
        interests.add("Movies");
        interests.add("Football");
        usr.put(Constants.USER_COLUMN_INTERESTS, interests);*/
        usr.put(Constants.USER_COLUMN_DISCOVERABLE, user.getDiscoverable());
        usr.put(Constants.USER_COLUMN_LOCATION, user.getLocation());
        usr.put(Constants.USER_COLUMN_TIMESTAMP, new Date());
        usr.put(Constants.USER_COLUMN_PROFILE_PIC_URL, user.getProfilePicUrl());
        usr.put(Constants.USER_COLUMN_LINK_URL, user.getLinkUrl());
        if (user.getToken() != null) {
            usr.put(Constants.USER_COLUMN_TOKEN, user.getToken());
        }
        return usr;
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
        if (usr.containsKey(Constants.USER_COLUMN_TOKEN)) {
            user.setToken((String) usr.get(Constants.USER_COLUMN_TOKEN));
        }
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