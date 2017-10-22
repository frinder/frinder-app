package com.frinder.frinder.model;

import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze={MessageThread.class})
public class MessageThread implements Comparable<MessageThread> {

    public String uid;
    public String userId;
    public String messageSnippet;
    public boolean unread;
    public Date lastTimestamp;


    @Override
    public int compareTo(MessageThread o) {
        return lastTimestamp.compareTo(o.lastTimestamp);
    }
}
