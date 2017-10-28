package com.frinder.frinder.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.frinder.frinder.R;
import com.frinder.frinder.activity.DiscoverActivity;
import com.frinder.frinder.dataaccess.RequestFirebaseDas;
import com.frinder.frinder.fragments.ShowFullProfileFragment;
import com.frinder.frinder.model.DiscoverUser;
import com.frinder.frinder.model.Request;
import com.frinder.frinder.model.User;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mallikaviswas on 10/12/17.
 */

public class DiscoverUsersAdapter extends RecyclerView.Adapter<DiscoverUsersAdapter.ViewHolder> {

    private static final String TAG = "DiscoverUsersAdapter";
    private List<DiscoverUser> mUsers;
    private Context mContext;

    // Pass in the contact array into the constructor
    public DiscoverUsersAdapter(Context context, List<DiscoverUser> users) {
        mUsers = users;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public DiscoverUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View userView = inflater.inflate(R.layout.item_discover_user, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final DiscoverUsersAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final DiscoverUser discoverUser = mUsers.get(position);
        final User user = discoverUser.getUser();

        // Set item views based on your views and data model
        if (user.getProfilePicUrl() == null || user.getProfilePicUrl().isEmpty()) {
            if (user.getGender().contentEquals("female")) {
                viewHolder.ivUserImage.setImageResource(R.drawable.profile_img_female);
            } else if (user.getGender().contentEquals("male")) {
                viewHolder.ivUserImage.setImageResource(R.drawable.profile_img_male);
            } else {
                viewHolder.ivUserImage.setImageResource(R.drawable.profile_img_neutral);
            }
        }
        else {
            Glide.with(mContext)
                    .load(user.getProfilePicUrl())
                    .centerCrop()
                    .into(viewHolder.ivUserImage);
        }

        viewHolder.tvUserName.setText(user.getName());

        double dInMtr = discoverUser.getDistanceFromAppUser();
        double dInMiles = dInMtr/1609;
        double dInFeet = dInMtr*3.2808;
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        double distance = Double.parseDouble(numberFormat.format(dInMiles));
        if (distance == 0) {
            distance = Double.parseDouble(numberFormat.format(dInFeet));
            viewHolder.tvDistance.setText(distance + "ft");
            //Log.d(TAG, "FINAL: " + user.getName() + " @ " + distance + "ft");
        }
        else {
            viewHolder.tvDistance.setText(distance + "mi");
            //Log.d(TAG, "FINAL: " + user.getName() + " @ " + distance + "mi");
        }

        viewHolder.tvUserDesc.setText(user.getDesc());

        viewHolder.llUserInterests.removeAllViews();
        TextView tvLikesLabel = new TextView(mContext);
        tvLikesLabel.setText("Likes:");
        tvLikesLabel.setTypeface(tvLikesLabel.getTypeface(), Typeface.BOLD);
        viewHolder.llUserInterests.addView(tvLikesLabel);
        for (String interest : user.getInterests()) {
            TextView textView = new TextView(mContext);
            textView.setText(interest);
            if (discoverUser.getCommonInterests().contains(interest)) {
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.dark_orange));
            }
            viewHolder.llUserInterests.addView(textView);
        }

        // TODO: Set this based on status of request
        viewHolder.tvBtnRequestToMeet.setClickable(true);
        viewHolder.tvBtnRequestToMeet.setText(R.string.tv_btn_request_to_meet_label);
        viewHolder.tvBtnRequestToMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Send meetup request to selected user");
                sendMeetupRequest(user.getUid());
                // TODO: Check whether request suceeded
                viewHolder.tvBtnRequestToMeet.setClickable(false);
                viewHolder.tvBtnRequestToMeet.setText(R.string.tv_btn_existing_request_label);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivUserImage)
        ImageView ivUserImage;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvDistance)
        TextView tvDistance;
        @BindView(R.id.tvUserDesc)
        TextView tvUserDesc;
        @BindView(R.id.llUserInterests)
        LinearLayout llUserInterests;
        @BindView(R.id.tvBtnRequestToMeet)
        TextView tvBtnRequestToMeet;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                DiscoverUser discoverUser = mUsers.get(position);
                User user = discoverUser.getUser();

                DiscoverActivity myActivity = (DiscoverActivity)mContext;
                FragmentManager fragmentManager = myActivity.getSupportFragmentManager();
                ShowFullProfileFragment showFullProfileFragment = ShowFullProfileFragment.newInstance(discoverUser);
                showFullProfileFragment.show(fragmentManager, "fragment_show_full_profile");
            }
        }
    }

    private void sendMeetupRequest(String id) {
        RequestFirebaseDas das = new RequestFirebaseDas(mContext);
        Request request = Request.newInstance(Profile.getCurrentProfile().getId(), id);
        das.addRequest(request);
    }
}