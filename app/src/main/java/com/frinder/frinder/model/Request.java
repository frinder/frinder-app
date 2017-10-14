package com.frinder.frinder.model;

import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze={Request.class})
public class Request {

    public String uid;
    public String senderId;
    public String receiverId;
    public boolean accepted;
    public Date sentTimestamp;
    public Date acceptedTimestamp;

}
