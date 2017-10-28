package com.frinder.frinder.views;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.MessageThread;
import com.frinder.frinder.model.User;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ThreadDialogViewHolder extends DialogsListAdapter.DialogViewHolder<MessageThread> {

    @BindView(R.id.dialogName)
    TextView dialogName;
    @BindView(R.id.dialogAvatar)
    ImageView dialogAvatar;

    private UserFirebaseDas mUserDas;
    private MessageThread mCurrentDialog;
    private Context mContext;

    public ThreadDialogViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();

        mUserDas = new UserFirebaseDas(mContext);
    }

    @Override
    public void onBind(final MessageThread dialog) {
        super.onBind(dialog);
        this.mCurrentDialog = dialog;
        mUserDas.getUser(dialog.userId, new UserFirebaseDas.OnCompletionListener() {
            @Override
            public void onUserReceived(User user) {
                // Ensure that the ViewHolder is still at the same position
                if (dialog == mCurrentDialog) {
                    dialogName.setText(user.getName());
                    Glide.with(mContext)
                            .load(user.getProfilePicUrl())
                            .centerCrop()
                            .into(dialogAvatar);
                }
            }
        });
    }
}