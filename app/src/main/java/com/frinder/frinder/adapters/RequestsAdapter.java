package com.frinder.frinder.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.Request;
import com.frinder.frinder.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RequestsAdapter extends
        RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private List<Request> mRequests;
    private Context mContext;

    public RequestsAdapter(Context context, List<Request> requests) {
        mRequests = requests;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Get the data model based on position
        final Request request = mRequests.get(position);

        String userId = getUserId(request);
        holder.position = position;
        holder.ivNewTag.setVisibility(request.unread ? View.VISIBLE : View.INVISIBLE);
        // TODO: Move this to a static variable/cache this
        UserFirebaseDas das = new UserFirebaseDas(mContext);
        das.getUser(userId, new UserFirebaseDas.OnCompletionListener() {
            @Override
            public void onUserReceived(User user) {
                // Ensure that the ViewHolder is still at the same position
                if (user != null && holder.position == position) {
                    populateUserDetails(holder, user);
                    holder.ivNewTag.setVisibility(request.unread ? View.VISIBLE : View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    private void populateUserDetails(ViewHolder holder, User user) {
        holder.tvUserName.setText(user.getName());
        holder.tvUserDesc.setText(user.getDesc());
        Glide.with(mContext)
                .load(user.getProfilePicUrl())
                .centerCrop()
                .into(holder.ivUserImage);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivUserImage)
        ImageView ivUserImage;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvUserDesc)
        TextView tvUserDesc;
        @BindView(R.id.ivNewTag)
        ImageView ivNewTag;

        int position;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    abstract String getUserId(Request request);

}
