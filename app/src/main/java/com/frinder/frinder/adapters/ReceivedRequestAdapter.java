package com.frinder.frinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frinder.frinder.R;
import com.frinder.frinder.model.Request;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReceivedRequestAdapter extends RequestsAdapter {

    public ReceivedRequestAdapter(Context context, List<Request> requests) {
        super(context, requests);
    }

    String getUserId(Request request) {
        return request.senderId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View requestView = inflater.inflate(R.layout.item_request_received, parent, false);

        // Return a new holder instance
        ReceivedViewHolder viewHolder = new ReceivedViewHolder(requestView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        final Request request = getRequest(position);
        ReceivedViewHolder viewHolder = (ReceivedViewHolder)holder;
        viewHolder.tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRequestDas().updateAccepted(request, true);
                deleteItem(position);
            }
        });

        viewHolder.tvDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRequestDas().deleteRequest(request);
                deleteItem(position);
            }
        });
    }

    @Override
    protected boolean shouldDisplayUnreadTag(Request request) {
        return request.unread;
    }


    public class ReceivedViewHolder extends RequestsAdapter.ViewHolder {

        @BindView(R.id.tvAccept)
        TextView tvAccept;
        @BindView(R.id.tvDeny)
        TextView tvDeny;

        public ReceivedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
