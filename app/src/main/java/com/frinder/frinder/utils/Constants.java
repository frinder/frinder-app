package com.frinder.frinder.utils;


public final class Constants {

    // User Firebase Datastore Keys (Column Names)
    public static final String USER_COLUMN_ID = "id" ;
    public static final String USER_COLUMN_NAME = "name" ;
    public static final String USER_COLUMN_GENDER = "gender" ;
    public static final String USER_COLUMN_EMAIL = "email" ;
    public static final String USER_COLUMN_LINK_URL = "linkUrl" ;
    public static final String USER_COLUMN_AGE = "age" ;
    public static final String USER_COLUMN_DESC = "desc" ;
    public static final String USER_COLUMN_INTERESTS = "interests" ;
    public static final String USER_COLUMN_LOCATION = "location" ;
    public static final String USER_COLUMN_PROFILE_PIC_URL = "profilePicUrl";
    public static final String USER_COLUMN_TIMESTAMP = "timestamp";
    public static final String USER_COLUMN_DISCOVERABLE = "discoverable";

    // Request Firebase Datastore Keys (Column Names)
    public static final String REQUEST_COLUMN_ACCEPTED = "accepted";
    public static final String REQUEST_COLUMN_ACCEPTED_TIMESTAMP = "acceptedTimestamp";
    public static final String REQUEST_COLUMN_RECEIVER_ID = "receiverId";
    public static final String REQUEST_COLUMN_SENDER_ID = "senderId";
    public static final String REQUEST_COLUMN_SENT_TIMESTAMP = "sentTimestamp";
    public static final String REQUEST_COLUMN_UNREAD = "unread";
    public static final String REQUEST_COLUMN_LOCATION_SHARE = "locationShare";

    // Thread Firebase Datastore Keys (Column Names)
    public static final String THREAD_COLUMN_MESSAGES = "messages";
    public static final String THREAD_COLUMN_UNREAD = "unread";
    public static final String THREAD_COLUMN_USER1 = "user1";
    public static final String THREAD_COLUMN_USER2 = "user2";
    public static final String THREAD_COLUMN_LAST_MESSAGE = "lastMessage";
    public static final String THREAD_COLUMN_LAST_TIMESTAMP = "lastTimestamp";
    public static final String THREAD_COLUMN_LAST_SENDERID = "lastSenderId";

    // Message Firebase Datastore Keys (Column Names)
    public static final String MESSAGE_COLUMN_ID = "messageId";
    public static final String MESSAGE_COLUMN_SENDERID = "senderId";
    public static final String MESSAGE_COLUMN_TEXT = "text";
    public static final String MESSAGE_COLUMN_TIMESTAMP = "timestamp";

    // Facebook data access keys
    public static final String FACEBOOK_PROFILE_ID = "id";
    public static final String FACEBOOK_PROFILE_NAME = "name";
    public static final String FACEBOOK_PROFILE_EMAIL = "email";
    public static final String FACEBOOK_PROFILE_GENDER = "gender";
    public static final String FACEBOOK_PROFILE_AGE_RANGE = "age_range";
    public static final String FACEBOOK_PROFILE_LINK = "link";
    public static final String FACEBOOK_PROFILE_PUBLIC_PROFILE = "public_profile";

    // Facebook places key
    public static final String FACEBOOK_PLACES_DESCRIPTION = "description";
    public static final String FACEBOOK_PLACES_CHECKINS = "checkins";
    public static final String FACEBOOK_PLACES_LOCATION = "location";
    public static final String FACEBOOK_PLACES_LOCATION_LATITUDE = "latitude";
    public static final String FACEBOOK_PLACES_LOCATION_LONGITUDE = "longitude";
    public static final String FACEBOOK_PLACES_NAME = "name";
    public static final String FACEBOOK_PLACES_PICTURE = "picture";
    public static final String FACEBOOK_PLACES_PICTURE_DATA = "data";
    public static final String FACEBOOK_PLACES_PICTURE_DATA_URL = "url";
    public static final String FACEBOOK_PLACES_ADDRESS = "single_line_address";

    // Intent extra
    public static final String INTENT_EXTRA_THREAD = "thread";

}
