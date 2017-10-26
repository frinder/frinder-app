package com.frinder.frinder.model;

import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze={Request.class})
public class Request {

    public String uid;
    public String senderId;
    public String receiverId;
    public boolean accepted;
    public boolean unread;
    public Date sentTimestamp;
    public Date acceptedTimestamp;
    public boolean locationShare;

    public static Request newInstance(String senderId, String receiverId) {
        Request request = new Request();
        request.senderId = senderId;
        request.receiverId = receiverId;
        request.unread = true;
        request.accepted = false;
        request.sentTimestamp = new Date();
        request.locationShare = true;
        return request;
    }

}
