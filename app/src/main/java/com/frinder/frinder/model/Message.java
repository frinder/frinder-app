package com.frinder.frinder.model;

import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze={Message.class})
public class Message {

    public enum Type {
        TYPE_SENT,
        TYPE_RECEIVED
    }

    public String uid;
    public MessageThread thread;
    public String text;
    public Type type;
    public Date timestamp;

}
