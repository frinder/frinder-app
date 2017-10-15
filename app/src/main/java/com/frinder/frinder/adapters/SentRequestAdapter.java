package com.frinder.frinder.adapters;

import android.content.Context;

import com.frinder.frinder.model.Request;

import java.util.List;

public class SentRequestAdapter extends RequestsAdapter {

    public SentRequestAdapter(Context context, List<Request> requests) {
        super(context, requests);
    }

    String getUserId(Request request) {
        return request.receiverId;
    }

}
