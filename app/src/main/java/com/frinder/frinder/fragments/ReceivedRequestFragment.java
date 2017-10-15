package com.frinder.frinder.fragments;

import android.content.Context;

import com.frinder.frinder.adapters.ReceivedRequestAdapter;
import com.frinder.frinder.adapters.RequestsAdapter;
import com.frinder.frinder.dataaccess.RequestFirebaseDas;
import com.frinder.frinder.model.Request;

import java.util.List;

public class ReceivedRequestFragment extends RequestFragment {

    public static ReceivedRequestFragment newInstance() {
        ReceivedRequestFragment fragment = new ReceivedRequestFragment();
        return fragment;
    }

    @Override
    RequestsAdapter createAdapater(List<Request> requests) {
        return new ReceivedRequestAdapter(getContext(), requests);
    }

    @Override
    void getRequests(Context context, RequestFirebaseDas.OnCompletionListener listener) {
        RequestFirebaseDas das = new RequestFirebaseDas(context);
        das.getReceivedRequests(listener);
    }

}
