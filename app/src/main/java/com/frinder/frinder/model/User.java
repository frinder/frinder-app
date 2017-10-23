package com.frinder.frinder.model;

import com.frinder.frinder.utils.Constants;
import com.stfalcon.chatkit.commons.models.IUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


//TODO change to parcelable
public class User implements Serializable, IUser {
    private String uid;
    private String name;
    private String email;
    private String gender;
    private String profilePicUrl;
    private String linkUrl;
    private int age;
    private String desc;
    private ArrayList<String> interests;
    private ArrayList<Double> location;
    private Date timestamp;
    private Boolean discoverable = true;
    private String token;

    public User() {
        interests = new ArrayList<>();
        location = new ArrayList<>();
    }

    public String getUid() {
        return uid;
    }

    @Override
    public String getId() {
        return uid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return profilePicUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getDesc() {
        return desc;
    }

    public String getToken() {
        return token;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Boolean getDiscoverable() {
        return discoverable;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public ArrayList<Double> getLocation() {
        return location;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setLocation(ArrayList<Double> location) {
        this.location = location;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", profilePicUrl='" + profilePicUrl + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", age=" + age +
                ", desc='" + desc + '\'' +
                ", interests=" + interests +
                ", location=" + location +
                ", timestamp=" + timestamp +
                ", discoverable=" + discoverable +
                ", token=" + token +
                '}';
    }

    public static User fromJSON(JSONObject object) {
        String id, name, email, gender, link, ageRange;
        User user = new User();
        try {
            id = object.getString(Constants.FACEBOOK_PROFILE_ID);

            if (object.has(Constants.FACEBOOK_PROFILE_NAME)) {
                name = object.getString(Constants.FACEBOOK_PROFILE_NAME);
            }
            else {
                name = "";
            }

            if (object.has(Constants.FACEBOOK_PROFILE_EMAIL)) {
                email = object.getString(Constants.FACEBOOK_PROFILE_EMAIL);
            }
            else {
                email = "";
            }

            if (object.has(Constants.FACEBOOK_PROFILE_GENDER)) {
                gender = object.getString(Constants.FACEBOOK_PROFILE_GENDER);
            }
            else {
                gender = "neutral";
            }

            if (object.has(Constants.FACEBOOK_PROFILE_LINK)) {
                link = object.getString(Constants.FACEBOOK_PROFILE_LINK);
            }
            else {
                link = "";
            }

            if (object.has(Constants.FACEBOOK_PROFILE_AGE_RANGE)) {
                ageRange = object.getString(Constants.FACEBOOK_PROFILE_AGE_RANGE);
            }
            else {
                ageRange = "";
            }

            user.setUid(id);
            user.setName(name);
            user.setEmail(email);
            user.setGender(gender);
            user.setLinkUrl(link);
            user.setProfilePicUrl("http://graph.facebook.com/" + id + "/picture?type=large");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
