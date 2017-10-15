package com.frinder.frinder.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frinder.frinder.R;
import com.frinder.frinder.adapters.RequestsAdapter;
import com.frinder.frinder.dataaccess.RequestFirebaseDas;
import com.frinder.frinder.model.Request;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class RequestFragment extends Fragment {

    private Unbinder mUnbinder;
    private ArrayList<Request> mRequests;
    private RequestsAdapter mAdapter;

    @BindView(R.id.rvRequests)
    RecyclerView rvRequests;

    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mRequests = new ArrayList<>();
        mAdapter = createAdapater(mRequests);
        rvRequests.setAdapter(mAdapter);
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));

        getRequests(getContext(), new RequestFirebaseDas.OnCompletionListener() {
            @Override
            public void onRequestsReceived(ArrayList<Request> requests) {
                mRequests.addAll(requests);
                mAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    abstract RequestsAdapter createAdapater(List<Request> requests);
    abstract void getRequests(Context context, RequestFirebaseDas.OnCompletionListener listener);
}
