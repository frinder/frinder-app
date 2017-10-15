package com.frinder.frinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Profile;
import com.frinder.frinder.R;
import com.frinder.frinder.model.Request;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AcceptedRequestAdapter extends RequestsAdapter {

    public AcceptedRequestAdapter(Context context, List<Request> requests) {
        super(context, requests);
    }

    String getUserId(Request request) {
        String currentProfileId = Profile.getCurrentProfile().getId();
        return (request.senderId.equals(currentProfileId)) ? request.receiverId : request.senderId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View requestView = inflater.inflate(R.layout.item_request_accepted, parent, false);

        // Return a new holder instance
        AcceptedViewHolder viewHolder = new AcceptedViewHolder(requestView);
        return viewHolder;
    }

    public class AcceptedViewHolder extends RequestsAdapter.ViewHolder {

        @BindView(R.id.tvMessage)
        TextView tvMessage;

        public AcceptedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
