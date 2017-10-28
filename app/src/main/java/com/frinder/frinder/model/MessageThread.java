package com.frinder.frinder.model;

import com.facebook.Profile;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Parcel(analyze={MessageThread.class})
public class MessageThread implements Comparable<MessageThread>, IDialog {

    public String uid;
    public String userId;
    public String messageSnippet;
    public boolean unread;
    public Date lastTimestamp;
    public String lastSenderId;

    @Override
    public int compareTo(MessageThread o) {
        if (lastTimestamp == null || o.lastTimestamp == null) {
            return 0;
        }

        return lastTimestamp.compareTo(o.lastTimestamp);
    }

    @Override
    public String getId() {
        return uid;
    }

    @Override
    public String getDialogPhoto() {
        // Loaded asynchronously
        return null;
    }

    @Override
    public String getDialogName() {
        // Loaded asynchronously
        return null;
    }

    @Override
    public List<? extends IUser> getUsers() {
        // Loaded asynchronously
        return new ArrayList<IUser>();
    }

    @Override
    public IMessage getLastMessage() {
        Message message = new Message();
        message.thread = this;
        message.timestamp = lastTimestamp;
        message.text = messageSnippet;
        message.senderId = lastSenderId;
        return message;
    }

    @Override
    public void setLastMessage(IMessage message) {
        //  not used
    }

    @Override
    public int getUnreadCount() {
        if (isCurrentUser(userId)) {
            return 0;
        }
        return unread ? 1 : 0;
    }

    private boolean isCurrentUser(String userId) {
        String currentUserId = Profile.getCurrentProfile().getId();
        return (currentUserId == null || currentUserId.equals(userId));
    }
}
