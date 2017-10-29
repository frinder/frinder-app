package com.frinder.frinder.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Profile;
import com.frinder.frinder.R;
import com.frinder.frinder.activity.MessageDetailActivity;
import com.frinder.frinder.dataaccess.MessageFirebaseDas;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.MessageThread;
import com.frinder.frinder.model.Request;
import com.frinder.frinder.model.User;
import com.frinder.frinder.utils.Constants;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AcceptedRequestAdapter extends RequestsAdapter {

    private MessageFirebaseDas mMessageFirebaseDas;
    private UserFirebaseDas mUserFirebaseDas;

    public AcceptedRequestAdapter(Context context, List<Request> requests) {
        super(context, requests);
        mMessageFirebaseDas = new MessageFirebaseDas(getContext());
        mUserFirebaseDas = new UserFirebaseDas(getContext());
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
        final AcceptedViewHolder viewHolder = (AcceptedViewHolder)holder;
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
        if(request.locationShare) {
            viewHolder.tvNavigateTo.setVisibility(View.VISIBLE);
        }
        viewHolder.tvNavigateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = getUserId(request);
                mUserFirebaseDas.getUser(userId, new UserFirebaseDas.OnCompletionListener() {
                    @Override
                    public void onUserReceived(User user) {
                        ArrayList<Double> location = user.getLocation();
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+location.get(0)+","+location.get(1)+"&mode=w");
                        try {
                            viewHolder.tvNavigateTo.setVisibility(View.INVISIBLE);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            getContext().startActivity(mapIntent);
                            getRequestDas().updateOneTimeLocation(request, false);
                        }
                        catch (Exception e)
                        {
                            getRequestDas().updateOneTimeLocation(request, true);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage("Please install Google Maps");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Install", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                                    getContext().startActivity(intent);
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected boolean shouldDisplayUnreadTag(Request request) {
        return request.unread && request.senderId.equals(Profile.getCurrentProfile().getId());
    }


    public class AcceptedViewHolder extends RequestsAdapter.ViewHolder {

        @BindView(R.id.tvMessage)
        TextView tvMessage;

        @BindView(R.id.tvNavigateTo)
        TextView tvNavigateTo;

        public AcceptedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
