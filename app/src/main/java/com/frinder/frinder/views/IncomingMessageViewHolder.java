package com.frinder.frinder.views;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.Message;
import com.frinder.frinder.model.User;
import com.stfalcon.chatkit.messages.MessageHolders;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IncomingMessageViewHolder extends MessageHolders.IncomingTextMessageViewHolder<Message> {

    @BindView(R.id.messageUserAvatar)
    ImageView messageUserAvatar;
    private Context mContext;
    private Message mCurrentMessage;

    private UserFirebaseDas mUserDas;

    public IncomingMessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();

        mUserDas = new UserFirebaseDas(mContext);
    }

    @Override
    public void onBind(final Message message) {
        super.onBind(message);
        this.mCurrentMessage = message;
        messageUserAvatar.setVisibility(View.VISIBLE);
        mUserDas.getUser(message.senderId, new UserFirebaseDas.OnCompletionListener() {
            @Override
            public void onUserReceived(User user) {
                // Ensure that the ViewHolder is still at the same position
                if (message == mCurrentMessage) {
                    Glide.with(mContext)
                            .load(user.getProfilePicUrl())
                            .centerCrop()
                            .into(messageUserAvatar);
                }
            }
        });
    }
}
