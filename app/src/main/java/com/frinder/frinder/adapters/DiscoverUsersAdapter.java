package com.frinder.frinder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.RequestFirebaseDas;
import com.frinder.frinder.model.Request;
import com.frinder.frinder.model.User;
import com.frinder.frinder.utils.LocationUtils;
import static com.frinder.frinder.utils.LocationUtils.locationUtilInstance;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mallikaviswas on 10/12/17.
 */

public class DiscoverUsersAdapter extends RecyclerView.Adapter<DiscoverUsersAdapter.ViewHolder> {

    private static final String TAG = "DiscoverUsersAdapter";
    private List<User> mUsers;
    private Context mContext;
    private LocationUtils locationUtilInstance;

    // Pass in the contact array into the constructor
    public DiscoverUsersAdapter(Context context, List<User> users) {
        mUsers = users;
        mContext = context;
        locationUtilInstance = LocationUtils.getInstance();
        locationUtilInstance.startLocationUpdates(getContext());
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
        final User user = mUsers.get(position);

        // Set item views based on your views and data model
        if (user.getProfilePicUrl().isEmpty()) {
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
        viewHolder.tvUserDesc.setText(user.getDesc());

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivUserImage)
        ImageView ivUserImage;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvUserDesc)
        TextView tvUserDesc;
        @BindView(R.id.tvBtnRequestToMeet)
        TextView tvBtnRequestToMeet;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void sendMeetupRequest(String id) {
        RequestFirebaseDas das = new RequestFirebaseDas(mContext);
        Request request = Request.newInstance(Profile.getCurrentProfile().getId(), id);
        das.addRequest(request);
    }
}