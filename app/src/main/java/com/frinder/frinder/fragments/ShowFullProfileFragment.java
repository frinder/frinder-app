package com.frinder.frinder.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.User;

/**
 * Created by mallikaviswas on 10/22/17.
 */

public class ShowFullProfileFragment extends DialogFragment implements View.OnClickListener {
        private static final String TAG = "ShowFullProfileFragment";

        public ShowFullProfileFragment() {
            // Empty constructor is required for DialogFragment
        }

        public static ShowFullProfileFragment newInstance(String userId) {
            ShowFullProfileFragment frag = new ShowFullProfileFragment();
            Bundle args = new Bundle();
            args.putString("userid", userId);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_show_full_profile, container);
        }

        @Override
        public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            UserFirebaseDas userFirebaseDas = new UserFirebaseDas(getActivity());
            userFirebaseDas.getUser(getArguments().getString("userid"), new UserFirebaseDas.OnCompletionListener() {
                @Override
                public void onUserReceived(User user) {
                    setupViews(view, user);
                }
            });

            Button btnProfileClose = (Button) view.findViewById(R.id.btnProfileClose);
            btnProfileClose.setOnClickListener(this);
        }

        private void setupViews(View v, User user) {
            // Set item views based on your views and data model
            ImageView ivUserImage = (ImageView) v.findViewById(R.id.ivUserImage);
            if (user.getProfilePicUrl() == null || user.getProfilePicUrl().isEmpty()) {
                if (user.getGender().contentEquals("female")) {
                    ivUserImage.setImageResource(R.drawable.profile_img_female);
                } else if (user.getGender().contentEquals("male")) {
                    ivUserImage.setImageResource(R.drawable.profile_img_male);
                } else {
                    ivUserImage.setImageResource(R.drawable.profile_img_neutral);
                }
            }
            else {
                Glide.with(getActivity())
                        .load(user.getProfilePicUrl())
                        .centerCrop()
                        .into(ivUserImage);
            }

            TextView tvUserName = (TextView) v.findViewById(R.id.tvUserName);
            tvUserName.setText(user.getName());

            TextView tvUserDesc = (TextView) v.findViewById(R.id.tvUserDesc);
            tvUserDesc.setText(user.getDesc());

            //TextView tvAge = (TextView) v.findViewById(R.id.tvAge);
            //tvAge.setText(user.getAge());

            TextView tvGender = (TextView) v.findViewById(R.id.tvGender);
            tvGender.setText(user.getGender());
        }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnProfileClose) {
            dismiss();
        }
    }
    }