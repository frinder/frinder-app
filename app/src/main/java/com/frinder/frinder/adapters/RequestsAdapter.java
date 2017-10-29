package com.frinder.frinder.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.RequestFirebaseDas;
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
    private UserFirebaseDas mUserDas;
    private RequestFirebaseDas mRequestDas;
    private RequestFirebaseDas mRequestFirebaseDas;

    public RequestsAdapter(Context context, List<Request> requests) {
        mRequests = requests;
        mContext = context;
        mUserDas = new UserFirebaseDas(mContext);
        mRequestDas = new RequestFirebaseDas(mContext);
        mRequestFirebaseDas = new RequestFirebaseDas(mContext);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Get the data model based on position
        final Request request = getRequest(position);

        String userId = getUserId(request);
        holder.position = position;
        final boolean shouldDisplayUnreadTag = shouldDisplayUnreadTag(request);
        // TODO: Move this to a static variable/cache this
        mUserDas.getUser(userId, new UserFirebaseDas.OnCompletionListener() {
            @Override
            public void onUserReceived(User user) {
                // Ensure that the ViewHolder is still at the same position
                if (user != null && holder.position == position) {
                    populateUserDetails(holder, user);
                    holder.ivNewTag.setVisibility(shouldDisplayUnreadTag ? View.VISIBLE : View.INVISIBLE);
                }
            }
        });
        if (shouldDisplayUnreadTag) {
            mRequestFirebaseDas.updateUnread(request, false);
        }
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    protected RequestFirebaseDas getRequestDas() {
        return mRequestDas;
    }

    protected Request getRequest(int position) {
        return mRequests.get(position);
    }

    protected void deleteItem(int position) {
        if (position < mRequests.size()) {
            mRequests.remove(position);
            notifyItemRemoved(position);
        }
    }

    protected abstract boolean shouldDisplayUnreadTag(Request request);

    protected Context getContext() {
        return mContext;
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
