package com.frinder.frinder.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.Message;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesAdapter extends
        RecyclerView.Adapter<MessagesAdapter.BaseViewHolder> {

    private List<Message> mMessages;
    private Context mContext;
    private UserFirebaseDas mUserDas;

    public MessagesAdapter(Context context, List<Message> messages) {
        mMessages = messages;
        mContext = context;
        mUserDas = new UserFirebaseDas(mContext);
    }

    @Override
    public MessagesAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;
        switch (Message.Type.values()[viewType]) {
            case TYPE_RECEIVED:
                view = inflater.inflate(R.layout.item_received_message, parent, false);
                return new MessagesAdapter.ReceivedViewHolder(view);
            case TYPE_SENT:
            default:
                view = inflater.inflate(R.layout.item_message, parent, false);
                return new MessagesAdapter.BaseViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        // Get the data model based on position
        final Message message = mMessages.get(position);

        holder.position = position;
        holder.tvMessage.setText(message.text);
        holder.tvTimestamp.setText(message.timestamp.toString());
        // TODO: Add relative timestamp

        switch (message.type) {
            case TYPE_RECEIVED:
                // TODO: Add user image
                break;
            case TYPE_SENT:
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        final Message message = mMessages.get(position);
        return message.type.ordinal();
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvMessage)
        TextView tvMessage;
        @BindView(R.id.tvTimestamp)
        TextView tvTimestamp;
        int position;

        public BaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ReceivedViewHolder extends BaseViewHolder {

        @BindView(R.id.ivUserImage)
        ImageView ivUserImage;

        public ReceivedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
