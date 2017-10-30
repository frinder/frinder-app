package com.frinder.frinder.fragments;

import android.content.Context;

import com.frinder.frinder.adapters.AcceptedRequestAdapter;
import com.frinder.frinder.adapters.RequestsAdapter;
import com.frinder.frinder.dataaccess.RequestFirebaseDas;
import com.frinder.frinder.model.Request;

import java.util.List;

public class AcceptedRequestFragment extends RequestFragment {

    public static AcceptedRequestFragment newInstance() {
        AcceptedRequestFragment fragment = new AcceptedRequestFragment();
        return fragment;
    }

    @Override
    RequestsAdapter createAdapater(List<Request> requests) {
        return new AcceptedRequestAdapter(getContext(), requests);
    }

    @Override
    void getRequests(Context context, RequestFirebaseDas.OnCompletionListener listener) {
        RequestFirebaseDas das = new RequestFirebaseDas(context);
        das.getAcceptedRequests(listener);
    }

    @Override
    boolean shouldRefetchOnUnreadUpdate() {
        return true;
    }

}
