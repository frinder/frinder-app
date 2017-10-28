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
        if (lastTimestamp == null || o.lastTimestamp == null) {
            return 0;
        }

        return lastTimestamp.compareTo(o.lastTimestamp);
    }
}
