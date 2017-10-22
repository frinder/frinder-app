package com.frinder.frinder.model;

import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze={MessageThread.class})
public class MessageThread {

    public String uid;
    public String userId;
    public String messageSnippet;
    public boolean unread;
    public Date lastTimestamp;

}
