package com.frinder.frinder.fragments;


import android.content.Context;

import com.frinder.frinder.adapters.RequestsAdapter;
import com.frinder.frinder.adapters.SentRequestAdapter;
import com.frinder.frinder.dataaccess.RequestFirebaseDas;
import com.frinder.frinder.model.Request;

import java.util.List;

public class SentRequestFragment extends RequestFragment {

    public static SentRequestFragment newInstance() {
        SentRequestFragment fragment = new SentRequestFragment();
        return fragment;
    }

    @Override
    RequestsAdapter createAdapater(List<Request> requests) {
        return new SentRequestAdapter(getContext(), requests);
    }

    @Override
    void getRequests(Context context, RequestFirebaseDas.OnCompletionListener listener) {
        RequestFirebaseDas das = new RequestFirebaseDas(context);
        das.getSentRequests(listener);
    }

    @Override
    boolean shouldRefetchOnUnreadUpdate() {
        return false;
    }

}
