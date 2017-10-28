package com.frinder.frinder.model;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import org.parceler.Parcel;

import java.util.Date;
import java.util.UUID;

@Parcel(analyze={Message.class})
public class Message implements IMessage{

    public enum Type {
        TYPE_SENT,
        TYPE_RECEIVED
    }

    public MessageThread thread;
    public String text;
    public Type type;
    public Date timestamp;
    public String senderId;
    public String uid;

    public Message() {
        this.uid = UUID.randomUUID().toString();
    }

    @Override
    public String getId() {
        return uid;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return null;
    }

    @Override
    public Date getCreatedAt() {
        return timestamp;
    }
}
