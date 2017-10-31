package com.frinder.frinder.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
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
import java.util.ArrayList;
import java.util.Arrays;
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
                viewHolder.ivDiscoverUserImage.setImageResource(R.drawable.profile_img_female);
            } else if (user.getGender().contentEquals("male")) {
                viewHolder.ivDiscoverUserImage.setImageResource(R.drawable.profile_img_male);
            } else {
                viewHolder.ivDiscoverUserImage.setImageResource(R.drawable.profile_img_neutral);
            }
        }
        else {
            Glide.with(mContext)
                    .load(user.getProfilePicUrl())
                    .centerCrop()
                    .into(viewHolder.ivDiscoverUserImage);
        }

        viewHolder.tvDiscoverUserName.setText(user.getName());

        double dInMtr = discoverUser.getDistanceFromAppUser();
        double dInMiles = dInMtr/1609;
        double dInFeet = dInMtr*3.2808;
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        double distance = Double.parseDouble(numberFormat.format(dInMiles));
        if (distance < 0.1) {
            distance = Double.parseDouble(numberFormat.format(dInFeet));
            if (distance < 10) {
                viewHolder.tvDiscoverDistance.setText("< 10ft");
            }
            else {
                viewHolder.tvDiscoverDistance.setText(distance + "ft");
                //Log.d(TAG, "FINAL: " + user.getName() + " @ " + distance + "ft");
            }
        }
        else {
            viewHolder.tvDiscoverDistance.setText(distance + "mi");
            //Log.d(TAG, "FINAL: " + user.getName() + " @ " + distance + "mi");
        }

        viewHolder.tvDiscoverUserDesc.setText(user.getDesc());

        //viewHolder.llDiscoverInterestsLayout.removeViews(1, viewHolder.llDiscoverInterestsLayout.getChildCount()-1);
        viewHolder.llDiscoverInterestsLayout.removeAllViews();

        if (!user.getInterests().isEmpty()) {
            viewHolder.hScrollViewDiscoverInterests.setVisibility(View.VISIBLE);

            ArrayList<String> filterInterestLabel = new ArrayList<String>(Arrays.asList(mContext.getResources().getStringArray(R.array.filter_interest_label)));
            ArrayList<String> filterInterestForDB = new ArrayList<String>(Arrays.asList(mContext.getResources().getStringArray(R.array.filter_interest_forDB)));
            TypedArray filterInterestColorArray = mContext.getResources().obtainTypedArray(R.array.filter_interest_color);
            TypedArray filterInterestIconArray = mContext.getResources().obtainTypedArray(R.array.filter_interest_icon);

            for (String interest : user.getInterests()) {
                int index = filterInterestForDB.indexOf(interest);

                ImageView imageView = new ImageView(mContext);
                imageView.setImageDrawable(ContextCompat.getDrawable(mContext, filterInterestIconArray.getResourceId(index, 0)));
                imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray ));

                viewHolder.llDiscoverInterestsLayout.addView(imageView);

                View tView = viewHolder.llDiscoverInterestsLayout.getChildAt(viewHolder.llDiscoverInterestsLayout.getChildCount() - 1);
                LinearLayout.LayoutParams tLayoutParams = (LinearLayout.LayoutParams) tView.getLayoutParams();
                tLayoutParams.setMargins(15, 15, 15, 15); // llp.setMargins(left, top, right, bottom);
                imageView.setLayoutParams(tLayoutParams);
            }

            filterInterestColorArray.recycle();
            filterInterestIconArray.recycle();
        }
        else {
            viewHolder.hScrollViewDiscoverInterests.setVisibility(View.GONE);
        }

        // TODO: Set this based on status of request
        if (!discoverUser.isMeetupRequestSent()) {
            viewHolder.llDiscoverBtnDistance.setBackground(ContextCompat.getDrawable(mContext, R.drawable.item_user_blueorange_gradient));
            viewHolder.tvDiscoverBtnRequestSent.setVisibility(View.GONE);
            viewHolder.ivDiscoverBtnRequestSentIcon.setVisibility(View.GONE);
            viewHolder.tvDiscoverBtnRequestToMeet.setVisibility(View.VISIBLE);

            viewHolder.tvDiscoverBtnRequestToMeet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Send meetup request to selected user");
                    discoverUser.setMeetupRequestSent(true);
                    sendMeetupRequest(user.getUid());
                    // TODO: Check whether request suceeded
                    viewHolder.llDiscoverBtnDistance.setBackground(ContextCompat.getDrawable(mContext, R.drawable.item_user_bluegreen_gradient));
                    viewHolder.tvDiscoverBtnRequestToMeet.setVisibility(View.GONE);
                    viewHolder.tvDiscoverBtnRequestSent.setVisibility(View.VISIBLE);
                    viewHolder.ivDiscoverBtnRequestSentIcon.setVisibility(View.VISIBLE);
                }
            });
        }
        else {
            viewHolder.llDiscoverBtnDistance.setBackground(ContextCompat.getDrawable(mContext, R.drawable.item_user_bluegreen_gradient));
            viewHolder.tvDiscoverBtnRequestToMeet.setVisibility(View.GONE);
            viewHolder.tvDiscoverBtnRequestSent.setVisibility(View.VISIBLE);
            viewHolder.ivDiscoverBtnRequestSentIcon.setVisibility(View.VISIBLE);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView (R.id.llDiscoverBtnDistance)
        LinearLayout llDiscoverBtnDistance;
        @BindView(R.id.ivDiscoverUserImage)
        ImageView ivDiscoverUserImage;
        @BindView(R.id.tvDiscoverUserName)
        TextView tvDiscoverUserName;
        @BindView(R.id.tvDiscoverDistance)
        TextView tvDiscoverDistance;
        @BindView(R.id.tvDiscoverUserDesc)
        TextView tvDiscoverUserDesc;
        @BindView(R.id.tvDiscoverBtnRequestToMeet)
        TextView tvDiscoverBtnRequestToMeet;
        @BindView(R.id.llDiscoverInterestsLayout)
        LinearLayout llDiscoverInterestsLayout;
        @BindView(R.id.hScrollViewDiscoverInterests)
        HorizontalScrollView hScrollViewDiscoverInterests;
        @BindView(R.id.tvDiscoverBtnRequestSent)
        TextView tvDiscoverBtnRequestSent;
        @BindView(R.id.ivDiscoverBtnRequestSentIcon)
        ImageView ivDiscoverBtnRequestSentIcon;

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