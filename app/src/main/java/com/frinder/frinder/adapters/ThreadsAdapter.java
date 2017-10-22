package com.frinder.frinder.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.MessageThread;
import com.frinder.frinder.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThreadsAdapter extends
        RecyclerView.Adapter<ThreadsAdapter.ViewHolder> {

    private List<MessageThread> mThreads;
    private Context mContext;
    private UserFirebaseDas mUserDas;

    public ThreadsAdapter(Context context, List<MessageThread> threads) {
        mThreads = threads;
        mContext = context;
        mUserDas = new UserFirebaseDas(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.item_thread, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Get the data model based on position
        final MessageThread thread = mThreads.get(position);

        holder.position = position;
        holder.tvSnippet.setText(thread.messageSnippet);
        mUserDas.getUser(thread.senderId, new UserFirebaseDas.OnCompletionListener() {
            @Override
            public void onUserReceived(User user) {
                // Ensure that the ViewHolder is still at the same position
                if (user != null && holder.position == position) {
                    populateUserDetails(holder, user);
                    holder.ivNewTag.setVisibility(thread.unread ? View.VISIBLE : View.INVISIBLE);
                }
            }
        });

        // TODO: Add last timestamp
    }

    @Override
    public int getItemCount() {
        return mThreads.size();
    }

    private void populateUserDetails(ViewHolder holder, User user) {
        holder.tvUserName.setText(user.getName());
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
        @BindView(R.id.tvSnippet)
        TextView tvSnippet;
        @BindView(R.id.tvTimestamp)
        TextView tvTimestamp;
        @BindView(R.id.ivNewTag)
        ImageView ivNewTag;

        int position;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
