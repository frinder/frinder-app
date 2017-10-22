package com.frinder.frinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Profile;
import com.frinder.frinder.R;
import com.frinder.frinder.activity.MessageDetailActivity;
import com.frinder.frinder.dataaccess.MessageFirebaseDas;
import com.frinder.frinder.model.MessageThread;
import com.frinder.frinder.model.Request;
import com.frinder.frinder.utils.Constants;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AcceptedRequestAdapter extends RequestsAdapter {

    private MessageFirebaseDas mMessageFirebaseDas;

    public AcceptedRequestAdapter(Context context, List<Request> requests) {
        super(context, requests);
        mMessageFirebaseDas = new MessageFirebaseDas(getContext());
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

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        final Request request = getRequest(position);
        AcceptedViewHolder viewHolder = (AcceptedViewHolder)holder;
        viewHolder.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMessageFirebaseDas.getOrCreateThread(Profile.getCurrentProfile().getId(),
                        getUserId(request),
                        new MessageFirebaseDas.OnCompletionListener() {
                            @Override
                            public void onThreadReceived(MessageThread thread) {
                                Intent i = new Intent(getContext(), MessageDetailActivity.class);
                                i.putExtra(Constants.INTENT_EXTRA_THREAD, Parcels.wrap(thread));
                                getContext().startActivity(i);
                            }
                        });
            }
        });
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
