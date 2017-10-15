package com.frinder.frinder.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frinder.frinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class NoRequestsFragment extends Fragment {

    private static final String ARGS_MESSAGE = "ARGS_MESSAGE";
    private Unbinder mUnbinder;
    private String mMessage;

    @BindView(R.id.tvMessage)
    TextView tvMessage;

    public static NoRequestsFragment newInstance() {
        return newInstance(null);
    }

    public static NoRequestsFragment newInstance(String message) {
        Bundle args = new Bundle();
        args.putString(ARGS_MESSAGE, message);
        NoRequestsFragment fragment = new NoRequestsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public NoRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessage = getArguments().getString(ARGS_MESSAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_requests, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        if (!TextUtils.isEmpty(mMessage)) {
            tvMessage.setText(mMessage);
        }

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}
