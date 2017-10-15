package com.frinder.frinder.adapters;

import android.content.Context;

import com.facebook.Profile;
import com.frinder.frinder.model.Request;

import java.util.List;

public class AcceptedRequestAdapter extends RequestsAdapter {

    public AcceptedRequestAdapter(Context context, List<Request> requests) {
        super(context, requests);
    }

    String getUserId(Request request) {
        String currentProfileId = Profile.getCurrentProfile().getId();
        return (request.senderId.equals(currentProfileId)) ? request.receiverId : request.senderId;
    }

}
