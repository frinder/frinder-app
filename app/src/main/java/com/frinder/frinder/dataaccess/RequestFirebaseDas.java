package com.frinder.frinder.dataaccess;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.Profile;
import com.frinder.frinder.model.Request;
import com.frinder.frinder.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestFirebaseDas {

    FirebaseFirestore db;
    private static final String TAG = "RequestFirebaseDAS";
    private Context context;

    public RequestFirebaseDas(Context context) {
        this.context = context;
        FirebaseApp.initializeApp(context);
        db = FirebaseFirestore.getInstance();
    }

    public void addRequest(Request request){
        Map<String, Object> requestMap = convertToFirebaseObject(request);
        Task task = getCollection()
                .document()
                .set(requestMap);
        setListeners(task);
    }

    public void deleteRequest(Request request){
        DocumentReference document = getDocument(request);
        if (document == null) {
            Log.w(TAG, "Cannot find request to be deleted");
            return;
        }
        setListeners(document.delete());
    }

    public void updateUnread(Request request, boolean unread) {
        DocumentReference document = getDocument(request);
        if (document == null) {
            Log.w(TAG, "Cannot find request to be updated");
            return;
        }
        Task task  = document.update(Constants.REQUEST_COLUMN_UNREAD , unread);
        setListeners(task);
    }

    public void updateAccepted(Request request, boolean accepted) {
        DocumentReference document = getDocument(request);
        if (document == null) {
            Log.w(TAG, "Cannot find request to be updated");
            return;
        }
        Task task  = document.update(Constants.REQUEST_COLUMN_ACCEPTED , accepted);
        setListeners(task);
    }

    public void updateOneTimeLocation(Request request, boolean locationShare) {
        DocumentReference document = getDocument(request);
        if (document == null) {
            Log.w(TAG, "Cannot find request to be updated");
            return;
        }
        Task task  = document.update(Constants.REQUEST_COLUMN_LOCATION_SHARE , locationShare);
        setListeners(task);
    }

    private CollectionReference getCollection() {
        return db.collection("requests");
    }

    private DocumentReference getDocument(Request request) {
        if (request.uid == null) return null;

        return getCollection().document(request.uid);
    }

    private void setListeners(Task task) {
        // TODO: Show error messages based on this
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });
    }

    public void getSentRequests(final OnCompletionListener listener) {
        getResults(getSentRequestsQuery(false), listener);
    }

    public void getReceivedRequests(final OnCompletionListener listener) {
        getResults(getReceivedRequestsQuery(false), listener);
    }

    public void getAcceptedRequests(final OnCompletionListener listener) {
        OnCompletionListener inListener = new OnCompletionListener() {

            private boolean mFirstReceived = false;
            private ArrayList<Request> mRequests;

            @Override
            public void onRequestsReceived(ArrayList<Request> requests) {
                // TODO: Can this be called from different thread? If so, add synchronization logic
                if (!mFirstReceived) {
                    mRequests = new ArrayList<>(requests);
                    mFirstReceived = true;
                } else {
                    mRequests.addAll(requests);
                    listener.onRequestsReceived(mRequests);
                }
            }
        };
        getResults(getSentRequestsQuery(true), inListener);
        getResults(getReceivedRequestsQuery(true), inListener);
    }

    private Query getSentRequestsQuery(boolean accepted) {
        return db.collection("requests")
                .whereEqualTo(Constants.REQUEST_COLUMN_SENDER_ID, Profile.getCurrentProfile().getId())
                .whereEqualTo("accepted", accepted);
    }

    private Query getReceivedRequestsQuery(boolean accepted) {
        return db.collection("requests")
                .whereEqualTo(Constants.REQUEST_COLUMN_RECEIVER_ID, Profile.getCurrentProfile().getId())
                .whereEqualTo("accepted", accepted);
    }

    private void getResults(Query query, final OnCompletionListener listener) {
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Request> requestList = parseRequestList(task.getResult());
                            listener.onRequestsReceived(requestList);
                            return;
                        }

                        Log.d(TAG, "Error getting documents: ", task.getException());
                        listener.onRequestsReceived(null);
                    }
                });
    }

    public static abstract class OnCompletionListener {
        abstract public void onRequestsReceived(ArrayList<Request> requests);
    }

    @NonNull
    private static Map<String, Object> convertToFirebaseObject(Request request) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(Constants.REQUEST_COLUMN_ACCEPTED, request.accepted);
        requestMap.put(Constants.REQUEST_COLUMN_ACCEPTED_TIMESTAMP, request.acceptedTimestamp);
        requestMap.put(Constants.REQUEST_COLUMN_RECEIVER_ID, request.receiverId);
        requestMap.put(Constants.REQUEST_COLUMN_SENDER_ID, request.senderId);
        requestMap.put(Constants.REQUEST_COLUMN_SENT_TIMESTAMP, request.sentTimestamp);
        requestMap.put(Constants.REQUEST_COLUMN_UNREAD, request.unread);
        requestMap.put(Constants.REQUEST_COLUMN_LOCATION_SHARE, request.locationShare);
        return requestMap;
    }

    @NonNull
    private static Request convertFromFirebaseObject(String id, Map<String, Object> requestMap) {
        Request request = new Request();
        request.uid = id;
        request.accepted = (boolean)requestMap.get(Constants.REQUEST_COLUMN_ACCEPTED);
        request.acceptedTimestamp = (Date)requestMap.get(Constants.REQUEST_COLUMN_ACCEPTED_TIMESTAMP);
        request.receiverId = (String)requestMap.get(Constants.REQUEST_COLUMN_RECEIVER_ID);
        request.senderId = (String)requestMap.get(Constants.REQUEST_COLUMN_SENDER_ID);
        request.sentTimestamp = (Date)requestMap.get(Constants.REQUEST_COLUMN_SENT_TIMESTAMP);
        request.unread = (boolean)requestMap.get(Constants.REQUEST_COLUMN_UNREAD);
        request.locationShare = (boolean) requestMap.get(Constants.REQUEST_COLUMN_LOCATION_SHARE);
        return request;
    }

    @NonNull
    private static ArrayList<Request> parseRequestList(QuerySnapshot snapshot) {
        ArrayList<Request> requestList = new ArrayList<Request>();
        for (DocumentSnapshot document : snapshot) {
            if (document.exists()) {
                Request request = convertFromFirebaseObject(document.getId(), document.getData());
                requestList.add(request);
            }
            else {
                Log.d(TAG, "No such document");
            }
        }
        return requestList;
    }
}
